package repository;

import interfaces.TypesInterface;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Profile;
import models.TreasureHunt;
import models.UndoStack;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
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
            ArrayList<UndoStack> undoStackList = getUsersStack(userId);

            for (UndoStack undoStack: undoStackList) {
                hardDeleteObject(undoStack);
            }
            ebeanServer.find(UndoStack.class)
                    .where()
                    .eq("profile_id", userId)
                    .delete();
            return null;
        });
    }

    /**
     * Deletes an item from the database
     * @param item the item object to be removed from the database
     * @return null
     */
    public CompletionStage<Void> removeItem(UndoStack item){
        return supplyAsync(() -> {
            UndoStack undoStack = ebeanServer.find(UndoStack.class)
                    .where()
                    .eq("profile_id", item.getProfileId())
                    .eq("object_id", item.getObjectId())
                    .findOne();

            hardDeleteObject(undoStack);

            return null;
        });
    }


    /**
     * takes a undoStack object and then calls the apppropriate repo to delete the object that undoStack represents
     * @param undoStack which represents the appropriate object to be deleted
     */
    private void hardDeleteObject(UndoStack undoStack) {
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


    /**
     * Method to check if the stack can be cleared
     * The conditions are: profile is admin/global admin AND stack is empty
     * @param profile - Profile wanting to clear stack
     * @return boolean - True if the profile can execute the operation to clear stack, false otherwise
     */
    public boolean canClearStack(Profile profile) {
        return getUsersStack(profile.getProfileId()).isEmpty() &&
                (profile.getRoles().contains("admin") || profile.getRoles().contains("global_admin"));
    }


    /**
     * Clear the stack if the user has permisison and stack is clearable
     * @param profile - Profile wanting to clear stack
     * @return null
     */
    public CompletionStage<Void> clearStackOnAllowed(Profile profile) {
        if (canClearStack(profile)) {
            clearStack(profile.getProfileId());
        }
        return null;
    }

}
