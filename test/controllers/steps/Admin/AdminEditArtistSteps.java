package controllers.steps.Admin;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import models.Artist;
import models.MusicGenre;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.fail;

public class AdminEditArtistSteps {

    private Map<String, String> EDIT_FORM = new HashMap<>();
    private final int ARTIST_ID_TO_EDIT = 5;
    private final List<String> EXPECTED_GENRES = new ArrayList<>(Arrays.asList("Rock", "Indie"));

    @And("^I select artist (\\d+) to edit$")
    public void iSelectArtistToEdit(int arg0) throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/artist/" + arg0 + "/edit/show");
        Helpers.route(TestApplication.getApplication(), request);
        Optional<Artist> artist = TestApplication.getArtistRepository().getArtist(arg0);
        if (artist.isPresent()) {
            EDIT_FORM.put("artistName", artist.get().getArtistName());
            EDIT_FORM.put("biography", artist.get().getBiography());
            EDIT_FORM.put("countries", artist.get().getCountryListString());
            EDIT_FORM.put("members", artist.get().getMembers());
            EDIT_FORM.put("genreForm", artist.get().getGenre());
            EDIT_FORM.put("adminForm", "1,");
        }
    }

    @And("^I change the \"([^\"]*)\" to \"([^\"]*)\" and \"([^\"]*)\"$")
    public void iChangeTheToAnd(String arg0, String arg1, String arg2) throws Throwable {
        EDIT_FORM.put(arg0, arg1 + ", " + arg2);
    }

    @And("^I save the edit of artist \"([^\"]*)\"$")
    public void iSaveTheEditOfArtist(String arg0) throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/admin/artist/" + arg0 + "/edit")
                .bodyForm(EDIT_FORM)
                .session("connected", "2");
        Result result = Helpers.route(TestApplication.getApplication(), request);

        assertTrue(result.flash().getOptional("info").isPresent());
    }

    @Then("^The new genres are saved$")
    public void theNewGenresAreSaved() throws Throwable {
        Optional<List<MusicGenre>> genres =TestApplication.getGenreRepository().getArtistGenres(ARTIST_ID_TO_EDIT);
        if (genres.isPresent()) {
            boolean valid = true;
            for (MusicGenre genre : genres.get()) {
                if (!EXPECTED_GENRES.contains(genre.getGenre())) {
                    valid = false;
                }
            }
            assertTrue(valid);
        } else {
            fail("Genres not found");
        }
    }

}
