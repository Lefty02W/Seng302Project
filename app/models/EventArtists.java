package models;

import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EventArtists {

    @Id
    private int eventId;

    @Constraints.Required
    private int artistId;

    /**
     * Traditional constructor for EventArtists. Used when retrieving the link between an event and its genre from the
     * database
     * @param eventId id of the linked event
     * @param genreId id of the genre that is linked to the given event
     */
    public EventArtists(int eventId, int artistId){
        this.eventId = eventId;
        this.artistId = artistId;
    }

    public int getEventId() {
        return eventId;
    }
}
