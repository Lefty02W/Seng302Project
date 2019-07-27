package repository;

import controllers.ProvideApplication;
import models.Profile;
import models.UndoStack;
import org.junit.Assert;
import org.junit.Test;

public class UndoStackRepositoryTest extends ProvideApplication {


    /**
     * Method to get a profile and set its roles correctly
     * @param id - ID of profile to get
     * @return profile - The profile instance
     */
    private Profile getProfile(Integer id) {
        Profile profile = profileRepository.getProfileByProfileId(1);
        profile.setRoles(rolesRepository.getProfileRoles(1).get());
        return profile;
    }


    /**
     * Check a non-admin user request to clear a non-empty stack
     * does not execute
     */
    @Test
    public void nonAdminClearStack() {
        injectRepositories();

        UndoStack undoDest = new UndoStack("destination", 1, 1);
        undoStackRepository.addToStack(undoDest);
        undoStackRepository.clearStackOnAllowed(getProfile(1));
        Assert.assertFalse(undoStackRepository.getUsersStack(1).isEmpty());
    }


    /**
     * Check admin request to clear a non-empty stack is executed
     */
    @Test
    public void adminClearStack() {
        injectRepositories();

        UndoStack undoDest = new UndoStack("destination", 1, 2);
        undoStackRepository.addToStack(undoDest);
        undoStackRepository.clearStackOnAllowed(getProfile(2));
        Assert.assertTrue(undoStackRepository.getUsersStack(2).isEmpty());
    }


    /**
     * Check item can be added to stack
     */
    @Test
    public void addToStack() {
        injectRepositories();

        UndoStack undoDest = new UndoStack("destination", 1, 1);
        undoStackRepository.addToStack(undoDest);
        Assert.assertFalse(undoStackRepository.getUsersStack(undoDest.getProfileId()).isEmpty());
    }


    /**
     * Check an item can be removed from the stack
     */
    @Test
    public void removeFromStack() {
        injectRepositories();

        UndoStack undoDest = new UndoStack("destination", 1, 1);
        undoStackRepository.addToStack(undoDest);
        undoStackRepository.removeItem(undoDest);
        Assert.assertTrue(undoStackRepository.getUsersStack(undoDest.getProfileId()).isEmpty());
    }


    /**
     * Check an admin can clear the stack
     */
    @Test
    public void adminCanClearStack() {
        injectRepositories();

        UndoStack undoDest = new UndoStack("destination", 1, 2);
        undoStackRepository.addToStack(undoDest);
        Assert.assertTrue(undoStackRepository.canClearStack(getProfile(2)));
    }


    /**
     * Check a non admin cannot clear the stack
     */
    @Test
    public void nonAdminCanClearStack() {
        injectRepositories();

        UndoStack undoDest = new UndoStack("destination", 1, 1);
        undoStackRepository.addToStack(undoDest);
        Assert.assertTrue(undoStackRepository.canClearStack(getProfile(1)));
    }
}
