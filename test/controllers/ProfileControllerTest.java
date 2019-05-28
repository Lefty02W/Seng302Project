package controllers;

import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
import static org.junit.Assert.fail;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test Set for profile controller
 */
public class ProfileControllerTest extends ProvideApplication {

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
     * Testing profile POST endpoint /profile
     */
    @Test
    public void update() {
        Integer userId = loginUser();
        if (userId != 0) {
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

            Http.RequestBuilder request = Helpers.fakeRequest()
                    .method("POST")
                    .uri("/profile")
                    .bodyForm(profileData)
                    .session("connected", userId.toString());

            Result result = Helpers.route(provideApplication(),request);


            assertEquals(303, result.status());
        } else {
            fail();
        }

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
                .session("connected", "1");

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
                        "image/jpg",
                        FileIO.fromPath(file.toPath()),
                        Files.size(file.toPath()));

        Http.RequestBuilder request = Helpers.fakeRequest()
                    .method(POST)
                    .uri("/profile/photo")
                    .session("connected", "john@gmail.com")
                    .bodyRaw(
                            Collections.singletonList(part),
                            play.libs.Files.singletonTemporaryFileCreator(),
                            app.asScala().materializer());

        // Checks for successful redirect to the profile page after successful image upload
        Result result = Helpers.route(provideApplication(), request);
        assertEquals(303, result.status());
    }

    @Test
    public void invalidContentTypeFileUpload() throws IOException {
        File file = getPersonalPhoto();
        Http.MultipartFormData.Part<Source<ByteString, ?>> part =

        new Http.MultipartFormData.FilePart<>(
                "image",
                "defaultPic.jpg",
                "image/bmp",
                FileIO.fromPath(file.toPath()),
                Files.size(file.toPath()));

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/profile/photo")
                .session("connected", "john@gmail.com")
                .bodyRaw(
                        Collections.singletonList(part),
                        play.libs.Files.singletonTemporaryFileCreator(),
                        app.asScala().materializer());

        Result result = Helpers.route(provideApplication(), request);
        Assert.assertTrue(result.flash().getOptional("invalid").isPresent());
    }

    @Test
    public void emptyFileUpload() throws IOException {
        File file = getPersonalPhoto();
        Http.MultipartFormData.Part<Source<ByteString, ?>> part =
        new Http.MultipartFormData.FilePart<>(
                "",
                "",
                "",
                FileIO.fromPath(file.toPath()),
                Files.size(file.toPath()));

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/profile/photo")
                .session("connected", "john@gmail.com")
                .bodyRaw(
                        Collections.singletonList(part),
                        play.libs.Files.singletonTemporaryFileCreator(),
                        app.asScala().materializer());

        Result result = Helpers.route(provideApplication(), request);
        Assert.assertTrue(result.flash().getOptional("invalid").isPresent());
    }

    @Test
    public void validPhotoDisplay() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/photo?id=1")
                .session("connected", "john@gmail.com");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }

    // Test when photo exists but does not belong to the session user
    @Test
    @Ignore
    public void invalidPhotoDisplay() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/photo?id=2")
                .session("connected", "john@gmail.com");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void noIdPhotoDisplay() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/photo?id=100")
                .session("connected", "john@gmail.com");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(303, result.status());
    }
}