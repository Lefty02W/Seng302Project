package controllers.steps.Events;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class CreateEventSteps {

    private Map<String, String> eventForm = new HashMap<>();
    private Result eventCreateResult;

    @Given("^I am on the events page$")
    public void iAmOnTheArtistCreatePage() throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/events")
                .session("connected", "2");
        Helpers.route(TestApplication.getApplication(), requestDest);
    }

    @And("^I put \"([^\"]*)\" into the \"([^\"]*)\" form field$")
    public void iEnterIntoTheFormField(String arg0, String arg1) throws Throwable {
        eventForm.put(arg1, arg0);
    }

    @And("^I submit the event form$")
    public void iSubmitTheEventForm() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/events/create")
                .bodyForm(eventForm)
                .session("connected", "2");
        eventCreateResult = Helpers.route(TestApplication.getApplication(), request);
    }

    @And("^The user is redirected with an \"([^\"]*)\" flash$")
    public void theUserIsRedirectedWithAnFlash(String arg0) throws Throwable {
        assertTrue(eventCreateResult.flash().getOptional(arg0).isPresent());
    }

}
