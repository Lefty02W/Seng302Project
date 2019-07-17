package controllers;

import models.Photo;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


public class PhotoControllerTest extends ProvideApplication {

    //@Test
    public void checkAddPhoto() {
        injectRepositories();
        Photo testPhoto  = new Photo("photos/personalPhotos/test.png", "image/png", 0, "test.png");
        Photo testThumbPhoto  = new Photo("photos/personalPhotos/test_thumb.png", "image/png", 0, "test_thumb.png");
        photoRepository.insert(testPhoto).thenApplyAsync(photoId -> {
            photoRepository.insertThumbnail(testThumbPhoto, photoId);
            if (photoRepository.getThumbnail(photoId).isPresent()) {
                assertEquals(testThumbPhoto, photoRepository.getThumbnail(photoId).get());
                return true;
            } else {
                return false;
            }
        });

    }
}
