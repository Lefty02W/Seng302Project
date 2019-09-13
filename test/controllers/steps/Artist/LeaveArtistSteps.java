package controllers.steps.Artist;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Artist;
import models.Profile;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

public class LeaveArtistSteps {

    private Result REDIRECT_RESULT;
    private final String REDIRECT_LOCATION = "/artists";
    private final int PROFILE_ID = 2;

    @Given("^I am on artist (\\d+) page$")
    public void iAmOnArtistPage(int arg0) throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/artists/" + arg0)
                .session("connected", "2");
        Helpers.route(TestApplication.getApplication(), requestDest);
    }

    @And("^I am an admin of artist (\\d+)$")
    public void iAmAnAdminOfArtist(int arg0) throws Throwable {
        Optional<Artist> artistOptional = TestApplication.getArtistRepository().getArtist(arg0);
        boolean valid = false;
        if (artistOptional.isPresent()) {

            for (Profile profile : artistOptional.get().getAdminsList()) {
                if (profile.getProfileId() == PROFILE_ID) {
                    valid = true;
                }
            }
        }
        assertTrue(valid);
    }

    @When("^I press the leave artist button$")
    public void iPressTheLeaveArtistButton() throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/artists/6/leave")
                .session("connected", "2");
        REDIRECT_RESULT = Helpers.route(TestApplication.getApplication(), requestDest);
    }

    @Then("^I am redirected to the artists page$")
    public void iAmRedirectedToTheArtistsPage() throws Throwable {
        if (REDIRECT_RESULT.redirectLocation().isPresent()) {
            assertEquals(REDIRECT_LOCATION, REDIRECT_RESULT.redirectLocation().get());
        } else {
            fail();
        }
    }

    @And("^I am no longer an admin of artist (\\d+)$")
    public void iAmNoLongerAnAdminOfArtist(int arg0) throws Throwable {
        Optional<Artist> artistOptional = TestApplication.getArtistRepository().getArtist(arg0);
        boolean valid = true;
        if (artistOptional.isPresent()) {

            for (Profile profile : artistOptional.get().getAdminsList()) {
                if (profile.getProfileId() == PROFILE_ID) {
                    valid = false;
                }
            }
        }
        assertTrue(valid);
    }

}
