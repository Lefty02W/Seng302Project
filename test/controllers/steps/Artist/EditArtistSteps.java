package controllers.steps.Artist;

import controllers.TestApplication;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Artist;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.*;
import static org.junit.Assert.*;


public class EditArtistSteps {

    private Map<String, String> artistForm = new HashMap<>();
    private List<String> ARTIST_GENRES = new ArrayList<>(Arrays.asList("Alternative", "Rock"));
    private List<String> ARTIST_COUNTRIES = new ArrayList<>(Arrays.asList("New Zealand", "Papua New Guinea"));
    private List<Integer> ARTIST_ADMINS = new ArrayList<>(Arrays.asList(1, 2));

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

}




