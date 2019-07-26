package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.UndoStack;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class UndoStackRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public UndoStackRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
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
     * Method to remove all items from undoStack for a given user
     * @param userId id of the user who's stack will be cleared
     * @return null
     */
    public CompletionStage<Void> clearStack(int userId){
        // TODO: 26/07/19 test that this deletes all items and not just the first 
        return supplyAsync(() -> {
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
            ebeanServer.find(UndoStack.class)
                    .where()
                    .eq("profile_id", item.getProfileId())
                    .eq("object_id", item.getObjectId())
                    .delete();
            return null;
        });
    }



}
