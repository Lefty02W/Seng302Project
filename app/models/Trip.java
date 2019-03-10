package models;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * This class holds the data for a profile trip
 */
public class Trip {


    private ArrayList<TripDestination> destinations;
    private String name;
    private Integer id;
    public Trip() {}




    /**
     * Constructor for a Trip
     * @param destinations The destinations in the trip
     */
    public Trip(ArrayList<TripDestination> destinations, String name) {
        this.destinations = destinations;
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
        return destinations;
    }

    public void setDestinations(ArrayList<TripDestination> destinations) {
        this.destinations = destinations;
    }
    public Integer getId() { return id; }
    public String getName() {
        return name;
    }
    public void setId(Integer id){ this.id = id;
  }
    public void setName(String name) {
        this.name = name;
    }

    public long getTravelTime() {
        TripDestination startDest = destinations.get(0);
        TripDestination endDest = destinations.get(destinations.size() - 1);
        long diff = endDest.getDeparture().getTime() - startDest.getArrival().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public String getDestinationNames() {
        String names = destinations.get(0).getDestination();
        for (int i = 1; i < destinations.size(); i++) {
            names += ", " + destinations.get(i).getDestination();
        }
        return names;
    }

}
