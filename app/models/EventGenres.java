package models;

import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EventGenres {

    @Id
    private int eventId;

    @Constraints.Required
    private int genreId;

    /**
     * Traditional constructor for EventGenres. Used when retrieving the link between an event and its Genre from the
     * database
     * @param eventId id of the linked event
     * @param genreId id of the type that is linked to the given event
     */
    public EventGenres(int eventId, int genreId){
        this.eventId = eventId;
        this.genreId = genreId;
    }

    public int getEventId() {
        return eventId;
    }

}
