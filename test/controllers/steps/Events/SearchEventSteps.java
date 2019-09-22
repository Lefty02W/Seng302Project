package controllers.steps.Events;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import models.EventFormData;
import models.Events;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import repository.EventRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SearchEventSteps {

    private Map<String, String> searchForm = new HashMap<>();
    private Result eventSearchResult;
    private EventRepository eventRepository = TestApplication.getEventRepository();


    @And("^I select attending from the advance search field$")
    public void iSelectAttendingFromTheAdvanceSearchField() throws Throwable {
        searchForm.put("attending", "on");
    }

    @And("^I submit the search form$")
    public void iSubmitTheSearchForm() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/events/all/search/0")
                .bodyForm(searchForm)
                .session("connected", "1");
        eventSearchResult = Helpers.route(TestApplication.getApplication(), request);
        assertEquals(200, eventSearchResult.status());
    }

    @Then("^a event is displayed that I am attending$")
    public void aEventIsDisplayedThatIAmAttending() throws Throwable {
        EventFormData eventForm = new EventFormData();
        eventForm.setAttending("on");
        List<Events>  result = eventRepository.searchEvent(eventForm, 0);
        assertEquals("Burning Man", result.get(0).getEventName());
    }

}
