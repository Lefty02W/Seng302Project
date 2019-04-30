package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Model;
import io.ebean.Transaction;
import models.Destination;
import models.Trip;
import models.TripDestination;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletionStage;
import repository.TripDestinationsRepository;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A trip repository that executes database operations in a different
 * execution context handles all interactions with the trip table .
 */
public class TripRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final TripDestinationsRepository tripDestinationsRepository;

    @Inject
    public TripRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext, TripDestinationsRepository tripDestinationsRepository) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.tripDestinationsRepository = tripDestinationsRepository;
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
     * Edit a trip in the database
     * @param trip
     * @param tripId
     * @param tripDestinations
     */
    public CompletionStage<Integer> update(Trip trip, Integer tripId, ArrayList<TripDestination> tripDestinations) {
        return supplyAsync(() -> {
            try (Transaction txn = ebeanServer.beginTransaction()) {
                List<Trip> result = Trip.find.query().where()
                        .eq("trip_id", tripId)
                        .findList();
                Trip tripEdit = result.get(0);
                if (tripEdit != null) {
                    tripEdit.setName(trip.getName());
                    tripEdit.setEmail(trip.getEmail());
                    ArrayList<TripDestination> originalTripDests = tripEdit.getDestinations();
                    int i;
                    for (i = 0; i < tripDestinations.size() && i < originalTripDests.size(); i++) {
                        tripDestinations.get(i).setTripId(trip.getId());
                        tripDestinationsRepository.editTrip(tripDestinations.get(i), originalTripDests.get(i).getTripDestinationId());
                    }
                    if (i + 1 < tripDestinations.size()) {
                        for (i++; i < tripDestinations.size(); i++) {
                            tripDestinations.get(i).setTripId(trip.getId());
                            tripDestinationsRepository.insert(tripDestinations.get(i));
                        }
                    } else if (i + 1 < originalTripDests.size()) {
                        for (i++; i < originalTripDests.size(); i++) {
                            tripDestinationsRepository.delete(originalTripDests.get(i).getTripDestinationId());
                        }
                    }
                    tripEdit.setDestinations(trip.getDestinations());
                    tripEdit.setOrderedDestiantions(trip.getOrderedDestiantions());
                    tripEdit.update();
                }
                txn.commit();
            }
            return trip.getId();
        }, executionContext);
    }

    /**
     * insert trip into database
     * @param trip
     * @param tripDestinations
     */
    public CompletionStage<Integer> insert(Trip trip, ArrayList<TripDestination> tripDestinations) {
        return supplyAsync(() -> {
            try (Transaction txn = ebeanServer.beginTransaction()) {
                ebeanServer.insert(trip);
                for (TripDestination tripDestination : tripDestinations) {
                    tripDestination.setTripId(trip.getId());
                    ebeanServer.insert(tripDestination);
                }
                txn.commit();
            }
            return trip.getId();
        }, executionContext);
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
        TreeMap<Integer, TripDestination> orderedDestiantions = new TreeMap<>();
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
            orderedDestiantions.put(tripDest.getDestOrder(), tripDest);
        }
        trip.setOrderedDestiantions(orderedDestiantions);
        return trip;
    }

}
