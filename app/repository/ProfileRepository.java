package repository;

import io.ebean.*;
import models.*;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;

import static java.lang.Integer.parseInt;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A profile repository that executes database operations in a different
 * execution context handles all interactions with the profile table .
 */
public class ProfileRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final ProfilePassportCountryRepository profilePassportCountryRepository;
    private final ProfileNationalityRepository profileNationalityRepository;
    private final ProfileTravellerTypeRepository profileTravellerTypeRepository;

    @Inject
    public ProfileRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.profilePassportCountryRepository = new ProfilePassportCountryRepository(ebeanConfig, executionContext);
        this.profileNationalityRepository = new ProfileNationalityRepository(ebeanConfig, executionContext);
        this.profileTravellerTypeRepository = new ProfileTravellerTypeRepository(ebeanConfig, executionContext);
    }

    /**
     * Method for login to check if there is a traveller account under the supplied email
     *
     * @param email String of the logged in users email
     * @return boolean if profile exists or not
     */
    public boolean checkProfileExists(String email) {
        String selectQuery = "Select * from profile WHERE email = ?";
        SqlRow row = ebeanServer.createSqlQuery(selectQuery).setParameter(1, email).findOne();
        return row != null;
    }


    /**
     * Method to validate if the given email and password match an account in the database
     * @param email users email input
     * @param password users password input
     * @return
     */
    public boolean validate(String email, String password) {
        String selectQuery = "Select * from profile WHERE email = ? and password = ?";
        SqlRow row = ebeanServer.createSqlQuery(selectQuery)
                .setParameter(1, email)
                .setParameter(2, password)
                .findOne();
        return row != null;
    }


