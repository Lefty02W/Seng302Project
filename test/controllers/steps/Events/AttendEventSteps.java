package controllers.steps.Events;

import controllers.TestApplication;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.assertTrue;

public class AttendEventSteps {

    private Result eventResult;

    @Given("^I am on the event page$")
    public void iAmOnTheEventPage() throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/events/0")
                .session("connected", "2");
        Helpers.route(TestApplication.getApplication(), requestDest);
    }

    @And("^I attend an event I am not currently attending$")
    public void iAttendAnEventIAmNotCurrentlyAttending() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/events/13/attend")
                .session("connected", "2");
        eventResult = Helpers.route(TestApplication.getApplication(), request);
    }

    @And("^I leave an event I am currently attending$")
    public void iLeaveAnEventIAmCurrentlyAttending() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/events/13/leave")
                .session("connected", "2");
        eventResult = Helpers.route(TestApplication.getApplication(), request);
    }

    @And("^I am redirected with an \"([^\"]*)\" flash$")
    public void iAmRedirectedWithAnFlash(String arg0) throws Throwable {

        assertTrue(eventResult.flash().getOptional(arg0).isPresent());
    }
}