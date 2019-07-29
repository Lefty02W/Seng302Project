package controllers.steps.Destinations;

import controllers.ProvideApplication;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
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
import static org.junit.Assert.fail;

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
    }

    @When("user clicks on the add new destination button")
    public void userClicksOnTheAddNewDestinationButton() {
        return;
    }

    @When("he presses Save")
    public void hePressesSave() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations")
                .bodyForm(destForm)
                .session("connected", "1");
        redirectDestination = Helpers.route(provideApplication(), request);
        assertEquals(303, redirectDestination.status());
    }

    @Then("he is redirected to the destinations page")
    public void theCreatedDestinationIsStoredInTheDatabase() {
        Assert.assertTrue(redirectDestination.flash().getOptional("success").isPresent());

    }

    @Then("the Destination page should be shown")
    public void theDestinationPageShouldBeShown() {
        Assert.assertTrue(redirectDestination.flash().getOptional("failure").isPresent());

    }

    @When("^he fills in Name with \"([^\"]*)\"$")
    public void heFillsInNameWith(String arg0) throws Throwable {
        destForm.put("name", arg0);
    }

    @When("^he fills in Type with \"([^\"]*)\"$")
    public void heFillsInTypeWith(String arg0) throws Throwable {
        destForm.put("type", arg0);
    }

    @When("^he fills in Country with \"([^\"]*)\"$")
    public void heFillsInCountryWith(String arg0) throws Throwable {
        destForm.put("country", arg0);
    }

    @When("^he selects \"([^\"]*)\" as the traveller type$")
    public void heSelectsAsTheTravellerType(String arg0) throws Throwable {
        System.out.println(arg0.equals("ThrillSeeker"));
        destForm.put("travellerTypesStringDest", arg0);
    }

    @When("^he fills in Longitude as \"([^\"]*)\"$")
    public void heFillsInLongitudeAs(String arg0) throws Throwable {
        destForm.put("Longitude", arg0);
    }

    @And("^he presses Destination Save$")
    public void hePressesDestinationSave() throws Throwable {
        System.out.println(destForm);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations")
                .bodyForm(destForm)
                .session("connected", "1");
        redirectDestination = Helpers.route(provideApplication(), request);
        assertEquals(303, redirectDestination.status());
    }
}
