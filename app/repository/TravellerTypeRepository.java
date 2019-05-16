package repository;

import io.ebean.*;
import models.PassportCountry;
import models.TravellerType;
import models.TravellerType;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A travellerTypes repository that executes database operations in a different
 * execution context handles all interactions with the travellerType table .
 */
public class TravellerTypeRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public TravellerTypeRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
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
    public Optional<Integer> insert(TravellerType travellerTypes) {
        ebeanServer.insert(travellerTypes);
        return Optional.of(travellerTypes.getTravellerTypeId());
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
     * Gets the ID of a passport country based on the name sent in
     * @param traveller The country to find
     * @return
     */
    public Optional<Integer> getTravellerTypeId(String traveller) {
        String sql = ("select traveller_type_id from traveller_type where traveller_type_name = ?");
        List<SqlRow> rowList = ebeanServer.createSqlQuery(sql).setParameter(1, traveller).findList();
        Integer travellerId;
        try {
            travellerId = rowList.get(0).getInteger("traveller_type_id");
        } catch(Exception e) {
            travellerId = null;
        }
        return Optional.of(travellerId);
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
