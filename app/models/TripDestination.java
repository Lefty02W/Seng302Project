package models;

import java.sql.Timestamp;
import java.util.Date;


/**
 * This class holds the data for a destination within a trip
 */
public class TripDestination {

    private String destination;
    private Date arrival;
    private Date departure;

    public TripDestination() {

    }

    /**
     * Constructor for a destination within a trip
     * @param destination the destination
     * @param arrival the arrival timestamp
     * @param departure the departure timestamp
     */
    public TripDestination(String destination, Date arrival, Date departure) {
        this.destination = destination;
        this.arrival = arrival;
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getArrival() {
        return arrival;
    }

    public void setArrival(Date arrival) {
        this.arrival = arrival;
    }

    public Date getDeparture() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure = departure;
    }

}
