package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Trip;
import play.db.ebean.EbeanConfig;
import scala.None;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

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

    public CompletionStage<Optional<String>> delete(int tripID) {

    }*/

}
