package controllers.steps.Admin;

import controllers.ProvideApplication;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Destination;
import models.TreasureHunt;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AdminEditTreasureHuntSteps  extends ProvideApplication {

    Map<String, String> huntForm = new HashMap<>();
    private Result redirectDestination;

    @When("^admin presses edit on one of the treasure hunts$")
    public void adminPressesEditOnOneOfTheTreasureHunts() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/hunts/2/edit/show")
                .session("connected", "1");
        Result result = Helpers.route(provideApplication(), request);
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(2);
        huntForm.put("riddle", hunt.getRiddle());
        huntForm.put("destinationId", Integer.toString(hunt.getTreasureHuntDestinationId()));
        huntForm.put("endDate", hunt.getEndDateString());
        huntForm.put("startDate", hunt.getStartDateString());
    }

    @And("^admin selects edit on treasure hunt (\\d+)$")
    public void adminSelectsEditOnTreasureHunt(int arg0) throws Throwable {
        huntForm.put("destinationId", Integer.toString(arg0));
    }

    @And("^changes the riddle to \"([^\"]*)\"$")
    public void changesTheRiddleTo(String arg0) throws Throwable {
        huntForm.put("riddle", arg0);
    }

    @And("^changes the start date to \"([^\"]*)\"$")
    public void changesTheStartDateTo(String arg0) throws Throwable {
        huntForm.put("startDate", arg0);
    }

    @And("^changes the end date to \"([^\"]*)\"$")
    public void changesTheEndDateTo(String arg0) throws Throwable {
        huntForm.put("endDate", arg0);
    }

    @And("^selects the the save treasure hunt button$")
    public void selectsTheTheSaveTreasureHuntButton() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/admin/hunts/2/edit")
                .bodyForm(huntForm)
                .session("connected", "2");
        redirectDestination = Helpers.route(provideApplication(), request);
    }

    @Then("^I am redirected to the admin page$")
    public void iAmRedirectedToTheTreasureHuntsPage() throws Throwable {
        if (redirectDestination.redirectLocation().isPresent()) {
            assertEquals("/admin", redirectDestination.redirectLocation().get());
        } else {
            Assert.fail();
        }
    }

    @Then("^The admins riddle is updated in the database$")
    public void theAdminsRiddleIsUpdatedInTheDatabase() throws Throwable {
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(2);
        assertEquals("A concrete jungle", hunt.getRiddle());
    }

    @Then("^The admins start date is updated in the database$")
    public void theAdminsStartDateIsUpdatedInTheDatabase() throws Throwable {
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(2);
        assertEquals("2019-01-04", hunt.getStartDateString());
    }

    @Then("^The admins end date is updated in the database$")
    public void theAdminsEndDateIsUpdatedInTheDatabase() throws Throwable {
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(2);
        assertEquals("2020-01-01", hunt.getEndDateString());
    }

    @Then("^The admins end date is not updated in the database$")
    public void theAdminsEndDateIsNotUpdatedInTheDatabase() throws Throwable {
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(2);
        assertNotEquals("2016-01-04", hunt.getEndDateString());
    }

    @Then("^The edit is not updated in the database$")
    public void theEditIsNotUpdatedInTheDatabase() throws Throwable {
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(2);
        assertNotEquals("2020-01-01", hunt.getStartDateString());
        assertNotEquals("2016-01-04", hunt.getEndDateString());
    }

    @Then("^I am redirected to the admin page with an invalid notification$")
    public void iAmRedirectedToTheAdminPageWithAnInvalidNotification() throws Throwable {
        Assert.assertTrue(redirectDestination.flash().getOptional("error").isPresent());
    }

    @Then("^I am redirected to the admin page with a valid notification$")
    public void iAmRedirectedToTheAdminPageWithAValidNotification() throws Throwable {
        Assert.assertTrue(redirectDestination.flash().getOptional("info").isPresent());
    }
}
