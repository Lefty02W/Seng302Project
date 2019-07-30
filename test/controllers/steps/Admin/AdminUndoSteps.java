package controllers.steps.Admin;

import controllers.ProvideApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Destination;
import models.UndoStack;
import org.joda.time.DateTime;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class AdminUndoSteps extends ProvideApplication {
    private Map<String, String> loginForm = new HashMap<>();
    private Result huntDeleteResult;
    private Result profileDeleteResult;


    @Given("the admin is on the admin page")
    public void theAdminIsOnTheAdminPage() {
        // Write code here that turns the phrase above into concrete actions
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin")
                .session("connected", "2");
        Helpers.route(provideApplication(), requestBuilder);
    }

    @And("there is a profile with id {string}")
    public void thereIsAProfileWithId(String string) {
        // Write code here that turns the phrase above into concrete actions
        injectRepositories();
        assertEquals(profileRepository.getProfileByProfileId(Integer.parseInt(string)).getProfileId().toString(), string);
    }

    @Given("the admin deletes the profile with id {string}")
    public void theAdminDeletesTheProfileWithId(String string) {
        // Write code here that turns the phrase above into concrete actions
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/" + string +"/delete")
                .session("connected", "2");
        Helpers.route(provideApplication(), requestBuilder);
    }

    @When("the admin presses the undo button")
    public void theAdminPressesTheUndoButton() {
        // Write code here that turns the phrase above into concrete actions
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/undo/")
                .session("connected", "2");
        Helpers.route(provideApplication(),requestBuilder);
    }

    @Then("the profile {string} is restored")
    public void theProfileIsRestored(String string) {
        // Write code here that turns the phrase above into concrete actions
        injectRepositories();
        System.out.println(profileRepository.getProfileByProfileId(Integer.parseInt(string)).getSoftDelete());
        assertTrue(profileRepository.getProfileByProfileId(Integer.parseInt(string)).getSoftDelete() == 1);
    }

    @Then("the profile {string} is no longer in the delete stack")
    public void theProfileIsNoLongerInTheDeleteStack(String string) {
        // Write code here that turns the phrase above into concrete actions
        injectRepositories();
        assertFalse(undoStackRepository.canClearStack(profileRepository.getProfileByProfileId(2)));

    }

    @Given("there is a treasure hunt with id {string}")
    public void thereIsATreasureHuntWithId(String string) {
        // Write code here that turns the phrase above into concrete actions
        injectRepositories();
        assertTrue(treasureHuntRepository.lookup(Integer.parseInt(string)).getTreasureHuntId() == Integer.parseInt(string));
    }

    @Given("the admin deletes the treasure hunt {string}")
    public void theAdminDeletesTheTreasureHunt(String string) {
        // Write code here that turns the phrase above into concrete actions
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/hunts/" + string + "/delete")
                .session("connected", "2");
        Helpers.route(provideApplication(),requestBuilder);
    }

    @Then("the treasure hut {string} is restored")
    public void theTreasureHutIsRestored(String string) {
        // Write code here that turns the phrase above into concrete actions
        injectRepositories();
        assertEquals(treasureHuntRepository.lookup(Integer.parseInt(string)).getSoftDelete(), 1);
    }

    @And("the treasure hunt is removed from the delete stack")
    public void theTreasureHuntIsRemovedFromTheDeleteStack() {
        // Write code here that turns the phrase above into concrete actions
        injectRepositories();
        assertFalse(undoStackRepository.canClearStack(profileRepository.getProfileByProfileId(2)));
    }

    @And("user {string} has a destination with id {string}")
    public void userHasADestinationWithId(String userId, String destId) {
        // Write code here that turns the phrase above into concrete actions
        Boolean flag = false;
        injectRepositories();
        List<Destination> myDestinations = destinationRepository.getUserDestinations(Integer.parseInt(userId));
        System.out.println(myDestinations);
        for (Destination dest : myDestinations){
            System.out.println("****************************************************");
            System.out.println(dest.getDestinationId());
            if (dest.getDestinationId() == Integer.parseInt(destId)){
                System.out.println("in if");
                flag = true;
            }
        } assertTrue(flag);
    }

    @Given("there is a trip with id {string}")
    public void thereIsATripWithId(String tripId) {
        // Write code here that turns the phrase above into concrete actions
        injectRepositories();
        assertEquals(tripRepository.getTrip(Integer.parseInt(tripId)).getTripId(), Integer.parseInt(tripId));
    }

    @Then("the admin deletes the trip {string}")
    public void theAdminDeletesTheTrip(String tripId) {
        // Write code here that turns the phrase above into concrete actions
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/trips/" + tripId + "/delete")
                .session("connected", "2");
        Helpers.route(provideApplication(),requestBuilder);
    }

    @And("the admin deletes the destination {string}")
    public void theAdminDeletesTheDestination(String destId) {
        // Write code here that turns the phrase above into concrete actions
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/destinations/" + destId + "/delete")
                .session("connected", "2");
        Helpers.route(provideApplication(),requestBuilder);
    }

    @Then("the trip {string} is restored")
    public void theTripIsRestored(String tripId) {
        // Write code here that turns the phrase above into concrete actions
        injectRepositories();
        //this should be 1 test is failing as code is bugged
        assertEquals(tripRepository.getTrip(Integer.parseInt(tripId)).getSoftDelete(), 1);
    }

    @And("user {string} destination {string} is still soft deleted")
    public void theDestinationIsStillSoftDeleted(String userId, String destId) {
        // Write code here that turns the phrase above into concrete actions
        List<Destination> myDestinaitons = destinationRepository.getUserDestinations(Integer.parseInt(userId));
        for (Destination dest : myDestinaitons){
            if (dest.getDestinationId() == Integer.parseInt(destId)){
                if (dest.getSoftDelete() == 0){
                    assertTrue(true);
                }
            }
        } fail();
    }


//    start of 2nd feature


    @And("^command stack item (\\d+) is more than one day old$")
    public void commandStackItemIsMoreThanOneDayOld(int arg0) throws Throwable {
        injectRepositories();
        UndoStack undoStack = undoStackRepository.getStackItem(arg0);
        System.out.println(undoStack);
        assertTrue(new DateTime().minusDays(1).toDate().getTime() > undoStack.getTimeCreated().getTime());
    }

    @When("^the admin leaves the admin page$")
    public void theAdminLeavesTheAdminPage() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/profile")
                .session("connected", "2");
        Helpers.route(provideApplication(), request);
    }

    @Then("^command (\\d+) should no longer be in the database$")
    public void commandShouldNoLongerBeInTheDatabase(int arg0) throws Throwable {
        injectRepositories();
        assertNull(undoStackRepository.getStackItem(arg0));
    }

    @And("^related destination (\\d+) should be removed from the database$")
    public void relatedDestinationShouldBeRemovedFromTheDatabase(int arg0) throws Throwable {
        injectRepositories();
        assertNull(destinationRepository.lookup(arg0));
    }

    @And("^the admin deletes treasure hunt (\\d+)$")
    public void theAdminDeletesTreasureHunt(int arg0) throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/hunts/" + arg0 + "/delete")
                .session("connected", "2");
        huntDeleteResult = Helpers.route(provideApplication(), request);
    }

    @Then("^a flashing is shown confirming the delete$")
    public void aFlashingIsShownConfirmingTheDelete() throws Throwable {
        Assert.assertTrue(huntDeleteResult.flash().getOptional("info").isPresent());
    }

    @And("^the treasure hunt is added to the undo stack$")
    public void theTreasureHuntIsAddedToTheUndoStack() throws Throwable {
        injectRepositories();

      //  ArrayList<UndoStack> stack = undoStackRepository.getUsersStack(2);
//        boolean found = false;
//        for (UndoStack item : stack) {
//            if (item.getItem_type().equals("treasure_hunt") && item.getObjectId() == 1) {
//                found = true;
//            }
//        }
//        assertTrue(found);
    }

    @And("And the admin deletes the profile with id {string}")
    public void admindeletesprofile(String profileId) throws Throwable {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/profile/" +profileId+ "/delete")
                .session("connected", "2");
        Helpers.route(provideApplication(),requestBuilder);
        profileDeleteResult = Helpers.route(provideApplication(), requestBuilder);
    }

    @Then("the profile is added to the undo stack")
    public void theProfileIsAddedToTheUndoStack() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("a profile flashing is shown confirming the delete")
    public void aFlashingIsShownConfirmingTheDelete() throws Throwable {
        Assert.assertTrue(profileDeleteResult.flash().getOptional("info").isPresent());
    }
}
