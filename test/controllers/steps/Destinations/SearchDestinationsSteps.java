package controllers.steps.Destinations;

import controllers.TestApplication;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

public class SearchDestinationsSteps {
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> destForm = new HashMap<>();
    private Result redirectDestination;

    @When("user searches for a public destination with name {string}")
    public void userSearchesForAPublicDestinationWithName(String name) {
        throw new cucumber.api.PendingException();
    }

    @When("uer searches for a private destination with name {string}")
    public void uerSearchesForAPrivateDestinationWithName(String name) {
        throw new cucumber.api.PendingException();
    }

    @Then("the destination {string} is displayed in the search result")
    public void theDestinationIsDisplayedInTheSearchResult(String name) {
        throw new cucumber.api.PendingException();
    }

}
