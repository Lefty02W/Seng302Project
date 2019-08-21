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

/**
 * Database access class for the traveller_type database table
 */
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
    public List<TreasureHunt> getAllActiveTreasureHunts(int offset) {
        return new ArrayList<> (ebeanServer.find(TreasureHunt.class)
                .setMaxRows(9)
                .setFirstRow(offset)
                .where()
                .eq("soft_delete", 0)
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
                .eq("soft_delete", 0)
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
                .eq("soft_delete", 0)
                .eq("profile_id", userId)
                .findList());
        for(TreasureHunt hunt : hunts) {
            hunt.setDestination(ebeanServer.find(Destination.class)
                .where()
                    .eq("soft_delete", 0)
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

    /**
     * sets soft delete for a treasureHunt which eather deletes it or
     * undoes the delete
     * @param huntId The ID of the treasure hunt to soft delete
     * @param softDelete Boolean, true if is to be deleted, false if cancel a delete
     * @return
     */
    public CompletionStage<Integer> setSoftDelete(int huntId, int softDelete) {
        return supplyAsync(() -> {
            try {
                TreasureHunt targetHunt = ebeanServer.find(TreasureHunt.class).setId(huntId).findOne();
                if (targetHunt != null) {
                    targetHunt.setSetSoftDelete(softDelete);
                    targetHunt.update();
                    return 1;
                } else {
                    return 0;
                }
            } catch(Exception e) {
                return 0;
            }
        }, executionContext);
    }

    /**
     * Method to get number of hunts in the system
     * used for pagination
     *
     * @return int number of hunts found
     */
    public int getNumHunts() {
        return ebeanServer.find(TreasureHunt.class).where().eq("soft_delete", 0).findCount();
    }

    /**
     * Method to get one page worth of treasure hunts
     *
     * @param offset offset of hunts to find
     * @param pageSize max amount to find
     * @return List of found hunts
     */
    public List<TreasureHunt> getPageHunts(Integer offset, int pageSize) {
        return ebeanServer.find(TreasureHunt.class)
                .setMaxRows(pageSize)
                .setFirstRow(offset)
                .where()
                .eq("soft_delete", 0)
                .findList();
    }
}
