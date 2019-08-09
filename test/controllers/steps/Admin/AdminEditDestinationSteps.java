package controllers.steps.Admin;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Destination;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AdminEditDestinationSteps {

    Map<String, String> loginForm = new HashMap<>();
    Map<String, String> destForm = new HashMap<>();
    Result loginResult;
    private Result result;


    @Given("Admin is logged in to the application")
    public void adminIsLoggedInToTheApplication() {
        loginForm.put("email", "bob@gmail.com");
        loginForm.put("password", "password");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(loginForm)
                .session("connected", "2");

        loginResult = Helpers.route(TestApplication.getApplication(), request);
        assertEquals("/profile", loginResult.redirectLocation().get());

    }

    @Given("admin is on the admin page")
    public void adminIsOnTheAdminPage() {
        // Write code here that turns the phrase above into concrete actions
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin")
                .session("connected", "2");
        result = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(200, result.status());
    }

    @When("selects the the save button")
    public void selectsTheTheSaveButton() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/admin/destinations/2")
                .bodyForm(destForm)
                .session("connected", "2");
        result = Helpers.route(TestApplication.getApplication(), request);
    }
    
    @When("^admin presses edit on destination (\\d+)$")
    public void adminPressesEditOnDestination(int arg0) throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/hunts/" + Integer.toString(arg0) + "/edit/show")
                .session("connected", "2");
        result = Helpers.route(TestApplication.getApplication(), request);
        Destination destination = TestApplication.getDestinationRepository().lookup(2);
        destForm.put("profileId", Integer.toString(destination.getProfileId()));
        destForm.put("name", destination.getName());
        destForm.put("type", destination.getType());
        destForm.put("country", destination.getCountry());
        destForm.put("district", destination.getDistrict());
        destForm.put("longitude", Double.toString(destination.getLongitude()));
        destForm.put("latitude", Double.toString(destination.getLatitude()));
        destForm.put("visible", Integer.toString(destination.getVisible()));
    }

    @And("^changes the latitude to \"([^\"]*)\"$")
    public void changesTheLatitudeTo(String arg0) throws Throwable {
        destForm.put("latitude", arg0);
    }

    @And("^sets the name to \"([^\"]*)\"$")
    public void setsTheNameTo(String arg0) throws Throwable {
        destForm.put("name", arg0);
    }

    @Then("^admin is redirected to the admin page with a valid notification$")
    public void adminIsRedirectedToTheAdminPageWithAValidNotification() throws Throwable {
        Assert.assertTrue(result.flash().getOptional("info").isPresent());
    }

    @Then("^admin is redirected to the admin page with an invalid notification$")
    public void adminIsRedirectedToTheAdminPageWithAnInvalidNotification() throws Throwable {
        Assert.assertTrue(result.flash().getOptional("error").isPresent());
    }


    @Then("^destinations latitude is updated in the database$")
    public void destinationsLatitudeIsUpdatedInTheDatabase() throws Throwable {
        Destination destination = TestApplication.getDestinationRepository().lookup(2);
        assertEquals("12.2", Double.toString(destination.getLatitude()));
    }

    @And("^destination name is updated in the database$")
    public void destinationNameIsUpdatedInTheDatabase() throws Throwable {
        Destination destination = TestApplication.getDestinationRepository().lookup(2);
        assertEquals("Haere Roa", destination.getName());
    }

    @And("^changes the longitude to \"([^\"]*)\"$")
    public void changesTheLongitudeTo(String arg0) throws Throwable {
        destForm.put("longitude", arg0);
    }

    @And("^destinations latitude is not updated in the database$")
    public void destinationsLatitudeIsNotUpdatedInTheDatabase() throws Throwable {
        Destination destination = TestApplication.getDestinationRepository().lookup(2);
        assertNotEquals("200", Double.toString(destination.getLongitude()));
    }

    @And("^changes the visibility to (\\d+)$")
    public void changesTheVisibilityTo(int arg0) throws Throwable {
        destForm.put("visible", Integer.toString(arg0));
    }

    @And("^destinations visibility is updated in the database$")
    public void destinationsVisibilityIsUpdatedInTheDatabase() throws Throwable {
        Destination destination = TestApplication.getDestinationRepository().lookup(2);
        assertEquals(1, destination.getVisible());
    }
}
