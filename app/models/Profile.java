package models;

import com.google.common.collect.TreeMultimap;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeMap;

/**
 * This class holds the data for a profile
 */
@Entity
public class Profile extends Model {

    @Constraints.Required
    private String firstName;

    private String middleName;

    @Constraints.Required
    private String lastName;

    @Id
    @Constraints.Required
    private String email;

    private String password;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @Constraints.Required
    private String gender;

    private String passports;

    @Constraints.Required
    private String nationalities;
    @Constraints.Required
    private String travellerTypes;

    private boolean admin;

    //@Formats.DateTime(pattern="dd-MM-yyyy")
    private Date timeCreated;

    private ArrayList<Destination> destinations = new ArrayList<>();
    TreeMultimap<Long, Integer> tripsMap = TreeMultimap.create();
    TreeMap <Integer, Trip> tripsTripMap = new TreeMap<>();
    //these booleans are chosen by the checkboxes, functions then create destinations (list of enums) from the booleans

    private static SimpleDateFormat dateFormatEntry = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat dateFormatSort = new SimpleDateFormat("dd/MM/yyyy");

    public Profile(String firstName, String lastName, String email, String password, Date birthDate,
                   String passports, String gender, Date timeCreated, String nationalities,
                   String travellerTypes, ArrayList<Trip> trips, boolean isAdmin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.passports = passports;
        this.gender = gender;
        this.timeCreated = timeCreated;
        this.nationalities = nationalities;
        this.travellerTypes = travellerTypes;
        this.admin = isAdmin;

    }

    // Finder for profile
    public static final Finder<String, Profile> find = new Finder<>(Profile.class);

    //--------------Setters----------------------
    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public void setAdmin(boolean isAdmin){
        this.admin = isAdmin;
    }
    public String getEntryDate() {
        return dateFormatEntry.format(birthDate);
    }

    //Getters
    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public String getTravellerTypes() {
        return travellerTypes;
    }


    public void setTravellerTypes(String travellerTypes) {
        this.travellerTypes = travellerTypes;
    }

    public ArrayList<String> getPassportsList() {
        return new ArrayList<>(Arrays.asList(passports.split(",")));
    }

    public ArrayList<String> getNationalityList() {
        return new ArrayList<>(Arrays.asList(nationalities.split(",")));
    }

    public ArrayList<String> getTravellerTypesList() {
        return new ArrayList<>(Arrays.asList(travellerTypes.split(",")));
    }

    public TreeMultimap<Long, Integer> getTrips() {
        return tripsMap;
    }

    public Trip getTripById(int tripId) {
        return tripsTripMap.get(tripId);
    }

    public void setTrips(TreeMultimap<Long, Integer> trips) {
        this.tripsMap = trips;
    }

    public void setTripMaps(TreeMap<Integer, Trip> trips) {
        this.tripsTripMap = trips;
    }

    public TreeMap<Integer, Trip> getTripsMap() {
        return tripsTripMap;
    }

    public ArrayList<Destination> getDestinations() {
        return destinations;
    }

    public void setDestinations(ArrayList<Destination> destinations) {
        this.destinations = destinations;
    }


    /**
     * This method creates a formatted date string of the profiles birth date
     * @return the formatted date string
     */
    public String getBirthString() {
        if (birthDate != null) {
            return dateFormatSort.format(birthDate);
        }
        return "";
    }


    public boolean isAdmin() { return this.admin; }

    public void setPassports(String passports) {
        this.passports = passports;
    }

    public String getPassports() {
        return passports;
    }

    public String getNationalities() {
        return nationalities;
    }

    public void setNationalities(String nationalities) {
        this.nationalities = nationalities;
    }
}