//    /**
//     * Finds one profile using its id as a query
//     * @param profileId the users profile id
//     * @return a Profile object that matches the email
//     */
//    public CompletionStage<Optional<Profile>> findById(int profileId) {
//        return supplyAsync(() -> {
//            String qry = "Select * from profile where profile_id = ?";
//            List<SqlRow> rowList = ebeanServer.createSqlQuery(qry).setParameter(1, profileId).findList();
//            Profile profile = null;
//            if (!rowList.get(0).isEmpty()) {
//                SqlRow p = rowList.get(0);
//                Map<Integer, PassportCountry> passportCountries = profilePassportCountryRepository.getList(profileId).get();
//                Map<Integer, Nationality> nationalities = profileNationalityRepository.getList(profileId).get();
//                Map<Integer, TravellerType> travellerTypes = profileTravellerTypeRepository.getList(profileId).get();
//                //TODO call function in role repo and get the users role then add into constructor
//                profile = new Profile(profileId, p.getString("first_name"),  p.getString("middle_name"), p.getString("last_name")
//                , p.getString("email"), p.getDate("birth_date"), passportCountries, p.getString("gender"), p.getDate("time_created")
//                , nationalities, travellerTypes);
//            }
//            return Optional.ofNullable(profile);
//        }, executionContext);
//    }


    /**
     * This method finds a profile in the database using a given profile id
     *
     * @param profileId the id of the profile to find
     * @return CompletionStage holding an optional of the profile found
     */
    public CompletionStage<Optional<Profile>> findById(int profileId) {
        return supplyAsync(() -> {
            Profile profile = ebeanServer.find(Profile.class).setId(profileId).findOne();
            if (profile != null) {
                profileNationalityRepository.getList(profileId).ifPresent(profile::setNationalities);
                profilePassportCountryRepository.getList(profileId).ifPresent(profile::setPassports);
                profileTravellerTypeRepository.getList(profileId).ifPresent(profile::setTravellerTypes);
                return Optional.of(profile);
            } else {
                return Optional.empty();
            }
        });
    }

    /**
     * Finds one profile using its email as a query
     * @param email the users email
     * @return a Profile object that matches the email
     */
    public CompletionStage<Optional<Profile>> lookupEmail(String email) {
        return supplyAsync(() -> {
            String qry = "Select * from profile where email = ?";
            List<SqlRow> rowList = ebeanServer.createSqlQuery(qry).setParameter(1, email).findList();
            Profile profile = null;
            if (!rowList.get(0).isEmpty()) {
                SqlRow p = rowList.get(0);
                Map<Integer, PassportCountry> passportCountries = profilePassportCountryRepository.getList(p.getInteger("profile_id")).get();
                Map<Integer, Nationality> nationalities = profileNationalityRepository.getList(p.getInteger("profile_id")).get();
                Map<Integer, TravellerType> travellerTypes = profileTravellerTypeRepository.getList(p.getInteger("profile_id")).get();
                //TODO call function in role repo and get the users role then add into constructor
                profile = new Profile(p.getInteger("profile_id"), p.getString("first_name"),  p.getString("middle_name"), p.getString("last_name")
                        , p.getString("email"), p.getDate("birthDate"), passportCountries, p.getString("gender"), p.getDate("time_created")
                        , nationalities, travellerTypes);
            }
            return Optional.ofNullable(profile);
        }, executionContext);
    }


    /**
     * Inserts a profile into the ebean database server
     *
     * @param profile Profile object to insert into the database
     * @return the image id
     */
    public CompletionStage<Optional<Integer>> insert(Profile profile) {
        return supplyAsync(() -> {
            profile.setTimeCreated(new Date());
            Transaction txn = ebeanServer.beginTransaction();
            String qry = "INSERT into profile (first_name, middle_name, last_name, email, " +
                    "password, birth_date, gender) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            Integer value = null;
            try {
                SqlUpdate query = Ebean.createSqlUpdate(qry);
                query.setParameter(1, profile.getFirstName());
                query.setParameter(2, profile.getMiddleName());
                query.setParameter(3, profile.getLastName());
                query.setParameter(4, profile.getEmail());
                query.setParameter(5, profile.getPassword());
                query.setParameter(6, profile.getBirthDate());
                query.setParameter(7, profile.getGender());
                query.setGetGeneratedKeys(true); // Need to set the ID of the generated key
                query.execute();
                txn.commit();
                value = parseInt(query.getGeneratedKey().toString()); // Id of the newly created profile
                for (String passportName: profile.getPassportsList()) {
                    profilePassportCountryRepository.insertProfilePassportCountry(new PassportCountry(passportName), value);
                }
                for (String nationalityName: profile.getNationalityList()) {
                    profileNationalityRepository.insertProfileNationality(new Nationality(nationalityName), value);
                }
                for (String travellerTypeName: profile.getTravellerTypesList()) {
                    profileTravellerTypeRepository.insertProfileTravellerType(new TravellerType(travellerTypeName), value);
                }
        } catch(Exception e) {
            System.err.println("Search This: "+e);
        } finally {
            txn.end();
        }
            return Optional.ofNullable(value);
        }, executionContext);
    }


    /**
     * Update profile in database using Profile model object,
     * and the raw password from an input field, which will be set if it is not null.
     * @param newProfile Profile object with new details
     * @return
     */
    public CompletionStage<Optional<Integer>> update(Profile newProfile, int userId) {

    return supplyAsync(
        () -> {
          Transaction txn = ebeanServer.beginTransaction();
          String updateQuery =
              "UPDATE profile SET first_name = ?, middle_name = ?, last_name = ?, email = ?, "
                  + "birth_date = ?, gender = ? "
                  + "WHERE profile_id = ?";
          Optional<Integer> value = Optional.empty();
          try {
            if (ebeanServer.find(Profile.class).setId(userId).findOne() != null) {
              SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
              query.setParameter(1, newProfile.getFirstName());
              query.setParameter(2, newProfile.getMiddleName());
              query.setParameter(3, newProfile.getLastName());
              query.setParameter(4, newProfile.getEmail());
              query.setParameter(5, newProfile.getBirthDate());
              query.setParameter(6, newProfile.getGender());
              query.setParameter(7, userId);
              query.execute();
              txn.commit();
              profileNationalityRepository.removeAll(userId);
              profilePassportCountryRepository.removeAll(userId);
              profileTravellerTypeRepository.removeAll(userId);
              for (String passportName : newProfile.getPassportsList()) {
                profilePassportCountryRepository.insertProfilePassportCountry(
                    new PassportCountry(0, passportName), userId);
              }
              for (String nationalityName : newProfile.getNationalityList()) {
                profileNationalityRepository.insertProfileNationality(
                    new Nationality(0, nationalityName), userId);
              }
              for (String travellerTypeName : newProfile.getTravellerTypesList()) {
                profileTravellerTypeRepository.insertProfileTravellerType(
                    new TravellerType(0, travellerTypeName), userId);
              }
            //TODO call function in role repo and edit the users role
              value = Optional.of(userId);
            }
          } finally {
            txn.end();
          }
          return value;
        },
        executionContext);
    }


    /**
     * Deletes a profile from the database that matches the given email
     *
     * @param profileId the users id
     * @return an optional profile
     */
    public CompletionStage<Optional<Integer>> delete(Integer profileId) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String deleteQuery = "delete * from profile Where profile_id = ?";
            SqlUpdate query = Ebean.createSqlUpdate(deleteQuery);
            query.setParameter(1, profileId);
            query.execute();
            txn.commit();
            Integer value;
            value = parseInt(query.getGeneratedKey().toString()); // Id of the newly created profile
            return Optional.of(value);
        }, executionContext);
    }


    /**
     * Used to update (add or remove) admin privilege to another user from the Travellers page.
     * @param clickedProfileEmail the email of the user that is going to have admin privilege updated.
     * @return The email member who had their admin updated.
     */
    public CompletionStage<Optional<String>> updateAdminPrivelege(String clickedProfileEmail) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<String> value = Optional.empty();
            try {
                Profile targetProfile = ebeanServer.find(Profile.class).setId(clickedProfileEmail).findOne();
                if (targetProfile != null) {

                    targetProfile.setAdmin(!targetProfile.isAdmin());
                    targetProfile.update();
                    txn.commit();
                    value = Optional.of(clickedProfileEmail);
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }


    /**
     * Function to get all the destinations created by the signed in user.
     * @param profileId users profile Id
     * @return destList arrayList of destinations registered by the user
     */
    public Optional<ArrayList<Destination>> getDestinations(int profileId) {
        String sql = ("select * from destination where profile_id = ?");
        List<SqlRow> rowList = ebeanServer.createSqlQuery(sql).setParameter(1, profileId).findList();
        ArrayList<Destination> destList = new ArrayList<>();
        Destination dest;
        for (SqlRow aRowList : rowList) {
            dest = new Destination();
            dest.setDestinationId(aRowList.getInteger("destination_id"));
            dest.setProfileId(aRowList.getInteger("profile_id"));
            dest.setName(aRowList.getString("name"));
            dest.setType(aRowList.getString("type"));
            dest.setCountry(aRowList.getString("country"));
            dest.setDistrict(aRowList.getString("district"));
            dest.setLatitude(aRowList.getDouble("latitude"));
            dest.setLongitude(aRowList.getDouble("longitude"));
            dest.setVisible(aRowList.getBoolean("visible") ? 1 : 0);
            destList.add(dest);
        }
        return Optional.of(destList);
    }

    public Optional<Integer> getAdminId() {
        //TODO Implement it to find admin id ideally default admin but any is fine
        return Optional.of(0);
    }
}
