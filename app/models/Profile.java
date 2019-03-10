package models;

import io.ebean.Finder;
import io.ebean.Model;
//import org.mindrot.jbcrypt.BCrypt;
import play.data.format.Formats;
import play.data.validation.Constraints;
import javax.persistence.Entity;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.Id;
import java.util.ArrayList;


@Entity
public class Profile extends Model {

    //private static final long serialVersionUID = 1L;

    private static final int WORKLOAD = 12;

    @Constraints.Required
    private String firstName;

    @Constraints.Required
    private String lastName;

    @Constraints.Required
    @Id
    private String email;

    @Constraints.Required
    private String password;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private Date birthDate;

    private String passports;

    @Constraints.Required
    private String gender;


    @Constraints.Required
    private String nationality;

    //@Formats.DateTime(pattern="dd-MM-yyyy")
    private Date timeCreated = new Date();

    private ArrayList<Destination> destinations = new ArrayList<Destination>();

    //these booleans are chosen by the checkboxes, functions then create destinations (list of enums) from the booleans
    private boolean groupie;

    private boolean thrillseeker;

    private boolean gapYear;

    private boolean weekender;

    private boolean holidaymaker;

    private boolean business;

    private boolean backpacker;

    private ArrayList<TravellerType> travellerTypes;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
    private static SimpleDateFormat dateFormatSort = new SimpleDateFormat("dd/MM/YYYY");
    private static SimpleDateFormat dateFormatEntry = new SimpleDateFormat("YYYY-MM-dd");



