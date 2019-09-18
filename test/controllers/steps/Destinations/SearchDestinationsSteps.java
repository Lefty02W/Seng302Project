package controllers.steps.Destinations;

import controllers.TestApplication;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SearchDestinationsSteps {
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> destForm = new HashMap<>();
    private Map<String, String> searchForm = new HashMap<>();
    private Result redirectDestination;


    @And("the search result is empty")
    public void theSearchResultIsEmpty() {
        throw new cucumber.api.PendingException();
    }

    @When("user submits empty search")
    public void userSubmitsEmptySearch() {
        searchForm.put("name", "");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
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
                .method("POST")
                .uri("/destinations/search/0")
                .bodyForm(searchForm)
                .session("connected", "1");
        redirectDestination = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(303, redirectDestination.status());
    }

    @Then("^the destination \"([^\"]*)\" is displayed in the search result$")
    public void theDestinationIsDisplayedInTheSearchResult(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^user searches for a public destination with name \"([^\"]*)\"$")
    public void userSearchesForAPublicDestinationWithName(String name) throws Throwable {
        searchForm.put("isPublic", "true");
        searchForm.put("name", name);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations/search/0")
                .bodyForm(searchForm)
                .session("connected", "1");
        redirectDestination = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(303, redirectDestination.status());
    }

    @Then("^the destination \"([^\"]*)\" is not displayed in the search result$")
    public void theDestinationIsNotDisplayedInTheSearchResult(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
