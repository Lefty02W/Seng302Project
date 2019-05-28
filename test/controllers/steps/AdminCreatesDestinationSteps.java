package controllers.steps;

import controllers.ProvideApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class AdminCreatesDestinationSteps extends ProvideApplication {

    private Map<String, String> destForm = new HashMap<>();
    private Result redirectDestination;

    @When("admin fills the create destination form with correct data")
    public void adminFillsTheCreateDestinationFormWithCorrectData() {
        destForm.put("name", "testName");
        destForm.put("type", "testType");
        destForm.put("country", "testCountry");
        System.out.println(destForm.size() + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        assertEquals(3, destForm.size());

    }

    @And("selects them self as the profile")
    public void selectsThemSelfAsTheProfile() {
        destForm.put("profileId", "2");
        assertEquals(4, destForm.size());
    }

    @Then("the admin presses save")
    public void theAdminPressesSave() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/admin/create/destinations")
                .bodyForm(destForm)
                .session("connected", "2");
        redirectDestination = Helpers.route(provideApplication(), request);
        assertEquals("/admin", redirectDestination.redirectLocation().get());
    }

    @Then("the new destination is added to the admins destinations")
    public void theNewDestinationIsAddedToTheAdminsDestinations() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("admin fills the form with correct data including name as {string}")
    public void adminFillsTheFormWithCorrectDataIncludingNameAs(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("selects user {int} as the profile")
    public void selectsUserAsTheProfile(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the new destination is added to user {int} destinations")
    public void theNewDestinationIsAddedToUserDestinations(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }
}
