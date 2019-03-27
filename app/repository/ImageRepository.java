package repository;

import io.ebean.*;
import models.Image;
import play.db.ebean.EbeanConfig;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A image repository that executes database operations in a different
 * execution context handles all interactions with the image table .
 */
public class ImageRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public ImageRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext){
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }


    /**
     * Inserts an image object into the ebean database server
     *
     * @param image Image object to insert into the database
     * @return the image id
     */
    public CompletionStage<Integer> insert(Image image){
        return supplyAsync(() -> {
            try {
                ebeanServer.insert(image);
            } catch (Exception e) {
                System.out.print(e);
            }
            return image.getImageId();
        }, executionContext);
    }


    /**
     * Update image visibility in database using Image model object,
     * Checks if it is 1 'public' and changes it to 0 'private' and vice versa.
     *
     * @param id Integer with the images 'visible' value. Either 1 or 0.
     * @return new 'visible' value
     */
    public CompletionStage<Optional<Integer>> updateVisibility(Integer id) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Integer> value = Optional.empty();
            try {
                Image targetImage = ebeanServer.find(Image.class).setId(id).findOne();
                if (targetImage != null) {
                    if(targetImage.getVisible() == 1) {
                        targetImage.setVisible(0); // Public to private
                    } else {
                        targetImage.setVisible(1); // Private to public
                    }
                    targetImage.update();
                    txn.commit();
                    value = Optional.of(targetImage.getVisible());
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }


    /**
     * Function to get all the images created by the signed in user.
     *
     * @param email String of logged in users email
     * @return imageList list of images uploaded by the user
     */
    public Optional<List<Image>> getImages(String email) {
        List<Image> imageList =
                ebeanServer.find(Image.class)
                        .where().eq("email", email)
                        .findList();
        return Optional.of(imageList);
    }
}
