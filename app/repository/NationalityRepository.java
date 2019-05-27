package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlRow;
import models.Nationality;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Repository class to provide data access methods for the Nationality model class.
 * This class implements the ModelRepository interface
 */
public class NationalityRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public NationalityRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Method to delete a passed Nationality from the database
     * @param id id of the entry to be deleted.
     * @return CompletionStage holding an Optional holding '1' for success and empty for failure
     */
    public CompletionStage<Optional<Integer>> delete(int id) {
        return supplyAsync(() -> {
            try {
                Nationality.find.deleteById(Integer.toString(id));
                return Optional.of(1);
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    /**
     * Method to insert a passed Nationality in the database
     * @param nationality the Nationality to delete
     * @return CompletionStage holding an Optional of the nationalities database id
     */
    public Optional<Integer> insert(Nationality nationality) {

        ebeanServer.insert(nationality);


        return Optional.of(nationality.getNationalityId());

    }

    /**
     * Method to find a Nationality by its database id
     * @param id id of the entry to be found.
     * @return CompletionStage holding an Optional of the Nationality if found
     */
    public Optional<Nationality> findById(int id) {
        return Optional.ofNullable(ebeanServer.find(Nationality.class).where().eq("nationality_id", id).findOne());
    }

    /**
     * Gets the ID of a passport country based on the name sent in
     * @param nationality The nationality to find
     * @return
     */
    public Optional<Integer> getNationalityId(String nationality) {
        String sql = ("select nationality_id from nationality where nationality_name = ?");
        List<SqlRow> rowList = ebeanServer.createSqlQuery(sql).setParameter(1, nationality).findList();
        Integer nationalityId;
        try {
            nationalityId = rowList.get(0).getInteger("nationality_id");
        } catch(Exception e) {
            nationalityId = null;
        }
        return Optional.ofNullable(nationalityId);
    }

    /**
     * Method to retrieve all Nationalities from the database
     *
     * @return CompletionStage holding an Optional of the a nationality Map keyed by the database id
     */
    public Optional<Map<Integer, Nationality>> getAll() {
        return Optional.of(ebeanServer.find(Nationality.class).findMap());
    }



}