    public Profile(String firstName, String lastName, String email, String password, Date birthDate,
                   String passports, String gender, Date timeCreated, String nationality, ArrayList<Destination> destinations,
                   boolean groupie, boolean thrillseeker, boolean gapYear, boolean weekender, boolean holidaymaker,
                   boolean business, boolean backpacker) {
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
        this.groupie = groupie;
        this.thrillseeker = thrillseeker;
        this.gapYear = gapYear;
        this.weekender = weekender;
        this.holidaymaker = holidaymaker;
        this.business = business;
        this.backpacker = backpacker;
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

    public void setPassports(String passports) {
        this.passports = passports;
    }

    public void setDestinations(ArrayList<Destination> destinations) {
        this.destinations = destinations;
    }

    /**
     * updates travellerTypes with the appropriate enum
     */
    public void setGroupie(boolean groupie) {
        this.groupie = groupie;
    }

    public void setThrillseeker(boolean thrillseeker) {
        this.thrillseeker = thrillseeker;
    }

    public void setGapYear(boolean gapYear) {
        this.gapYear = gapYear;
    }

    public void setWeekender(boolean weekender) {
        this.weekender = weekender;
    }

    public void setHolidaymaker(boolean holidaymaker) {
        this.holidaymaker = holidaymaker;
    }

    public void setBusiness(boolean business) {
        this.business = business;
    }

    public void setBackpacker(boolean backpacker) {
        this.backpacker = backpacker;
    }

    private void setTravellerTypes() {
        travellerTypes = new ArrayList<TravellerType>();
        if (this.groupie) {
            this.travellerTypes.add(TravellerType.GROUPIE);
        } else if (this.travellerTypes.contains(TravellerType.GROUPIE)) {
            this.travellerTypes.remove(TravellerType.GROUPIE);
        }
        if (this.thrillseeker) {
            this.travellerTypes.add(TravellerType.THRILLSEEKER);
        } else if (this.travellerTypes.contains(TravellerType.THRILLSEEKER)) {
            this.travellerTypes.remove(TravellerType.THRILLSEEKER);
        }
        if (this.gapYear) {
            travellerTypes.add(TravellerType.GAP_YEAR);
        } else if (travellerTypes.contains(TravellerType.GAP_YEAR)) {
            travellerTypes.remove(TravellerType.GROUPIE);
        }
        if (this.weekender) {
            travellerTypes.add(TravellerType.WEEKENDER);
        } else if (travellerTypes.contains(TravellerType.WEEKENDER)) {
            travellerTypes.remove(TravellerType.WEEKENDER);
        }
        if (this.holidaymaker) {
            travellerTypes.add(TravellerType.HOLIDAYMAKER);
        } else if (travellerTypes.contains(TravellerType.HOLIDAYMAKER)) {
            travellerTypes.remove(TravellerType.HOLIDAYMAKER);
        }
        if (this.business) {
            travellerTypes.add(TravellerType.BUSINESS);
        } else if (travellerTypes.contains(TravellerType.BUSINESS)) {
            travellerTypes.remove(TravellerType.BUSINESS);
        }
        if (this.backpacker) {
            travellerTypes.add(TravellerType.BACKPACKER);
        } else if (travellerTypes.contains(TravellerType.BACKPACKER)) {
            travellerTypes.remove(TravellerType.BACKPACKER);
        }
    }

    public void setNationality(String nationalities) {
        this.nationality = nationalities;
    }

    //Getters
    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
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

    public String getPassports() {
        return passports;
    }

    public ArrayList<Destination> getDestinations() {
        return destinations;
    }

    public String getNationality() {
        return nationality;
    }

    public boolean checkPassword(String password) {
        // TODO FIX THIS
        return false;
        //BCrypt.checkpw(password, this.password);
    }

    public boolean getGroupie() {
        return groupie;
    }

    public boolean getThrillseeker() {
        return thrillseeker;
    }

    public boolean getGapYear() {
        return gapYear;
    }

    public boolean getWeekender() {
        return weekender;
    }

    public boolean getHolidaymaker() {
        return holidaymaker;
    }

    public boolean getBusiness() {
        return business;
    }

    public boolean getBackpacker() {
        return backpacker;
    }

    /**
     * needs to set traveller types first
     */
    public ArrayList<TravellerType> getTravellerTypes() {
        setTravellerTypes();
        return travellerTypes;
    }

    /**
     * needs to set traveller types first
     * @return all enum traveller types in a string
import java.sql.Timestamp; format so that it can be printed to a webpage
     */
    public String getTravellerTypesString() {
        setTravellerTypes();
        String temporary = "";
        for (int i = 0; i < travellerTypes.size(); i++) {
            temporary += travellerTypes.get(i).name() + " ";
        }
        return temporary;
    }

    /**
     *this method is called from homecontroller save to make sure a traveller type has been picked
     * @return boolean which is true if a traveller type has been picked
     */
    public boolean travellerTypeIsPicked() {
        boolean isPicked = false;
        if (groupie) {
            isPicked = true;
        } else if (thrillseeker) {
            isPicked = true;
        } else if (gapYear) {
            isPicked = true;
        } else if (weekender) {
            isPicked = true;
        } else if (holidaymaker) {
            isPicked = true;
        } else if (business) {
            isPicked = true;
        } else if (backpacker) {
            isPicked = true;
        }
        return isPicked;
    }

    /**
     * Method to convert a nationality string seperated by commas to a list for display in frontend
     * @return a list of nationalities belonging to a profile
     */
    public ArrayList<String> nationalityToList() {
        return new ArrayList<>(Arrays.asList(nationality.split("\\s*,\\s*")));
    }

    /**
     * Method to convert a passport string seperated by commas to a list for display in frontend
     * @return a list of passports belonging to a profile
     */
    public ArrayList<String> passportToList() {
        return new ArrayList<>(Arrays.asList(passports.split("\\s*,\\s*")));
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
            if (dest.getDestination_id() == destinationID) {
                toReturn = dest;
                break;
            }
        }
        return toReturn;
    }


    /**
     * Delete a destination from the profile
     * @param destinationID ID of destination to delete
     */
    public void deleteDestination(int destinationID) {
        for(Destination dest : destinations) {
            if (dest.getDestination_id() == destinationID) {
                destinations.remove(dest);
                return;
            }
        }
    }
}