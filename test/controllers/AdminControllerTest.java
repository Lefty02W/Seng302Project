package controllers;

import models.Artist;
import models.Profile;
import models.Trip;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminControllerTest {

    private Integer profileId = 2;

    @Before
    public void setUp() throws Exception {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "bob@gmail.com");
        formData.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(TestApplication.getApplication(), request);

        request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin")
                .session("connected", "2");
    }


    /**
     * Tests deleting a valid (one that exists) destination
     * from the admin page
     */
    @Test
    public void deleteValidDestination() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/destinations/1/delete")
                .session("connected", "2");
        Result result = Helpers.route(TestApplication.getApplication(), request);

        Assert.assertNotNull(TestApplication.getDestinationRepository().lookup(1));
    }


    /**
     * Test the deletion of a valid trip (i.e. one that exists)
     */
    @Test
    public void deleteValidTrip() {

        List<Trip> trips = Trip.find.all();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/trips/"+ trips.get(0).getId() + "/delete")
                .session("connected", profileId.toString());
        Result result = Helpers.route(TestApplication.getApplication(), request);

        Assert.assertTrue(result.flash().getOptional("info").isPresent());
    }

    /**
     * Test deleting an artist
     */
    @Test
    public void deleteValidArtist() {
        List<Artist> artists = TestApplication.getArtistRepository().getAllArtists();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/artist/" + artists.get(0).getArtistId() + "/delete")
                .session("connected", profileId.toString());
        Result result = Helpers.route(TestApplication.getApplication(), request);

        Assert.assertTrue(result.flash().getOptional("info").isPresent());
    }

    /**
     * Test deleting a non-existent profile, the application should not crash
     */
    @Test
    public void deleteInvalidProfile() {

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/-1/delete")
                .session("connected", profileId.toString());
        Result result = Helpers.route(TestApplication.getApplication(), request);


        Assert.assertEquals(303, result.status());
    }

    /**
     * Test a profile deletion by retrieving all profiles
     * and deleting the first.
     */
    @Test
    public void deleteProfile() {

        List<Profile> profiles = TestApplication.getProfileRepository().getAll();
        Integer originalSize = profiles.size();

        Profile toDelete = profiles.get(4);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/"+ toDelete.getProfileId() + "/delete")
                .session("connected", profileId.toString());
        Result result = Helpers.route(TestApplication.getApplication(), request);
        Integer expected = originalSize - 1;
        Integer newSize = TestApplication.getProfileRepository().getAll().size();
        Assert.assertEquals(expected, newSize);

        //Add the profile back to the DB so that other tests can use it.
        TestApplication.getProfileRepository().insert(toDelete);

    }


    /**
     * Test deleting a non-existent profile, the application should not crash
     */
    @Test
    public void deleteInvalidArtist() {

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/artist/-1/delete")
                .session("connected", profileId.toString());
        Result result = Helpers.route(TestApplication.getApplication(), request);


        Assert.assertEquals(303, result.status());
    }


    @Test
    public void adminPageAcceptDestinationRequest() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/destinations/1/request/accept")
                .session("connected", profileId.toString());
        Result result = Helpers.route(TestApplication.getApplication(), request);
        Assert.assertTrue(result.flash().getOptional("info").isPresent());

    }

    @Test
    public void adminPageDeclineDestinationRequest() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/destinations/2/request/reject")
                .session("connected", profileId.toString());
        Result result = Helpers.route(TestApplication.getApplication(), request);
        Assert.assertTrue(result.flash().getOptional("info").isPresent());

    }

}
