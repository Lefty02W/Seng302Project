package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Transaction;
import models.TreasureHunt;
import models.TreasureHunt;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class TreasureHuntRepository {


    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public TreasureHuntRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Inserts an TreasureHunt object into the ebean database server
     *
     * @param treasureHunt TreasureHunt object to insert into the database
     * @return the TreasureHunt id
     */
    public CompletionStage<Integer> insert(TreasureHunt treasureHunt){
        return supplyAsync(() -> {
            ebeanServer.insert(treasureHunt);
            return treasureHunt.getTreasureHuntId();
        }, executionContext);
    }




    public CompletionStage<Optional<Integer>> update(TreasureHunt treasureHunt, Integer id) {
        treasureHunt.setTreasureHuntId(id);
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Integer> value = Optional.empty();
            try {
                TreasureHunt targetHunt = ebeanServer.find(TreasureHunt.class).setId(id).findOne();
                if (targetHunt != null) {
                 targetHunt = treasureHunt;
                 targetHunt.update();
                 txn.commit();
                 value = Optional.of(id);
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }
}
