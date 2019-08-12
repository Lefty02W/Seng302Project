package controllers;

import models.Photo;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;


public class PhotoControllerTest {

    @Test
    public void checkAddPhoto() {
        Photo testPhoto  = new Photo("photos/personalPhotos/test.png", "image/png", 0, "test.png");
        Photo testThumbPhoto  = new Photo("photos/personalPhotos/test_thumb.png", "image/png", 0, "test_thumb.png");
        TestApplication.getPhotoRepository().insert(testPhoto).thenApplyAsync(photoId -> {
            TestApplication.getPhotoRepository().insertThumbnail(testThumbPhoto, photoId);
            if (TestApplication.getPhotoRepository().getThumbnail(photoId).isPresent()) {
                assertEquals(testThumbPhoto, TestApplication.getPhotoRepository().getThumbnail(photoId).get());
            } else {
                fail();
            }
            return true;
        });

    }
}
