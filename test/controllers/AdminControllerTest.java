package controllers;

import models.Profile;
import models.Trip;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.List;

public class AdminControllerTest extends ProvideApplication {

    private Integer profileId;

    @Before
    public void setUp() throws Exception {
        profileId = adminLogin();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin")
                .session("connected", profileId.toString());;

        injectRepositories();
    }


    /**
     * Tests deleting a valid (one that exists) destination
     * from the admin page
     */
    @Test
    public void deleteValidDestination() {
        injectRepositories();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/destinations/1/delete")
                .session("connected", "1");
        Result result = Helpers.route(provideApplication(), request);

        Assert.assertNotNull(destinationRepository.lookup(1));
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
        Result result = Helpers.route(provideApplication(), request);

        Assert.assertTrue(result.flash().getOptional("info").isPresent());
    }


    /**
     * Test a profile deletion by retrieving all profiles
     * and deleting the first.
     */
    @Test
    public void deleteProfile() {

        List<Profile> profiles = Profile.find.all();
        Integer originalSize = profiles.size();

        Profile toDelete = profiles.get(4);
        profiles = profileRepository.getAll();
        Integer newSize = profiles.size();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/"+ toDelete.getProfileId() + "/delete")
                .session("connected", profileId.toString());
        Result result = Helpers.route(provideApplication(), request);
        Integer expected = originalSize - 1;
        Assert.assertEquals(expected, newSize);

        //Add the profile back to the DB so that other tests can use it.
        profileRepository.insert(toDelete);

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
        Result result = Helpers.route(provideApplication(), request);


        Assert.assertEquals(303, result.status());
    }
}