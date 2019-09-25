package controllers.steps.Artist;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.*;

public class RemoveArtistPhotoSteps {

    private Result REMOVE_PHOTO_RESULT;


    @Given("^I am on the detailed view for artist (\\d+)$")
    public void iAmOnTheDetailedViewForArtist(int arg0) throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/artists/"+arg0)
                .session("connected", "2");
        Helpers.route(TestApplication.getApplication(), requestDest);
    }

    @When("^I press the button to remove the artists profile picture$")
    public void iPressTheButtonToRemoveTheArtistsProfilePicture() throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/artists/1/remove/photo")
                .session("connected", "2");
        REMOVE_PHOTO_RESULT = Helpers.route(TestApplication.getApplication(), requestDest);
    }

    @Then("^I am redirected to the detailed view for artist (\\d+)$")
    public void iAmRedirectedToTheDetailedViewForArtist(int arg0) throws Throwable {
        if (REMOVE_PHOTO_RESULT.redirectLocation().isPresent()) {
            assertEquals("/artists/1", REMOVE_PHOTO_RESULT.redirectLocation().get());
        } else {
            fail();
        }
    }

    @And("^Artists (\\d+) profile picture has been removed$")
    public void artistsProfilePictureHasBeenRemoved(int arg0) throws Throwable {
        TestApplication.getArtistProfilePictureRepository().getArtistProfilePicture(arg0).thenApplyAsync(optional -> {
            assertFalse(optional.isPresent());
            return 1;
        });
    }
}
