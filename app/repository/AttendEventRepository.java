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
            ebeanServer.find(AttendEvent.class).where()
                    .eq("attend_event_id", Integer.toString(attendEventId))
                    .delete();
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
        System.out.println(eventId);
        return ebeanServer.find(AttendEvent.class).select("profileId").where().eq("event_id", eventId)
                .findSingleAttributeList();
    }


    /**
     * Method to get the ids of all the events a user is attending
     *
     * @param profileId id of profile
     * @return List of event ids
     */
    public List<Integer> getAttendingEvents(int profileId) {
        return ebeanServer.find(AttendEvent.class).select("eventId").where().eq("profile_id", profileId)
                .findSingleAttributeList();

    }

    /**
     * Gets the attend event linking table ID.
     * @param eventId Event id
     * @param currentUserId Currently logged-in user
     * @return attendEventId Integer - Id of the attended event
     */
    public int getAttendEventId(Integer eventId, Integer currentUserId) {
        System.out.println(eventId);
        System.out.println(currentUserId);
        return ebeanServer.find(AttendEvent.class).select("attendEventId").where().eq("event_id", eventId)
                .eq("profile_id", currentUserId).findOne().getAttendEventId();
    }
}
