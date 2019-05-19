package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * This class holds the data for a profile destination
 */
@Entity
public class Destination extends Model {

    @Id
    @Constraints.Required
    private int destinationId;
    private String userEmail;
    @Constraints.Required
    private String name;
    @Constraints.Required
    private String type;
    @Constraints.Required
    private String country;

    private String district;

    private double latitude;

    private double longitude;
    @Constraints.Required
    private int visible;

    /**
     * This constructor is used by scala variables
     */
    public Destination() {

    }

    /**
     * Creating a destination
     * @param destinationId the destinations db id
     * @param userEmail the email of the destination owner
     * @param name the destination name
     * @param type the destination type
     * @param country the destinations country
     * @param district the destinations district
     * @param latitude the destinations latitude
     * @param longitude the destinations longitude
     */
    public Destination(int destinationId, String userEmail, String name, String type, String country, String district, double latitude, double longitude, int visible) {
        this.destinationId = destinationId;
        this.userEmail = userEmail;
        this.name = name;
        this.type = type;
        this.country = country;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visible = visible;
    }

    /**
     * Creating a destination
     * @param userEmail the email of the destination owner
     * @param name the destination name
     * @param type the destination type
     * @param country the destinations country
     * @param district the destinations district
     * @param latitude the destinations latitude
     * @param longitude the destinations longitude
     */
    public Destination(String userEmail, String name, String type, String country, String district, double latitude, double longitude, int visible) {
        this.userEmail = userEmail;
        this.name = name;
        this.type = type;
        this.country = country;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visible = visible;
    }

    // Finder for destination
    public static final Finder<String, Destination> find = new Finder<>(Destination.class);

    /**
     * Returns the destination ID
     * @return
     */
    public int getDestinationId() {
        return destinationId;
    }

    /**
     * Sets the destinationId
     * @param destinationId the destinations db id
     */
    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }


    /**
     * Returns the name of the destination
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the destination
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the type of the destination
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the destination
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the country of the destination
     * @return
     */
    public String getCountry() {
        return country;
    }

    /**
     * Returns the user email of the destination
     * @return
     */
    public String getUserEmail() { return userEmail; }

    /**
     * Sets the country of the destination
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Returns the district of the destination
     * @return
     */
    public String getDistrict() {
        return district;
    }

    /**
     * Sets the district of the destination
     * @param district
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * Returns the latitude of the destination
     * @return
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the destination
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the longitude of the destination
     * @return
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the destination
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Sets the userEmail of the destination
     * @param userEmail
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getVisible() { return visible; }

    public void setVisible(int visible) { this.visible = visible; }
}
