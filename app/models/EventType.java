package models;

import play.data.validation.Constraints;

import javax.persistence.Entity;

@Entity
public class EventType {

    private int eventId;

    @Constraints.Required
    private int typeId;

    /**
     * Traditional constructor for EventType. Used when retrieving the link between an event and its type from the
     * database
     * @param eventId id of the linked event
     * @param typeId id of the type that is linked to the given event
     */
    public EventType(int eventId, int typeId){
        this.eventId = eventId;
        this.typeId = typeId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
