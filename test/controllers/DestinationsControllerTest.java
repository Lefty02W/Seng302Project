package controllers;

import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;

/**
 * Testing endpoints related to the destination controller
 */
public class DestinationsControllerTest extends ProvideApplication {

    /**
     * Testing the GET /destinations/create endpoint
     */
    @Test
    public void showDestinationCreateEndPoint() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/destinations/create")
                .session("connected", "admin@admin.com");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }


    /**
     * Testing the GET /destinations endpoint
     */
    @Test
    public void showDestinationsEndPoint() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/destinations/show/false")
                .session("connected", "admin@admin.com");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }




    /**
     * Testing trying to editDestinations a destination that does not exists
     */
    //@Test //TODO make provide application add a destination so it can be used for testing
    public void showEditDestination() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/42/editDestinations")
                .session("connected", "admin@admin.com");

        Result result = Helpers.route(provideApplication(), request);


        assertEquals(200, result.status());
    }

}