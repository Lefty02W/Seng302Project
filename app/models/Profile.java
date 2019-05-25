package models;

import com.google.common.collect.TreeMultimap;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class holds the data for a profile
 */
@Entity
public class Profile extends Model {

    @Id
    private Integer profileId;

    @Constraints.Required
    private String firstName;

    private String middleName;

    @Constraints.Required
    private String lastName;

    @Constraints.Required
    private String email;

    private String password;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @Constraints.Required
    private String gender;

    @Transient
    private String passportsForm;

    @Transient
    @Constraints.Required
    private String nationalitiesForm;

    @Transient
    @Constraints.Required
    private String travellerTypesForm;

    @Transient
    private Map<Integer, PassportCountry> passports;
    @Transient
    private Map<Integer, Nationality> nationalities;
    @Transient
    private Map<Integer, TravellerType> travellerTypes;
    // Finder for profile
    public static final Finder<Integer, Profile> find = new Finder<>(Profile.class);


    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private Date timeCreated;

    @Transient
    private ArrayList<Destination> destinations = new ArrayList<>();
    @Transient
    private ArrayList<Trip> trips;
    @Transient
    private TreeMultimap<Long, Integer> tripsMap = TreeMultimap.create();
    @Transient
    private Map <Integer, Trip> tripsTripMap = new TreeMap<>();
    //these booleans are chosen by the checkboxes, functions then create destinations (list of enums) from the booleans
    private SimpleDateFormat dateFormatEntry = new SimpleDateFormat("YYYY-MM-dd");
    private SimpleDateFormat dateFormatSort = new SimpleDateFormat("dd/MM/YYYY");
    @Transient
    private List<String> roles;



    /**
     * Traditional constructor for profile. Used when retrieving a Profile from DB.
     * @param firstName
     * @param lastName
     * @param email
     * @param birthDate
     * @param passports
     * @param gender
     * @param timeCreated
     * @param nationalities
     * @param travellerTypes
     * @param roles
     */
    public Profile(Integer profileId, String firstName, String middleName,
                   String lastName, String email, Date birthDate,
                   Map<Integer, PassportCountry> passports, String gender, Date timeCreated, Map<Integer, Nationality> nationalities,
                   Map<Integer, TravellerType> travellerTypes, List<String> roles) {
        this.profileId = profileId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.passports = passports;
        this.gender = gender;
        this.timeCreated = timeCreated;
        this.nationalities = nationalities;
        this.travellerTypes = travellerTypes;
        this.roles = roles;

    }

    /**
     * Overloaded constructor which takes in the user's scala form data to create a Profile.
     * @param firstName
     * @param lastName
     * @param email
     * @param password
     * @param birthDate
     * @param passports
     * @param gender
     * @param timeCreated
     * @param nationalities
     * @param travellerTypes
     * @param trips
     * @param roles
     */
    public Profile(String firstName, String lastName, String email, String password, Date birthDate,
                   String passports, String gender, Date timeCreated, String nationalities,
                   String travellerTypes, ArrayList<Trip> trips, ArrayList<String> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.passports = new HashMap<>();
        for (String passportString : (passports.split(","))) {
            PassportCountry passport = new PassportCountry(0, passportString);
            this.passports.put(passport.getPassportId(), passport);
        }
        this.gender = gender;
        this.timeCreated = timeCreated;

        this.nationalities = new HashMap<>();
        for (String nationalityString : (nationalities.split(","))) {

            Nationality nationality = new Nationality(0, nationalityString);
            this.nationalities.put(nationality.getNationalityId(), nationality);
        }
        this.travellerTypes = new HashMap<>();
        for (String travellerTypesString : (travellerTypes.split(","))) {

            TravellerType travellerType = new TravellerType(0, travellerTypesString);
            this.travellerTypes.put(travellerType.getTravellerTypeId(), travellerType);

        }
        this.trips = trips;
        this.roles = roles;
    }

    /**
     * A function to turn the profile class created by the create user form. It is required to turn the
     * , separated strings into maps.
     */
    public void initProfile() {
        this.passports = new HashMap<>();
        int i = 1;
        for (String passportString : (passportsForm.split(","))) {
            i++;
            PassportCountry passport = new PassportCountry(i, passportString);
            this.passports.put(passport.getPassportId(), passport);
        }
        i = 1;
        this.nationalities = new HashMap<>();
        for (String nationalityString : (nationalitiesForm.split(","))) {
            i++;
            Nationality nationality = new Nationality(i, nationalityString);
            this.nationalities.put(nationality.getNationalityId(), nationality);
        }
        i = 1;
        this.travellerTypes = new HashMap<>();
        this.travellerTypes = new HashMap<>();
        for (String travellerTypesString : (travellerTypesForm.split(","))) {
            i++;
            TravellerType travellerType = new TravellerType(i, travellerTypesString);
            this.travellerTypes.put(travellerType.getTravellerTypeId(), travellerType);
        }
    }


