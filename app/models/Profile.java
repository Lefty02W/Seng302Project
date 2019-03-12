package models;

import io.ebean.Finder;
import io.ebean.Model;
//import org.mindrot.jbcrypt.BCrypt;
import play.data.format.Formats;
import play.data.validation.Constraints;
import javax.persistence.Entity;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.Id;
import java.util.ArrayList;


@Entity
public class Profile extends Model {

    //private static final long serialVersionUID = 1L;

    private static final int WORKLOAD = 12;

    @Id
    private int userId;

    @Constraints.Required
    private String firstName;

    private String middleName;

    @Constraints.Required
    private String lastName;

    @Constraints.Required
    private String email;

    @Constraints.Required
    private String password;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private Date birthDate;



    @Constraints.Required
    private String gender;




    //@Formats.DateTime(pattern="dd-MM-yyyy")
    private Date timeCreated;

    private ArrayList<Destination> destinations = new ArrayList<Destination>();
    private ArrayList<String> nationality;
    private ArrayList<String> passports;

    //these booleans are chosen by the checkboxes, functions then create destinations (list of enums) from the booleans


    private ArrayList<String> travellerTypes;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
    private static SimpleDateFormat dateFormatSort = new SimpleDateFormat("dd/MM/YYYY");
    private static SimpleDateFormat dateFormatEntry = new SimpleDateFormat("YYYY-MM-dd");



    public Profile(String firstName, String lastName, String email, String password, Date birthDate,
                   ArrayList<String> passports, String gender, Date timeCreated, ArrayList<String> nationality, ArrayList<Destination> destinations,
                   ArrayList<String> travellerTypes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.passports = passports;
        this.gender = gender;
        this.timeCreated = timeCreated;
        this.nationality = nationality;
        this.destinations = destinations;
        this.travellerTypes = travellerTypes;
    }

    // Finder for profile
    public static final Finder<String, Profile> find = new Finder<>(Profile.class);

    //Setters
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

    public void setPassports(ArrayList<String> passports) {
        this.passports = passports;
    }

    public void setDestinations(ArrayList<Destination> destinations) {
        this.destinations = destinations;
    }



    public void setNationality(ArrayList<String> nationalities) {
        this.nationality = nationalities;
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

    public ArrayList<String> getPassports() {
        return passports;
    }

    public ArrayList<Destination> getDestinations() {
        return destinations;
    }

    public ArrayList<String> getNationality() {
        return nationality;
    }

    public boolean checkPassword(String password) {
        // TODO FIX THIS
        return false;
        //BCrypt.checkpw(password, this.password);
    }


    /**
     * Searches the users destinations for the given search term.
     *
     * @param searchTerm The term that will be searched for.
     * @return A arraylists of the destinations that contain the search term.
     */
    public ArrayList<Destination> searchDestinations(String searchTerm) {
        ArrayList<Destination> resultDestinations = new ArrayList<Destination>();
        for (Destination dest : destinations) {
            if (dest.getName().contains(searchTerm) || dest.getType().contains(searchTerm) || dest.getCountry().contains(searchTerm) || dest.getDistrict().contains(searchTerm)) {
                resultDestinations.add(dest);
            }
        }
        return resultDestinations;
    }

    /**
     * Returns a single destination.
     *
     * @param destinationID The id of the required destination.
     * @return The destination required.
     */
    public Destination returnDestination(int destinationID) {
        Destination toReturn = null;
        for (Destination dest : destinations) {
            if (dest.getDestinationId() == destinationID) {
                toReturn = dest;
                break;
            }
        }
        return toReturn;
    }

    //TODO FIX THIS PLS AND THANK
    public String getTravellerTypesString() {
        return travellerTypes.get(0);
    }


    /**
     * Delete a destination from the profile
     * @param destinationID ID of destination to delete
     */
    public void deleteDestination(int destinationID) {
        for(Destination dest : destinations) {
            if (dest.getDestinationId() == destinationID) {
                destinations.remove(dest);
                return;
            }
        }
    }
}