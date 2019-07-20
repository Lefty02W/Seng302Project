package controllers.steps.TreasureHunts;

import controllers.ProvideApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.TreasureHunt;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EditHuntSteps extends ProvideApplication {

    private Map<String, String> huntForm = new HashMap<>();
    private Result redirectDestination;


    @Given("^I am on the treasure hunts page$")
    public void iAmOnTheTreasureHuntsPage() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/treasure")
                .session("connected", "1");
        Result result = Helpers.route(provideApplication(), request);
        assertEquals(200, result.status());
    }

    @When("^I press edit on one of my treasure hunts$")
    public void iPressEditOnOneOfMyTreasureHunts() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/hunts/1/edit/show")
                .session("connected", "1");
        Result result = Helpers.route(provideApplication(), request);
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(1);
        huntForm.put("riddle", hunt.getRiddle());
        huntForm.put("destinationId", Integer.toString(hunt.getTreasureHuntDestinationId()));
        huntForm.put("endDate", hunt.getEndDateString());
        huntForm.put("startDate", hunt.getStartDateString());
    }

    @And("^I select destination (\\d+) from the dropdown$")
    public void iSelectDestinationFromTheDropdown(int arg0) throws Throwable {
        huntForm.put("destinationId", Integer.toString(arg0));
    }

    @When("^I press the save button to save the treasure hunt$")
    public void iPressTheSaveButtonToSaveTheTreasureHunt() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/hunts/1/edit")
                .bodyForm(huntForm)
                .session("connected", "1");
        redirectDestination = Helpers.route(provideApplication(), request);
    }

    @Then("^I am redirected to the treasure hunts page$")
    public void iAmRedirectedToTheTreasureHuntsPage() throws Throwable {
        if (redirectDestination.redirectLocation().isPresent()) {
            assertEquals("/treasure", redirectDestination.redirectLocation().get());
        } else {
            fail();
        }
    }

    @And("^The edit is saved to the database$")
    public void theEditIsSavedToTheDatabase() throws Throwable {
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(1);
        assertEquals(2, hunt.getTreasureHuntDestinationId());
    }
}
