package controllers.steps.Artist;

import controllers.ProvideApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Artist;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


public class CreateArtistSteps extends ProvideApplication {


    private Map<String, String> artistForm = new HashMap<>();
    private Map<String, String> dupArtistFrom = new HashMap<>();
    private Result dupResult;


    @When("^user is at the artist page$")
    public void userIsAtTheArtistPage() throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/artists")
                .session("connected", "1");
        Helpers.route(provideApplication(), requestDest);
    }

    @And("^user enters \"([^\"]*)\" for artist name$")
    public void userEntersForArtistName(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        artistForm.put("artistName", arg0);
    }

    @And("^user enters \"([^\"]*)\" for artist genres$")
    public void userEntersForArtistGenres(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        artistForm.put("genreForm", arg0);
    }

    @And("^user enters \"([^\"]*)\" for artist members$")
    public void userEntersForArtistMembers(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        artistForm.put("members", arg0);
    }

    @And("^user enters \"([^\"]*)\" for artist bio$")
    public void userEntersForArtistBio(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        artistForm.put("biography", arg0);
    }

    @And("^user enters \"([^\"]*)\" for artist country$")
    public void userEntersForArtistCountry(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        artistForm.put("countries", arg0);
    }

    @And("^user presses save artist$")
    public void userPressesSaveArtist() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/artists")
                .bodyForm(artistForm)
                .session("connected", "1");

        Helpers.route(provideApplication(), request);
    }

    @Then("^the artist is saved in the database$")
    public void theArtistIsSavedInTheDatabase() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        injectRepositories();
        List<Artist> userArtists = artistRepository.getAllUserArtists(1);
        if (userArtists.size() > 0) {

            Artist newArtist = userArtists.get(userArtists.size() - 1);
            assertEquals(newArtist.getArtistName(), "King Crimson");

        } else {
            fail();
        }


    }

    @Given("^I am on the artist create page$")
    public void iAmOnTheArtistCreatePage() throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/artists")
                .session("connected", "2");
        Helpers.route(provideApplication(), requestDest);
    }

    @And("^I enter \"([^\"]*)\" into the \"([^\"]*)\" form field$")
    public void iEnterIntoTheFormField(String arg0, String arg1) throws Throwable {
        dupArtistFrom.put(arg1, arg0);
    }

    @And("^I enter \"([^\"]*)\" and \"([^\"]*)\" into the \"([^\"]*)\" form field$")
    public void iEnterAndIntoTheFormField(String arg0, String arg1, String arg2) throws Throwable {
        dupArtistFrom.put(arg2, arg0 + "," + arg1);
    }

    @And("^I submit the form$")
    public void iSubmitTheForm() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/artists")
                .bodyForm(dupArtistFrom)
                .session("connected", "2");
        dupResult = Helpers.route(provideApplication(), request);
    }

    @And("^There is a flashing sent with id \"([^\"]*)\"$")
    public void thereIsAFlashingSentWithId(String arg0) throws Throwable {
        assertTrue(dupResult.flash().getOptional("error").isPresent());
    }
}




