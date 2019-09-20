package controllers;

import models.Destination;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Testing endpoints related to the destination controller
 */
public class DestinationsControllerTest {

    private ArrayList<Destination> destinationList = TestApplication.getProfileRepository().getDestinations(1, 0).get();

    @Before
    public void setUp() throws Exception {

        Map<String, String> formData = new HashMap<>();
        formData.put("email", "john@gmail.com");
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
     * Testing edit destination modal page loads fine
     */
    @Test
    public void showEditDestination() {

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/" + destinationList.get(0).getDestinationId() + "/edit/show/" + destinationList.get(0).getVisible())
                .session("connected", "1");

        Result result = Helpers.route(TestApplication.getApplication(), request);


        assertEquals(200, result.status());
    }


    /**
     * Testing  page loads fine
     */
    @Test
    public void showPrivateDestinations() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/show/false/0")
                .session("connected", "1");

        Result result = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(200, result.status());
    }

    /**
     * Testing edit destination modal page loads fine
     */
    @Test
    public void showPublicDestinations() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/show/true/0")
                .session("connected", "1");

        Result result = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(200, result.status());
    }
}