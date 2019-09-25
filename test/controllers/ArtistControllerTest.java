package controllers;

import akka.stream.Materializer;
import akka.util.ByteString;
import models.ArtistProfilePhoto;
import models.Photo;
import org.junit.Test;
import play.api.libs.Files;
import play.api.mvc.MultipartFormData;
import play.mvc.Http;
import play.test.Helpers;
import akka.stream.javadsl.*;
import repository.ArtistProfilePictureRepository;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.libs.Files.singletonTemporaryFileCreator;

public class ArtistControllerTest {

    private ArtistProfilePictureRepository artistProfilePictureRepository = TestApplication.getArtistProfilePictureRepository();

    @Test
    public void uploadArtistProfilePhoto() {
        Http.MultipartFormData.FilePart<Source<ByteString, ?>> part = new Http.MultipartFormData.FilePart<>(
                "image",
                "defaultPic.jpg",
                "image/jpg",
                FileIO.fromPath(Paths.get("photos/personalPhotos/defaultPic.jpg")),
                40000);

        Http.RequestBuilder request = (Http.RequestBuilder) Helpers.fakeRequest()
                .method("POST")
                .uri("/artists/2/upload/photo")
                .session("connected", "1")
                .bodyRaw(Collections.singletonList(part),
                        singletonTemporaryFileCreator(),
                        TestApplication.getApplication().asScala().materializer());
        artistProfilePictureRepository.getArtistProfilePicture(1)
                .thenApplyAsync(artistProfilePhoto1 -> {
                    assertEquals(2, artistProfilePhoto1.get().getArtistId());
                    return null;
                });
    }


}
