package controllers.steps.TreasureHunts;

import controllers.ProvideApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import models.TreasureHunt;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.assertNull;

public class DeleteHuntSteps extends ProvideApplication {

    @When("^I press delete on treasure hunt (\\d+)$")
    public void iPressDeleteOnTreasureHunt(int arg0) throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/hunts/2/delete")
                .session("connected", "1");
        Result result = Helpers.route(provideApplication(), request);
    }

    @And("^The treasure hunt is removed from the database$")
    public void theTreasureHuntIsRemovedFromTheDatabase() throws Throwable {
        injectRepositories();
        TreasureHunt hunt = treasureHuntRepository.lookup(2);
        assertNull(hunt);
    }
}
