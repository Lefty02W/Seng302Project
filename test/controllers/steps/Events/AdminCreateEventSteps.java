package controllers.steps.Events;

import controllers.TestApplication;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.junit.Ignore;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import repository.ArtistRepository;
import scala.Int;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class AdminCreateEventSteps {

    private Map<String, String> eventForm = new HashMap<>();
    private Result artistCreateEventResult;

    @And("^artist \"([^\"]*)\" is verified$")
    public void artistIsVerified(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        try{
            Assert.assertTrue(TestApplication.getArtistRepository().isArtistAdmin(Integer.parseInt(arg0)));
        } catch (Exception e){
            fail();
        }

    }

    @When("^admin enters \"([^\"]*)\" for event name$")
    public void adminEntersForEventName(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        eventForm.put("eventName", arg0);
    }

    @And("^admin selects \"([^\"]*)\" for event type$")
    public void adminSelectsForEventType(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        eventForm.put("typeForm", arg0);
    }

    @And("^admin selects \"([^\"]*)\" for age restriction$")
    public void adminSelectsForAgeRestriction(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        eventForm.put("ageForm", arg0);
    }

    @And("^admin selects destination (\\d+) for event destination$")
    public void adminSelectsDestinationForEventDestination(int arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        eventForm.put("destinationId", Integer.toString(arg0));
    }

    @And("^admin selects \"([^\"]*)\" for event genre$")
    public void adminSelectsForEventGenre(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        eventForm.put("genreForm", arg0);
    }

    @And("^admin selects artist (\\d+) for event artist$")
    public void adminSelectsArtistForEventArtist(int arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        eventForm.put("artistDropDown", Integer.toString(arg0));
    }

    @And("^admin enters \"([^\"]*)\" for event start date$")
    public void adminEntersForEventStartDate(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        eventForm.put("startDate", arg0);
    }

    @And("^admin enters \"([^\"]*)\" for event end date$")
    public void adminEntersForEventEndDate(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        eventForm.put("endDate", arg0);
    }

    @And("^admin enter \"([^\"]*)\" for event description$")
    public void adminEnterForEventDescription(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        eventForm.put("description", arg0);
    }

    @Then("^the admin presses the save event$")
    public void theAdminPressesTheSaveEvent() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest()
                .method("POST")
                .uri("/admin/events/create")
                .bodyForm(eventForm)
                .session("connected", "2");
        artistCreateEventResult = Helpers.route(TestApplication.getApplication(), requestBuilder);

    }

    @And("^the admin is redirected to \"([^\"]*)\"$")
    public void theAdminIsRedirectedTo(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        if (artistCreateEventResult.redirectLocation().isPresent()){
            Assert.assertTrue(artistCreateEventResult.redirectLocation().get().contains(arg0));
        } else {
            fail();
        }
    }

    @And("^the event has been successfully created$")
    public void theEventHasBeenSuccessfullyCreated() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        assertTrue(artistCreateEventResult.flash().getOptional("info").isPresent());
    }
}
