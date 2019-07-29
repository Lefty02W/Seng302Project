package controllers.steps.Admin;

import controllers.ProvideApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class AdminDeleteHuntSteps extends ProvideApplication {

    private Result result;

    @When("^Press the delete button on treasure hunt \"([^\"]*)\"$")
    public void pressTheDeleteButtonOnTreasureHunt(String arg0) throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/hunts/" + arg0 + "/delete")
                .session("connected", "2");
        result = Helpers.route(provideApplication(), request);
    }

    @Then("^I am be redirected back to the admin page$")
    public void iAmBeRedirectedBackToTheAdminPage() throws Throwable {
        if (result.redirectLocation().isPresent()) {
            assertEquals("/admin", result.redirectLocation().get());
        } else {
            fail();
        }
    }

    @And("^The treasure hunt is deleted from the database$")
    public void theTreasureHuntIsDeletedFromTheDatabase() throws Throwable {
        injectRepositories();
        assertNull(treasureHuntRepository.lookup(4));
    }
}