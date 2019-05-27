package controllers;

import models.Destination;
import models.Trip;
import org.junit.After;
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
    }


    @Test
    public void deleteValidDestination() {
        injectRepositories();
        List<Destination> destinationList = Destination.find.all();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/destinations/"+destinationList.get(0).getDestinationId()+"/delete")
                .session("connected", profileId.toString());
        Result result = Helpers.route(provideApplication(), request);

        Assert.assertTrue(result.flash().getOptional("info").isPresent());
    }


    @Test
    public void deleteValidTrip() {
        injectRepositories();
        List<Trip> trips = Trip.find.all();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/trips/"+ trips.get(0).getId() + "/delete")
                .session("connected", profileId.toString());
        Result result = Helpers.route(provideApplication(), request);

        Assert.assertTrue(result.flash().getOptional("info").isPresent());
    }
}