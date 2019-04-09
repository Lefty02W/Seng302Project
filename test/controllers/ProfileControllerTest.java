package controllers;

import org.junit.Before;
import play.Application;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import org.junit.Test;

/**
 * Test Set for profile controller
 */
public class ProfileControllerTest extends ProvideApplication{

    private Application app;

    @Before
    public void setUp() {
        app = super.provideApplication();
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "admin@admin.com");
        formData.put("password", "admin123");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(provideApplication(), request);
    }

    /**
     * Testing profile GET endpoint /profile/editDestinations/:id
     */
    //@Test
    public void showEdit() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/admin@admin.com/editDestinations")
                .session("connected", "admin@admin.com");

        Result result = Helpers.route(provideApplication(), request);
    }


    /**
     * Testing profile POST endpoint /profile
     */
    //@Test
    public void update() {
        loginUser();
        Map<String, String> profileData = new HashMap<>();
        profileData.put("firstName", "admin");
        profileData.put("middleName", "admin");
        profileData.put("lastName", "admin");
        profileData.put("email", "admin@admin.com");
        profileData.put("birthDate", "2016-05-08");
        profileData.put("gender", "male");
        profileData.put("travellerTypes", "Backpacker");
        profileData.put("nationalities", "NZ");
        profileData.put("passports", "NZ");



        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/profile")
                .bodyForm(profileData)
                .session("connected", "admin@admin.com");

        Result result = Helpers.route(provideApplication(),request);
        //System.out.println(Helpers.contentAsString(result));


        assertEquals(303, result.status());
    }

    /**
     * Testing profile GET endpoint /profile
     */
    //@Test // Having issues with this test will sort at a later date
    public void show() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile")
                .session("connected", "admin@admin.com");

        Result result = Helpers.route(provideApplication(),request);

        assertEquals(OK, result.status());
    }
}