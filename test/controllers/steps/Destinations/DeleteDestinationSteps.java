package controllers.steps.Destinations;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DeleteDestinationSteps {

    private Result REDIRECT_RESULT;

    @Given("^I am on the destinations page$")
    public void iAmOnTheDestinationsPage() throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/show/false/0")
                .session("connected", "2");
        Helpers.route(TestApplication.getApplication(), requestDest);
    }

    @When("^I select destination (\\d+) to delete$")
    public void iSelectDestinationToDelete(int arg0) throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/" + arg0 +"/delete")
                .session("connected", "2");
        REDIRECT_RESULT = Helpers.route(TestApplication.getApplication(), requestDest);
    }

    @Then("^A flashing is shown$")
    public void aFlashingIsShown() throws Throwable {
        Assert.assertTrue(REDIRECT_RESULT.flash().getOptional("failure").isPresent());
    }

    @And("^Destination (\\d+) has not been deleted$")
    public void destinationHasNotBeenDeleted(int arg0) throws Throwable {
        assertNotNull(TestApplication.getDestinationRepository().lookup(arg0));
    }

    @And("^Destination (\\d+) has been deleted$")
    public void destinationHasBeenDeleted(int arg0) throws Throwable {
        try {
            TestApplication.getDestinationRepository().lookup(arg0);
            fail();
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }
}
