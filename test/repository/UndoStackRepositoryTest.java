package repository;

import controllers.TestApplication;
import models.Profile;
import models.UndoStack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class UndoStackRepositoryTest {

    @Before
    public void clearStack() {
        TestApplication.getUndoStackRepository().clearStack(1);
        TestApplication.getUndoStackRepository().clearStack(2);
    }


    /**
     * Method to get a profile and set its roles correctly
     * @param id - ID of profile to get
     * @return profile - The profile instance
     */
    @Ignore
    private Profile getProfile(Integer id) {
        Profile profile = TestApplication.getProfileRepository().getProfileByProfileId(id);
        profile.setRoles(TestApplication.getRolesRepository().getProfileRoles(id).get());
        return profile;
    }


    /**
     * Check a non-admin user request to clear a non-empty stack
     * does not execute
     */
    @Test
    @Ignore
    public void nonAdminClearStack() {
        UndoStack undoDest = TestApplication.getUndoStackRepository().getStackItem(3);
        TestApplication.getUndoStackRepository().addToStack(undoDest);
        TestApplication.getUndoStackRepository().clearStackOnAllowed(getProfile(undoDest.getProfileId()));
        Assert.assertFalse(TestApplication.getUndoStackRepository().getUsersStack(undoDest.getProfileId()).isEmpty());
    }


    /**
     * Check admin request to clear a non-empty stack is executed
     */
    @Test
    @Ignore
    public void adminClearStack() {
        UndoStack undoDest = new UndoStack("destination", 2, 2);
        TestApplication.getUndoStackRepository().addToStack(undoDest);
        TestApplication.getUndoStackRepository().clearStackOnAllowed(getProfile(2));

        Assert.assertTrue(TestApplication.getUndoStackRepository().getUsersStack(2).isEmpty());
    }


    /**
     * Check item can be added to stack
     */
    @Test
    @Ignore
    public void addToStack() {
        UndoStack undoDest = TestApplication.getUndoStackRepository().getStackItem(2);
        TestApplication.getUndoStackRepository().addToStack(undoDest);
        Assert.assertFalse(TestApplication.getUndoStackRepository().getUsersStack(undoDest.getProfileId()).isEmpty());
    }


    /**
     * Check an item can be removed from the stack
     */
    @Test
    @Ignore
    public void removeFromStack() {
        UndoStack undoDest = new UndoStack("destination", 3, 1);
        TestApplication.getUndoStackRepository().addToStack(undoDest);
        TestApplication.getUndoStackRepository().removeItem(undoDest);
        Assert.assertTrue(TestApplication.getUndoStackRepository().getUsersStack(undoDest.getProfileId()).isEmpty());
    }


    /**
     * Check an admin can clear the stack
     */
    @Test
    @Ignore
    public void adminCanClearStack() {
        UndoStack undoDest = new UndoStack("destination", 4, 2);
        TestApplication.getUndoStackRepository().addToStack(undoDest);
        Assert.assertTrue(TestApplication.getUndoStackRepository().canClearStack(getProfile(2)));
    }


    /**
     * Check a non admin cannot clear the stack
     */
    @Test
    @Ignore
    public void nonAdminCanClearStack() {
        UndoStack undoDest = new UndoStack("destination", 5, 1);
        TestApplication.getUndoStackRepository().addToStack(undoDest);
        Assert.assertFalse(TestApplication.getUndoStackRepository().canClearStack(getProfile(1)));
    }
}
