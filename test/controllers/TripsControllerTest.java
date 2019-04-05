package controllers;


import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import repository.DestinationRepository;
import repository.TripDestinationsRepository;
import repository.TripRepository;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;


public class TripsControllerTest extends ProvideApplication {

    protected TripRepository tripRepository;
    protected DestinationRepository destinationRepository;
    protected TripDestinationsRepository tripDestinationsRepository;


    @Test
    public void showCreatePageEndPoint() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips/create")
                .session("connected", "admin");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }


    @Test
    public void showTripsPageEndPoint() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips")
                .session("connected", "admin");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }


    @Test
    public void showEditPageEndPoint() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips/214/edit")
                .session("connected", "admin");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }

    @Test
    public void deleteTripDestination() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/trips/edit/3/delete?id=214")
                .session("connected", "admin");

        Result result = Helpers.route(provideApplication(), request);
        assertEquals(303, result.status());

        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips/214/edit")
                .session("connected", "admin");

        result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }

    @Test
    public void checkTripDestDoesntExist() {

    }


}