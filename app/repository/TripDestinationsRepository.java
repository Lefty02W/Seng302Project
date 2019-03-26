package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Model;
import io.ebean.Transaction;
import models.Destination;
import models.Profile;
import models.Trip;
import models.TripDestination;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

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

}