    public Integer getProfileId() {
        return profileId;
    }



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
        //Hash the password for added security
        // String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(WORKLOAD));
        this.password = password;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setRoles(List<String> newRoles){
        this.roles = newRoles;
    }

    public String getEntryDate() {
        return dateFormatEntry.format(timeCreated);
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

    public Map<Integer, TravellerType> getTravellerTypes() {
        return travellerTypes;
    }


    public void setTravellerTypes(Map<Integer, TravellerType> travellerTypes) {
        this.travellerTypes = travellerTypes;
    }

    public ArrayList<String> getPassportsList() {
        if (!(passports == null)) {
            ArrayList<PassportCountry> passportObjects = new ArrayList<PassportCountry>(passports.values());
            ArrayList<String> toReturn = new ArrayList<>();
            for (PassportCountry passport : passportObjects) {
                toReturn.add(passport.getPassportName());
            }

            return toReturn;
        } else {
            return new ArrayList<String>();
        }
    }

    public ArrayList<String> getNationalityList() {
        if (!(nationalities == null)) {
            ArrayList<Nationality> nationalityObjects = new ArrayList<Nationality>(nationalities.values());
            ArrayList<String> toReturn = new ArrayList<>();
            for (Nationality nationality : nationalityObjects) {
                toReturn.add(nationality.getNationalityName());
            }
            return toReturn;
        } else {
            return new ArrayList<String>();
        }
    }

    public ArrayList<String> getTravellerTypesList() {
        if (!(travellerTypes == null)) {
            ArrayList<TravellerType> typeObjects = new ArrayList<TravellerType>(travellerTypes.values());
            ArrayList<String> toReturn = new ArrayList<>();
            for (TravellerType type : typeObjects) {
                toReturn.add(type.getTravellerTypeName());
            }
            return toReturn;
        } else {
            return new ArrayList<String>();
        }
    }

    public String getTravellerTypesString() {
        if (!(travellerTypes.isEmpty())) {
            ArrayList<TravellerType> typeObjects = new ArrayList<>(travellerTypes.values());
            StringBuilder toReturn = new StringBuilder();
            for (TravellerType type : typeObjects) {
                toReturn.append(type.getTravellerTypeName() + ", ");
            }
            return toReturn.toString().substring(0, toReturn.length() - 2);
        } else {
            return "";
        }
    }

    public String getNationalityString() {
        if (!(nationalities.isEmpty())) {
            ArrayList<Nationality> nationalityObjects = new ArrayList<>(nationalities.values());
            StringBuilder toReturn = new StringBuilder();
            for (Nationality nationality : nationalityObjects) {
                toReturn.append(nationality.getNationalityName() + ",");
            }
            return toReturn.toString().substring(0, toReturn.length() - 1);
        } else {
            return "";
        }
    }


    public String getPassportsString() {
        if (!(passports.isEmpty())) {

            ArrayList<PassportCountry> passportObjects = new ArrayList<>(passports.values());
            StringBuilder toReturn = new StringBuilder();
            for (PassportCountry passport : passportObjects) {
                toReturn.append(passport.getPassportName() + ", ");
            }
            return toReturn.toString().substring(0, toReturn.length()-2);
        } else {
            return "";
        }
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

    public void setTripMaps(Map<Integer, Trip> trips) {
        this.tripsTripMap = trips;
    }

    public Map<Integer, Trip> getTripsMap() {
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


    public String getPassportsForm() {
        return passportsForm;
    }

    public void setPassportsForm(String passportsForm) {
        this.passportsForm = passportsForm;
    }

    public String getNationalitiesForm() {
        return nationalitiesForm;
    }

    public void setNationalitiesForm(String nationalitiesForm) {
        this.nationalitiesForm = nationalitiesForm;
    }

    public String getTravellerTypesForm() {
        return travellerTypesForm;
    }

    public void setTravellerTypesForm(String travellerTypesForm) {
        this.travellerTypesForm = travellerTypesForm;
    }

    public List<String> getRoles() { return this.roles; }

    /**
     * Check the user has a given role name by searching their roles list, if present.
     */
    public boolean hasRole(String role) {

        if (this.roles != null) {

            return this.roles.contains(role);
        } else {

            return false;
        }
    }

    public void setPassports(Map<Integer, PassportCountry> passports) {
        this.passports = passports;
    }

    public Map<Integer, PassportCountry> getPassports() {
        return passports;
    }

    public Map<Integer, Nationality> getNationalities() {
        return nationalities;
    }

    public void setNationalities(Map<Integer, Nationality> nationalities) {
        this.nationalities = nationalities;
    }
}