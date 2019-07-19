package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.TreasureHunt;
import org.joda.time.DateTime;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.ArrayList;

public class TreasureHuntRepository {


    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public TreasureHuntRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
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
     * Method to return all of a users treasureHunts
     * @param userId, the id of the active user
     * @return TreasureHunts, an ArrayList of all currently active TreasureHunts
     */
    public List<TreasureHunt> getAllUserTreasureHunts(int userId) {
        return new ArrayList<>(ebeanServer.find(TreasureHunt.class)
                .where()
                .eq("user_id", userId)
                .findList());
    }
}
