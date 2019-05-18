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
     * insert trip destination into database
     * @param tripDestination
     * @return
     */
    public CompletionStage<Integer> insert(TripDestination tripDestination) {
        return supplyAsync(() -> {
            ebeanServer.insert(tripDestination);
            return tripDestination.getTripId();
        }, executionContext);
    }

    public CompletionStage<Integer> editTrip(TripDestination tripDestination, int tripDestinationId) {
        return supplyAsync(() -> {
            try (Transaction txn = ebeanServer.beginTransaction()) {
                TripDestination tripDestEdit = ebeanServer.find(TripDestination.class).setId(tripDestinationId).findOne();
                if (tripDestEdit != null) {
                    tripDestEdit.setArrival(tripDestination.getArrival());
                    tripDestEdit.setDeparture(tripDestination.getDeparture());
                    tripDestEdit.setDestination(tripDestination.getDestination());
                    tripDestEdit.setDestinationId(tripDestination.getDestinationId());
                    tripDestEdit.setDestOrder(tripDestination.getDestOrder());
                    //tripDestEdit.setTripDestinationId(tripDestination.getTripDestinationId());
                    tripDestEdit.setTripId(tripDestination.getTripId());
                    tripDestEdit.update();
                }
                txn.commit();
            }
            return tripDestination.getTripDestinationId();
        }, executionContext);
    }

    /**
     * delete tripdestination from database
     * @param tripDestinationId
     * @return
     */
    public CompletionStage<Optional<Integer>> delete(int tripDestinationId) {
        return supplyAsync(() -> {
            try {
                final Optional<TripDestination> tripDestOptional = Optional.ofNullable(ebeanServer.find(TripDestination.class).setId(tripDestinationId).findOne());
                tripDestOptional.ifPresent(Model::delete);
                return tripDestOptional.map(p -> p.getTripDestinationId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    /**
     * Method to check if a passed destination to be delete is within a trip in the database
     *
     * @param destinationId the id of the destination to check
     * @return the result of the check with and optional id list of the trips which contain the destination within a completion stage
     */
    public CompletionStage<Optional<List<Integer>>> checkDestinationExists(int destinationId) {
        return supplyAsync(
            () -> {
              List<Integer> foundIds =
                  ebeanServer
                      .find(TripDestination.class)
                      .where()
                      .eq("destination_id", destinationId)
                      .select("tripId")
                      .findSingleAttributeList();
              if (foundIds.isEmpty()) return Optional.empty();
              else return Optional.of(foundIds);
            },
            executionContext);
    }

}
