package controllers.steps.Artist;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Artist;
import models.MusicGenre;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.*;

import static org.junit.Assert.*;


public class CreateArtistSteps {


    private Map<String, String> artistForm = new HashMap<>();
    private Map<String, String> dupArtistFrom = new HashMap<>();
    private Result dupResult;
    private Artist foundArtist;
    private List<String> ARTIST_GENRES = new ArrayList<>(Arrays.asList("Rock", "Indie"));


    @When("^user is at the artist page$")
    public void userIsAtTheArtistPage() throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri("/artists")
                .session("connected", "1");
        Helpers.route(TestApplication.getApplication(), requestDest);
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

        Helpers.route(TestApplication.getApplication(), request);
    }

    @Then("^the artist is saved in the database$")
    public void theArtistIsSavedInTheDatabase() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        List<Artist> userArtists = TestApplication.getArtistRepository().getAllUserArtists(1);
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
        Helpers.route(TestApplication.getApplication(), requestDest);
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
        dupResult = Helpers.route(TestApplication.getApplication(), request);
    }

    @And("^There is a flashing sent with id \"([^\"]*)\"$")
    public void thereIsAFlashingSentWithId(String arg0) throws Throwable {
        assertTrue(dupResult.flash().getOptional("error").isPresent());
    }


    @Then("^The artist genre links are saved$")
    public void theArtistGenreLinksAreSaved() throws Throwable {
        List<Artist> artists = TestApplication.getArtistRepository().getAllUserArtists(2);
        System.out.println(artists);
        System.out.println(artists.size());
        for (Artist artist : artists) {
            System.out.println(artist.getArtistName());
            if (artist.getArtistName().equals("Jim James")) {
                foundArtist = artist;
            }
        }
        if (foundArtist != null) {
            Optional<List<MusicGenre>> optional = TestApplication.getGenreRepository().getArtistGenres(foundArtist.getArtistId());
            if (optional.isPresent()) {
                for (MusicGenre genre : optional.get()) {
                    if (!ARTIST_GENRES.contains(genre.getGenre())) {
                        fail();
                    }
                }
            } else {
                fail();
            }
        } else {
            fail();
        }
    }
}




