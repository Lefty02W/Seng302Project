package repository;

import io.ebean.*;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Destination;
import models.Trip;
import models.TripDestination;
import play.db.ebean.EbeanConfig;
import scala.None;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

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


    public ArrayList<Trip> getUsersTrips() {
        //TODO pass current user to this or grab user id from here
        ArrayList<Trip> trips = new ArrayList<>();

        List<Trip> result = Trip.find.query().where()
                // TODO once merged with branch that has getCurrent User uncomment -> .eq("email", currentUser.getEmail)
                .eq("email", "bender@momcorp.com") // Delete once current user can be
                .findList();

        for (Trip trip : result) {
            ArrayList<TripDestination> tripDestinations = new ArrayList<>();
            List<TripDestination> tripDests = TripDestination.find.query()
                    .where()
                    .eq("trip_id", trip.getId())
                    .findList();
            for (TripDestination tripDest : tripDests) {
                Destination destination = Destination.find.query()
                        .where()
                        .eq("destination_id", tripDest.getDestinationId())
                        .findSingleAttribute();
                tripDest.setDestination(destination);
                tripDestinations.add(tripDest);
            }
            trip.setDestinations(tripDestinations);
            trips.add(trip);
        }

        return trips;
    }

}
