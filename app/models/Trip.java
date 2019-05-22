package models;


import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * This class holds the data for a profile trip
 */
@Entity
public class Trip extends Model {


    private ArrayList<TripDestination> destinations;
    private TreeMap<Integer, TripDestination> orderedDestiantions;
    @Constraints.Required
    private String name;
    @Id
    private int tripId;
    private String email;
    public static final Finder<String, Trip> find = new Finder<>(Trip.class);

    public Trip() {}

    /**
     * Constructor for a Trip
     *
     * @param destinations The destinations in the trip
     */
    public Trip(ArrayList<TripDestination> destinations, String name) {
        this.destinations = destinations;
        this.name = name;
        for (TripDestination tripDestination : destinations) {
            this.orderedDestiantions.put(tripDestination.getDestOrder(), tripDestination);
        }
    }


    /**
     * Adds the passed TripDestination to the trip
     *
     * @param toAdd the TripDestination to add
     */
    public void addDestination(TripDestination toAdd) {
        destinations.add(toAdd);
    }

    /**
     * Removes the destination at the passed position
     *
     * @param index the show of the TripDestination to remove
     */
    public void removeDestination(int index) {
        destinations.remove(index);
    }

    public TreeMap<Integer, TripDestination> getOrderedDestiantions() {
        return orderedDestiantions;
    }

    public void setOrderedDestiantions(TreeMap<Integer, TripDestination> orderedDestiantions) {
        this.orderedDestiantions = orderedDestiantions;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    /**
     * calculate total travel time
     *
     * @return travel time
     */
    public Long getTravelTime() {
        if (!destinations.isEmpty()) {
            this.destinations = sortDestinationsByOrder(destinations);
            TripDestination startDest = destinations.get(0);
            TripDestination endDest = destinations.get(destinations.size() - 1);
            if (startDest.getArrival() == null || endDest.getDeparture() == null) {
                return null;
            }
            long diff = endDest.getDeparture().getTime() - startDest.getArrival().getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } else {
            return null;
        }
    }

    /**
     * Get the date of arrival at the first destination in the trip, as a string.
     *
     * @return formatted start date
     */
    public String getStartDateString() {
        this.destinations = sortDestinationsByOrder(destinations);
        Date startDate = destinations.get(0).getArrival();
        if (startDate == null) {
            return "";
        }
        return new SimpleDateFormat("dd-MMM-yyyy").format(startDate);
    }

    /**
     * get time value
     *
     * @return true time value
     */
    public long getTimeVal() {
        this.destinations = sortDestinationsByOrder(destinations);
        Date startDate = destinations.get(0).getArrival();
        if (startDate != null) {
            return startDate.getTime();
        } else {
            return 0;
        }
    }

    /**
     * This method gets the first date stored within a trip
     *
     * @return the date found
     */
    public long getFirstDate() {
        Date toReturn = null;
        for (TripDestination tripDestination : destinations) {
            if (tripDestination.getArrival() != null) {
                toReturn = tripDestination.getArrival();
                break;
            } else if (tripDestination.getDeparture() != null) {
                toReturn = tripDestination.getDeparture();
                break;
            }
        }
        if (toReturn != null) {
            return toReturn.getTime();
        }
        return 0;
    }

    public Date getStartDate(){
        if (destinations.size() > 0) {
            return destinations.get(0).getArrival();
        } else {
            return null;
        }
    }

    /**
     * create list of printable destinations
     *
     * @return printable destination names
     */
    public String getDestinationNames() {
        this.destinations = sortDestinationsByOrder(destinations);
        String names = "" + destinations.get(0).getDestinationName();
        for (int i = 1; i < destinations.size(); i++) {
            names += ", " + destinations.get(i).getDestinationName();
        }
        return names;
    }

    /**
     * sort destinations by order
     *
     * @param array
     * @return sorted destination list
     */
    public ArrayList<TripDestination> sortDestinationsByOrder(ArrayList<TripDestination> array) {
        ArrayList<TripDestination> temp = new ArrayList<>();
        if (array == null) {
            return temp;
        }
        for (int i = 0; i < array.size(); i++) {
            for (int x = 0; x < array.size(); x++) {
                if (array.get(x).getDestOrder() == i + 1) {
                    temp.add(array.get(x));
                }
            }
        }
        return temp;
    }

}
