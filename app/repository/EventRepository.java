package repository;

import io.ebean.*;
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
        List<Events> events = ebeanServer.find(Events.class).where().eq("soft_delete", 0).findList();
        for (Events event : events){
            eventsList.add(populateEvent(event));
        }
        return Optional.of(eventsList);
    }


    /**
     * Method to get one page of events to display
     *
     * @param offset offset of events to get
     * @return Optional List of events found
     */
    public List<Events> getPage(int offset) {
        List<Events> toReturn = new ArrayList<>();
        List<Events> events = ebeanServer.find(Events.class).setMaxRows(8).setFirstRow(offset).where().eq("soft_delete", 0).findList();
        for (Events event : events) {
            toReturn.add(populateEvent(event));
        }
        return toReturn;
    }

    /**
     * Method to retrieve all events for a given artist
     *
     * @param artistId id of artist
     * @return List of events found
     */
    public List<Events> getArtistEvents(int artistId) {
        List<Integer> ids = ebeanServer.find(EventArtists.class).where().eq("artist_id", artistId).findIds();
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        return ebeanServer.find(Events.class).where().idIn(ids).findList();
    }


    /**
     * Pagination helper method to get the total number of events in teh system
     * @return int of number found
     */
    public int getNumEvents() {
        return ebeanServer.find(Events.class).where().eq("soft_delete", 0).findCount();
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


    /**
     * Private method to form the query string from the given data in the eventForm
     * @param eventFormData eventForm data required to generate the search query
     * @return query string for the events search
     */
    private SqlQuery formSearchQuery(EventFormData eventFormData) {
        String query = "SELECT DISTINCT events.event_id, events.event_name, events.description, events.destination_id, " +
                "events.start_date, events.end_date, events.age_restriction FROM events " +
                "LEFT OUTER JOIN event_genres ON events.event_id = event_genres.event_id " +
                "LEFT OUTER JOIN event_type ON events.event_id = event_type.event_id " +
                "LEFT OUTER JOIN event_artists ON events.event_id = event_artists.event_id ";
        boolean whereAdded = false;
        boolean likeAdded = false;
        List<String> args = new ArrayList<>();
        if (!eventFormData.getEventName().equals("")){
            query += "WHERE events.event_name LIKE ? ";
            likeAdded = true;
            whereAdded = true;
            args.add(eventFormData.getEventName());
        }
        if (!eventFormData.getArtistName().equals("")) {
            if (whereAdded){
                query += "AND event_artists.artist_id = ?";
            } else {
                query += "WHERE event_artists.artist_id = ?";
                whereAdded = true;
            }
            args.add(eventFormData.getArtistName());
        }
        if (!eventFormData.getEventType().equals("")) {
            if (whereAdded){
                query += "AND event_type.type_id = ?";
            } else {
                query += "WHERE event_type.type_id = ?";
                whereAdded = true;
            }
            args.add(eventFormData.getEventType());
        }
        if (!eventFormData.getDestinationId().equals("")) {
            if (whereAdded){
                query += "AND events.destination_id = ?";
            } else {
                query += "WHERE events.destination_id = ?";
                whereAdded = true;
            }
            args.add(eventFormData.getDestinationId());
        }
        if (!eventFormData.getAgeRestriction().equals("")) {
            if(whereAdded){
                query += "AND events.age_restriction = ?";
            } else {
                query += "WHERE events.age_restriction = ?";
                whereAdded = true;
            }
            args.add(eventFormData.getAgeRestriction());
        }
        if (!eventFormData.getGenre().equals("")) {
            if (whereAdded){
                query += "AND event_genres.genre_id = ?";
            } else {
                query += "WHERE event_genres.genre_id = ?";
                whereAdded = true;
            }
            args.add(eventFormData.getGenre());
        }
        if (!eventFormData.getStartDate().equals("")) {
            if (whereAdded){
                query += "AND events.start_date <= ? And events.end_date >= ?";
            } else {
                query += "WHERE events.start_date <= ? And events.end_date >= ?";
            }
            args.add(eventFormData.getStartDate());
            args.add(eventFormData.getStartDate());
        }
        return createSqlQuery(query, args, likeAdded);
    }

    /**
     * method to turn the given string into an SQL query adding a list of parameters as wildcards
     * @param query base string query with wild cards
     * @param args parameters to be added to the wildcards
     * @return SqlQuery for searching events
     */
    private SqlQuery createSqlQuery(String query, List<String> args, Boolean likeAdded){
        SqlQuery sqlQuery = ebeanServer.createSqlQuery(query);
        for (int i=0; i < args.size(); i++){
            if (likeAdded) {
                sqlQuery.setParameter(i + 1, "%" + args.get(i) + "%");
                likeAdded = false;
            } else {
                sqlQuery.setParameter(i + 1, args.get(i));
            }
        }
        return sqlQuery;
    }


    /**
     * Method to search for events in the database
     *
     * @param eventFormData data used in search
     * @return List holding resulting events from search
     */
    public List<Events> searchEvent(EventFormData eventFormData) {
        SqlQuery query = formSearchQuery(eventFormData);
        List<SqlRow> sqlRows = query.findList();
        List<Events> events = new ArrayList<>();
        if (!sqlRows.isEmpty()){
            for (SqlRow foundEvent : sqlRows){
                Events event = populateEvent((new Events(foundEvent.getInteger("event_id"), foundEvent.getString("event_name"),
                        foundEvent.getString("description"), foundEvent.getInteger("destination_id"),
                        foundEvent.getDate("start_date"), foundEvent.getDate("end_date"),
                        foundEvent.getInteger("age_restriction"))));
                events.add(event);
            }
        }
        return events;
    }

    /**
     * Method to get one event
     *
     * @param id id of event to get
     * @return Optional event found
     */
    public CompletionStage<Optional<Events>> getEvent(int id) {
        return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(Events.class).where().eq("event_id", id).findOne()));
    }

    /**
     * Soft deletes an event
     * @param event the event to soft delete
     */
    public void setSoftDelete(Events event, int delete) {
        event.setSoftDelete(delete);
        event.update();
    }


    /**
     * Soft deletes an event
     * @param event the id of the event to soft delete
     */
    void setSoftDeleteId(int event, int delete) {
        Events events = ebeanServer.find(Events.class).where().eq("event_id", event).findOne();
        if (events != null) {
            events.setSoftDelete(delete);
            events.update();
        }
    }
}
