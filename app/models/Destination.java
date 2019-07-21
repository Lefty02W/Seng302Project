package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Transient
    private List<Photo> usersPhotos = new ArrayList<>();

    @Transient
    private List<Photo> worldPhotos = new ArrayList<>();

    @Transient
    private String travellerTypesStringDest;

    @Transient
    private Map<Integer, TravellerType> travellerTypes;

    /**
     * custom empty destination constructor
     */
    public Destination() {

    }

    /**
     * Overloaded constructor which takes in the user's scala form data to create a Profile.
     * @param destinationId
     * @param profileId
     * @param name
     * @param type
     * @param country
     * @param district
     * @param latitude
     * @param longitude
     * @param visible
     * @param travellerTypes
     */
    public Destination(int destinationId, int profileId, String name, String type, String country, String district,
                       double latitude, double longitude, int visible,  String travellerTypes) {
        System.out.println("THIS ONE PLEASE");
        this.destinationId = destinationId;
        this.profileId = profileId;
        this.name = name;
        this.type = type;
        this.country = country;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visible = visible;
        this.travellerTypes = new HashMap<>();
        for (String travellerTypesString : (travellerTypes.split(","))) {
            TravellerType travellerType = new TravellerType(0, travellerTypesString);
            this.travellerTypes.put(travellerType.getTravellerTypeId(), travellerType);
        }
    }

    /**
     * Creating a destination for USERS
     *
     * @param destinationId the destinations db id
     * @param name the destination name
     * @param type the destination type
     * @param country the destinations country
     * @param district the destinations district
     * @param latitude the destinations latitude
     * @param longitude the destinations longitude
     * @param travellerTypes a map of a travellertype and its id
     */
    public Destination(int destinationId, int profileId, String name, String type, String country, String district,
                       double latitude, double longitude, int visible,  Map<Integer, TravellerType> travellerTypes) {
        this.destinationId = destinationId;
        this.profileId = profileId;
        this.name = name;
        this.type = type;
        this.country = country;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visible = visible;
        this.travellerTypes = travellerTypes;
    }

    /**
     * Creating a destination for ADMINS
     *
     * @param profileId
     * @param name the destination name
     * @param type the destination type
     * @param country the destinations country
     * @param district the destinations district
     * @param latitude the destinations latitude
     * @param longitude the destinations longitude
     */
    public Destination(int profileId, String name, String type, String country, String district, double latitude, double
            longitude, int visible) {
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
     * A function to turn the destination class created by the create destination form. It is required to turn the
     * , separated strings into maps.
     */
    public void initTravellerType() {
        this.travellerTypes = new HashMap<>();
        if (travellerTypesStringDest != null) {
            int i = 1;
            for (String travellerTypesString : (travellerTypesStringDest.split(","))) {
                TravellerType travellerType = new TravellerType(i, travellerTypesString);
                this.travellerTypes.put(travellerType.getTravellerTypeId(), travellerType);
                i++;
            }
        }
    }

    /**
     * Return the travellers types as a readable list
     * @return Array of Strings of traveller types
     */
    public ArrayList<String> getTravellerTypesList() {
        if (travellerTypes != null) {
            ArrayList<TravellerType> typeObjects = new ArrayList<TravellerType>(travellerTypes.values());
            ArrayList<String> toReturn = new ArrayList<>();
            for (TravellerType type : typeObjects) {
                toReturn.add(type.getTravellerTypeName());
            }
            return toReturn;
        } else {
            return new ArrayList<>();
        }
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

    public Map<Integer, TravellerType> getTravellerTypes() { return travellerTypes;}


    public String getTravellerTypesString() {
        ArrayList<String> listOfTravellerTypes = getTravellerTypesList();
        String travellerTypesStrings = "";
        for (String travellerType : listOfTravellerTypes) {
            travellerTypesStrings += travellerType + ", ";
        }
        return travellerTypesStrings;
    }

    public void setTravellerTypes(Map<Integer, TravellerType> travellerTypes) { this.travellerTypes = travellerTypes; }

    public void setTravellerTypesStringDest(String travellerTypesString) {
        this.travellerTypesStringDest = travellerTypesString;
    }

    public String getTravellerTypesForm() {
        return travellerTypesStringDest;
    }

//    public void addTravellerType(TravellerType travellerType) {
//        if (!travellerTypes.contains(travellerType)) {
//            this.travellerTypes.add(travellerType);
//        }
//    }
//
//    public void removeTravellerType(TravellerType travellerType) {
//        if (travellerTypes.contains(travellerType)) {
//            travellerTypes.remove(travellerType);
//        }
//    }
}
