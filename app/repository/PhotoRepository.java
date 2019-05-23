package repository;

import io.ebean.*;
import models.Image;
import play.db.ebean.EbeanConfig;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A photo repository that executes database operations in a different
 * execution context handles all interactions with the photo table .
 */
public class PhotoRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public PhotoRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext){
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }


    /**
     * Update image in the photo table in the database by accessing it with the image Id
     * @param newImage New image with recent updates to be finalized in the database
     * @param oldID Id of the image to be changed
     * @return
     */
    public CompletionStage<Optional<String>> update(Image newImage, int oldID) {

        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String updateQuery = "UPDATE photo SET image = ?, visible = ?, content_type = ?, " +
                    "name = ?, crop_x = ?, crop_y = ?, crop_width = ?, crop_height = ?, " +
                    "is_profile_pic = ? WHERE photo_id = ?";
            Optional<String> value = Optional.empty();
            try {
                if (ebeanServer.find(Image.class).setId(oldID).findOne() != null) {
                    SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
                    query.setParameter(1, newImage.getImage());
                    query.setParameter(2, newImage.getVisible());
                    query.setParameter(3, newImage.getType());
                    query.setParameter(4, newImage.getName());
                    query.setParameter(5, newImage.getCropX());
                    query.setParameter(6, newImage.getCropY());
                    query.setParameter(7, newImage.getCropWidth());
                    query.setParameter(8, newImage.getCropHeight());
                    query.setParameter(9, oldID);
                    query.execute();
                    txn.commit();
                    value = Optional.of("Updated");
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }

    /**
     * Inserts an image object into the ebean database server
     *
     * @param image Image object to insert into the database
     * @return the image id
     */
    public CompletionStage<Integer> insert(Image image){
        return supplyAsync(() -> {
            ebeanServer.insert(image);
            return image.getImageId();
        }, executionContext);
    }

    //TODO needs to be in personal photo repository
    public void removeProfilePic(String email) {
        String updateQuery = "UPDATE photo SET is_profile_pic = 0 where email = ?";
        SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
        query.setParameter(1, email);
        query.execute();
    }

    //TODO needs to be in personal photo repository
    public Optional<Image> getProfilePicture(int profileId) {
        String sql = "select photo_id from personal_photo where personal_photo_id = 1 and profile_id = ?"; //todo change photo_id = 1 and actually implement it
        List<SqlRow> rowList = ebeanServer.createSqlQuery(sql).setParameter(1, profileId).findList();
        if (rowList.size() < 1) {
            return null;
        } else {
            SqlRow row = rowList.get(0);
            int id = row.getInteger("photo_id");
            return getImage(id);
        }
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
     * @param profileId  of logged in users id
     * @return imageList list of images uploaded by the user
     */
    //TODO needs to be in personal photo repository
    public Optional<List<Image>> getImages(int profileId) {
        try {
            List<Image> imageList =
                    ebeanServer.find(Image.class)
                            .where().eq("profile_id", profileId)
                            .findList();
            return Optional.of(imageList);
        } catch (PersistenceException e) {
            List<Image> imageList = new ArrayList<>();
            return Optional.of(imageList);
        }

    }


    /**
     * Function to get a single image of a given id
     * @param id of the required image
     * @return a single image
     */
    public Optional<Image> getImage(Integer id) {

        Image image =
                ebeanServer.find(Image.class)
                .where().eq("photo_id", id)
                .findOne();

        return Optional.of(image);
    }
}
