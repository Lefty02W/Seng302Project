package controllers.steps.Artist;

import controllers.TestApplication;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CreateEventFromArtistSteps {

    private Map<String, String> eventCreateForm = new HashMap<>();
    private Result artistEventCreateResult;



    @Given("^I am on my view artists page for artist \"([^\"]*)\"$")
    public void iAmOnMyViewArtistsPageForArtist(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/artists/" + arg0)
                .session("connected", "12");
        Helpers.route(TestApplication.getApplication(), requestDest);
    }

    @And("^I enter \"([^\"]*)\" into the \"([^\"]*)\" field for the CreateEventForm$")
    public void iEnterIntoTheFieldForTheCreateEventForm(String arg0, String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        eventCreateForm.put(arg1, arg0);
    }

    @And("^I submit the Create Event Form$")
    public void iSubmitTheCreateEventForm() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/artists/9/events/create")
                .bodyForm(eventCreateForm)
                .session("connected", "12");
        artistEventCreateResult = Helpers.route(TestApplication.getApplication(), request);
    }

    @Then("^I am redirected back to my artists page with \"([^\"]*)\" as the url$")
    public void iAmRedirectedBackToMyArtistsPageWithAsTheUrl(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        if (artistEventCreateResult.redirectLocation().isPresent()){
            Assert.assertTrue(artistEventCreateResult.redirectLocation().get().contains(arg0));
        } else {
            fail();
        }

    }
}
