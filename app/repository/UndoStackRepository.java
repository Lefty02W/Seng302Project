package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Profile;
import models.UndoStack;
import org.joda.time.DateTime;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Database access class for the undo_stack database table
 */
public class UndoStackRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final ProfileRepository profileRepository;
    private final TripRepository tripRepository;
    private final DestinationRepository destinationRepository;
    private final TreasureHuntRepository treasureHuntRepository;
    private final ArtistRepository artistRepository;
    private final EventRepository eventRepository;

    @Inject
    public UndoStackRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext,
                               ProfileRepository profileRepository, TripRepository tripRepository,
                               DestinationRepository destinationRepository, TreasureHuntRepository treasureHuntRepository,
                               ArtistRepository artistRepository, EventRepository eventRepository) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.profileRepository = profileRepository;
        this.tripRepository = tripRepository;
        this.destinationRepository = destinationRepository;
        this.treasureHuntRepository = treasureHuntRepository;
        this.artistRepository = artistRepository;
        this.eventRepository = eventRepository;
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


    public UndoStack getStackItem(int id) {
        return ebeanServer.find(UndoStack.class).where().eq("entry_id", id).findOne();
    }

    /**
     * Method to check if the current user has any changes on the stack
     * @param userId id of the current user
     * @return list of current users stack
     */
    public List<UndoStack> getUsersStack(int userId){
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
            List<UndoStack> undoStackList = getUsersStack(userId);

            for (UndoStack undoStack: undoStackList) {
                processStackItem(undoStack);
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
    CompletionStage<Void> removeItem(UndoStack item){
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
            List<UndoStack> undoStackList = getUsersStack(userId);
            if (undoStackList.isEmpty()) {
                return 0;
            }
            UndoStack topOfStack = undoStackList.get(0);
            for (UndoStack item : undoStackList) {
                if (item.getEntryId() > topOfStack.getEntryId()) {
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
                case "artist":
                    artistRepository.setSoftDelete(topOfStack.getObjectId(), 0);
                    break;
                case "event":
                    eventRepository.setSoftDeleteId(topOfStack.getObjectId(), 0);
                    break;
                default:
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
        return (!getUsersStack(profile.getProfileId()).isEmpty());
    }


    /**
     * Clear the stack if the user has permission and stack is clear
     * @param profile - Profile wanting to clear stack
     */
    public void clearStackOnAllowed(Profile profile) {
        if ((profile.getRoles().contains("admin") || profile.getRoles().contains("global_admin"))) {
            if (canClearStack(profile)) {
                clearStack(profile.getProfileId());
            }
        }
    }

    /**
     * Checks for any commands in the stack that are over a day old
     * Any commands that are found are executed and removed from the stack
     *
     */
    private void clearOutdatedRecords() {
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new DateTime().minusDays(1).toDate());
        List<UndoStack> outdatedCommands = ebeanServer.find(UndoStack.class)
                .where()
                .lt("time_created", dateString)
                .findList();

        for (UndoStack i : outdatedCommands) {
            ebeanServer.delete(i);
            processStackItem(i);
        }
    }


    /**
     * Helper function to process and item off the undo stack
     *
     * @param command the command to be processed
     */
    private void processStackItem(UndoStack command) {
        switch (command.getItem_type()) {
            case "profile":
                profileRepository.delete(command.getObjectId());
                break;
            case "trip":
                tripRepository.delete(command.getObjectId());
                break;
            case "destination":
                destinationRepository.delete(command.getObjectId());
                break;
            case "treasure_hunt":
                treasureHuntRepository.deleteTreasureHunt(command.getObjectId());
                break;
            case "artist":
                artistRepository.deleteArtist(command.getObjectId());
                break;
            default:
                break;
        }
    }

}
