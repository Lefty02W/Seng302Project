package models;


import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This class holds the data for a destination within a trip
 */
@Entity
public class TripDestination extends Model {


    private Date arrival;
    private Date departure;
    @Id
    private int tripDestinationId;

    @Constraints.Required
    private int destinationId;

    private int tripId;

    private Destination destination;
    public static final Finder<String, TripDestination> find = new Finder<>(TripDestination.class);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY mm:hh");




    public TripDestination() {

    }

    /**
     * Constructor for a destination within a trip
     * @param destinationId the destination
     * @param arrival the arrival timestamp
     * @param departure the departure timestamp
     */
    public TripDestination(int destinationId, Date arrival, Date departure) {
        this.destinationId = destinationId;
        this.arrival = arrival;
        this.departure = departure;
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

    public int getTripDestinationId() {
        return tripDestinationId;
    }

    public void setTripDestinationId(int tripDestinationId) {
        this.tripDestinationId = tripDestinationId;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getFormattedDate(Date toFormat) {
        return dateFormat.format(toFormat);
    }

    /**
     * Creates a formatted data string of the arrival date of thr destination
     * @return the date string
     */
    public String getArrivalString() {
        return dateFormat.format(arrival);
    }

    /**
     * Creates a formatted data string of the departure date of thr destination
     * @return the date string
     */
    public String getDepartureString() {
        return dateFormat.format(departure);
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }
}
