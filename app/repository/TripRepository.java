package repository;

import io.ebean.*;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Trip;
import play.db.ebean.EbeanConfig;
import scala.None;
import javax.inject.Inject;
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

}
