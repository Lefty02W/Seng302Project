package repository;

import io.ebean.*;
import models.TravellerType;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class DestinationTravellerTypeRepository {

    private final EbeanServer ebeanServer;
    private final TravellerTypeRepository travellerTypeRepository;

    @Inject
    public DestinationTravellerTypeRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.travellerTypeRepository = new TravellerTypeRepository(ebeanConfig, executionContext);
    }

    /**
     * Inserts a nationality into the profile passport country linking table
     * @param travellerType The nationality to add
     * @return
     */
    public Optional<Integer> insertDestinationTravellerType(TravellerType travellerType, Integer destinationId) {
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
        String qry = "INSERT into destination_traveller_type (destination_id, traveller_type_id) " +
                "VALUES (?, ?)";
        try {
            SqlUpdate query = Ebean.createSqlUpdate(qry);
            query.setParameter(1, destinationId);
            query.setParameter(2, travellerId);
            query.execute();
            txn.commit();
        } finally {
            txn.end();
        }
        return Optional.of(travellerId);
    }

    /**
     * Gets a list of the destinations travelers types
     * @param destinationId The given destination ID
     * @return
     */
    public Optional<Map<Integer, TravellerType>> getDestinationTravellerList(Integer destinationId) {
        String qry = "Select * from destination_traveller_type where profile = ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(qry).setParameter(1, destinationId).findList();
        Map<Integer, TravellerType> travellerTypeList = new TreeMap<>();
        for (SqlRow aRowList : rowList) {
            Optional<TravellerType> typeOp = travellerTypeRepository.findById(aRowList.getInteger("traveller_type"));
            typeOp.ifPresent(travellerType -> travellerTypeList.put(aRowList.getInteger("traveller_type"), travellerType));
        }
        return Optional.of(travellerTypeList);
    }

    /**
     * Removes all of the traveller type linking rows corresponding to the sent in destination
     * @param destinationId The given destination ID
     */
    public void removeAll(Integer destinationId) {
        Transaction txn = ebeanServer.beginTransaction();
        String qry = "DELETE from destination_traveller_type where destination_id " +
                "= ?";
        try {
            SqlUpdate query = Ebean.createSqlUpdate(qry);
            query.setParameter(1, destinationId);
            query.execute();
            txn.commit();
        } finally {
            txn.end();
        }
    }
}
