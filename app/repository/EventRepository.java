package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Artist;
import models.Events;
import models.MusicGenre;
import play.db.ebean.EbeanConfig;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Event Repository that handles all interactions with the event table in the database
 */
public class EventRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    /**
     * Constructor for the events repository class
     */
    public EventRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Get all events from the events table in the database
     * uses ebeans to access the database
     * @return Optional<List<Events>> Events - Optional list of all events in the database
     */
    public Optional<List<Events>> getAll() {
        // TODO Needs to be populated by link tables
        return Optional.of(ebeanServer.find(Events.class).findList());
    }

    /**
     * Inserts an Event object into the database
     * Uses ebeans to insert and access the database
     * @param event Event object to be inserted into the database
     * @return CompletionStage<Integer> Holding the inserted events Id
     */
    private CompletionStage<Integer> insertEvent(Events event){
            return supplyAsync(() -> {
                ebeanServer.insert(event);
                return event.getEventId();
            }, executionContext);
    }

    /**
     * Inserts all linking tables and event for an event objects
     * uses ebeans and accesses other repository's to insert the objects into the database
     * @param event Event object that has been fully populated and is ready to insert
     * @return CompletionStage<Integer> Holding the inserted events Id
     */
    public CompletionStage<Integer> insert(Events event) {
        CompletionStage<Integer> event_id_out = insertEvent(event);
        for (MusicGenre genre : event.getEventGenres()) {
            //eventGenreRepository.insert(genre, event.getEventId())
        }
        for (String type : event.getEventTypes()) {
            //eventTypeRepository.insert(type, event.getEventId())
        }
        for (Artist artist : event.getEventArtists()) {
            //artistEventRepository.insert(artist, event.getEventId())
        }
        return event_id_out;
    }
    }
