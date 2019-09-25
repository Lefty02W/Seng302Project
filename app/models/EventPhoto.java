package models;
import io.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Model class to hold link between and event and an event photo
 */
@Entity
public class EventPhoto extends Model {

    @Id
    private int eventId;
    private int photoId;

    public EventPhoto(int eventId, int photoId) {
        this.eventId = eventId;
        this.photoId = photoId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }
}
