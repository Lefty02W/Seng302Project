package controllers.steps;

import controllers.ProvideApplication;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CreateDestinationSteps extends ProvideApplication {
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> destForm = new HashMap<>();
    private Result redirectDestination;

    @Given("User is logged in to the application")
    public void userIsLoggedInToTheApplication() {
        // TODO: finish implement and check it passes
        loginForm.put("email", "john@gmail.com");
        loginForm.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(loginForm)
                .session("connected", "1");

        Result loginResult = Helpers.route(provideApplication(), request);
    }

    @Given("user is at the destinations page")
    public void userIsAtTheDestinationsPage() {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/show/false")
                .session("connected", "1");
        Result destinationResult = Helpers.route(provideApplication(), requestDest);
        assertEquals(200, destinationResult.status());

        // TODO check on trips page
    }

    @When("user clicks on the add new destination button")
    public void userClicksOnTheAddNewDestinationButton() {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/create")
                .session("connected", "1");
        Result destinationResult = Helpers.route(provideApplication(), requestDest);
        Assert.assertEquals(200, destinationResult.status());
    }

    @When("he fills in Name with {string}")
    public void heFillsInNameWith(String string) { destForm.put("name", string); }

    @When("he fills in Type with {string}")
    public void heFillsInTypeWith(String string) { destForm.put("type", string); }

    @When("he fills in Country with {string}")
    public void heFillsInCountryWith(String string) { destForm.put("country", string); }

    @When("he presses Save")
    public void hePressesSave() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations")
                .session("connected", "1");
        redirectDestination = Helpers.route(provideApplication(), request);
        assertEquals(303, redirectDestination.status());
    }

    @Then("he is redirected to the destinations page")
    public void theCreatedDestinationIsStoredInTheDatabase() {
        assertEquals(303, redirectDestination.status());
        assertEquals("/destinations/show/false", redirectDestination.redirectLocation().get());
    }

    @When("he fills in Longitude as {string}")
    public void heFillsInLongitudeAs(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the Create Destination page should be shown")
    public void theCreateDestinationPageShouldBeShown() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }
}
