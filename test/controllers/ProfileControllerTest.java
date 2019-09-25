package controllers;

import org.junit.Assert;
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
public class ProfileControllerTest {



    @Before
    public void setUp() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "john@gmail.com");
        formData.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Helpers.route(TestApplication.getApplication(), request);
    }


    /**
     * Testing profile POST endpoint /profile
     */
    @Test
    public void update() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "john@gmail.com");
        formData.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(TestApplication.getApplication(), request);

        Map<String, String> profileData = new HashMap<>();
        profileData.put("firstName", "admin");
        profileData.put("middleName", "admin");
        profileData.put("lastName", "admin");
        profileData.put("email", "john@gmail.com");
        profileData.put("birthDate", "2016-05-08");
        profileData.put("gender", "male");
        profileData.put("travellerTypesForm", "Backpacker");
        profileData.put("nationalitiesForm", "NZ");
        profileData.put("passportsForm", "NZ");

        request = Helpers.fakeRequest()
                .method("POST")
                .uri("/profile")
                .bodyForm(profileData)
                .session("connected", "1");

        result = Helpers.route(TestApplication.getApplication(),request);


        assertEquals(303, result.status());


    }

    /**
     * Testing profile GET endpoint /profile
     */
    @Test // Having issues with this test will sort at a later date
    public void show() {
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
                .uri("/profile")
                .session("connected", "1");

        result = Helpers.route(TestApplication.getApplication(),request);

        assertEquals(OK, result.status());
    }


    @Test
    public void validPhotoDisplay() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/photo?id=2")
                .session("connected", "john@gmail.com");

        Result result = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(OK, result.status());
    }

    @Test
    public void noIdPhotoDisplay() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/photo?id=100")
                .session("connected", "1");

        Result result = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(303, result.status());
    }

    @Test
    public void setProfilePicture() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/photo/save/1")
                .session("connected", "1");

        Result result = Helpers.route(TestApplication.getApplication(), request);
        Assert.assertTrue(result.flash().getOptional("success").isPresent());
    }

    @Test
    public void removeProfilePicture() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/photo/remove")
                .session("connected", "1");

        Result result = Helpers.route(TestApplication.getApplication(), request);
        Assert.assertTrue(result.flash().getOptional("success").isPresent());
    }

}