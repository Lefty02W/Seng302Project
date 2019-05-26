package controllers.steps.Destinations;

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
import static org.junit.Assert.fail;

public class publicDestinationSteps extends ProvideApplication {
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> destForm = new HashMap<>();
    private Result redirectDestination;

    @When("he fills in name with {string}")
    public void heFillsInNameWith(String string) { destForm.put("name", string); }

    @When("he fills in type with {string}")
    public void heFillsInTypeWith(String string) { destForm.put("type", string); }

    @When("he fills in country with {string}")
    public void heFillsInCountryWith(String string) { destForm.put("country", string); }

    @When("he presses save")
    public void hePressesSave() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations")
                .bodyForm(destForm)
                .session("connected", "1");
        redirectDestination = Helpers.route(provideApplication(), request);
        assertEquals(303, redirectDestination.status());
    }

    @Then("he is redirected to the create destination page and destination is not saved")
    public void heIsRedirectedToTheCreateDestinationPageAndDestinationIsNotSaved() {
        assertEquals(303, redirectDestination.status());
        assertEquals("/destinations/create", redirectDestination.redirectLocation().get());
    }
}
