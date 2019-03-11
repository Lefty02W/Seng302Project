package repository;

import io.ebean.*;
import models.Destination;
import models.Profile;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
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


    public CompletionStage<Optional<Profile>> lookup(String email) {
        return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(Profile.class).setId(email).findOne()), executionContext);
    }



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
                    targetProfile.setNationality(newProfile.getNationality());
                    targetProfile.setGroupie(newProfile.getGroupie());
                    targetProfile.setThrillseeker((newProfile.getThrillseeker()));
                    targetProfile.setGapYear(newProfile.getGapYear());
                    targetProfile.setWeekender(newProfile.getWeekender());
                    targetProfile.setHolidaymaker(newProfile.getHolidaymaker());
                    targetProfile.setBusiness(newProfile.getBusiness());
                    targetProfile.setBackpacker(newProfile.getBackpacker());

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
