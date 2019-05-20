package controllers;

import models.Destination;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.ArrayList;
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
     * Testing trying to editDestinations a destination that does not exists
     */
    @Test
    public void showEditDestination() {
        ArrayList<Destination> destinationList = profileRepository.getDestinations(0).get();
        loginUser();
        System.out.println(destinationList.get(0).getDestinationId());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/" + destinationList.get(0).getDestinationId() + "/edit")
                .session("connected", "john@gmail.com");

        Result result = Helpers.route(provideApplication(), request);


        assertEquals(200, result.status());
    }

}