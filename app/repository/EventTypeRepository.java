package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.EventType;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EventTypeRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EventTypeRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext){
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * EventType Create
     * Inserts an EventType into the ebean database server
     * @param eventType EventType object that will be inserted into the database
     * @return Integer: eventType id
     */
    public CompletionStage<Integer> insertEventType(EventType eventType){
        return supplyAsync(() -> {
            ebeanServer.insert(eventType);
            return eventType.getEventId();
        }, executionContext);
    }

    /**
     * EventType Read
     * Method to retrieve an EventType object from the database using a past event id
     * @param eventId id of the event that will be retrieved
     * @return EventType object that was retrieved from the database
     */
    public EventType getEventType(int eventId){
        return ebeanServer.find(EventType.class).where().eq("event_id", eventId).findOne();
    }

    /**
     * EventType Update
     * Method to update an event type id to the new given typeId
     * @param eventId id of the EventType object that will be updated
     * @param newTypeId Type id the event type will be changed too
     * @return id of the event
     */
    public CompletionStage<Integer> updateEventType(int eventId, int newTypeId){
        return supplyAsync(() -> {
            ebeanServer.update(EventType.class).set("type_id", newTypeId)
                    .where()
                    .eq("event_id", Integer.toString(eventId))
                    .update();
            return eventId;
        }, executionContext);
    }

    /**
     * EventType Delete
     * Method to delete an EventType object from the database by a given id
     * @param eventId int: id of the eventType that will be removed
     * @return null
     */
    public CompletionStage<Void> removeEventType(int eventId){
        return supplyAsync(() -> {
            ebeanServer.find(EventType.class)
                    .where()
                    .eq("event_id", eventId)
                    .delete();
            return null;
        });
    }
}
