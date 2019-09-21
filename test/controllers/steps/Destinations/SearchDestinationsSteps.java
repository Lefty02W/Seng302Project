package controllers.steps.Destinations;

import controllers.TestApplication;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Destination;
import org.junit.Assert;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import repository.DestinationRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SearchDestinationsSteps {
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> destForm = new HashMap<>();
    private Map<String, String> searchForm = new HashMap<>();
    private Result redirectDestination;
    private DestinationRepository destinationRepository = TestApplication.getDestinationRepository();

    @When("user submits empty search")
    public void userSubmitsEmptySearch() {
        searchForm.put("name", "");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/search/0")
                .bodyForm(searchForm)
                .session("connected", "1");
        redirectDestination = Helpers.route(TestApplication.getApplication(), request);
        assertEquals(303, redirectDestination.status());
    }


    @Then("an error message should be shown telling the user to enter a name")
    public void anErrorMessageShouldBeShownTellingTheUserToEnterAName() {
        Assert.assertTrue(redirectDestination.flash().getOptional("error").isPresent());
    }


    @When("^user searches for a private destination with name \"([^\"]*)\"$")
    public void userSearchesForAPrivateDestinationWithName(String name) throws Throwable {
        searchForm.put("isPublic", "false");
        searchForm.put("name", name);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/search/0")
                .bodyForm(searchForm)
                .session("connected", "1");
        redirectDestination = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(200, redirectDestination.status());
    }

    @Then("^the private destination \"([^\"]*)\" is displayed in the search result$")
    public void thePrivateDestinationIsDisplayedInTheSearchResult(String name) throws Throwable {
        List<Destination> destinationList = destinationRepository.searchDestinations(name, 0, false, 1);
        assertEquals(true, destinationInList(name, destinationList));
    }

    @Then("^the public destination \"([^\"]*)\" is displayed in the search result$")
    public void thePublicDestinationIsDisplayedInTheSearchResult(String name) throws Throwable {
        List<Destination> destinationList = destinationRepository.searchDestinations(name, 0, true, 1);
        assertEquals(true, destinationInList(name, destinationList));
    }

    @When("^user searches for a public destination with name \"([^\"]*)\"$")
    public void userSearchesForAPublicDestinationWithName(String name) throws Throwable {
        searchForm.put("isPublic", "true");
        searchForm.put("name", name);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/search/0")
                .bodyForm(searchForm)
                .session("connected", "1");
        redirectDestination = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(200, redirectDestination.status());
    }


    /**
     * Check if a destination list contains a destination of matching name
     * @param name - Name of destination
     * @param destinationList - List of destinations found from search
     * @return boolean - True if destination in list, false otherwise
     */
    private boolean destinationInList(String name, List<Destination> destinationList) {
        for (Destination destination: destinationList) {
            if (destination.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Then("^the private destination \"([^\"]*)\" is not displayed in the search result$")
    public void thePrivateDestinationIsNotDisplayedInTheSearchResult(String name) throws Throwable {
        List<Destination> destinationList = destinationRepository.searchDestinations(name, 0, false, 1);
        assertEquals(false, destinationInList(name, destinationList));
    }

    @Then("^the public destination \"([^\"]*)\" is not displayed in the search result$")
    public void thePublicDestinationIsNotDisplayedInTheSearchResult(String name) {
        List<Destination> destinationList = destinationRepository.searchDestinations(name, 0, true, 1);
        assertEquals(false, destinationInList(name, destinationList));
    }
}
