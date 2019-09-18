package controllers.steps.Events;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class DeleteEventSteps {

    private Result REDIRECT_RESULT;
    private final String REDIRECT_LOCATION = "/artists/2/events/0";


    @Given("^I am on the events tab for my artist (\\d+)$")
    public void iAmOnTheEventsTabForMyArtist(int arg0) throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/artists/" + arg0 + "/events/0")
                .session("connected", "2");
        Helpers.route(TestApplication.getApplication(), requestDest);
    }

    @When("^I select event (\\d+) to delete$")
    public void iSelectEventToDelete(int arg0) throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/artists/2/events/" + arg0 + "/delete")
                .session("connected", "2");
        REDIRECT_RESULT = Helpers.route(TestApplication.getApplication(), requestDest);
    }

    @Then("^I am redirected to the events tab for artist (\\d+)$")
    public void iAmRedirectedToTheEventsTabForArtist(int arg0) throws Throwable {
        if (REDIRECT_RESULT.redirectLocation().isPresent()) {
            assertEquals(REDIRECT_LOCATION, REDIRECT_RESULT.redirectLocation().get());
        } else {
            fail();
        }
    }

    @And("^Event (\\d+) has been deleted$")
    public void eventHasBeenDeleted(int arg0) throws Throwable {
        TestApplication.getEventRepository().getEvent(arg0).thenApplyAsync(eventOpt -> {
           assertFalse(eventOpt.isPresent());
           return null;
        });
    }
}
