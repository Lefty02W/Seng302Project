package repository;

import io.ebean.*;
import models.Nationality;
import models.PassportCountry;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class ProfileNationalityRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final NationalityRepository nationalityRepository;

    @Inject
    public ProfileNationalityRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.nationalityRepository = new NationalityRepository(ebeanConfig, executionContext);
    }

    /**
     * Inserts a nationality into the profile passport country linking table
     * @param nationality The nationality to add
     * @return
     */
    public Optional<Integer> insertProfileNationality(Nationality nationality, Integer profileId) {
        Integer idOpt;
        try {
            idOpt = nationalityRepository.getNationalityId(nationality.getNationalityName()).get();
        } catch(Exception e) {
            idOpt = null;
        }
        Integer nationalityId;
        if (idOpt == null) {
            nationalityId = nationalityRepository.insert(nationality).get();
        } else {
            nationalityId = idOpt;
        }
        Transaction txn = ebeanServer.beginTransaction();
        String qry = "INSERT into profile_nationality (profile, nationality) " +
                "VALUES (?, ?)";
        try {
            SqlUpdate query = Ebean.createSqlUpdate(qry);
            query.setParameter(1, profileId);
            query.setParameter(2, nationalityId);
            query.execute();
            txn.commit();
        } finally {
            txn.end();
        }
        return Optional.of(nationalityId);
    }
}
