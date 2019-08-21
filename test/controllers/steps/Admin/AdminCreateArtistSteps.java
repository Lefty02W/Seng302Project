package controllers.steps.Admin;

import controllers.TestApplication;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Artist;
import models.MusicGenre;
import models.Profile;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.*;

import static org.junit.Assert.*;


public class AdminCreateArtistSteps {

    private Result redirectDestination;
    private Map<String, String> artistForm;
    private Map<String, String> dupArtistFrom = new HashMap<>();
    private Result dupResult;
    private Artist foundArtist;
    private List<String> ARTIST_GENRES = new ArrayList<>(Arrays.asList("Rock", "Indie"));

    @Then("^the admin artist is saved in the database$")
    public void theAdminArtistIsSavedInTheDatabase() throws Throwable {
        List<Artist> userArtists = TestApplication.getArtistRepository().getAllUserArtists(2);

        assertNotNull(userArtists);
        boolean found = false;
        for (Artist artist : userArtists) {
            if (artist.getArtistName().equals("Green Day")) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @When("^admin presses the create artist button$")
    public void adminPressesTheCreateArtistButton() throws Throwable {
        artistForm = new HashMap<>();
    }

    @And("^admin enters \"([^\"]*)\" for artist name$")
    public void adminEntersForArtistName(String arg0) throws Throwable {
        artistForm.put("artistName", arg0);
    }

    @And("^admin enters \"([^\"]*)\" for artist genres$")
    public void adminEntersForArtistGenres(String arg0) throws Throwable {
        artistForm.put("genreForm", arg0);
    }

    @And("^admin enters \"([^\"]*)\" for artist members$")
    public void adminEntersForArtistMembers(String arg0) throws Throwable {
        artistForm.put("members", arg0);
    }

    @And("^admin enters \"([^\"]*)\" for artist bio$")
    public void adminEntersForArtistBio(String arg0) throws Throwable {
        artistForm.put("biography", arg0);
    }

    @And("^admin enters \"([^\"]*)\" for artist country$")
    public void adminEntersForArtistCountry(String arg0) throws Throwable {
        artistForm.put("countries", arg0);
    }

    @And("^admin presses save artist$")
    public void adminPressesSaveArtist() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/admin/artists/create")
                .bodyForm(artistForm)
                .session("connected", "2");
        redirectDestination = Helpers.route(TestApplication.getApplication(), request);
    }

    @And("^admin enters the user with email \"([^\"]*)\" for artist admins$")
    public void adminEntersTheUserWithEmailForArtistAdmins(String arg0) throws Throwable {
        Profile profile = TestApplication.getProfileRepository().getProfileById(arg0);
        artistForm.put("adminForm", profile.getProfileId().toString());
    }

    @And("^admin enters \"([^\"]*)\" for artist admin$")
    public void adminEntersForArtistAdmin(String arg0) throws Throwable {
        artistForm.put("adminForm", arg0);
    }
}




