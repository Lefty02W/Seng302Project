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


public class TripsControllerTest {



    @Test
    public void showCreatePageEndPoint() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "john@gmail.com");
        formData.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(TestApplication.getApplication(), request);

        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips/1/create")
                .session("connected", "1");

        result = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(OK, result.status());
    }





}