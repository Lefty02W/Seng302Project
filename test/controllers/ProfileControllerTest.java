package controllers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static play.mvc.Http.Status.NOT_FOUND;
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

    // Need to refactor these tests to work with the in memory database
    // After Dev into refactor-personal-photo merge

    @Test
    @Ignore
    public void fileUpload() throws IOException {
//        File file = getPersonalPhoto();
//        Http.MultipartFormData.Part<Source<ByteString, ?>> part =
//                new Http.MultipartFormData.FilePart<>(
//                        "image",
//                        "testPic1.jpg",
//                        "image/jpg",
//                        FileIO.fromPath(file.toPath()),
//                        Files.size(file.toPath()));
//
//        Http.RequestBuilder request = Helpers.fakeRequest()
//                    .method(POST)
//                    .uri("/profile/photo")
//                    .session("connected", "1")
//                    .bodyRaw(
//                            Collections.singletonList(part),
//                            play.libs.Files.singletonTemporaryFileCreator(),
//                            TestApplication.getApplication().asScala().materializer());
//
//        // Checks for successful redirect to the profile page after successful image upload
//        Result result = Helpers.route(TestApplication.getApplication(), request);
//        assertEquals(303, result.status());
    }

    @Test
    @Ignore
    public void invalidContentTypeFileUpload() throws IOException {
//        File file = getPersonalPhoto();
//        Http.MultipartFormData.Part<Source<ByteString, ?>> part =
//
//        new Http.MultipartFormData.FilePart<>(
//                "image",
//                "testPic1.jpg",
//                "image/bmp",
//                FileIO.fromPath(file.toPath()),
//                Files.size(file.toPath()));
//
//        Http.RequestBuilder request = Helpers.fakeRequest()
//                .method(POST)
//                .uri("/profile/photo")
//                .session("connected", "1")
//                .bodyRaw(
//                        Collections.singletonList(part),
//                        play.libs.Files.singletonTemporaryFileCreator(),
//                        TestApplication.getApplication().asScala().materializer());
//
//        Result result = Helpers.route(TestApplication.getApplication(), request);
//        Assert.assertTrue(result.flash().getOptional("invalid").isPresent());
    }

    @Test
    @Ignore
    public void emptyFileUpload() throws IOException {
//        File file = getPersonalPhoto();
//        Http.MultipartFormData.Part<Source<ByteString, ?>> part =
//        new Http.MultipartFormData.FilePart<>(
//                "",
//                "",
//                "",
//                FileIO.fromPath(file.toPath()),
//                Files.size(file.toPath()));
//
//        Http.RequestBuilder request = Helpers.fakeRequest()
//                .method(POST)
//                .uri("/profile/photo")
//                .session("connected", "1")
//                .bodyRaw(
//                        Collections.singletonList(part),
//                        play.libs.Files.singletonTemporaryFileCreator(),
//                        TestApplication.getApplication().asScala().materializer());
//
//        Result result = Helpers.route(TestApplication.getApplication(), request);
//        Assert.assertTrue(result.flash().getOptional("invalid").isPresent());
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

    // Test when photo exists but does not belong to the session user
    @Test
    @Ignore
    public void invalidPhotoDisplay() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/photo?id=2")
                .session("connected", "1");

        Result result = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(NOT_FOUND, result.status());
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

    @Test
    @Ignore
    public void deletePersonalPhoto() {

        try {
            Path sourceDirectory = Paths.get(System.getProperty("user.dir") + "/public/images/" + "testPic1.jpg");
            Path targetDirectory = Paths.get(System.getProperty("user.dir") + "/photos/personalPhotos/" + "testPic1.jpg");
            Files.copy(sourceDirectory, targetDirectory);
        } catch (IOException e) {
            fail("Image upload to test delete has failed.");
        }

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/profile/photo/1/delete")
                .session("connected", "1");

        Result result = Helpers.route(TestApplication.getApplication(), request);
        Assert.assertTrue(result.flash().getOptional("success").isPresent());
    }
}