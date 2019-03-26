package models;


import io.ebean.Finder;
import io.ebean.Model;
import org.checkerframework.checker.signedness.qual.Constant;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Constraint;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * This class holds the data for a profile trip
 */
@Entity
public class Trip extends Model {


    private ArrayList<TripDestination> destinations;
    @Constraints.Required
    private String name;
    @Id
    private int tripId;
    private String email;
    public static final Finder<String, Trip> find = new Finder<>(Trip.class);

    public Trip() {}

    /**
     * Constructor for a Trip
     * @param destinations The destinations in the trip
     */
    public Trip(ArrayList<TripDestination> destinations, String name) {
        this.destinations = sortDestinationsByOrder(destinations);
        this.name = name;

    }

    /**
     * Adds the passed TripDestination to the trip
     * @param toAdd the TripDestination to add
     */
    public void addDestination(TripDestination toAdd) {
        destinations.add(toAdd);
    }

    /**
     * Removes the destination at the passed position
     * @param index the show of the TripDestination to remove
     */
    public void removeDestination(int index) {
        destinations.remove(index);
    }

    public ArrayList<TripDestination> getDestinations() {
        this.destinations = sortDestinationsByOrder(destinations);
        return destinations;
    }

    public void setDestinations(ArrayList<TripDestination> destinations) {
        this.destinations = sortDestinationsByOrder(destinations);
    }

    public Integer getId() { return tripId; }
    public String getName() {
        return name;
    }
    public void setId(int id){ this.tripId = id; }
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }


    public long getTravelTime() {
        this.destinations = sortDestinationsByOrder(destinations);
        TripDestination startDest = destinations.get(0);
        TripDestination endDest = destinations.get(destinations.size() - 1);
        if (startDest.getArrival() == null || endDest.getDeparture() == null) {
            return 0; //TODO fix this or remove this
        }
        long diff = endDest.getDeparture().getTime() - startDest.getArrival().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    /**
     * Get the date of arrival at the first destination in the trip, as a string.
     */
    public String getStartDateString(){
        this.destinations = sortDestinationsByOrder(destinations);
        Date startDate = destinations.get(0).getArrival();
        if (startDate == null) {
            return "";
        }
        return new SimpleDateFormat("dd-MMM-yyyy").format(startDate);
    }

    public long getTimeVal() {
        this.destinations = sortDestinationsByOrder(destinations);
        Date startDate = destinations.get(0).getArrival();
        if (startDate != null) {
            return startDate.getTime();
        } else {
            return 0;
        }
    }

    public Date getStartDate(){
        return destinations.get(0).getArrival();
    }

    public String getDestinationNames() {
        this.destinations = sortDestinationsByOrder(destinations);
        //TODO fix this to get name not id
        String names = "" + destinations.get(0).getDestinationName();
        for (int i = 1; i < destinations.size(); i++) {
            names += ", " + destinations.get(i).getDestinationName();
        }
        return names;
    }

    public ArrayList<TripDestination> sortDestinationsByOrder(ArrayList<TripDestination> array) {
        ArrayList<TripDestination> temp = new ArrayList<TripDestination>();
        for (int i = 0; i<array.size(); i++) {
            for (int x=0; x < array.size(); x++) {
                if (array.get(x).getDestOrder() == i+1) {
                    temp.add(array.get(x));
                }
            }
        }
        return temp;
    }

}
