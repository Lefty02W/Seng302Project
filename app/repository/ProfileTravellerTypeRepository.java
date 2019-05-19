package repository;

import io.ebean.*;
import models.Nationality;
import models.PassportCountry;
import models.TravellerType;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import static java.util.concurrent.CompletableFuture.supplyAsync;

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
    public Optional<Integer> insertProfileTravellerType(TravellerType travellerType, Integer profileId) {
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
}
