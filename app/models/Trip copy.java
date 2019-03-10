package models;

import io.ebean.Model;
import play.data.validation.Constraints;

import java.util.ArrayList;

public class Trip extends Model {

    @Constraints.Required
    private ArrayList<TripStop> destinations;

    public ArrayList<TripStop> getDestinations() {
        return destinations;
    }

    public void setDestinations(ArrayList<TripStop> destinations) {
        this.destinations = destinations;
    }
}
