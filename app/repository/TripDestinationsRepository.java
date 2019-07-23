package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Model;
import io.ebean.Transaction;
import models.TripDestination;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A trip destination repository that executes database operations in a different
 * execution context handles all interactions with the trip destination table .
 */
public class TripDestinationsRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public TripDestinationsRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Insert trip destination into database
     * @param tripDestination trip destination to be inserted
     * @return Id of the newly inserted trip
     */
    public CompletionStage<Integer> insert(TripDestination tripDestination) {
        return supplyAsync(() -> {
            ebeanServer.insert(tripDestination);
            return tripDestination.getTripId();
        }, executionContext);
    }

    /**
     * remove a trip destination from the database
     * @param tripDestinationId Id of the trip to be deleted
     * @return optional of the deleted trip destinations id
     */
    public CompletionStage<Optional<Integer>> delete(int tripDestinationId) {
        return supplyAsync(() -> {
            try {
                final Optional<TripDestination> tripDestOptional = Optional.ofNullable(ebeanServer.find(TripDestination.class).setId(tripDestinationId).findOne());
                tripDestOptional.ifPresent(Model::delete);
                return tripDestOptional.map(TripDestination::getTripDestinationId);
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    /**
     * returns all tripDestinations with a given destination
     * @param destinationId the id of the destination
     * @return an optional list of TripDestinations
     */
    public Optional<List<TripDestination>> getTripDestsWithDestId(Integer destinationId) {
        return Optional.of(TripDestination.find.query()
                .where()
                .eq("destination_id", destinationId)
                .findList());
    }

    /**
     * Method to update the trip destination ID inside a trip
     * called when a trip destination needs to be replaced by a new public destination
     * @param tripDestination Trip destination to be updated
     * @param newDestinationId new destination Id for the trip destination
     */
    public void editTripId(TripDestination tripDestination, Integer newDestinationId) {
        Transaction txn = ebeanServer.beginTransaction();
        try {
            TripDestination targetTripDestination = ebeanServer.find(TripDestination.class).setId(tripDestination.getTripDestinationId()).findOne();
            if (targetTripDestination != null) {
                targetTripDestination.setDestinationId(newDestinationId);
                targetTripDestination.update();
                txn.commit();
            }
        } finally {
            txn.end();
        }
    }

}