package controllers;

import org.junit.Before;
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
 * Test Set for profile controller
 */
public class ProfileControllerTest extends ProvideApplication{

    @Before
    public void setUp() {
        app = super.provideApplication();
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "john@gmail.com");
        formData.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Helpers.route(provideApplication(), request);
    }

    /**
     * Testing profile GET endpoint /profile/editDestinations/:id
     */
    @Test
    public void showEdit() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/john@gmail.comedit")
                .session("connected", "john@gmail.com");

        Result result = Helpers.route(provideApplication(), request);
    }


    /**
     * Testing profile POST endpoint /profile
     */
    @Test
    public void update() {
        loginUser();
        Map<String, String> profileData = new HashMap<>();
        profileData.put("firstName", "admin");
        profileData.put("middleName", "admin");
        profileData.put("lastName", "admin");
        profileData.put("email", "john@gmail.com");
        profileData.put("birthDate", "2016-05-08");
        profileData.put("gender", "male");
        profileData.put("travellerTypes", "Backpacker");
        profileData.put("nationalities", "NZ");
        profileData.put("passports", "NZ");



        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/profile")
                .bodyForm(profileData)
                .session("connected", "john@gmail.com");

        Result result = Helpers.route(provideApplication(),request);


        assertEquals(303, result.status());
    }

    /**
     * Testing profile GET endpoint /profile
     */
    @Test // Having issues with this test will sort at a later date
    public void show() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile")
                .session("connected", "john@gmail.com");

        Result result = Helpers.route(provideApplication(),request);

        assertEquals(OK, result.status());
    }
}