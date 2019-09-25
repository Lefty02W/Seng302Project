package models;

import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.*;

/**
 * Model class to hold profiles attending events
 */
@Entity
public class AttendEvent extends Model {

    @Id
    private Integer attendEventId;

    @Constraints.Required
    private Integer eventId;

    @Constraints.Required
    private Integer profileId;

    /**
     * Constructor for attendEvent
     * @param eventId
     * @param profileId
     */
    public AttendEvent(Integer eventId, Integer profileId) {
        this.eventId = eventId;
        this.profileId = profileId;
    }

    /**
     * Constructor for attendEvent
     * @param attendEventId
     * @param eventId
     * @param profileId
     */
    public AttendEvent(Integer attendEventId, Integer eventId, Integer profileId) {
        this.attendEventId = attendEventId;
        this.eventId = eventId;
        this.profileId = profileId;
    }

    public void setEventId(int eventId) {this.eventId = eventId;}

    public int getEventId() { return eventId; }

    public void setProfileId(int profileId) {this.profileId = profileId;}

    public int getProfileId() { return profileId; }

    public void setAttendEventId(int attendEventId) {this.attendEventId = attendEventId;}

    public int getAttendEventId() { return attendEventId; }

}