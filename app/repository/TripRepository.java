package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;


public class TripRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public TripRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }
    /**
    public CompletionStage<String> insert(Trip trip) {

    }

    public CompletionStage<Optional<String>> update(Trip trip, int tripId){

    }

    public CompletionStage<Optional<String>> delete(int tripId) {

    }
    */
}
