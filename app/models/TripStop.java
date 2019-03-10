package models;

import io.ebean.Model;
import org.joda.time.DateTime;
import play.data.validation.Constraints;

public class TripStop extends Model {

    @Constraints.Required
    private Destination stop;

    @Constraints.Required
    private DateTime arrival;

    @Constraints.Required
    private DateTime departure;

    public Destination getStop() {
        return stop;
    }

    public DateTime getArrival() {
        return arrival;
    }

    public DateTime getDeparture() {
        return departure;
    }

    public void setStop(Destination stop) {
        this.stop = stop;
    }

    public void setArrival(DateTime arrival) {
        this.arrival = arrival;
    }

    public void setDeparture(DateTime departure) {
        this.departure = departure;
    }
}
