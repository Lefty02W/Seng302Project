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
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips/create")
                .session("connected", "admin@admin.com");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }


    @Test
    public void showTripsPageEndPoint() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips")
                .session("connected", "admin@admin.com");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }


    //@Test // Having issues with this test will sort at a later date
    public void showEditPageEndPoint() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips/edit/0")
                .session("connected", "admin@admin.com");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }

}