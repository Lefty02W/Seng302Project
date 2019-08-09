package controllers.steps.Admin;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AdminCreatesDestinationSteps {

    private Map<String, String> destForm = new HashMap<>();
    private Result redirectDestination;

    @When("admin fills the create destination form with correct data")
    public void adminFillsTheCreateDestinationFormWithCorrectData() {
        destForm.put("name", "testName");
        destForm.put("type", "testType");
        destForm.put("country", "testCountry");
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
        redirectDestination = Helpers.route(TestApplication.getApplication(), request);
        assertEquals("/admin", redirectDestination.redirectLocation().get());
        assertNotNull(request.session());
    }

    @And("the new destination is added to the admins {int} destinations")
    public void theNewDestinationIsAddedToTheAdminsDestinations(Integer adminId) {
        assertTrue(TestApplication.getProfileRepository().getDestinations(adminId).isPresent());

    }

    @When("admin fills the form with correct data including name as {string}")
    public void adminFillsTheFormWithCorrectDataIncludingNameAs(String string) {
        // Write code here that turns the phrase above into concrete actions
        destForm.put("name", string);
        destForm.put("type", "testType");
        destForm.put("country", "testCountry");

        //use check recomended in review
        assertEquals(3, destForm.size());

    }

    @When("selects user {int} as the profile")
    public void selectsUserAsTheProfile(Integer int1) {
        destForm.put("profileId", int1.toString());
        assertEquals(4, destForm.size());
    }

    @And("the new destination is added to user {int} destinations")
    public void theNewDestinationIsAddedToUserDestinations(Integer int1) {
        //user has 2 destinations on startup
        assertTrue(TestApplication.getProfileRepository().getDestinations(int1).isPresent());

    }
}
