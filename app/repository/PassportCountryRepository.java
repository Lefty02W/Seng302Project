package repository;

import io.ebean.*;
import models.PassportCountry;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Repository class to provide data access methods for the PassportCountry model class.
 * This class implements the ModelRepository interface
 */
public class PassportCountryRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public PassportCountryRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Method to update a passed PassportCountry in the database
     * @param passport the passport to be updated
     * @param id id of the entry to be updated.
     * @return CompletionStage holding an Optional value of the updated database id
     */
    public CompletionStage<Optional<Integer>> update(PassportCountry passport, int id) {
        return supplyAsync(() -> {
            Transaction transaction = ebeanServer.beginTransaction();
            String updateQuery = "UPDATE passport_country SET passport_name = ? WHERE passport_country_id = ?";
            Optional<Integer> value = Optional.empty();
            try {
                if(ebeanServer.find(PassportCountry.class).setId(id).findOne() != null){
                    SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
                    query.setParameter(1, passport.getPassportName());
                    query.setParameter(2, id);
                    query.execute();
                    transaction.commit();
                    value = Optional.of(passport.getPassportId());
                }
            } finally {
                transaction.end();
            }
            return value;
        }, executionContext);

    }


    /**
     * Method to delete a passed PassportCountry from the database
     * @param id id of the entry to be deleted.
     * @return CompletionStage holding an Optional holding '1' for success and empty for failure
     */
    public CompletionStage<Optional<Integer>> delete(int id) {
        return supplyAsync(() -> {
            try {
                PassportCountry.find.deleteById(Integer.toString(id));
                return Optional.of(1);
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    /**
     * Method to insert a passed PassportCountry in the database
     * @param passport the PassportCountry to delete
     * @return CompletionStage holding an Optional of the nationalities database id
     */
    public Optional<Integer> insert(PassportCountry passport) {

        ebeanServer.insert(passport);


        return Optional.of(passport.getPassportId());
    }

    /**
     * Method to find a PassportCountry by its database id
     * @param id id of the entry to be found.
     * @return CompletionStage holding an Optional of the PassportCountry if found
     */
    public Optional<PassportCountry> findById(int id) {
            return Optional.ofNullable(ebeanServer.find(PassportCountry.class).where().eq("passport_country_id", id).findOne());
    }

    /**
     * Gets the ID of a passport country based on the name sent in
     * @param country The country to find
     * @return
     */
    public Optional<Integer> getPassportCountryId(String country) {
        String sql = ("select passport_country_id from passport_country where passport_name = ?");
        List<SqlRow> rowList = ebeanServer.createSqlQuery(sql).setParameter(1, country).findList();
        Integer countryId;
        try {
            countryId = rowList.get(0).getInteger("passport_country_id");
        } catch(Exception e) {
            countryId = null;
        }
        return Optional.of(countryId);
    }


    /**
     *
     * Method to retrieve all Passports from the database
     *
     * @return CompletionStage holding an Optional of the a passport Map keyed by the database id
     */
    public CompletionStage<Optional<Map<Integer, PassportCountry>>> getAll() {
        return supplyAsync(() -> Optional.of(ebeanServer.find(PassportCountry.class).findMap()), executionContext);
    }



}
