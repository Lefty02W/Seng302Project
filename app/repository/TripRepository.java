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
    private final DestinationRepository destinationRepository;

    @Inject
    public TripRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext, TripDestinationsRepository tripDestinationsRepository, DestinationRepository destinationRepository) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.tripDestinationsRepository = tripDestinationsRepository;
        this.destinationRepository = destinationRepository;
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
    ebeanServer.insert(trip);
    for (TripDestination tripDestination : tripDestinations) {
            tripDestination.setTripId(trip.getId());
            Destination dest = destinationRepository.lookup(tripDestination.getDestinationId());
            if(dest.getVisible() == 1 && !dest.getUserEmail().equals("admin@admin.com")) {
                makeAdmin(dest);
            }
            ebeanServer.insert(tripDestination);

            }
    }

    private void makeAdmin(Destination destination) {
        destinationRepository.followDesination(destination.getDestinationId(), destination.getUserEmail());
        Destination targetDestination = ebeanServer.find(Destination.class).setId(destination.getDestinationId()).findOne();
        //TODO set as default admin
        targetDestination.setUserEmail("admin@admin.com");
        targetDestination.update();
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
