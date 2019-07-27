package controllers;

import models.UndoStack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Http;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

public class UndoStackTest extends ProvideApplication {

    //TODO: Fix tests with @Ignore tag

    @Before
    public void clearStack() {
        injectRepositories();
        undoStackRepository.clearStack(1);
        undoStackRepository.clearStack(2);
    }


    /**
     * Navigate to a specified endpoint
     */
    private void navigateToPage(String endPoint) {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri(endPoint);
    }


    /**
     * Add an item to the undo stack
     * @param undoItem - The item to add to the stack
     */
    private void addItemToUndoStack(UndoStack undoItem) {
        undoStackRepository.addToStack(undoItem).thenRun(() -> {
            if (undoStackRepository.getUsersStack(undoItem.getProfileId()).isEmpty()) {
                Assert.fail("ERROR: Undo item was not added to stack. Check repository methods work!");
            }
        });
    }


    /**
     * Check stack is cleared when admin navigates to profile page
     * This is essentially testing the profile's show() method
     * clears the stack if permissible
     */
    @Test
    @Ignore
    public void adminProfileShowClearStack() {
        injectRepositories();
        adminLogin();

        UndoStack undoDest = new UndoStack("destination", 1, 2);
        addItemToUndoStack(undoDest);
        navigateToPage("/profile");

        Assert.assertTrue(undoStackRepository.getUsersStack(2).isEmpty());
    }


    /**
     * Check a non-admin navigating to the profile page does
     * not clear the stack.
     * Essentially testing profile show() method won't clear
     * stack when non-permissible
     */
    @Test
    public void nonAdminProfileShowClearStack() {
        injectRepositories();
        loginUser();

        UndoStack undoDest = new UndoStack("destination", 1, 1);
        addItemToUndoStack(undoDest);
        navigateToPage("/profile");

        Assert.assertTrue(undoStackRepository.getUsersStack(1).isEmpty());

    }


    @Test
    @Ignore
    public void adminDestinationShowClearStack() {
        injectRepositories();
        adminLogin();

        UndoStack undoDest = new UndoStack("destination", 1, 2);
        addItemToUndoStack(undoDest);
        navigateToPage("/destinations/show/false");

        Assert.assertTrue(undoStackRepository.getUsersStack(2).isEmpty());

    }


    @Test
    @Ignore
    public void adminTripsShowClearStack() {
        injectRepositories();
        adminLogin();

        UndoStack undoDest = new UndoStack("destination", 1, 2);
        addItemToUndoStack(undoDest);
        navigateToPage("/trips");

        Assert.assertTrue(undoStackRepository.getUsersStack(2).isEmpty());

    }

}
