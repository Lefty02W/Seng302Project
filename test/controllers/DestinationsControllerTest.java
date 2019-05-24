package controllers;

import models.Destination;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Testing endpoints related to the destination controller
 */
public class DestinationsControllerTest extends ProvideApplication {

    /**
     * Testing trying to editDestinations a destination that does not exists
     */
    @Test
    public void showEditDestination() {
        injectRepositories();
        ArrayList<Destination> destinationList = profileRepository.getDestinations(1).get();
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/" + destinationList.get(0).getDestinationId() + "/edit")
                .session("connected", "1");

        Result result = Helpers.route(provideApplication(), request);


        assertEquals(200, result.status());
    }

}