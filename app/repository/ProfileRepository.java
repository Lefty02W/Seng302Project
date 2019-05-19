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
        Profile existingEmail = ebeanServer.find(Profile.class).where().like("email", email).findOne();
        return existingEmail != null;
    }


    /**
     * Method to validate if the given email and password match an account in the database
     *
     * @param email users email input
     * @param password users password input
     * @return
     */
    public boolean validate(String email, String password) {
        //TODO improve this please
        Profile profile = ebeanServer.find(Profile.class).where().like("email", email).findOne();
        if (profile == null) {
            return false;
        }
        return profile.getEmail().equals(email) && profile.getPassword().equals(password);
    }


    /**
     * Finds one profile using a given email as a query
     * @param email the users email
     * @return a Profile object that matches the email
     */
    public CompletionStage<Optional<Profile>> lookup(String email) {
        return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(Profile.class).setId(email).findOne()), executionContext);
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
//            System.out.println("Info\n"+profile.getFirstName()+"\n"+profile.getMiddleName()+"\n"+profile.getLastName()+
//                    "\n"+profile.getEmail()+"\n"+profile.getPassword()+"\n"+profile.getBirthDate()+"\n"+profile.getGender()
//            +"\n"+profile.getPassportsList().get(0));
            try {
                SqlUpdate query = Ebean.createSqlUpdate(qry);
                query.setParameter(1, profile.getFirstName());
                query.setParameter(2, profile.getMiddleName());
                query.setParameter(3, profile.getLastName());
                query.setParameter(4, profile.getEmail());
                query.setParameter(5, profile.getPassword());
                query.setParameter(6, profile.getBirthDate());
                query.setParameter(7, profile.getGender());
                //query.setParameter(8, profile.isAdmin());
                query.setGetGeneratedKeys(true); // Need to set the ID of the generated key
                query.execute();
                txn.commit();
                value = parseInt(query.getGeneratedKey().toString()); // Id of the newly created profile
                for (String passportName: profile.getPassportsList()) {
                    System.out.println("YEEETETETETE: "+passportName + value);
                    profilePassportCountryRepository.insertProfilePassportCountry(new PassportCountry(0, passportName), value);
                }
                for (String nationalityName: profile.getNationalityList()) {
                    System.out.println("YEEETETETETE 2: "+nationalityName);
                    profileNationalityRepository.insertProfileNationality(new Nationality(0, nationalityName), value);
                }
                for (String travellerTypeName: profile.getTravellerTypesList()) {
                    System.out.println("YEEETETETETE 3: "+travellerTypeName);
                    profileTravellerTypeRepository.insertProfileTravellerType(new TravellerType(0, travellerTypeName), value);
                }
        } catch(Exception e) {
            System.out.println("Search This: "+e);
        } finally {
            txn.end();
        }
            return Optional.of(value);
        }, executionContext);
    }


    /**
     * Update profile in database using Profile model object,
     * and the raw password from an input field, which will be set if it is not null.
     * @param newProfile Profile object with new details
     * @param password String of unhashed password.
     * @return
     */
    public CompletionStage<Optional<Integer>> update(Profile newProfile, String password, int userId) {

        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String updateQuery = "UPDATE profile SET first_name = ?, middle_name = ?, last_name = ?, email = ?, " +
                    "password = ?, birth_date = ?, gender = ?, passports = ?, nationalities = ?, traveller_types = ?, " +
                    "admin = ? WHERE id = ?";
            Optional<Integer> value = Optional.empty();
            try {
                if (ebeanServer.find(Profile.class).setId(userId).findOne() != null) {
                    SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
                    query.setParameter(1, newProfile.getFirstName());
                    query.setParameter(2, newProfile.getMiddleName());
                    query.setParameter(3, newProfile.getLastName());
                    query.setParameter(4, newProfile.getEmail());
                    query.setParameter(5, password);
                    query.setParameter(6, newProfile.getBirthDate());
                    query.setParameter(7, newProfile.getGender());
                    query.setParameter(11, newProfile.isAdmin());
                    query.setParameter(12, userId);
                    query.execute();
                    txn.commit();
                    profileNationalityRepository.removeAll(userId);
                    profilePassportCountryRepository.removeAll(userId);
                    profileTravellerTypeRepository.removeAll(userId);
                    for (String passportName: newProfile.getPassportsList()) {
                        System.out.println("YEEETETETETE: "+passportName + value);
                        profilePassportCountryRepository.insertProfilePassportCountry(new PassportCountry(0, passportName), userId);
                    }
                    for (String nationalityName: newProfile.getNationalityList()) {
                        System.out.println("YEEETETETETE 2: "+nationalityName);
                        profileNationalityRepository.insertProfileNationality(new Nationality(0, nationalityName), userId);
                    }
                    for (String travellerTypeName: newProfile.getTravellerTypesList()) {
                        System.out.println("YEEETETETETE 3: "+travellerTypeName);
                        profileTravellerTypeRepository.insertProfileTravellerType(new TravellerType(0, travellerTypeName), userId);
                    }
                    value = Optional.of(newProfile.getProfileId());
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }


    /**
     * Deletes a profile from the database that matches the given email
     *
     * @param email the users email
     * @return an optional profile
     */
    public CompletionStage<Optional<String>> delete(String email) {
        return supplyAsync(() -> {
            try {
                final Optional<Profile> profileOptional = Optional.ofNullable(ebeanServer.find(Profile.class).setId(email).findOne());
                profileOptional.ifPresent(Model::delete);
                return profileOptional.map(p -> p.getEmail());
            } catch (Exception e) {
                return Optional.empty();
            }
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
     * @param email user email
     * @return destList arrayList of destinations registered by the user
     */
    public Optional<ArrayList<Destination>> getDestinations(String email) {
        String sql = ("select * from destination where user_email = ?");
        List<SqlRow> rowList = ebeanServer.createSqlQuery(sql).setParameter(1, email).findList();
        ArrayList<Destination> destList = new ArrayList<>();
        Destination dest;
        for (SqlRow aRowList : rowList) {
            dest = new Destination();
            dest.setDestinationId(aRowList.getInteger("destination_id"));
            dest.setUserEmail(aRowList.getString("user_email"));
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

}
