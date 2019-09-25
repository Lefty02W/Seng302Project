package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.ArtistProfilePhoto;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
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
     * Database access method to attach a picture to an artist's profile picture
     *
     * @param artistProfilePhoto - Profile photo object to add to artist
     */
    public CompletionStage<Void> addArtistProfilePicture(ArtistProfilePhoto artistProfilePhoto) {
        if(!checkArtistHasProfilePicture(artistProfilePhoto.getArtistId())) {
            return supplyAsync(() -> {
                ebeanServer.insert(artistProfilePhoto);
                return null;
            }, executionContext);
        } else {
            return supplyAsync(() -> {
                updateArtistProfilePicture(artistProfilePhoto);
                return null;
                }, executionContext);
        }
    }


    /**
     * Check if an artist has a profile picture already
     * @param artistId - ID of the artist to check if they have a profile picture
     */
    private Boolean checkArtistHasProfilePicture(Integer artistId) {
        return ebeanServer.find(ArtistProfilePhoto.class)
                .where()
                .eq("artist_id", artistId)
                .exists();
    }

    /**
     * Database access method to update the profile picture attached to an artist
     *
     * @param newProfilePicture The profile picture object to be set
     * @return the new profilePicture object
     */
    public CompletionStage<ArtistProfilePhoto> updateArtistProfilePicture(ArtistProfilePhoto newProfilePicture) {
        return supplyAsync(() -> {
            ebeanServer.update(ArtistProfilePhoto.class)
                    .set("personal_photo_id", newProfilePicture.getPhotoId())
                    .where()
                    .eq("artist_id", newProfilePicture.getArtistId())
                    .update();
            return newProfilePicture;
        }, executionContext);
    }


    /**
     * Database access method to remove an artists profile picture
     *
     * @param artistId database id of picture to remove
     * @return Void CompletionStage
     */
    public CompletionStage<Integer> removeArtistProfilePicture(int artistId) {
        return supplyAsync(() -> {
            ebeanServer.find(ArtistProfilePhoto.class).where().eq("artist_id", artistId).delete();
            return artistId;
        }, executionContext);
    }


    /**
     * Method to retrieve an ArtistProfilePhoto object from teh database using an artist id
     *
     * @param artistId the artist id
     * @return CompletionStage holding Optional ArtistProfilePhoto object
     */
    public CompletionStage<Optional<ArtistProfilePhoto>> getArtistProfilePicture(int artistId) {
        return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(ArtistProfilePhoto.class)
                .where()
                .eq("artist_id", artistId)
                .findOne()), executionContext);
    }


    /**
     * Lookup an artist profile picture using the id to find the object in the database
     * @param artistId Id of the artist
     * @return artistProfilePhoto ArtistProfilePhoto object that has been found
     */
    public ArtistProfilePhoto lookup(Integer artistId) {
        return ebeanServer.find(ArtistProfilePhoto.class).where().eq("artist_id", artistId).findOne();
    }

}
