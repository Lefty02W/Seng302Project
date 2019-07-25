package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the data for a profile destination
 */
@Entity
public class Destination extends Model {

    @Id
    @Constraints.Required
    private int destinationId;
    private int profileId;
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

    private int softDelete;

    @Transient
    private List<Photo> usersPhotos = new ArrayList<>();

    @Transient
    private List<Photo> worldPhotos = new ArrayList<>();

    /**
     * This constructor is used by scala variables
     */
    public Destination() {

    }

    /**
     * Creating a destination
     * @param destinationId the destinations db id
     * @param name the destination name
     * @param type the destination type
     * @param country the destinations country
     * @param district the destinations district
     * @param latitude the destinations latitude
     * @param longitude the destinations longitude
     */
    public Destination(int destinationId, int profileId, String name, String type, String country, String district, double latitude, double longitude, int visible) {
        this.destinationId = destinationId;
        this.profileId = profileId;
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
     * @param name the destination name
     * @param type the destination type
     * @param country the destinations country
     * @param district the destinations district
     * @param latitude the destinations latitude
     * @param longitude the destinations longitude
     */
    public Destination(int profileId, String name, String type, String country, String district, double latitude, double longitude, int visible) {
        this.profileId = profileId;
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

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public int getProfileId() { return profileId; }

    public void setCountry(String country) {
        this.country = country;
    }


    public void setSetSoftDelete(int setSoftDelete) { this.softDelete = setSoftDelete; }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getVisible() { return visible; }

    public void setVisible(int visible) { this.visible = visible; }

    public List<Photo> getUsersPhotos() {
        return usersPhotos;
    }

    public void setUsersPhotos(List<Photo> usersPhotos) {
        this.usersPhotos = usersPhotos;
    }

    public List<Photo> getWorldPhotos() {
        return worldPhotos;
    }

    public void setWorldPhotos(List<Photo> worldPhotos) {
        this.worldPhotos = worldPhotos;
    }
}
