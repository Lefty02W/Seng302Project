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


    public CompletionStage<Integer> insertTripDestination(TripDestination tripDestination) {
        return supplyAsync(() -> {
            ebeanServer.insert(tripDestination);
            return tripDestination.getTripId();
        }, executionContext);
    }



    public void insert(Trip trip, ArrayList<TripDestination> tripDestinations) {
        //TODO maybe look into async again
        //TODO transactions pls
        ebeanServer.insert(trip);
        for (TripDestination tripDestination : tripDestinations) {
            tripDestination.setTripId(trip.getId());
            System.out.println(tripDestination.getTripId());
            ebeanServer.insert(tripDestination);
        }

    }

    public CompletionStage<Optional<String>> updateName(int tripId, String name) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<String> value = Optional.empty();
            try {
                Trip targetTrip = ebeanServer.find(Trip.class).setId(tripId).findOne();
                if (targetTrip != null) {
                    targetTrip.setName(name);
                }
                targetTrip.update();
                txn.commit();
                value = Optional.of(targetTrip.getName());
            } finally {
                txn.end();
            }
            return value;
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


    public int getLatestId() {
        String query = ("SELECT MAX(trip_id) FROM trip");
        SqlRow row = ebeanServer.createSqlQuery(query).findOne();
        return row.getInteger("max(trip_id)");
    }

}
