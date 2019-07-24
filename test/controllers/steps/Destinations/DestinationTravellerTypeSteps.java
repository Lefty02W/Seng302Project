package controllers.steps.Destinations;

import controllers.ProvideApplication;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import repository.DestinationRepository;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DestinationTravellerTypeSteps extends ProvideApplication {
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> requestForm = new HashMap<>();
    private Map<String, String> secondRequestForm = new HashMap<>();
    private Map<String, String> thirdRequestForm = new HashMap<>();
    private Result result;

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

    @Given("there is a public destination with id {string}")
    public void thereIsAPublicDestinationWithTravellerType(String id) {
        requestForm.put("destinationId", id);
        secondRequestForm.put("destinationId", id);
        thirdRequestForm.put("destinationId", id);
    }

    @When("the user fills the request form for the destination to remove traveller type {string}")
    public void theUserFillsTheRequestFormForTheDestinationToRemoveTravellerType(String string) {
        requestForm.put("toRemove", string);
    }

    @When("adds traveller type {string}")
    public void addsTravellerType(String string) {
        requestForm.put("toAdd", string);
    }

    @Then("the user presses the submit button")
    public void theUserIsRedirectedToTheDestinationsPage() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations/type/request")
                .bodyForm(requestForm)
                .session("connected", "1");
        result = Helpers.route(provideApplication(), request);

    }

    @Then("the user submits the second request")
    public void theUserSubmitsTheSecondRequest() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations/type/request")
                .bodyForm(secondRequestForm)
                .session("connected", "1");
        result = Helpers.route(provideApplication(), request);
    }

    @Then("the requests pass to the admin")
    public void theRequestsPassToTheAdmin() {
        Assert.assertTrue(result.flash().getOptional("success").isPresent());
    }

    @When("the user fills in the request form with add {string}")
    public void theUserFillsInTheRequestFormWithAdd(String string) {
        secondRequestForm.put("toAdd", string);
        secondRequestForm.put("toRemove", "");
    }

    @When("the user fills in the request form with remove {string}")
    public void theUserFillsInTheRequestFormWithRemove(String string) {
        thirdRequestForm.put("toAdd", "");
        thirdRequestForm.put("toRemove", string);
    }

    @Then("the user submits the third request")
    public void theUserSubmitsTheThirdRequest() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations/type/request")
                .bodyForm(thirdRequestForm)
                .session("connected", "1");
        result = Helpers.route(provideApplication(), request);
    }
}