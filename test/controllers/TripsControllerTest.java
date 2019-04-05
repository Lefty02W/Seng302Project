package controllers;


import org.junit.Assert;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

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
                .uri("/trips/1/edit")
                .session("connected", "admin");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }


   // @Test
    public void deleteTripDestination() {
        loginUser();

        boolean exists = tripDestinationsRepository.validate(3);
        Assert.assertTrue(exists);
        //user deletes the 3rd tripDest
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/trips/edit/3/delete?id=1")
                .session("connected", "admin");

        Result result = Helpers.route(provideApplication(), request);
        assertEquals(303, result.status());

        //and then clicks save
        Map<String, String> formData = new HashMap<>();
        formData.put("name", "yes");
        request = Helpers.fakeRequest()
                .method("POST")
                .uri("/trips/edit?id=1")
                .bodyForm(formData)
                .session("connected", "admin");

        result = Helpers.route(provideApplication(), request);
        assertEquals(303, result.status());

        //should equal false as it is not deleted
        //exists = tripDestinationsRepository.validate(3);
        //Assert.assertFalse(exists);


        //TODO Tests cannot be hard coded

    }


}