package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Model;
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


    public CompletionStage<Integer> insert(TripDestination tripDestination) {
        return supplyAsync(() -> {
            System.out.println("here");
            System.out.println(tripDestination.getDestinationId());
            System.out.println(tripDestination.getTripId());
            ebeanServer.insert(tripDestination);
            System.out.println("boi");
            return tripDestination.getTripId();
        }, executionContext);
    }
}
