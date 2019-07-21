package controllers.steps.Admin;

import controllers.ProvideApplication;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Destination;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class AdminEditTreasureHuntSteps  extends ProvideApplication {

    Map<String, String> loginForm = new HashMap<>();
    Map<String, String> huntForm = new HashMap<>();
    Result loginResult;


    @When("^admin selects edit on treasure hunt (\\d+)$")
    public void adminSelectsEditOnTreasureHunt(int arg0) throws Throwable {
        huntForm.put("treasureHuntId", Integer.toString(arg0));
    }

    @And("^changes the riddle to \"([^\"]*)\"$")
    public void changesTheRiddleTo(String arg0) throws Throwable {
        huntForm.put("riddle", "A concrete jungle");
    }

    @And("^selects the the save treasure hunt button$")
    public void selectsTheTheSaveTreasureHuntButton() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/admin/hunts/2/edit")
                .session("connected", "2");
        Result result = Helpers.route(provideApplication(), request);

        assertEquals(200, result.status());
    }
}
