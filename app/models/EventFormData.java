package models;

import static java.lang.Integer.parseInt;

/**
 * Class for getting inputs for searching functionality to find events
 */
public class EventFormData {

    private String eventName = "";
    private String artistName = "";
    private String eventType = "";
    private String destinationId = "";
    private String ageRestriction = "";
    private String genre = "";
    private String startDate = "";
    private String followedArtists = "";
    private String historic = "";
    private String attending = "";

    public String getHistoric() { return historic; }

    public void setAttending(String attending) {this.attending = attending;}

    public void setHistoric(String historic) { this.historic = historic; }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public void setAgeRestriction(String ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFollowedArtists() { return followedArtists; }

    public String getAttending(){return attending;}

    public void setFollowedArtists(String followedArtists) { this.followedArtists = followedArtists; }

    public String getEventName() {
        return eventName;
    }

    public String getArtistName() {
        return artistName;
    }

    public Integer getArtistID() {
        if (!artistName.isEmpty()) {
            return parseInt(artistName);
        } else {
            return 0;
        }
    }

    public String getEventType() {
        return eventType;
    }

    public Integer getEventTypeId() {
        if (!eventType.isEmpty()) {
            return parseInt(eventType);
        } else {
            return 0;
        }
    }

    public String getDestinationId() {
        return destinationId;
    }

    public Integer getIntegerDestinationId() {
        if (!destinationId.isEmpty()) {
            return parseInt(destinationId);
        } else {
            return 0;
        }
    }

    public String getAgeRestriction() {
        return ageRestriction;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getGenreId() {
        if (!genre.isEmpty()) {
            return parseInt(genre);
        } else {
            return 0;
        }
    }

    public String getStartDate() {
        return startDate;
    }
}
