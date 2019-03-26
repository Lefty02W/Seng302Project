package repository;

import io.ebean.*;
import models.Destination;
import models.Profile;
import models.Trip;
import play.db.ebean.EbeanConfig;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
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
        System.out.println(existingEmail);
        System.out.println(ebeanServer.find(Profile.class).findList());
        if (existingEmail == null) {
            return false;
        } else {
            return true;
        }
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
        if (profile.getEmail().equals(email) && profile.getPassword().equals(password)) {
            return true;
        } else {
            return false;
        }
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
                    //TODO get actual trips out of the database
                    targetProfile.setTrips(new ArrayList<Trip>());

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
     * Deletes a single destination that belongs to a particular user (email)
     *
     * @param email users email
     * @param destID the unique id of the desired destination to be deleted
     * @return an optional object
     */
    public CompletionStage<Optional<String>> deleteDestination(String email, int destID) {
        return supplyAsync(() -> {
            try {
                final Optional<Profile> profileOptional = Optional.ofNullable(ebeanServer.find(Profile.class)
                        .setId(email).findOne());
                final Optional<Destination> destOptional = Optional.ofNullable(ebeanServer.find(Destination.class)
                        .setId(destID).findOne());
                Profile profile = profileOptional.get();
                profile.deleteDestination(destID);
                profile.update();
                destOptional.ifPresent(Model::delete);
                return Optional.of("Successfully deleted destination");
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

}
