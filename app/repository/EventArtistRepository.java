package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.EventArtists;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EventArtistRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public EventArtistRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext){
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * EventArtists create
     * Inserts an EventArtists into the ebean database server
     * @param eventArtists EventArtists object that will be inserted into the database
     * @return Integer: eventArtists id
     */
    public CompletionStage<Integer> insert(EventArtists eventArtists){
        return supplyAsync(() -> {
            ebeanServer.insert(eventArtists);
            return eventArtists.getEventId();
        }, executionContext);
    }

    /**
     * EventArtists Read
     * Method to retrieve an EventArtists object from the database using a past event id
     * @param eventId id of the event that will be retrieved
     * @return EventArtists object that was retrieved from the database
     */
    public EventArtists getEventArtist(int eventId){
        return ebeanServer.find(EventArtists.class).where().eq("event_id", eventId).findOne();
    }


    /**
     * EventArtists Read to return a list
     * Method to retrieve an EventArtists object from the database using a past event id
     * @param eventId id of the event that will be retrieved
     * @return EventArtists List object that was retrieved from the database
     */
    public List<Integer> getEventArtistList(int eventId){
        return ebeanServer.find(EventArtists.class).select("artistId").where().eq("event_id", eventId).findSingleAttributeList();
    }


    /**
     * EventArtists Delete
     * Method to delete an EventArtists object from the database by a given id
     * @param eventId int: id of the eventArtist that will be removed
     * @return null
     */
    public CompletionStage<Void> remove(int eventId){
        return supplyAsync(() -> {
            ebeanServer.find(EventArtists.class)
                    .where()
                    .eq("event_id", eventId)
                    .delete();
            return null;
        });
    }


}
