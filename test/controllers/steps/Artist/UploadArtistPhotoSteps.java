package controllers.steps.Artist;

import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Destination;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import repository.ArtistProfilePictureRepository;
import repository.DestinationRepository;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.libs.Files.singletonTemporaryFileCreator;

public class UploadArtistPhotoSteps {
    private Result redirectArtists;
    private ArtistProfilePictureRepository artistProfilePictureRepository = TestApplication.getArtistProfilePictureRepository();

    @When("the user uploads a photo for artist with id \"([^\"]*)\"$")
    public void theUserUploadsAPhoto(int artistId) {
        Http.MultipartFormData.FilePart<Source<ByteString, ?>> part = new Http.MultipartFormData.FilePart<>(
                "image",
                "artist-icon.png",
                "image/png",
                FileIO.fromPath(Paths.get("photos/personalPhotos/artist-icon.png")),
                40000);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/artists/" + artistId + "/upload/photo")
                .session("connected", "1")
                .bodyRaw(Collections.singletonList(part),
                        singletonTemporaryFileCreator(),
                        TestApplication.getApplication().asScala().materializer());

        redirectArtists = Helpers.route(TestApplication.getApplication(), request);
        assertEquals(303, redirectArtists.status());
    }


    @Then("the artist with id \"([^\"]*)\" has that photo as their profile photo")
    public void theArtistHasThatPhotoAsTheirProfilePhoto(int artistId) {
        artistProfilePictureRepository.getArtistProfilePicture(artistId)
                .thenApplyAsync(artistProfilePhoto1 -> {
                    assertEquals(artistId, artistProfilePhoto1.get().getArtistId());
                    return null;
                });
    }


    @And("the artist with id \"([^\"]*)\" has an existing photo as their profile photo")
    public void theArtistWithIdHasAnExistingPhotoAsTheirProfilePhoto(int artistId) {
        artistProfilePictureRepository.getArtistProfilePicture(artistId)
                .thenApplyAsync(artistProfilePhoto1 -> {
                    assertEquals(artistId, artistProfilePhoto1.get().getArtistId());
                    return null;
                });
    }
}
