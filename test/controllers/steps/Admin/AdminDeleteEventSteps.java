package controllers.steps.Admin;

import controllers.TestApplication;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import play.mvc.Http;
import play.test.Helpers;

import static junit.framework.TestCase.assertEquals;

public class AdminDeleteEventSteps {


    @When("^I delete event (\\d+)$")
    public void iDeleteEvent(int arg0) throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/events/0/delete/" + arg0)
                .session("connected", "2");
        Helpers.route(TestApplication.getApplication(), requestDest);
    }


    @Then("^Event (\\d+) is marked as soft deleted$")
    public void eventIsMarkedAsSoftDeleted(int arg0) throws Throwable {
        TestApplication.getEventRepository().getEvent(arg0).thenApplyAsync(events -> {
            events.ifPresent(events1 -> assertEquals(1, events1.getSoftDelete()));
           return 0;
        });
    }
}
