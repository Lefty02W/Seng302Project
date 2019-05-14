package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Model;
import io.ebean.Transaction;
import models.TravellerTypes;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A travellerTypes repository that executes database operations in a different
 * execution context handles all interactions with the travellerType table .
 */
public class TravelerTypeRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public TravelerTypeRepository(EbeanServer ebeanServer, DatabaseExecutionContext executionContext) {
        this.ebeanServer = ebeanServer;
        this.executionContext = executionContext;
    }

    /**
     * update function to update the travellerType object using a given id
     * @param travellerTypes object of type TravellerType to be updated in the database
     * @param travellerTypeId id of the entry to be updated.
     * @return Optional completion stage holding the id of the entry that has been updated
     */
    CompletionStage<Optional<Integer>> update(TravellerTypes travellerTypes, int travellerTypeId) {
        return supplyAsync(() -> {
            Optional<Integer> value = Optional.empty();
            try (Transaction txn = ebeanServer.beginTransaction()) {
                TravellerTypes travellerTypes1 = ebeanServer.find(TravellerTypes.class).setId(travellerTypeId).findOne();
                if (travellerTypes1 != null) {
                    travellerTypes1.setTravellerTypeName(travellerTypes.getTravellerTypeName());
                    travellerTypes1.update();
                    txn.commit();
                    value = Optional.of(travellerTypes.getTravellerTypeId());
                    txn.end();
                }
            }
            return value;
        }, executionContext);
    }

    /**
     * Delete function to delete a TravellerType object using a given id
     * @param id id of the entry to be deleted.
     * @return Optional completion stage holding the id of the entry that has been deleted
     */
    CompletionStage<Optional<Integer>> delete(int id) {
        return supplyAsync(() -> {
            try {
                final Optional<TravellerTypes> travellerTypesOptional = Optional.ofNullable(ebeanServer.find(TravellerTypes.class).setId(id).findOne());
                travellerTypesOptional.ifPresent(Model::delete);
                return travellerTypesOptional.map(p -> p.getTravellerTypeId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    /**
     * Insert function to insert the TravellerType object
     * @param travellerTypes object of type TravellerTypes to be inserted in the database
     * @return Optional completion stage holding the id of the entry that has been created
     */
    public CompletionStage<Integer> insert(TravellerTypes travellerTypes) {
        return supplyAsync(() -> {
            ebeanServer.insert(travellerTypes);
            return travellerTypes.getTravellerTypeId();
        }, executionContext);
    }

    /**
     * Select function to find a TravellerType object using the given id
     * @param id id of the entry to be found.
     * @return Optional completion stage holding the object of type T found using the given id.
     */
    CompletionStage<Optional<TravellerTypes>> findById(int id) {
        return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(TravellerTypes.class).setId(id).findOne()), executionContext);
    }
}
