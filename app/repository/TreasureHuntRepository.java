package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.TreasureHunt;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
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


    /**
     * Deletes a TreasureHunt from the database
     *
     * @param treasureHuntId id of the treasureHunt the user wishes to delete
     */
    public CompletionStage<Integer> deleteTreasureHunt(int treasureHuntId, Integer userId){
        return supplyAsync(() -> {
            ebeanServer.find(TreasureHunt.class).where().eq("treasureHuntId", treasureHuntId).eq("user_id", userId).delete();
            return 1;
        });
    }



}
