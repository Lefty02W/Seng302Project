package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Transaction;
import models.EventPhoto;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Repository class holding functionality used to manipulate an event photo
 */
public class EventPhotoRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;


    @Inject
    public EventPhotoRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }


    /**
     * Database access method to remove the link between and event and a photo
     *
     * @param eventId the event id
     * @return CompletionStage
     */
    public CompletionStage<Integer> removeEventCoverPhoto(int eventId) {
        return supplyAsync(() -> {
           ebeanServer.find(EventPhoto.class).where().eq("event_id", eventId).delete();
           return eventId;
        });
    }


    /**
     * Database access method to get the personal_photo_id of the events cover photo
     *
     * @param eventId id of event to get photo id for
     * @return Optional of photo id found
     */
    public Optional<Integer> getEventPhotoId(int eventId) {
        return Optional.ofNullable(ebeanServer.find(EventPhoto.class).select("photoId").where().eq("event_id", eventId).findSingleAttribute());
    }


    /**
     * Database access method to update the photo link for an event cover photo
     * @param eventId id of the event to update the photo
     * @param photoId id of the photo to be added to the event
     * @return CompletionStage
     */

    public CompletionStage<Integer> update(int eventId, int photoId){
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            EventPhoto targetPhoto = ebeanServer.find(EventPhoto.class).setId(eventId).findOne();
            if (targetPhoto != null) {
                targetPhoto.setPhotoId(photoId);
                targetPhoto.update();
                txn.commit();
            }
            txn.end();

            return eventId;
        });
    }

}
