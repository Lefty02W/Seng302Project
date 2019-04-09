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
                .uri("/destinations")
                .session("connected", "admin@admin.com");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }


    /**
     * Testing the POST /destinations endpoint, passing a mocked form to post a destination
     */
    @Test
    public void postDestination() {
        loginUser();
        Map<String, String> formData = new HashMap<>();
        formData.put("name", "china");
        formData.put("type", "country");
        formData.put("district", "china");
        formData.put("country", "China");
        formData.put("latitude", "78.95");
        formData.put("longitude", "105.67");

        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations")
                .bodyForm(formData)
                .session("connected", "admin@admin.com");

        Result result = Helpers.route(provideApplication(), request);


        assertEquals(303, result.status());
    }

    /**
     * Testing trying to edit a destination that does not exists
     */
    //@Test //TODO make provide application add a destination so it can be used for testing
    public void showEditDestination() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/42/edit")
                .session("connected", "admin@admin.com");

        Result result = Helpers.route(provideApplication(), request);


        assertEquals(200, result.status());
    }

}