package controllers.steps.Admin;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.TreasureHunt;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class AdminCreateTreasureHuntSteps{

    private Map<String, String> huntForm;
    private Result redirectDestination;

    @When("^Press the create treasure hunt button$")
    public void pressTheCreateTreasureHuntButton() throws Throwable {
        huntForm = new HashMap<>();
    }

    @And("^I enter \"([^\"]*)\" as the \"([^\"]*)\"$")
    public void iEnterAsThe(String arg0, String arg1) throws Throwable {
        huntForm.put(arg1, arg0);
    }

    @When("^I save the treasure hunt$")
    public void iSaveTheTreasureHunt() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/admin/hunts/create")
                .bodyForm(huntForm)
                .session("connected", "2");
        redirectDestination = Helpers.route(TestApplication.getApplication(), request);
    }

    @Then("^I should be redirected back to the admin page with a valid notification$")
    public void iShouldBeRedirectedBackToTheAdminPage() throws Throwable {
        if (redirectDestination.redirectLocation().isPresent()) {
            Assert.assertTrue(redirectDestination.flash().getOptional("info").isPresent());
        } else {
            fail();
        }
    }

    @And("^The treasure hunt is saved to the database$")
    public void theTreasureHuntIsSavedToTheDatabase() throws Throwable {
        List<TreasureHunt> hunts = TestApplication.getTreasureHuntRepository().getAllUserTreasureHunts(3);
        assertNotNull(hunts);
        boolean found = false;
        for (TreasureHunt hunt : hunts) {
            if (hunt.getRiddle().equals("Another riddle")) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Then("^I should be redirected back to the admin page with an invalid notification$")
    public void iShouldBeRedirectedBackToTheAdminPageWithAnInvalidNotification() throws Throwable {
        if (redirectDestination.redirectLocation().isPresent()) {
            Assert.assertTrue(redirectDestination.flash().getOptional("error").isPresent());
        } else {
            fail();
        }
    }

    @And("^The treasure hunt is not saved to the database$")
    public void theTreasureHuntIsNotSavedToTheDatabase() throws Throwable {
        List<TreasureHunt> hunts = TestApplication.getTreasureHuntRepository().getAllUserTreasureHunts(3);
        assertNotNull(hunts);
        boolean found = false;
        for (TreasureHunt hunt : hunts) {
            if (!hunt.getRiddle().equals("Welcome to the Jungle")) {
                found = true;
            }
        }
        assertTrue(found);
    }
}
