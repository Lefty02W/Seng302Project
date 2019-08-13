package controllers;

import models.UndoStack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;


public class UndoStackTest {


    @Before
    @Ignore
    public void clearStack() {
        TestApplication.getUndoStackRepository().clearStack(1);
        TestApplication.getUndoStackRepository().clearStack(2);
    }


    /**
     * Navigate to a specified endpoint
     */
    @Ignore
    private void navigateToPage(String endPoint, Integer profileId) {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri(endPoint)
                .session("connected", profileId.toString());
        Helpers.route(TestApplication.getApplication(), request);
    }


    /**
     * Add an item to the undo stack
     * @param undoItem - The item to add to the stack
     */
    @Ignore
    private void addItemToUndoStack(UndoStack undoItem) {
        TestApplication.getUndoStackRepository().addToStack(undoItem).thenRun(() -> {
            if (TestApplication.getUndoStackRepository().getUsersStack(undoItem.getProfileId()).isEmpty()) {
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
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "bob@gmail.com");
        formData.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(TestApplication.getApplication(), request);

        TestApplication.getUndoStackRepository().clearStack(2);

        UndoStack undoDest = new UndoStack("destination", 1, 2);
        addItemToUndoStack(undoDest);
        navigateToPage("/profile", 2);

        Assert.assertTrue(TestApplication.getUndoStackRepository().getUsersStack(2).isEmpty());
    }


    /**
     * Check a non-admin navigating to the profile page does
     * not clear the stack.
     * Essentially testing profile show() method won't clear
     * stack when non-permissible
     */
    @Test
    @Ignore
    public void nonAdminProfileShowClearStack() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "john@gmail.com");
        formData.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(TestApplication.getApplication(), request);

        UndoStack undoDest = new UndoStack("destination", 3, 1);
        addItemToUndoStack(undoDest);
        navigateToPage("/profile", 1);

        Assert.assertFalse(TestApplication.getUndoStackRepository().getUsersStack(1).isEmpty());
    }


    /**
     * Check stack is cleared when admin navigates to destinations page
     * This is essentially testing the profile's show() method
     * clears the stack if permissible
     */
    @Test
    @Ignore
    public void adminDestinationShowClearStack() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "bob@gmail.com");
        formData.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(TestApplication.getApplication(), request);

        UndoStack undoDest = new UndoStack("destination", 1, 2);
        addItemToUndoStack(undoDest);
        navigateToPage("/destinations/show/false", 2);

        Assert.assertTrue(TestApplication.getUndoStackRepository().getUsersStack(2).isEmpty());
    }


    /**
     * Check stack is cleared when admin navigates to trips page
     * This is essentially testing the profile's show() method
     * clears the stack if permissible
     */
    @Test
    @Ignore
    public void adminTripsShowClearStack() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "bob@gmail.com");
        formData.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(TestApplication.getApplication(), request);

        UndoStack undoDest = new UndoStack("destination", 2, 2);
        addItemToUndoStack(undoDest);
        navigateToPage("/trips", 2);

        Assert.assertTrue(TestApplication.getUndoStackRepository().getUsersStack(2).isEmpty());
    }

}
