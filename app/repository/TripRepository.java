package repository;

import controllers.SessionController;
import io.ebean.*;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Destination;
import models.Profile;
import models.Trip;
import models.TripDestination;
import play.api.mvc.Request;
import play.db.ebean.EbeanConfig;
import scala.None;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class TripRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public TripRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }


    public CompletionStage<Integer> insert(Trip trip) {
        return supplyAsync(() -> {

            ebeanServer.insert(trip);
            return trip.getId();
        }, executionContext);
    }

    //TODO change this to add tripdests to the trip such as getUsersTrips
    public Optional<ArrayList<Trip>> getAllTrips() {
        try {
            Optional<List<Trip>> toReturnOptional = Optional.ofNullable(ebeanServer.find(Trip.class).findList());
            ArrayList<Trip> toReturn = (ArrayList<Trip>) toReturnOptional.get();
            return Optional.of(toReturn);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public CompletionStage<Optional<Integer>> delete(int tripID) {
        return supplyAsync(() -> {
            try {
                final Optional<Trip> tripOptional = Optional.ofNullable(ebeanServer.find(Trip.class).setId(tripID).findOne());
                tripOptional.ifPresent(Model::delete);
                return tripOptional.map(p -> p.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }


    /**
     * Gets an ArrayList of trips from the database that related to a passed profile
     * @param currentUser the profile to get rips for
     * @return the trips found for the passed profile
     */
    public ArrayList<Trip> getUsersTrips(Profile currentUser) {
        ArrayList<Trip> trips = new ArrayList<>();

        // Getting the trips out of the database
        List<Trip> result = Trip.find.query().where()
                .eq("email", currentUser.getEmail())
                .findList();

        for (Trip trip : result) {
            ArrayList<TripDestination> tripDestinations = new ArrayList<>();
            // Getting the tripDestinations out of the database for each trip returned
            List<TripDestination> tripDests = TripDestination.find.query()
                    .where()
                    .eq("trip_id", trip.getId())
                    .findList();
            for (TripDestination tripDest : tripDests) {
                // Getting the destinations for each tripDestination
                List<Destination> destinations = Destination.find.query()
                        .where()
                        .eq("destination_id", tripDest.getDestinationId())
                        .findList();
                tripDest.setDestination(destinations.get(0));
                tripDestinations.add(tripDest);
            }
            trip.setDestinations(tripDestinations);
            trips.add(trip);
        }
        // Returning the trips found
        return trips;
    }

    public Trip getTrip(int tripId) {
        // Getting the trips out of the database
        List<Trip> result = Trip.find.query().where()
                .eq("trip_id", tripId)
                .findList();

        Trip trip = result.get(0);

        ArrayList<TripDestination> tripDestinations = new ArrayList<>();
        List<TripDestination> tripDests = TripDestination.find.query()
                .where()
                .eq("trip_id", tripId)
                .findList();

        for (TripDestination tripDest : tripDests) {
            // Getting the destinations for each tripDestination
            List<Destination> destinations = Destination.find.query()
                    .where()
                    .eq("destination_id", tripDest.getDestinationId())
                    .findList();
            tripDest.setDestination(destinations.get(0));
            tripDestinations.add(tripDest);
        }
        trip.setDestinations(tripDestinations);
        return trip;
    }

}
