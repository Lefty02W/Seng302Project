package controllers;

import org.junit.Assert;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

/**
 * Test set for Login Controller
 */
public class LoginControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
    return new GuiceApplicationBuilder().build();
  }

    /**
     * Testing login POST endpoint /login
     */
    @Test
    public void login() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "admin");
        formData.put("password", "admin123");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(303, result.status());

    }

    /**
     * Testing login GET endpoint /
     */
    @Test
    public void loadLoginOnStart(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/");
        Result result = Helpers.route(provideApplication(),request);

        assertEquals(OK, result.status());
    }

    /**
     * Testing login GET endpoint /login
     */
    @Test
    public void show(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/login");
        Result result = Helpers.route(provideApplication(),request);

        assertEquals(OK, result.status());
    }
}
