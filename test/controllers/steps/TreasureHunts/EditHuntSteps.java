package controllers.steps.TreasureHunts;

import controllers.ProvideApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.TreasureHunt;
import org.junit.Ignore;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

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

    @And("^I set the end date to \"([^\"]*)\"$")
    public void iSetTheEndDateTo(String arg0) throws Throwable {
        huntForm.put("endDate", arg0);
    }

    @And("^The end date is updated in the database$")
    public void theEndDateIsUpdatedInTheDatabase() throws Throwable {
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(1);
        assertEquals("2020-12-30", hunt.getEndDateString());
    }

    @And("^I set the riddle to \"([^\"]*)\"$")
    public void iSetTheRiddleTo(String arg0) throws Throwable {
        huntForm.put("riddle", arg0);
    }

    @And("^The riddle is updated in the database$")
    public void theRiddleIsUpdatedInTheDatabase() throws Throwable {
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(1);
        assertEquals("A new riddle", hunt.getRiddle());
    }

    @And("^I set the start date to \"([^\"]*)\"$")
    public void iSetTheStartDateTo(String arg0) throws Throwable {
        huntForm.put("startDate", arg0);
    }

    @And("^The start date is updated in the database$")
    public void theStartDateIsUpdatedInTheDatabase() throws Throwable {
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(1);
        assertEquals("2000-12-30", hunt.getStartDateString());
    }

    @Ignore
    @And("^The edit is not saved to the database$")
    public void theEditIsNotSavedToTheDatabase() throws Throwable {
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(1);
        assertNotEquals("2020-12-30", hunt.getStartDateString());
        assertNotEquals("2000-12-30", hunt.getEndDateString());
    }
}
