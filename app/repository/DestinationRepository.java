package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Model;
import io.ebean.Transaction;
import models.Destination;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;


/**
 * A destination repository that executes database operations in a different
 * execution context handles all interactions with the destination table .
 */
public class DestinationRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    /**
     * A Constructor which links to the ebeans database
     * @param ebeanConfig
     * @param executionContext
     */
    @Inject
    public DestinationRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Returns a specific destination
     * @param destID The ID of the destination to return
     * @return
     */
    public CompletionStage<Optional<Destination>> lookup(int destID) {
        return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(Destination.class).setId(destID).findOne()), executionContext);
    }

    /**
     * Get the users destination list
     * @param email
     * @return destinations, list of all user destination
     */
    public ArrayList<Destination> getUserDestinations(String email) {
        ArrayList<Destination> destinations = new ArrayList<>(Destination.find.query()
                .where()
                .eq("user_email", email)
                .findList());
        return destinations;
    }

    /**
     * Inserts a new destination to the database.
     * @param dest The destination to insert
     * @return
     */
    public CompletionStage<String> insert(Destination dest) {
        return supplyAsync(() -> {
            ebeanServer.insert(dest);
            return String.format("Destination %s added", dest.getName());
        }, executionContext);
    }

    /**
     * Deletes a destination from the database
     * @param destID The ID of the destination to delete
     * @return
     */
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

    /**
     * Updates a destination in the database
     * @param newDestination The new info to change the destination to
     * @param Id The ID of the destination to editDestinations
     * @return
     */
    public CompletionStage<Optional<String>> update(Destination newDestination, Integer Id) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<String> value = Optional.empty();
            try {
                Destination targetDestination = ebeanServer.find(Destination.class).setId(Id).findOne();
                if (targetDestination != null) {
                    targetDestination.setName(newDestination.getName());
                    targetDestination.setType(newDestination.getType());
                    targetDestination.setCountry(newDestination.getCountry());
                    targetDestination.setDistrict(newDestination.getDistrict());
                    targetDestination.setLatitude(newDestination.getLatitude());
                    targetDestination.setLongitude(newDestination.getLongitude());
                    targetDestination.setVisible(newDestination.getVisible());
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
