package controllers;

import models.UndoStack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.test.Helpers;

import java.util.Date;


public class UndoStackTest extends ProvideApplication {


    @Before
    public void clearStack() {
        injectRepositories();
        undoStackRepository.clearStack(1);
        undoStackRepository.clearStack(2);
    }


    /**
     * Navigate to a specified endpoint
     */
    private void navigateToPage(String endPoint, Integer profileId) {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri(endPoint)
                .session("connected", profileId.toString());
        Helpers.route(provideApplication(), request);
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
    public void adminProfileShowClearStack() {
        adminLogin();
        undoStackRepository.clearStack(2);

        UndoStack undoDest = new UndoStack("destination", 1, 2, new Date());
        addItemToUndoStack(undoDest);
        navigateToPage("/profile", 2);

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
        loginUser();
        injectRepositories();


        UndoStack undoDest = new UndoStack("destination", 3, 1, new Date());
        addItemToUndoStack(undoDest);
        navigateToPage("/profile", 1);

        Assert.assertFalse(undoStackRepository.getUsersStack(1).isEmpty());
    }


    /**
     * Check stack is cleared when admin navigates to destinations page
     * This is essentially testing the profile's show() method
     * clears the stack if permissible
     */
    @Test
    public void adminDestinationShowClearStack() {
        adminLogin();
        injectRepositories();


        UndoStack undoDest = new UndoStack("destination", 1, 2, new Date());
        addItemToUndoStack(undoDest);
        navigateToPage("/destinations/show/false", 2);

        Assert.assertTrue(undoStackRepository.getUsersStack(2).isEmpty());
    }


    /**
     * Check stack is cleared when admin navigates to trips page
     * This is essentially testing the profile's show() method
     * clears the stack if permissible
     */
    @Test
    public void adminTripsShowClearStack() {
        adminLogin();
        injectRepositories();

        UndoStack undoDest = new UndoStack("destination", 2, 2, new Date());
        addItemToUndoStack(undoDest);
        navigateToPage("/trips", 2);

        Assert.assertTrue(undoStackRepository.getUsersStack(2).isEmpty());
    }

}
