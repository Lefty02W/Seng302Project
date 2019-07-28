package controllers.steps.Admin;

import controllers.ProvideApplication;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.UndoStack;
import org.joda.time.DateTime;
import org.junit.Assert;
import play.mvc.Http;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class AdminUndoSteps extends ProvideApplication {
    private Map<String, String> loginForm = new HashMap<>();

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

    @Given("there is a treasure hunt with id")
    public void thereIsATreasureHuntWithId() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("the admin deletes the treasure hunt")
    public void theAdminDeletesTheTreasureHunt() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("the admin selects the treasure hunt on the undo dropdown")
    public void theAdminSelectsTheTreasureHuntOnTheUndoDropdown() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the treasure hut is restored")
    public void theTreasureHutIsRestored() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the treasure hunt is removed from the delete stack")
    public void theTreasureHuntIsRemovedFromTheDeleteStack() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("there is a destination with id")
    public void thereIsADestinationWithId() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("there is a trip with id")
    public void thereIsATripWithId() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the admin deletes the destination")
    public void theAdminDeletesTheDestination() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the admin deletes the trip")
    public void theAdminDeletesTheTrip() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("the admin selects the destination on the undo dropdown")
    public void theAdminSelectsTheDestinationOnTheUndoDropdown() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the destination is restored")
    public void theDestinationIsRestored() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the trip is still on the delete stack")
    public void theTripIsStillOnTheDeleteStack() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("user is logged in to the application")
    public void userIsLoggedInToTheApplication() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("user with id {string} has a private destination with name {string}, type {string}, and country {string}")
    public void userWithIdHasAPrivateDestinationWithNameTypeAndCountry(String string, String string2, String string3, String string4) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("user creates a public destination with name {string}, type {string}, and country NewZealand")
    public void userCreatesAPublicDestinationWithNameTypeAndCountryNewZealand(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("user is logged into the application")
    public void userIsLoggedIntoTheApplication() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("John is on his profile page")
    public void johnIsOnHisProfilePage() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("he presses the delete button on photo with id {int}")
    public void hePressesTheDeleteButtonOnPhotoWithId(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("photo {int} is removed from the database")
    public void photoIsRemovedFromTheDatabase(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

//    start of 2nd feature


    @And("^command stack item (\\d+) is more than one day old$")
    public void commandStackItemIsMoreThanOneDayOld(int arg0) throws Throwable {
        injectRepositories();
        UndoStack undoStack = undoStackRepository.getStackItem(arg0);
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

}
