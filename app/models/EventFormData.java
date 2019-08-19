package models;

/**
 * Class for getting inputs for searching functionality to find events
 */
public class EventFormData {

    private String eventName = "";
    private String artistName = "";
    private String eventType = "";
    private String country = "";
    private String ageRestriction = "";
    private String genre = "";
    private String startDate = "";

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getEventName() {
        return eventName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getEventType() {
        return eventType;
    }

    public String getCountry() {
        return country;
    }

    public String getAgeRestriction() {
        return ageRestriction;
    }

    public String getGenre() {
        return genre;
    }

    public String getStartDate() {
        return startDate;
    }
}
