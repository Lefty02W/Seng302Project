package repository;

import controllers.ProvideApplication;
import models.Profile;
import models.UndoStack;
import org.junit.*;

import java.util.Date;
import java.util.List;


public class UndoStackRepositoryTest extends ProvideApplication {

    @Before
    public void clearStack() {
        injectRepositories();
        undoStackRepository.clearStack(1);
        undoStackRepository.clearStack(2);
    }


    /**
     * Method to get a profile and set its roles correctly
     * @param id - ID of profile to get
     * @return profile - The profile instance
     */
    private Profile getProfile(Integer id) {
        Profile profile = profileRepository.getProfileByProfileId(id);
        profile.setRoles(rolesRepository.getProfileRoles(id).get());
        return profile;
    }


    /**
     * Check a non-admin user request to clear a non-empty stack
     * does not execute
     */
    @Test
    public void nonAdminClearStack() {
        UndoStack undoDest = undoStackRepository.getStackItem(3);
        undoStackRepository.addToStack(undoDest);
        undoStackRepository.clearStackOnAllowed(getProfile(undoDest.getProfileId()));
        Assert.assertFalse(undoStackRepository.getUsersStack(undoDest.getProfileId()).isEmpty());
    }


    /**
     * Check admin request to clear a non-empty stack is executed
     */
    @Test
    public void adminClearStack() {
        UndoStack undoDest = new UndoStack("destination", 2, 2, new Date());
        undoStackRepository.addToStack(undoDest);
        undoStackRepository.clearStackOnAllowed(getProfile(2));

        Assert.assertTrue(undoStackRepository.getUsersStack(2).isEmpty());
    }


    /**
     * Check item can be added to stack
     */
    @Test
    public void addToStack() {

        UndoStack undoDest = undoStackRepository.getStackItem(2);
        undoStackRepository.addToStack(undoDest);
        Assert.assertFalse(undoStackRepository.getUsersStack(undoDest.getProfileId()).isEmpty());
    }


    /**
     * Check an item can be removed from the stack
     */
    @Test
    public void removeFromStack() {
        UndoStack undoDest = new UndoStack("destination", 3, 1, new Date());
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

        UndoStack undoDest = new UndoStack("destination", 4, 2, new Date());
        undoStackRepository.addToStack(undoDest);
        Assert.assertTrue(undoStackRepository.canClearStack(getProfile(2)));
    }


    /**
     * Check a non admin cannot clear the stack
     */
    @Test
    public void nonAdminCanClearStack() {
        UndoStack undoDest = new UndoStack("destination", 5, 1, new Date());
        undoStackRepository.addToStack(undoDest);
        Assert.assertFalse(undoStackRepository.canClearStack(getProfile(1)));
    }
}
