package controllers;

import controllers.routes;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;

/**
 * Test Set for profile controller
 */
public class ProfileControllerTest extends  ProvideApplication{

    private Application app;

    @Before
    public void setUp() {
        app = super.provideApplication();
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "admin");
        formData.put("password", "admin123");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(provideApplication(), request);
    }

    /**
     * Testing profile GET endpoint /profile/edit/:id
     */
    @Test
    public void showEdit() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/edit/admin")
                .session("connected", "admin");

        Result result = Helpers.route(provideApplication(),request);
        //System.out.println(Helpers.contentAsString(result));


        assertEquals(200, result.status());
    }

    /**
     * Testing profile POST endpoint /profile
     */
    @Test
    public void update() {
        Map<String, String> profileData = new HashMap<>();
        profileData.put("first_name", "admin");
        profileData.put("middle_name", "admin");
        profileData.put("last_name", "admin");
        profileData.put("email", "admin");
        profileData.put("birth_date", "2016-05-08");



        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/profile")
                .bodyForm(profileData)
                .session("connected", "admin");

        Result result = Helpers.route(provideApplication(),request);
        //System.out.println(Helpers.contentAsString(result));


        assertEquals(303, result.status());
    }

    /**
     * Testing profile GET endpoint /profile
     */
    @Test
    public void show() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile")
                .session("connected", "admin");

        Result result = Helpers.route(provideApplication(),request);

        assertEquals(OK, result.status());
    }
}