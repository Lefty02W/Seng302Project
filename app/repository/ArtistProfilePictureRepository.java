package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.ArtistProfilePhoto;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Database access method used to manipulate the artist_profile_photo table
 */
public class ArtistProfilePictureRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    /**
     * Ebeans injector constructor method for Artist repository.
     *
     * @param ebeanConfig The ebeans config which the ebean server will be supplied from
     * @param executionContext the database execution context object for this instance.
     */
    @Inject
    public ArtistProfilePictureRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }


    /**
     * Database access method to update the profile picture attached to an artist
     *
     * @param newProfilePicture The profile picture object to be set
     * @return the new profilePicture object
     */
    public CompletionStage<ArtistProfilePhoto> updateArtistProfilePicture(ArtistProfilePhoto newProfilePicture) {
        return supplyAsync(() -> {
            ebeanServer.update(ArtistProfilePhoto.class).set("personal_photo_id", newProfilePicture.getPersonalPhotoId()).where().eq("artist_id", newProfilePicture.getArtistId()).update();
            return newProfilePicture;
        }, executionContext);
    }


    /**
     * Database access method to remove an artists profile picture
     *
     * @param artistId database id of picture to remove
     * @return Void CompletionStage
     */
    public CompletionStage<Void> removeArtistProfilePicture(int artistId) {
        return supplyAsync(() -> {
            ebeanServer.find(ArtistProfilePhoto.class).where().eq("artist_id", artistId).delete();
            return null;
        }, executionContext);
    }

}
