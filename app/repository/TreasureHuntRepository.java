package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Transaction;
import models.Destination;
import models.TreasureHunt;
import org.joda.time.DateTime;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
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
     * Method to retrieve a specific treasure hunt using the id/
     * @param id Id of the treasure hunt to be found
     * @return TreasureHunt that has been found
     */
    public TreasureHunt lookup(Integer id)  {
        return ebeanServer.find(TreasureHunt.class).setId(id).findOne();
    }

    /**
     * Method to retrieve all TreasureHunts from the database which are currently active
     *
     * @return TreasureHunts, an arrayList of all currently active treasureHunts
     */
    public List<TreasureHunt> getAllActiveTreasureHunts() {
        return new ArrayList<> (ebeanServer.find(TreasureHunt.class)
                .where()
                .gt("end_date", DateTime.now())
                .lt("start_date", DateTime.now())
                .findList());
    }


    /**
     * Method to find all treasure hunts in the database
     *
     * @return list holding all found treasure hunts
     */
    public List<TreasureHunt> getAllTreasureHunts() {
        return ebeanServer.find(TreasureHunt.class)
                .where()
                .findList();
    }



    /**
     * Updates a TreasureHunt object in the database by taking in an id of an already existing treasurehunt and a new edited treasure hunt
     * @param treasureHunt New treasure hunt with edited changes
     * @param id Id of the treasureHunt to be updated
     * @return Id of the treasure hunt updated
     */
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

    /**
     * Method to return all of a users treasureHunts
     * @param userId, the id of the active user
     * @return TreasureHunts, an ArrayList of all currently active TreasureHunts
     */
    public List<TreasureHunt> getAllUserTreasureHunts(int userId) {
        List<TreasureHunt> hunts = new ArrayList<>(ebeanServer.find(TreasureHunt.class)
                .where()
                .eq("profile_id", userId)
                .findList());
        for(TreasureHunt hunt : hunts) {
            hunt.setDestination(ebeanServer.find(Destination.class)
                .where()
                .eq("destination_id", hunt.getTreasureHuntDestinationId())
                .findOne());
        }
        return hunts;
    }

    /**
     * Deletes a TreasureHunt from the database
     *
     * @param treasureHuntId id of the treasureHunt the user wishes to delete
     */
    public CompletionStage<Integer> deleteTreasureHunt(int treasureHuntId){
        return supplyAsync(() -> {
            ebeanServer.find(TreasureHunt.class).where().eq("treasureHuntId", treasureHuntId).delete();
            return 1;
        });
    }



}
