package repository;

import io.ebean.*;
import models.PassportCountry;
import models.TravellerType;
import models.TravellerType;

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
     * Delete function to delete a TravellerType object using a given id
     * @param id id of the entry to be deleted.
     * @return Optional completion stage holding the id of the entry that has been deleted
     */
    public CompletionStage<Optional<Integer>> delete(int id) {
        return supplyAsync(() -> {
            try {
                final Optional<TravellerType> travellerTypesOptional = Optional.ofNullable(ebeanServer.find(TravellerType.class).setId(id).findOne());
                travellerTypesOptional.ifPresent(Model::delete);
                return travellerTypesOptional.map(TravellerType::getTravellerTypeId);
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
    public CompletionStage<Optional<Integer>> insert(TravellerType travellerTypes) {
        return supplyAsync(() -> {
            ebeanServer.insert(travellerTypes);
            return Optional.of(travellerTypes.getTravellerTypeId());
        }, executionContext);
    }

    /**
     * Select function to find a TravellerType object using the given id
     * @param id id of the entry to be found.
     * @return Optional completion stage holding the object of type T found using the given id.
     */
    public CompletionStage<Optional<TravellerType>> findById(int id) {
        return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(TravellerType.class).setId(id).findOne()), executionContext);
    }


    /**
     * Method to retrieve all TravellerTypes from the database
     *
     * @return CompletionStage holding an Optional of the a TravellerTypes Map keyed by the database id
     */
    public Optional<Map<Integer, TravellerType>> getAll() {
        return Optional.of(ebeanServer.find(TravellerType.class).findMap());
    }

}
