package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.EventGenres;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EventGenreRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EventGenreRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext){
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * EventGenres Create
     * Inserts an EventGenres into the ebean database server
     * @param eventGenre EventGenres object that will be inserted into the database
     * @return Integer: eventGenre id
     */
    public CompletionStage<Integer> insert(EventGenres eventGenre){
        return supplyAsync(() -> {
            ebeanServer.insert(eventGenre);
            return eventGenre.getEventId();
        }, executionContext);
    }

    /**
     * EventGenres Read
     * Method to retrieve an EventGenres object from the database using a past event id
     * @param eventId id of the event that will be retrieved
     * @return EventGenres object that was retrieved from the database
     */
    public EventGenres getEventGenre(int eventId){
        return ebeanServer.find(EventGenres.class).where().eq("event_id", eventId).findOne();
    }


    /**
     * EventGenres Delete
     * Method to delete an EventGenres object from the database by a given id
     * @param eventId int: id of the eventGenre that will be removed
     * @return null
     */
    public CompletionStage<Void> remove(int eventId){
        return supplyAsync(() -> {
            ebeanServer.find(EventGenres.class)
                    .where()
                    .eq("event_id", eventId)
                    .delete();
            return null;
        });
    }

}
