package repository;


import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.AttendEvent;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;


public class AttendEventRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;


    @Inject
    public AttendEventRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }


    /**
     * Inserts attend event object into the database
     * @param attendEvent AttendEvent object to be inserted
     * @return completionStage<Integer> the id of the AttendEvent inserted
     */
    public CompletionStage<Integer> insert(AttendEvent attendEvent) {
        return supplyAsync(() -> {
            ebeanServer.insert(attendEvent);
            return attendEvent.getAttendEventId();
        }, executionContext);
    }


    /**
     * Deletes the AttendEvent row from the database with the id given
     * @param attendEventId the id of the event to be deleted
     * @return completionStage<null>
     */
    public CompletionStage<Void> delete(int attendEventId) {
        return supplyAsync(() -> {
            ebeanServer.find(AttendEvent.class).where().eq("attend_event_id", Integer.toString(attendEventId)).delete();
            return null;
        });
    }


    /**
     * Method to get the profile ids of all the profiles attending an event
     *
     * @param eventId id of event
     * @return List if ids found
     */
    public List<Integer> getAttendingUsers(int eventId) {
        return ebeanServer.find(AttendEvent.class).select("profileId").where().eq("event_id", eventId).findSingleAttributeList();
    }
}
