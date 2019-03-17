package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Model;
import io.ebean.Transaction;
import models.Destination;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class                                                                                                                                                                                                                                                                                                                                                                                   DestinationRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;


    @Inject
    public DestinationRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }


    public CompletionStage<Optional<Destination>> lookup(int destID) {
        return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(Destination.class).setId(destID).findOne()), executionContext);
    }

    public CompletionStage<String> insert(Destination dest) {
        return supplyAsync(() -> {
            ebeanServer.insert(dest);
            return String.format("Destination %s added", dest.getName());
        }, executionContext);
    }


    public CompletionStage<Optional<String>> delete(int destID) {
        return supplyAsync(() -> {
            try {
                final Optional<Destination> destinationOptional = Optional.ofNullable(ebeanServer.find(Destination.class)
                        .setId(destID).findOne());
                destinationOptional.ifPresent(Model::delete);
                return Optional.of(String.format("Destination %s deleted", destinationOptional.map(p -> p.getName())));
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    public CompletionStage<Optional<String>> update(Destination newDestination, Integer Id) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<String> value = Optional.empty();
            try {
                Destination targetDestination = ebeanServer.find(Destination.class).setId(Id).findOne();
                if (targetDestination != null) {
                    targetDestination.setUserEmail(newDestination.getUserEmail());
                    targetDestination.setName(newDestination.getName());
                    targetDestination.setType(newDestination.getType());
                    targetDestination.setCountry(newDestination.getCountry());
                    targetDestination.setDistrict(newDestination.getDistrict());
                    targetDestination.setLatitude(newDestination.getLatitude());
                    targetDestination.setLongitude(newDestination.getLongitude());
                    targetDestination.update();
                    txn.commit();
                    value = Optional.of(String.format("Destination %s edited", newDestination.getDestinationId()));
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }

}
