package controllers.steps.Events;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Events;
import models.MusicGenre;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.*;

import static org.junit.Assert.*;

public class EditEventsSteps {

    private Map<String, String> editForm = new HashMap<>();
    private Result REDIRECT_RESULT;
    private final String REDIRECT_LOCATION = "/artists/1/events/0";
    private final String EXPECTED_NAME = "Mono";
    private final int EXPECTED_DESTINATION_ID = 13;
    private final int EXPECTED_AGE_RESTRICTION = 16;
    private final List<Integer> EXPECTED_GENRES = new ArrayList<>(Arrays.asList(2, 3));

    @When("^I select event (\\d+) to edit$")
    public void iSelectEventToEdit(int arg0) {
        Events event = TestApplication.getEventRepository().lookup(arg0);
        if (event != null) {
            editForm.put("eventName", event.getEventName());
            editForm.put("description", event.getDescription());
            editForm.put("destinationId", Integer.toString(event.getDestinationId()));
            editForm.put("startDate", event.formatLocalDate(event.getStartDate()));
            editForm.put("endDate", event.formatLocalDate(event.getEndDate()));
            editForm.put("ageForm", "0");
            editForm.put("genreForm", "1,2");
            editForm.put("artistForm", "2");
            editForm.put("typeForm", "Gig");
        } else {
            fail();
        }
    }

    @And("^I change event field \"([^\"]*)\" to \"([^\"]*)\"$")
    public void iChangeEventFieldTo(String arg0, String arg1) {
        editForm.put(arg0, arg1);
    }

    @And("^I save the edit of event (\\d+)$")
    public void iSaveTheEditOfEvent(int arg0) {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("POST")
                .uri("/artists/1/events/edit/" + arg0)
                .bodyForm(editForm)
                .session("connected", "2");
        REDIRECT_RESULT = Helpers.route(TestApplication.getApplication(), requestDest);
    }

    @Then("^I am redirected to the events page$")
    public void iAmRedirectedToTheEventsPage() {
        if (REDIRECT_RESULT.redirectLocation().isPresent()) {
            assertEquals(REDIRECT_LOCATION, REDIRECT_RESULT.redirectLocation().get());
        } else {
            fail();
        }
    }

    @And("^The new event data is saved$")
    public void theNewEventDataIsSaved() {
        Events event = TestApplication.getEventRepository().lookup(5);
        if (event != null) {
            assertEquals(EXPECTED_NAME, event.getEventName());
            assertEquals(EXPECTED_AGE_RESTRICTION, event.getAgeRestriction());
            assertEquals(EXPECTED_DESTINATION_ID, event.getDestinationId());
            for (MusicGenre genre : event.getEventGenres()) {
                assertTrue(EXPECTED_GENRES.contains(genre.getGenreId()));
            }
        } else {
            fail();
        }
    }
}
