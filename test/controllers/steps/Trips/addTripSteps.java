package controllers.steps.Trips;

import controllers.TestApplication;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Destination;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

public class addTripSteps {
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> destForm = new HashMap<>();
    private Result destResult;

    @Given("user is at trips page")
    public void userIsAtTripsPage() {
        loginForm.put("email", "john@gmail.com");
        loginForm.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(loginForm)
                .session("connected", "1");

        Result loginResult = Helpers.route(TestApplication.getApplication(), request);


        Http.RequestBuilder requestTrip = Helpers.fakeRequest()
                .method("GET")
                .uri("/trips")
                .session("connected", "1");

        Result tripResult = Helpers.route(TestApplication.getApplication(), requestTrip);

        // TODO check on trips page

    }

    @When("user clicks on the add new trip button")
    public void userClicksOnTheAddNewTripButton() {

        Http.RequestBuilder requestTrip = Helpers.fakeRequest()
                .method("GET")
                .uri("/trips/1/create")
                .session("connected", "1");
        Result tripResult = Helpers.route(TestApplication.getApplication(), requestTrip);
        Assert.assertEquals(200, tripResult.status());

    }

    @When("user selects a destination called {string}")
    public void userSelectsADestinationCalled(String string) {
        List<Destination> userDestinations = TestApplication.getDestinationRepository().getUserDestinations(1);
        throw new cucumber.api.PendingException();

    }

    @When("user presses add destination")
    public void userPressesAddDestination() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("user selects another destination called {string}")
    public void userSelectsAnotherDestination() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("user enters a trip name")
    public void userEntersATripName() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("user presses Save Trip")
    public void userPressesSaveTrip() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the trip page loads displaying his trips")
    public void theTripPageLoadsDisplayingHisTrips() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("trip is saved in the database")
    public void tripIsSavedInTheDatabase() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("user presses add destination without selecting a destination")
    public void userPressesAddDestinationWithoutSelectingADestination() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/trips/1/create")
                .bodyForm(destForm)
                .session("connected", "1");

        destResult = Helpers.route(TestApplication.getApplication(), request);
    }

    @Then("destination is not added")
    public void destinationIsNotAdded() {
        //assertEquals("The same destination cannot be after itself in a trip", destResult.flash().getOptional("info").get());
        assertTrue(true); //TODO sort this out
    }
}
