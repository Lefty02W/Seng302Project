package repository;

import io.ebean.*;
import models.*;
import org.mindrot.jbcrypt.BCrypt;
import play.db.ebean.EbeanConfig;
import scala.xml.Null;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
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
    private final RolesRepository rolesRepository;

    @Inject
    public ProfileRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.profilePassportCountryRepository = new ProfilePassportCountryRepository(ebeanConfig, executionContext);
        this.profileNationalityRepository = new ProfileNationalityRepository(ebeanConfig, executionContext);
        this.profileTravellerTypeRepository = new ProfileTravellerTypeRepository(ebeanConfig, executionContext);
        this.rolesRepository = new RolesRepository(ebeanConfig, executionContext);
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
     *
     * @param email    users email input
     * @param password users password input
     * @return
     */
    public boolean validate(String email, String password) {
        String selectQuery = "SELECT * FROM profile WHERE email = ?";// and password = ?";
        List<SqlRow> rows = ebeanServer.createSqlQuery(selectQuery)
                .setParameter(1, email)
                .findList();
        for (SqlRow row: rows) {
            if (BCrypt.checkpw(password, row.getString("password"))) {
                return true;
            }
        }
        return false;
//        return row != null;
    }


    /**
     * Method to get all profiles, their roles will also be filled.
     *
     * @return List of all profiles
     */
    public List<Profile> getAll() {
        String selectQuery = "SELECT * FROM profile WHERE soft_delete = 0;";

        List<SqlRow> rows = ebeanServer.createSqlQuery(selectQuery).findList();
        List<Profile> allProfiles = new ArrayList<>();

        for (SqlRow row : rows) {

            allProfiles.add(profileFromRow(row));

        }

        return allProfiles;

    }


    /**
     * Method for getting a profile
     *
     * @param email String of the email to get
     * @return Profile class of the user
     */
    public Profile getProfileById(String email) {
        Profile profile = ebeanServer.find(Profile.class).where().like("email", email).findOne();

        profile.setNationalities(profileNationalityRepository.getList(profile.getProfileId()).get());
        profile.setPassports(profilePassportCountryRepository.getList(profile.getProfileId()).get());
        profile.setTravellerTypes(profileTravellerTypeRepository.getList(profile.getProfileId()).get());
        profile.setRoles(rolesRepository.getProfileRoles(profile.getProfileId()).get());

        return profile;
    }

    /**
     * Method for getting a profile that is not soft deleted
     *
     * @param userId String of the email to get
     * @return Profile class of the user
     */
    public Profile getExistingProfileByProfileId(Integer userId) {
        return ebeanServer.find(Profile.class)
                .where().eq("soft_delete", "0").and()
                .like("profile_id", userId.toString()).findOne();
    }

    /**
     * Method for getting a profile
     *
     * @param userId String of the email to get
     * @return Profile class of the user
     */
    public Profile getProfileByProfileId(Integer userId) {
        return ebeanServer.find(Profile.class).setId(userId).findOne();
    }


    /**
     * Database access method to query the database for profiles that match the given search parameters
     *
     * @param travellerType Traveller type to search for
     * @param lowerAge Lower limit for age of profile
     * @param upperAge Upper limit for age of profile
     * @param gender Gender of profile
     * @param nationality nationality of profile
     * @return List of profiles that match query parameters
     */
    public List<Profile> searchProfiles(String travellerType, Date lowerAge, Date upperAge, String gender, String nationality) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String queryString = "SELECT profile_traveller_type.profile FROM profile_traveller_type " +
                "JOIN profile_nationality ON profile_nationality.profile = profile_traveller_type.profile " +
                "JOIN nationality ON profile_nationality.nationality = nationality_id " +
                "JOIN traveller_type ON profile_traveller_type.traveller_type = traveller_type_id";
        boolean whereAdded = false;
        if (!travellerType.equals("")) {
            queryString += " WHERE traveller_type_name = ?";
            whereAdded = true;
        }
        if (!nationality.equals("")) {
            if (!whereAdded) {
                queryString += " WHERE nationality_name = ?";
            } else {
                queryString += " AND nationality_name = ?";
            }

        }
        SqlQuery query = ebeanServer.createSqlQuery(queryString);

        if (!travellerType.equals("")) {
            query.setParameter(1, travellerType);
        }
        if (!nationality.equals("")) {
            if (!whereAdded) {
                query.setParameter(1, nationality);
            } else {
                query.setParameter(2, nationality);
            }
        }
        List<SqlRow> foundRows = query.findList();
        List<Integer> foundIds = new ArrayList<>();
        List<Profile> foundProfiles = new ArrayList<>();
        if (!foundRows.isEmpty()) {
            for (SqlRow row : foundRows) {
                foundIds.add(row.getInteger("profile"));
            }
            foundProfiles = ebeanServer.find(Profile.class).where()
                    .idIn(foundIds)
                    .contains("gender", gender)
                    .gt("birth_date", dateFormat.format(upperAge))
                    .lt("birth_date", dateFormat.format(lowerAge))
                    .findList();
            for (Profile profile : foundProfiles) {
                Optional<Map<Integer, PassportCountry>> optionalIntegerPassportCountryMap = profilePassportCountryRepository.getList(profile.getProfileId());
                optionalIntegerPassportCountryMap.ifPresent(profile::setPassports);
                Optional<Map<Integer, Nationality>> optionalNationalityMap = profileNationalityRepository.getList(profile.getProfileId());
                optionalNationalityMap.ifPresent(profile::setNationalities);
                Optional<Map<Integer, TravellerType>> optionalTravellerTypeMap = profileTravellerTypeRepository.getList(profile.getProfileId());
                optionalTravellerTypeMap.ifPresent(profile::setTravellerTypes);

            }
        }

        return foundProfiles;
    }


    /**
     * Create a profile instance from data of an SQL Row result
     *
     * @param row The SQL query result as a row
     * @return A profile made based on data from row
     */
    private Profile profileFromRow(SqlRow row) {
        Integer profileId = row.getInteger("profile_id");
        Map<Integer, PassportCountry> passportCountries = new HashMap<>();
        Map<Integer, Nationality> nationalities = new HashMap<>();
        Map<Integer, TravellerType> travellerTypes = new HashMap<>();
        List<String> roles = new ArrayList<>();
        Optional<Map<Integer, PassportCountry>> optionalIntegerPassportCountryMap = profilePassportCountryRepository.getList(profileId);
        if (optionalIntegerPassportCountryMap.isPresent()) {
            passportCountries = optionalIntegerPassportCountryMap.get();
        }
        Optional<Map<Integer, Nationality>> optionalNationalityMap = profileNationalityRepository.getList(profileId);
        if (optionalNationalityMap.isPresent()) {
            nationalities = optionalNationalityMap.get();
        }
        Optional<Map<Integer, TravellerType>> optionalTravellerTypeMap = profileTravellerTypeRepository.getList(profileId);
        if (optionalTravellerTypeMap.isPresent()) {
            travellerTypes = optionalTravellerTypeMap.get();
        }
        Optional<List<String>> optionalRoles = rolesRepository.getProfileRoles(profileId);
        if (optionalRoles.isPresent()) {
            roles = optionalRoles.get();
        }
        return new Profile(profileId, row.getString("first_name"),
                row.getString("middle_name"), row.getString("last_name"), row.getString("email"),
                row.getDate("birth_date"), passportCountries, row.getString("gender"),
                row.getDate("time_created"), nationalities, travellerTypes, roles);
    }

    /**
     * This method finds a profile in the database using a given profile id
     *
     * @param profileId the id of the profile to find
     * @return CompletionStage holding an optional of the profile found
     */
    public CompletionStage<Optional<Profile>> findById(int profileId) {
        return supplyAsync(() -> {
            Profile profile = ebeanServer.find(Profile.class).setId(profileId).findOne();
            profile.setNationalities(profileNationalityRepository.getList(profile.getProfileId()).get());
            profile.setPassports(profilePassportCountryRepository.getList(profile.getProfileId()).get());
            profile.setTravellerTypes(profileTravellerTypeRepository.getList(profile.getProfileId()).get());
            profile.setRoles(rolesRepository.getProfileRoles(profile.getProfileId()).get());
            return Optional.of(profile);
        });
    }

    /**
     * Finds one profile using its email as a query
     *
     * @param email the users email
     * @return a Profile object that matches the email
     */
    public CompletionStage<Optional<Profile>> lookupEmail(String email) {
        return supplyAsync(() -> {
            String qry = "Select * from profile where email = ? and soft_delete = 0";
            List<SqlRow> rowList = ebeanServer.createSqlQuery(qry).setParameter(1, email).findList();
            Profile profile = null;
            if (!rowList.isEmpty() && !rowList.get(0).isEmpty()) {
                profile = profileFromRow(rowList.get(0));
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
            String qry = "INSERT INTO profile (first_name, middle_name, last_name, email, " +
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
                for (String passportName : profile.getPassportsList()) {
                    profilePassportCountryRepository.insertProfilePassportCountry(new PassportCountry(passportName), value);
                }
                for (String nationalityName : profile.getNationalityList()) {
                    profileNationalityRepository.insertProfileNationality(new Nationality(nationalityName), value);
                }
                for (String travellerTypeName : profile.getTravellerTypesList()) {
                    profileTravellerTypeRepository.insertProfileTravellerType(new TravellerType(travellerTypeName), value);
                }
            } catch (Exception e) {
                System.err.println("Search This: " + e);
            } finally {
                txn.end();
            }
            return Optional.ofNullable(value);
        }, executionContext);
    }


    /**
     * Update profile in database using Profile model object,
     * and the raw password from an input field, which will be set if it is not null.
     *
     * @param newProfile Profile object with new details
     * @return
     */
    public CompletionStage<Optional<Integer>> update(Profile newProfile, int userId) {

        if (isEmailTaken(newProfile.getEmail(), userId)) {
            throw new IllegalArgumentException("Email is already taken");
        }
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
     * @return an optional profile id
     */
    public CompletionStage<Optional<Integer>> delete(Integer profileId) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String deleteQuery = "DELETE FROM profile Where profile_id = ?";
            SqlUpdate query = Ebean.createSqlUpdate(deleteQuery);
            query.setParameter(1, profileId);
            query.execute();
            txn.commit();
            return Optional.of(0);
        }, executionContext);
    }


    /**
     * sets soft delete for a profile which eather deletes it or
     * undoes the delete
     * @param profileId The ID of the profile to soft delete
     * @param value, the value softDelete is to be set to
     * @return
     */
    public CompletionStage<Integer> setSoftDelete(int profileId, int value) {
        return supplyAsync(() -> {
            try {
                Profile targetProfile = ebeanServer.find(Profile.class).setId(profileId).findOne();
                if (targetProfile != null) {
                    targetProfile.setSetSoftDelete(value);
                    targetProfile.update();
                    return 1;
                } else {
                    return 0;
                }
            } catch(Exception e) {
                return 0;
            }
        }, executionContext);
    }


    /**
     * Used to update (add or remove) admin privilege to another user from the Travellers page.
     *
     * @param clickedId the id of the user that is going to have admin privilege updated.
     * @return The email member who had their admin updated.
     */
    public CompletionStage<Optional<Integer>> updateAdminPrivelege(Integer clickedId) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Integer> value = Optional.empty();
            try {
                Profile targetProfile = ebeanServer.find(Profile.class).setId(clickedId).findOne();
                if (targetProfile != null) {

                    List<String> roles = targetProfile.getRoles();

                    if (targetProfile.hasRole("admin")) {

                        roles.remove("admin");
                    } else {

                        roles.add("admin");

                    }

                    targetProfile.setRoles(roles);

                    targetProfile.update();
                    txn.commit();
                    value = Optional.of(clickedId);
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }

    /**
     * returns a list of all users Id's
     * @return a list of integers of the profile Ids of all the users
     */
    public CompletionStage<Optional<List<Integer>>> getAllUsersId() {
        return supplyAsync(() -> {
            String qry = "Select profile_id from profile";
            List<SqlRow> rowList = ebeanServer.createSqlQuery(qry).findList();
            List<Integer> profileIdList = new ArrayList<>();
            for (SqlRow aRowList : rowList) {
                profileIdList.add(aRowList.getInteger("profile_id"));
            }
            return Optional.of(profileIdList);
        }, executionContext);
    }


    /**
     * Function to get all the destinations created by the signed in user.
     *
     * @param profileId users profile Id
     * @return destList arrayList of destinations registered by the user
     */
    public Optional<ArrayList<Destination>> getDestinations(int profileId, int rowOffset) {
        String sql = ("SELECT * FROM destination WHERE profile_id = ? AND soft_delete = 0 LIMIT 7 OFFSET ?");
        List<SqlRow> rowList = ebeanServer.createSqlQuery(sql).setParameter(1, profileId).setParameter(2, rowOffset).findList();
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
            if ((aRowList.getBoolean("soft_delete") ? 1: 0) == 0) {
                destList.add(dest);
            }
        }
        return Optional.of(destList);
    }


    /**
     * Finds the number of profiles in the database
     * Used for pagination purposes
     * @return int of number found
     */
    public int getNumProfiles() {
        return ebeanServer.find(Profile.class).where().eq("soft_delete", 0).findCount();
    }

    /**
     * Method for edit profile-email to check if there is a traveller account under the supplied email that already
     * exists (not the same user)
     *
     * @param email String The new email the user has proposed to change to
     * @param profileId int of the porfileid of the user
     * @return boolean true if email is taken, false if it is a change that will be allowed
     */
    private boolean isEmailTaken(String email, int profileId) {
        boolean isTaken = false;
        String selectQuery = "Select * from profile WHERE email = ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(selectQuery).setParameter(1, email).findList();
        for (SqlRow aRow : rowList) {
            if (aRow.getInteger("profile_id") != profileId) {
                isTaken = true;
            }
        }
        return isTaken;
    }


    /**
     * Gets the number of admins
     * @return int number of admins
     */
    public int getNumAdmins() {
        return ebeanServer.find(ProfileRoles.class).setDistinct(true).findCount();
    }

    /**
     * Method to get a page of profiles to display
     *
     * @param offset offset for profiles to find
     * @param limit number of profiles to find
     * @return List of found profiles
     */
    public List<Profile> getPage(Integer offset, Integer limit) {
        String selectQuery = "SELECT * FROM profile WHERE soft_delete = 0 LIMIT ? OFFSET ?;";
        List<SqlRow> rows = ebeanServer.createSqlQuery(selectQuery).setParameter(1, limit).setParameter(2, offset).findList();
        List<Profile> profiles = new ArrayList<>();
        for (SqlRow row : rows) {
            profiles.add(profileFromRow(row));
        }
        return profiles;
    }
}
