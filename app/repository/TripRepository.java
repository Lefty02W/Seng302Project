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

/**
 * A trip repository that executes database operations in a different
 * execution context handles all interactions with the trip table .
 */
public class TripRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public TripRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * insert trip destination into the database
     * @param tripDestination
     * @return trip id
     */
    public CompletionStage<Integer> insertTripDestination(TripDestination tripDestination) {
        return supplyAsync(() -> {
            ebeanServer.insert(tripDestination);
            return tripDestination.getTripId();
        }, executionContext);
    }


    /**
     * insert trip into database
     * @param trip
     * @param tripDestinations
     */
    public void insert(Trip trip, ArrayList<TripDestination> tripDestinations) {
        //TODO maybe look into async again
        //TODO transactions pls
        ebeanServer.insert(trip);
        for (TripDestination tripDestination : tripDestinations) {
            tripDestination.setTripId(trip.getId());
            ebeanServer.insert(tripDestination);
        }

    }



    /**
     * Removes a trip from the database
     * @param tripID the id of the trip to remove
     * @return the completionStage
     */
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
     * code to return trip from id
     * @param tripId
     * @return
     */
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
