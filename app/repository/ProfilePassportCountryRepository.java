package repository;

import io.ebean.*;
import models.PassportCountry;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * This class provides database access methods for profile passport countries
 */
public class ProfilePassportCountryRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final PassportCountryRepository passportCountryRepository;

    @Inject
    public ProfilePassportCountryRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.passportCountryRepository = new PassportCountryRepository(ebeanConfig, executionContext);
    }

    /**
     * Inserts a passport into the profile passport country linking table
     * @param passport The passport to add
     * @return
     */
    public void insertProfilePassportCountry(PassportCountry passport, Integer profileId) {
        Integer idOpt;
        try {
            idOpt = passportCountryRepository.getPassportCountryId(passport.getPassportName()).get();
        } catch(Exception e) {
           idOpt = null;
        }
        if (idOpt == -1) {
            passportCountryRepository.insert(passport).thenApplyAsync(id -> {
                id.ifPresent(integer -> insertPassportCountry(profileId, integer));
                return null;
            });
        } else {
            insertPassportCountry(profileId, idOpt);
        }
    }


    /**
     * Helper method to perform insert of profile passport country
     * @param profileId profile id
     * @param id passport country id
     */
    private void insertPassportCountry(int profileId, int  id) {
        Transaction txn = ebeanServer.beginTransaction();
        String qry = "INSERT into profile_passport_country (profile, passport_country) " +
                "VALUES (?, ?)";
        try {
            SqlUpdate query = Ebean.createSqlUpdate(qry);
            query.setParameter(1, profileId);
            query.setParameter(2, id);
            query.execute();
            txn.commit();
        } finally {
            txn.end();
        }
    }

    /**
     * Gets a list of the users passport countries
     * @param profileId The given user ID
     * @return
     */
    Optional<Map<Integer, PassportCountry>> getList(Integer profileId){
        String qry = "Select * from profile_passport_country where profile = ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(qry).setParameter(1, profileId).findList();
        Map<Integer, PassportCountry> passportList = new TreeMap<>();
        for (SqlRow aRowList : rowList) {
            passportList.put(aRowList.getInteger("passport_country"), passportCountryRepository.findById(aRowList.getInteger("passport_country")).get());
        }
        return Optional.of(passportList);
    }

    /**
     * Removes all of the passport country linking rows corresponding to the sent in user
     * @param profileId The given user ID
     */
    void removeAll(Integer profileId) {
        Transaction txn = ebeanServer.beginTransaction();
        String qry = "DELETE from profile_passport_country where profile " +
                "= ?";
        try {
            SqlUpdate query = Ebean.createSqlUpdate(qry);
            query.setParameter(1, profileId);
            query.execute();
            txn.commit();
        } finally {
            txn.end();
        }
    }
}
