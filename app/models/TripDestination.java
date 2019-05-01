package models;


import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
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

    private int destOrder;

    private Destination destination;
    public static final Finder<String, TripDestination> find = new Finder<>(TripDestination.class);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
    private static SimpleDateFormat dateFormatEntry = new SimpleDateFormat("YYYY-MM-dd");


    /**
     *
     * @param destinationId
     * @param arrival
     * @param departure
     * @param destOrder
     */
    public TripDestination(int destinationId, Date arrival, Date departure, int destOrder) {
        this.destinationId = destinationId;
        this.arrival = arrival;
        this.departure = departure;
        this.destOrder = destOrder;
    }

    /**
     * Formats a passed date into the correct format for input type datetime-local
     * @param toFormat The date t format
     * @return the formatted date string
     */
    public String formatLocalDate(Date toFormat) {
        if (toFormat == null) {
            return "";
        }
        return dateFormatEntry.format(toFormat);
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

    /**
     * format the date  string
     * @param toFormat
     * @return formated date string
     */
    public String getFormattedDate(Date toFormat)
    {
        if (toFormat == null) {
            return "";
        }
        return dateFormat.format(toFormat);
    }

    /**
     * Creates a formatted data string of the arrival date of thr destination
     * @return the date string
     */
    public String getArrivalString() {
        if (arrival == null) {
            return "";
        }
        return dateFormat.format(arrival);
    }

    /**
     * Creates a formatted data string of the departure date of thr destination
     * @return the date string
     */
    public String getDepartureString() {

        if (departure == null) {
            return "";
        }return dateFormat.format(departure);
    }

    /**
     * Forms a string holding the arrival and departure dates of the trip destination if they are present
     * @return
     */
    public String getDisplayDates() {
        if (departure == null && arrival == null) {
            return "No dates given";
        } else if (departure == null) {
            return  dateFormat.format(arrival) + " to -";
        } else if (arrival == null) {
            return "- to " + dateFormat.format(departure);
        } else {
            return dateFormat.format(arrival) + " to " + dateFormat.format(departure);
        }
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public String getDestinationName() {
        return Destination.find.byId(Integer.toString(destinationId)).getName();
    }

    public int getDestOrder() {
        return destOrder;
    }

    public void setDestOrder(int dest_order) {
        this.destOrder = dest_order;
    }
}
