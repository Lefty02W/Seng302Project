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

public class CreateDestinationSteps extends ProvideApplication {
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> destForm = new HashMap<>();
    private Result destResult;

    @Given("User is at the destinations page")
    public void userIsAtTheDestinationsPage() {
        loginForm.put("email", "john@gmail.com");
        loginForm.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(loginForm)
                .session("connected", "john@gmail.com");

        Result loginResult = Helpers.route(provideApplication(), request);


        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations")
                .session("connected", "john@gmail.com");

        Result destinationResult = Helpers.route(provideApplication(), requestDest);

        // TODO check on trips page
    }

    @When("user clicks on the add new destination button")
    public void userClicksOnTheAddNewDestinationButton() {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/create")
                .session("connected", "john@gmail.com");
        Result destinationResult = Helpers.route(provideApplication(), requestDest);
        Assert.assertEquals(200, destinationResult.status());
    }

    @When("he fills in Name with {string}")
    public void heFillsInNameWith(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("he fills in Type with {string}")
    public void heFillsInTypeWith(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("he fills in Country with {string}")
    public void heFillsInCountryWith(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("he presses Save then Create Destination page should be shown")
    public void hePressesSaveThenCreateDestinationPageShouldBeShown() {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations")
                .session("connected", "john@gmail.com");
        Result destinationResult = Helpers.route(provideApplication(), requestDest);
        Assert.assertEquals(200, destinationResult.status());
    }

    @Then("the created destination is stored in the database")
    public void theCreatedDestinationIsStoredInTheDatabase() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
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
