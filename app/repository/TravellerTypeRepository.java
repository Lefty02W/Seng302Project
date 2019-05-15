import io.ebean.EbeanServer;
import io.ebean.Model;
import io.ebean.Transaction;
import models.TravellerType;
import repository.DatabaseExecutionContext;
import repository.ModelRepository;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A travellerTypes repository that executes database operations in a different
 * execution context handles all interactions with the travellerType table .
 */
public class TravellerTypeRepository implements ModelRepository<TravellerType> {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public TravellerTypeRepository(EbeanServer ebeanServer, DatabaseExecutionContext executionContext) {
        this.ebeanServer = ebeanServer;
        this.executionContext = executionContext;
    }

    /**
     * update function to update the travellerTypes object using a given id
     * @param travellerTypes object of type TravellerTypes to be updated in the database
     * @param travellerTypeId id of the entry to be updated.
     * @return Optional completion stage holding the id of the entry that has been updated
     */
    public CompletionStage<Optional<Integer>> update(TravellerType travellerTypes, int travellerTypeId) {
        return supplyAsync(() -> {
            Optional<Integer> value = Optional.empty();
            try (Transaction txn = ebeanServer.beginTransaction()) {
                TravellerType travellerTypes1 = ebeanServer.find(TravellerType.class).setId(travellerTypeId).findOne();
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
     * Delete function to delete a TravellerTypes object using a given id
     * @param id id of the entry to be deleted.
     * @return Optional completion stage holding the id of the entry that has been deleted
     */
    public CompletionStage<Optional<Integer>> delete(int id) {
        return supplyAsync(() -> {
            try {
                final Optional<TravellerType> travellerTypesOptional = Optional.ofNullable(ebeanServer.find(TravellerTypes.class).setId(id).findOne());
                travellerTypesOptional.ifPresent(Model::delete);
                return travellerTypesOptional.map(p -> p.getTravellerTypeId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    /**
     * Insert function to insert the TravellerTypes object
     * @param travellerTypes object of type TravellerTypes to be inserted in the database
     * @return Optional completion stage holding the id of the entry that has been created
     */
    public CompletionStage<Optional<Integer>> insert(TravellerType travellerTypes) {
        return supplyAsync(() -> {
            ebeanServer.insert(travellerTypes);
            return Optional.of(travellerTypes.getTravellerTypeId());
        }, executionContext);
    }

    /**
     * Select function to find a TravellerTypes object using the given id
     * @param id id of the entry to be found.
     * @return Optional completion stage holding the object of type T found using the given id.
     */
    public CompletionStage<Optional<TravellerType>> findById(int id) {
        return supplyAsync(() ->
                Optional.ofNullable(ebeanServer.find(TravellerType.class).where()
                        .eq("traveller_type_id", id).findOne()), executionContext);
    }

    /**
     * Method to retrieve all TravellerTypes from the database
     *
     * @return CompletionStage holding an Optional of the a travellerType Map keyed by the database id
     */
    public CompletionStage<Optional<Map<Integer, TravellerType>>> getAll() {
        return supplyAsync(() -> Optional.of(ebeanServer.find(TravellerType.class).findMap()), executionContext);
    }
}