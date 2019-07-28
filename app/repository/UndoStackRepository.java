package repository;

import interfaces.TypesInterface;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Profile;
import models.TreasureHunt;
import models.UndoStack;
import org.joda.time.DateTime;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class UndoStackRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final ProfileRepository profileRepository;
    private final TripRepository tripRepository;
    private final DestinationRepository destinationRepository;
    private final TreasureHuntRepository treasureHuntRepository;

    @Inject
    public UndoStackRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext,
                               ProfileRepository profileRepository, TripRepository tripRepository,
                               DestinationRepository destinationRepository, TreasureHuntRepository treasureHuntRepository) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.profileRepository = profileRepository;
        this.tripRepository = tripRepository;
        this.destinationRepository = destinationRepository;
        this.treasureHuntRepository = treasureHuntRepository;
    }

    /**
     * Method to insert a soft deleted item into the undo stack
     * @param undoItem object holding the variables required for inserting item into the undoStack database table
     * @return null
     */
    public CompletionStage<Void> addToStack(UndoStack undoItem){
        return supplyAsync(() -> {
            ebeanServer.insert(undoItem);
            return null;
        }, executionContext);
    }

    /**
     * Method to check if the current user has any changes on the stack
     * @param userId id of the current user
     * @return list of current users stack
     */
    public ArrayList<UndoStack> getUsersStack(int userId){
        return new ArrayList<>(ebeanServer.find(UndoStack.class)
                .where()
                .eq("profile_id", userId)
                .findList());
    }

    /**
     * Method to remove all items from undoStack for a given user and calls for the appropriate object to be
     * hard deleted
     * @param userId id of the user who's stack will be cleared
     * @return null
     */
    public CompletionStage<Void> clearStack(int userId) {
        return supplyAsync(() -> {
            clearOutdatedRecords();
            ArrayList<UndoStack> undoStackList = getUsersStack(userId);

            for (UndoStack undoStack: undoStackList) {
                switch (undoStack.getItem_type()) {
                    case "profile":
                        profileRepository.delete(undoStack.getObjectId());
                        break;
                    case "trip":
                        tripRepository.delete(undoStack.getObjectId());
                        break;
                    case "destination":
                        destinationRepository.delete(undoStack.getObjectId());
                        break;
                    case "treasure_hunt":
                        treasureHuntRepository.deleteTreasureHunt(undoStack.getObjectId());
                        break;
                }
            }
            ebeanServer.find(UndoStack.class)
                    .where()
                    .eq("profile_id", userId)
                    .delete();
            return null;
        });
    }


    /**
     * Deletes an item from the undoStack
     * @param item the item object to be removed from the database
     * @return null
     */
    public CompletionStage<Void> removeItem(UndoStack item){
        return supplyAsync(() -> {
            ebeanServer.find(UndoStack.class)
                    .where()
                    .eq("profile_id", item.getProfileId())
                    .eq("object_id", item.getObjectId())
                    .delete();

            return null;
        });
    }


    /**
     * Undoes a soft delete for the item on top of the stack,
     * this deletes the item from the stack and,
     * sets the softDelete value to 0 for the appropriate object in the database (profile, trip...)
     * @return boolean, true if worked successfully, else false
     */
    public CompletionStage<Integer> undoItemOnTopOfStack(Integer userId) {
        return supplyAsync(() -> {
            ArrayList<UndoStack> undoStackList = getUsersStack(userId);
            if (undoStackList.size() == 0) {
                return 0;
            }
            UndoStack topOfStack = undoStackList.get(0);
            for (UndoStack item : undoStackList) {
                if (item.getEntryId() < topOfStack.getEntryId()) {
                    topOfStack = item;
                }
            }

            switch (topOfStack.getItem_type()) {
                case "profile":
                    profileRepository.setSoftDelete(topOfStack.getObjectId(), 0);
                    break;
                case "trip":
                    tripRepository.setSoftDelete(topOfStack.getObjectId(), 0);
                    break;
                case "destination":
                    destinationRepository.setSoftDelete(topOfStack.getObjectId(), 0);
                    break;
                case "treasure_hunt":
                    treasureHuntRepository.setSoftDelete(topOfStack.getObjectId(), 0);
                    break;
            }

            ebeanServer.find(UndoStack.class)
                    .where()
                    .eq("profile_id", topOfStack.getProfileId())
                    .eq("object_id", topOfStack.getObjectId())
                    .delete();

            return 1;
        }, executionContext);
    }


    /**
     * Method to check if the stack can be cleared
     * The conditions are: profile is admin/global admin AND stack is empty
     * @param profile - Profile wanting to clear stack
     * @return boolean - True if the profile can execute the operation to clear stack, false otherwise
     */
    public boolean canClearStack(Profile profile) {
        return (!getUsersStack(profile.getProfileId()).isEmpty() &&
                (profile.getRoles().contains("admin") || profile.getRoles().contains("global_admin")));
    }


    /**
     * Clear the stack if the user has permission and stack is clear
     * @param profile - Profile wanting to clear stack
     * @return null
     */
    public CompletionStage<Void> clearStackOnAllowed(Profile profile) {
        if (canClearStack(profile)) {
            clearStack(profile.getProfileId());
        }
        return null;
    }

    private void clearOutdatedRecords() {
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new DateTime().minusDays(1).toDate());
        List<UndoStack> outdatedCommands = ebeanServer.find(UndoStack.class)
                .where()
                .lt("time_created", dateString)
                .findList();
        for (UndoStack i : outdatedCommands) {
            ebeanServer.delete(i);
        }
    }

}
