package controllers.steps.Admin;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import models.Artist;
import play.mvc.Http;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.*;

public class AdminArtistsRequestsSteps {

    @Given("^A regular user creates a new artist with name \"([^\"]*)\"$")
    public void aRegularUserCreatesANewArtistWithName(String arg0) throws Throwable {
        Map<String, String> artistForm = new HashMap<>();
        artistForm.put("artistName", arg0);
        artistForm.put("members", "James");
        artistForm.put("biography", "A big band");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/artists")
                .bodyForm(artistForm)
                .session("connected", "1");

        Helpers.route(TestApplication.getApplication(), request);
    }

    @And("^An admin navigates to the artists requests table on the admin page$")
    public void anAdminNavigatesToTheArtistsRequestsTableOnTheAdminPage() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin")
                .session("connected", "2");

        Helpers.route(TestApplication.getApplication(), request);
    }

    @Then("^The new artist \"([^\"]*)\" is in the table$")
    public void theNewArtistIsInTheTable(String arg0) throws Throwable {
        for (Artist artist : TestApplication.getArtistRepository().getInvalidArtists()) {
            if (artist.getArtistName().equals(arg0)) {
                assertTrue(true);
            }
        }
    }

    @And("^Artist with id (\\d+) is pending admin approval$")
    public void artistWithIdIsPendingAdminApproval(int arg0) throws Throwable {
        Artist artist = TestApplication.getArtistRepository().getArtist(arg0);
        assertEquals(0, artist.getVerified());
    }

    @And("^The admin accepts the request for artist (\\d+)$")
    public void theAdminAcceptsTheRequestForArtist(int arg0) throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/artist/verify/" + arg0)
                .session("connected", "2");

        Helpers.route(TestApplication.getApplication(), request);
    }

    @Then("^Artist (\\d+) should no longer be pending approval$")
    public void artistShouldNoLongerBePendingApproval(int arg0) throws Throwable {
        Artist artist = TestApplication.getArtistRepository().getArtist(arg0);
        assertEquals(1, artist.getVerified());
    }

    @And("^The admin declines the request for artist (\\d+)$")
    public void theAdminDeclinesTheRequestForArtist(int arg0) throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/artist/decline/" + arg0)
                .session("connected", "2");

        Helpers.route(TestApplication.getApplication(), request);
    }

    @Then("^Artist (\\d+) should no longer be in the database$")
    public void artistShouldNoLongerBeInTheDatabase(int arg0) throws Throwable {
        assertNull( TestApplication.getArtistRepository().getArtist(arg0));
    }
}
