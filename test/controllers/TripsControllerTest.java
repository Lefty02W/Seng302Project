package controllers;


import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;


public class TripsControllerTest extends ProvideApplication {



    @Test
    public void showCreatePageEndPoint() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips/create");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }


    @Test
    public void showTripsPageEndPoint() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }


    @Test
    public void showEditPageEndPoint() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips/edit/0");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }

}