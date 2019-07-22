package controllers.steps.Destinations;

import controllers.ProvideApplication;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DestinationTravellerTypeSteps extends ProvideApplication {
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> destForm = new HashMap<>();

    @Given("A logged in user is on the destinations page")
    public void aLoggedInUserIsOnTheDestinationsPage() {
        loginForm.put("email", "john@gmail.com");
        loginForm.put("password", "password");

        //logs user in
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(loginForm)
                .session("connected", "1");
        Result loginResult = Helpers.route(provideApplication(), request);

        //navigates to destinations scene
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/show/false")
                .session("connected", "1");
        Result destinationResult = Helpers.route(provideApplication(), requestDest);
        assertEquals(200, destinationResult.status());
    }

    @Given("there is a public destination with traveller type {string}")
    public void thereIsAPublicDestinationWithTravellerType(String string) {
        destForm.put("name", "travellerTypeTestDestination");
        destForm.put("type", "City");
        destForm.put("country", "New Zealand");
        destForm.put("travellerTypesStringDest", "[" + string + "]");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations")
                .bodyForm(destForm)
                .session("connected", "1");
        Helpers.route(provideApplication(), request);
        injectRepositories();
        throw new cucumber.api.PendingException();
        // TODO: 22/07/19 need to write a method to get a destination id 
    }

    @When("the user fills the request form for the destination to remove traveller type {string}")
    public void theUserFillsTheRequestFormForTheDestinationToRemoveTravellerType(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("adds traveller type {string}")
    public void addsTravellerType(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the user is redirected to the destinations page")
    public void theUserIsRedirectedToTheDestinationsPage() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the requests pass to the admin")
    public void theRequestsPassToTheAdmin() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("the user fills in the request form with add {string}")
    public void theUserFillsInTheRequestFormWithAdd(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("the user fills in the request form with remove {string}")
    public void theUserFillsInTheRequestFormWithRemove(String string) {
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
}
