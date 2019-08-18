package models;

import play.data.validation.Constraints;

import javax.persistence.Entity;

@Entity
public class EventArtists {

    private int eventId;

    @Constraints.Required
    private int artistId;

    /**
     * Traditional constructor for EventArtists. Used when retrieving the link between an event and its genre from the
     * database
     * @param eventId id of the linked event
     * @param artistId id of the artist that is linked to the given event
     */
    public EventArtists(int eventId, int artistId){
        this.eventId = eventId;
        this.artistId = artistId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getArtistId() { return artistId;
    }
}
