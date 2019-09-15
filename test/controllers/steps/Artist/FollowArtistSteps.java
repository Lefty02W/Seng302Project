package controllers.steps.Artist;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Artist;
import models.MusicGenre;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.*;

import static org.junit.Assert.*;


public class FollowArtistSteps {
    private Integer artistId;
    private Result redirect;
    private Integer initialfollowerCount;

    @And("user selects an artist with id {string}")
    public void userSelectsAnArtistWithId(String arg0) {
        artistId = Integer.parseInt(arg0);
        initialfollowerCount = TestApplication.getArtistRepository().getNumFollowers(artistId);
    }

    @And("user presses follow artist")
    public void userPressesFollowArtist() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/artist/"+artistId+"/follow")
                .session("connected", "1");
        redirect = Helpers.route(TestApplication.getApplication(), request);
    }

    @Then("link is updated flashing is shown")
    public void theArtistFollowLinkIsSavedIntoTheDatabase() {
        Assert.assertTrue(redirect.flash().getOptional("info").isPresent());

    }

    @And("the user has followed this artist")
    public void theUserHasFollowedThisArtist() {
    }

    @And("user presses un follow artist")
    public void userPressesUnfollowArtist() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/artist/"+artistId+"/unfollow")
                .session("connected", "1");
        redirect = Helpers.route(TestApplication.getApplication(), request);
    }

    @And("the artist's follower count has increased by {int}")
    public void theArtistSFollowerCountHasIncreasedBy(int increase) {
        Assert.assertEquals(initialfollowerCount + increase, TestApplication.getArtistRepository()
                .getNumFollowers(artistId));
    }


    @And("the artist's follower count has decreased by {int}")
    public void theArtistSFollowerCountHasDecreasedBy(int decrease) {
        Assert.assertEquals(initialfollowerCount - decrease, TestApplication.getArtistRepository()
                .getNumFollowers(artistId));
    }
}




