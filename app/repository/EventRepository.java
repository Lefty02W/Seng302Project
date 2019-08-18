package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Transaction;
import models.*;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
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
    private final EventTypeRepository eventTypeRepository;
    private final EventArtistRepository eventArtistRepository;
    private final EventGenreRepository eventGenreRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;

    /**
     * Constructor for the events repository class
     */
    @Inject
    public EventRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext,
                           EventTypeRepository eventTypeRepository, EventArtistRepository eventArtistRepository,
                           EventGenreRepository eventGenreRepository, ArtistRepository artistRepository,
                           GenreRepository genreRepository) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.eventTypeRepository = eventTypeRepository;
        this.eventArtistRepository = eventArtistRepository;
        this.eventGenreRepository = eventGenreRepository;
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;

    }

    /**
     * Get all events from the events table in the database
     * uses ebeans to access the database
     * also populates each event object with linking table data
     * @return Optional<List<Events>> Events - Optional list of all events in the database
     */
    public Optional<List<Events>> getAll() {
        List<Events> eventsList = new ArrayList<>();
        List<Events> events = ebeanServer.find(Events.class).findList();
        for (Events event : events){
            eventsList.add(populateEvent(event));
        }
        return Optional.of(eventsList);
    }

    /**
     * Lookup an event using the id to find the object in the database
     * @param eventId Id of the event to find
     * @return event Event object that has been found then populated then returned
     */
    public Events lookup(Integer eventId) {
        return populateEvent(ebeanServer.find(Events.class).where().eq("event_id", eventId).findOne());
    }

    /**
     * Method to populate an event with the objects from the linking tables
     * including Genres, Types and Artists for the event
     * @param event Event object to be populated
     * @return event Event object that has been populated
     */
    private Events populateEvent(Events event) {
        event.setEventGenres(genreRepository.getEventGenres(event.getEventId()));
        event.setEventTypes(eventTypeRepository.getEventTypeOfEvents(event.getEventId()));
        event.setEventArtists(artistRepository.getEventArtists(event.getEventId()));
        return event;
    }

    /**
     * Inserts an Event object into the database
     * Uses ebeans to insert and access the database
     * @param event Event object to be inserted into the database
     * @return CompletionStage<Integer> Holding the inserted events Id
     */
    private CompletionStage<Integer> insertEvent(Events event){
            return supplyAsync(() -> {
                System.out.println("insert");
                try {
                    ebeanServer.insert(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return event.getEventId();
            }, executionContext);
    }

    /**
     * Inserts all linking tables and event for an event object
     * uses ebeans and accesses other repository's to insert the objects into the database
     * @param event Event object that has been fully populated and is ready to insert
     * @return CompletionStage<Integer> Holding the inserted events Id
     */
    public CompletionStage<Integer> insert(Events event) {
        return insertEvent(event).thenApplyAsync(eventId -> {
                    event.setEventId(eventId);
                    saveLinkingTables(event);
                    return eventId;
                });
    }

    /**
     * Method to update a given event and event linking tables
     * @param eventId id of event to be updated
     * @param event Event object storing new changes
     * @return Completion stage integer holding event id
     */
    public CompletionStage<Integer> update(Integer eventId, Events event){
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Events targetEvent = ebeanServer.find(Events.class).setId(eventId).findOne();
            if (targetEvent != null) {
                targetEvent.setAgeRestriction(event.getAgeRestriction());
                targetEvent.setDescription(event.getDescription());
                targetEvent.setDestinationId(event.getDestinationId());
                targetEvent.setEndDate(event.getEndDate());
                targetEvent.setStartDate(event.getStartDate());
                targetEvent.update();
                txn.commit();
                event.setEventId(targetEvent.getEventId());
                removeLinkingTables(event);
                saveLinkingTables(event);
            }
            txn.end();
        return eventId;
    });
    }

    /**
     * helper function to remove links from a given event
     * @param event Event to have links removed
     */
    private void removeLinkingTables(Events event) {
        eventGenreRepository.remove(event.getEventId());
        eventTypeRepository.remove(event.getEventId());
        eventArtistRepository.remove(event.getEventId());
    }

    /**
     * Helper function to save links inside the table so accesses all linking tables when event is stored
     * @param event Event holding forms with updated values
     */
    private void saveLinkingTables(Events event) {
        for (String genreId : event.getGenreForm().split(",")) {
            eventGenreRepository.insert(new EventGenres(event.getEventId(), Integer.parseInt(genreId)));
        }
        for (String type : event.getTypeForm().split(",")) {
            eventTypeRepository.insert(new EventType(event.getEventId(),eventTypeRepository.getTypeOfEventsIdByName(type)));
        }
        for (String artistId : event.getArtistForm().split(",")) {
            eventArtistRepository.insert(new EventArtists(event.getEventId(), Integer.parseInt(artistId)));
        }
    }
}
