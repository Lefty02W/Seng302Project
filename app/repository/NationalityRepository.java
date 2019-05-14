package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlUpdate;
import io.ebean.Transaction;
import models.Nationality;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Repository class to provide data access methods for the Nationality model class.
 * This class implements the ModelRepository interface
 */
public class NationalityRepository implements ModelRepository<Nationality> {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public NationalityRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Method to update a passed Nationality in the database
     * @param nationality the nationality to be updated
     * @param id id of the entry to be updated.
     * @return CompletionStage holding an Optional value of the updated database id
     */
    public CompletionStage<Optional<Integer>> update(Nationality nationality, int id) {
        return supplyAsync(() -> {
            Transaction transaction = ebeanServer.beginTransaction();
            String updateQuery = "UPDATE nationality SET nationality_name = ? WHERE nationality_id = ?";
            Optional<Integer> value = Optional.empty();
            try {
                if(ebeanServer.find(Nationality.class).setId(id).findOne() != null){
                    SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
                    query.setParameter(1, nationality.getNationalityName());
                    query.setParameter(2, id);
                    query.execute();
                    transaction.commit();
                    value = Optional.of(nationality.getNationalityId());
                }
            } finally {
                transaction.end();
            }
            return value;
        }, executionContext);

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
    public CompletionStage<Optional<Integer>> insert(Nationality nationality) {
        return supplyAsync(() -> {
            try {
                ebeanServer.insert(nationality);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return Optional.of(nationality.getNationalityId());
        }, executionContext);
    }

    /**
     * Method to find a Nationality by its database id
     * @param id id of the entry to be found.
     * @return CompletionStage holding an Optional of the Nationality if found
     */
    public CompletionStage<Optional<Nationality>> findById(int id) {
        return supplyAsync(() ->
                 Optional.ofNullable(ebeanServer.find(Nationality.class).where()
                .eq("nationality_id", id).findOne()), executionContext);
    }



}
