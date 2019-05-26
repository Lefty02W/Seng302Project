package controllers;

import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static org.hamcrest.MatcherAssert.assertThat;

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

    // Need to refactor these tests to work with the in memory database
    // After Dev into refactor-personal-photo merge

    @Test
    public void fileUpload() throws IOException {
        File file = getPersonalPhoto();
        Http.MultipartFormData.Part<Source<ByteString, ?>> part =
                new Http.MultipartFormData.FilePart<>(
                        "image",
                        "defaultPic.jpg",
                        "image/png",
                        FileIO.fromPath(file.toPath()),
                        Files.size(file.toPath()));

        Http.RequestBuilder request = Helpers.fakeRequest()
                    .method(POST)
                    .uri("/profile/photo")
                    .session("connected", "admin@admin.com")
                    .bodyRaw(
                            Collections.singletonList(part),
                            play.libs.Files.singletonTemporaryFileCreator(),
                            app.asScala().materializer());

        // Checks for successful redirect to the profile page after successful image upload
        Result redirectPhotoUploadResult = Helpers.route(provideApplication(), request);
            assertEquals(303, redirectPhotoUploadResult.status());
    }

    @Test
    public void validPhotoDisplay() throws IOException {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/photo?id=50")
                .session("connected", "john@gmail.com");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }

    // Test when photo exists but does not belong to the session user
    @Test
    public void invalidPhotoDisplay() throws IOException {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/photo?id=71")
                .session("connected", "john@gmail.com");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void noIdPhotoDisplay() throws IOException {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/photo?id=100")
                .session("connected", "john@gmail.com");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(303, result.status());
    }
}