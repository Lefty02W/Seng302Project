package repository;

import io.ebean.*;
import models.TravellerType;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Database access class for the profile_traveller_type database table
 */
public class ProfileTravellerTypeRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final TravellerTypeRepository travellerTypeRepository;

    @Inject
    public ProfileTravellerTypeRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.travellerTypeRepository = new TravellerTypeRepository(ebeanConfig, executionContext);
    }

    /**
     * Inserts a nationality into the profile passport country linking table
     * @param travellerType The nationality to add
     * @return
     */
    Optional<Integer> insertProfileTravellerType(TravellerType travellerType, Integer profileId) {
        Integer idOpt;
        try {
            idOpt = travellerTypeRepository.getTravellerTypeId(travellerType.getTravellerTypeName()).get();
        } catch(Exception e) {
            idOpt = null;
        }
        Integer travellerId;
        if (idOpt == null) {
            travellerId = travellerTypeRepository.insert(travellerType).get();
        } else {
            travellerId = idOpt;
        }
        Transaction txn = ebeanServer.beginTransaction();
        String qry = "INSERT into profile_traveller_type (profile, traveller_type) " +
                "VALUES (?, ?)";
        try {
            SqlUpdate query = Ebean.createSqlUpdate(qry);
            query.setParameter(1, profileId);
            query.setParameter(2, travellerId);
            query.execute();
            txn.commit();
        } finally {
            txn.end();
        }
        return Optional.of(travellerId);
    }

    /**
     * Gets a list of the users travelers types
     * @param profileId The given user ID
     * @return
     */
    Optional<Map<Integer, TravellerType>> getList(Integer profileId) {
        String qry = "Select * from profile_traveller_type where profile = ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(qry).setParameter(1, profileId).findList();
        Map<Integer, TravellerType> travellerTypeList = new TreeMap<>();
        for (SqlRow aRowList : rowList) {
            Optional<TravellerType> typeOp = travellerTypeRepository.findById(aRowList.getInteger("traveller_type"));
            typeOp.ifPresent(travellerType -> travellerTypeList.put(aRowList.getInteger("traveller_type"), travellerType));
        }
        return Optional.of(travellerTypeList);
    }

    /**
     * Removes all of the traveller type linking rows corresponding to the sent in user
     * @param profileId The given user ID
     */
    void removeAll(Integer profileId) {
        Transaction txn = ebeanServer.beginTransaction();
        String qry = "DELETE from profile_traveller_type where profile " +
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
