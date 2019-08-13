package controllers;

import models.Destination;
import org.junit.Ignore;
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

    /**
     * Testing trying to editDestinations a destination that does not exists
     */
    @Ignore
    public void showEditDestination() {
        ArrayList<Destination> destinationList = TestApplication.getProfileRepository().getDestinations(1).get();
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
                .uri("/destinations/" + destinationList.get(0).getDestinationId() + "/edit")
                .session("connected", "1");

        result = Helpers.route(TestApplication.getApplication(), request);


        assertEquals(200, result.status());
    }
}