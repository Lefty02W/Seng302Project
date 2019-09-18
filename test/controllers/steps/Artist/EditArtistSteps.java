package controllers.steps.Artist;

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

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.*;


public class EditArtistSteps {

    private Map<String, String> artistForm = new HashMap<>();
    private final List<String> EXPECTED_GENRES = new ArrayList<>(Arrays.asList("Rock", "Reggae"));
    private final List<String> EXPECTED_COUNTRIES = new ArrayList<>(Arrays.asList("Mexico", " Peru"));
    private final List<Integer> EXPECTED_ADMINS = new ArrayList<>(Arrays.asList(1, 2));

    @When("^user is at their detailed artist page with id \"([^\"]*)\"$")
    public void userIsAtTheirDetailedArtistPageWithId(String arg0) throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/artists/" + Integer.parseInt(arg0))
                .session("connected", "1");
        Helpers.route(TestApplication.getApplication(), request);

        Result result = Helpers.route(TestApplication.getApplication(), request);
        Artist artist = TestApplication.getArtistRepository().getArtistById(7);
        artistForm.put("artistName", artist.getArtistName());
        artistForm.put("members", artist.getMembers());
        artistForm.put("biography", artist.getBiography());
    }

    @And("^user changes artist name to \"([^\"]*)\"$")
    public void userChangesArtistNameTo(String arg0) throws Throwable {
        artistForm.put("artistName", arg0);
    }

    @And("^user changes members to \"([^\"]*)\"$")
    public void userChangesMembersTo(String arg0) throws Throwable {
        artistForm.put("members", arg0);
    }

    @And("^user changes biography to \"([^\"]*)\"$")
    public void userChangesBiographyTo(String arg0) throws Throwable {
        artistForm.put("biography", arg0);
    }

    @And("^user saves the edit of artist wih id \"([^\"]*)\"$")
    public void userSavesTheEditOfArtistWihId(String arg0) throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/artists/" + arg0 + "/edit")
                .bodyForm(artistForm)
                .session("connected", "1");

        Helpers.route(TestApplication.getApplication(), request);
    }

    @Then("^the artist changes are saved in the database$")
    public void theArtistChangesAreSavedInTheDatabase() throws Throwable {
        Artist artist = TestApplication.getArtistRepository().getArtistById(7);
        assertEquals("The Kings of the Amazon", artist.getArtistName());
        assertEquals("Buck, Canopy", artist.getMembers());
        assertEquals("Brand new band from the Amazon Rainforest", artist.getBiography());
    }

    @And("^user changes country to \"([^\"]*)\" and \"([^\"]*)\"$")
    public void userChangesCountryToAnd(String arg0, String arg1) throws Throwable {
        artistForm.put("countries", arg0 + ", " + arg1);
    }

    @Then("^the artist country changes are saved in the database$")
    public void theArtistCountryChangesAreSavedInTheDatabase() throws Throwable {
        Optional<Artist> artist = TestApplication.getArtistRepository().getArtist(7);
        if (!artist.isPresent()) {
            fail("Artist not found");
        } else {
            boolean valid = true;
            for (String country : artist.get().getCountryList()) {
                if (!EXPECTED_COUNTRIES.contains(country)) {
                    valid = false;
                }
            }
            assertTrue(valid);
        }
    }

    @And("^user changes genre to \"([^\"]*)\" and \"([^\"]*)\"$")
    public void userChangesGenreToAnd(String arg0, String arg1) throws Throwable {
        artistForm.put("genreForm", arg0 + "," + arg1);
    }

    @Then("^the artist genre changes are saved in the database$")
    public void theArtistGenreChangesAreSavedInTheDatabase() throws Throwable {
        Optional<List<MusicGenre>> genres = TestApplication.getGenreRepository().getArtistGenres(7);
        if (!genres.isPresent()) {
            fail("No genres found in profile");
        } else {
            boolean valid = true;
            for (MusicGenre genre : genres.get()) {
                if (!EXPECTED_GENRES.contains(genre.getGenre())) {
                    valid = false;
                }
            }
            assertTrue(valid);
        }
    }

    @And("^user changes admins to \"([^\"]*)\"$")
    public void userChangesAdminsTo(String arg0) throws Throwable {
        artistForm.put("adminForm", arg0);
    }

    @Then("^the artist admin changes are saved in the database$")
    public void theArtistAdminChangesAreSavedInTheDatabase() throws Throwable {
        Optional<Artist> artist = TestApplication.getArtistRepository().getArtist(7);
        if (!artist.isPresent()) {
            fail("Artist not found");
        } else {
            boolean valid = true;
            for (Profile profile : artist.get().getAdminsList()) {
                if (!EXPECTED_ADMINS.contains(profile.getProfileId())) {
                    valid = false;
                }
            }
            assertTrue(valid);
        }
    }

    @And("^user changes facebook link to \"([^\"]*)\"$")
    public void userChangesFacebookLinkTo(String arg0) throws Throwable {
        artistForm.put("facebookLink", arg0);
    }

    @And("^user changes instagram link to \"([^\"]*)\"$")
    public void userChangesInstagramLinkTo(String arg0) throws Throwable {
        artistForm.put("instagramLink", arg0);
    }

    @And("^user changes spotify link to \"([^\"]*)\"$")
    public void userChangesSpotifyLinkTo(String arg0) throws Throwable {
        artistForm.put("spotifyLink", arg0);
    }

    @And("^user changes twitter link to \"([^\"]*)\"$")
    public void userChangesTwitterLinkTo(String arg0) throws Throwable {
        artistForm.put("twitterLink", arg0);
    }

    @And("^user changes website link to \"([^\"]*)\"$")
    public void userChangesWebsiteLinkTo(String arg0) throws Throwable {
        artistForm.put("websiteLink", arg0);
    }

    @Then("^the artist link changes are saved in the database$")
    public void theArtistLinkChangesAreSavedInTheDatabase() throws Throwable {
        Artist artist = TestApplication.getArtistRepository().getArtistById(7);
        assertEquals("https://www.facebook.com/kingsofamazon", artist.getFacebookLink());
        assertEquals("https://www.instagram.com/kingsofamazon", artist.getInstagramLink());
        assertEquals("https://www.spotify.com/kingsofamazon", artist.getSpotifyLink());
        assertEquals("https://www.twitter.com/kingsofamazon", artist.getTwitterLink());
        assertEquals("https://www.amazon.com/kingsofamazon", artist.getWebsiteLink());
    }
}