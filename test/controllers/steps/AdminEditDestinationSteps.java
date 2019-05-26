package controllers.steps;

import controllers.ProvideApplication;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Destination;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class AdminEditDestinationSteps extends ProvideApplication {

    Map<String, String> loginForm = new HashMap<>();
    Map<String, String> destForm = new HashMap<>();
    Result loginResult;


    @Given("Admin is logged in to the application")
    public void adminIsLoggedInToTheApplication() {
        loginForm.put("email", "bob@gmail.com");
        loginForm.put("password", "password");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(loginForm)
                .session("connected", "2");

        loginResult = Helpers.route(provideApplication(), request);
        assertEquals("/profile", loginResult.redirectLocation().get());

    }

    @Given("admin is on the admin page")
    public void adminIsOnTheAdminPage() {
        // Write code here that turns the phrase above into concrete actions
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin")
                .session("connected", "2");
        Result result = Helpers.route(provideApplication(), request);

        assertEquals(200, result.status());
    }

    @When("admin selects edit on destination {int}")
    public void adminSelectsEditOnDestination(Integer int1) {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/destinations/2?isEdit=true")
                .session("connected", "2");
        Result result = Helpers.route(provideApplication(), request);

        assertEquals(200, result.status());

        destForm.put("country", "Yeet Nation");
        destForm.put("type", "Country");
    }

    @When("changes the latitude to {double}")
    public void changesTheLatitudeTo(Double double1) {
        destForm.put("latitude", double1.toString());
    }

    @When("sets the name to {string}")
    public void setsTheNameTo(String string) {
        destForm.put("name", string);
    }

    @When("selects the the save button")
    public void selectsTheTheSaveButton() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/admin/destinations/2")
                .bodyForm(destForm)
                .session("connected", "2");
        Result result = Helpers.route(provideApplication(), request);

        if (result.redirectLocation().isPresent()) {
            assertEquals("/admin", result.redirectLocation().get());
        } else {
            fail();
        }
    }

    @Then("destination {int} latitude is updated to {double}")
    public void destinationLatitudeIsUpdatedTo(Integer int1, Double double1) {
        injectRepositories();
        Destination dest = destinationRepository.lookup(int1);

        assertEquals(double1, dest.getLatitude(), 0.0);
    }

    @Then("destination {int} name is updated to {string}")
    public void destinationNameIsUpdatedTo(Integer int1, String string) {
        injectRepositories();
        Destination dest = destinationRepository.lookup(int1);
        assertEquals(string, dest.getName());
    }
}
