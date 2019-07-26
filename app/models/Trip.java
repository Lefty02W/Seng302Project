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
    private TreeMap<Integer, TripDestination> orderedDestinations;
    @Constraints.Required
    private String name;
    @Id
    private int tripId;
    private int profileId;

    private int softDelete;

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
            this.orderedDestinations.put(tripDestination.getDestOrder(), tripDestination);
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


    public TreeMap<Integer, TripDestination> getOrderedDestinations() {
        return orderedDestinations;
    }

    public void setOrderedDestinations(TreeMap<Integer, TripDestination> orderedDestinations) {
        this.orderedDestinations = orderedDestinations;
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

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setSetSoftDelete(int setSoftDelete) { this.softDelete = setSoftDelete; }

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
     * sort destinations by order
     *
     * @param arrayList of trip destinations in order
     * @return sorted destination list
     */
    private ArrayList<TripDestination> sortDestinationsByOrder(ArrayList<TripDestination> array) {
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
