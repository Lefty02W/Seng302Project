package repository;

import io.ebean.*;
import models.Destination;
import models.Profile;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A profile repository that executes database operations in a different
 * execution context handles all interactions with the profile table .
 */
public class ProfileRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public ProfileRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
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
        Profile profile = ebeanServer.find(Profile.class).where().like("email", email).findOne();
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
    public CompletionStage<String> insert(Profile profile) {
        return supplyAsync(() -> {
            profile.setTimeCreated(new Date());
            ebeanServer.insert(profile);
            return profile.getEmail();
        }, executionContext);
    }


    /**
     * Update profile in database using Profile model object,
     * and the raw password from an input field, which will be set if it is not null.
     * @param newProfile Profile object with new details
     * @param password String of unhashed password.
     * @return
     */
    public CompletionStage<Optional<String>> update(Profile newProfile, String password) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<String> value = Optional.empty();
            try {
                Profile targetProfile = ebeanServer.find(Profile.class).setId(newProfile.getEmail()).findOne();
                if (targetProfile != null) {
                    targetProfile.setEmail(newProfile.getEmail());

                    if (password.length() != 0) {
                        targetProfile.setPassword(password);
                    }
                    targetProfile.setFirstName(newProfile.getFirstName());
                    targetProfile.setLastName(newProfile.getLastName());
                    targetProfile.setBirthDate(newProfile.getBirthDate());
                    targetProfile.setGender(newProfile.getGender());
                    targetProfile.setPassports(newProfile.getPassports());
                    targetProfile.setNationalities(newProfile.getNationalities());
                    targetProfile.setTravellerTypes(newProfile.getTravellerTypes());
                    targetProfile.update();
                    txn.commit();
                    value = Optional.of(newProfile.getEmail());
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
        for (int i = 0; i < rowList.size(); i++) {
            dest = new Destination();
            dest.setDestinationId(rowList.get(i).getInteger("destination_id"));
            dest.setUserEmail(rowList.get(i).getString("user_email"));
            dest.setName(rowList.get(i).getString("name"));
            dest.setType(rowList.get(i).getString("type"));
            dest.setCountry(rowList.get(i).getString("country"));
            dest.setDistrict(rowList.get(i).getString("district"));
            dest.setLatitude(rowList.get(i).getDouble("latitude"));
            dest.setLongitude(rowList.get(i).getDouble("longitude"));
            destList.add(dest);
        }
        return Optional.of(destList);
    }
}
