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


    public CompletionStage<Optional<String>> update(Image newImage, int oldID) {

        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String updateQuery = "UPDATE image SET email = ?, image = ?, visible = ?, content_type = ?, " +
                    "name = ?, crop_x = ?, crop_y = ?, crop_width = ?, crop_height = ?, " +
                    "is_profile_pic = ? WHERE image_id = ?";
            Optional<String> value = Optional.empty();
            try {
                if (ebeanServer.find(Image.class).setId(oldID).findOne() != null) {
                    SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
                    query.setParameter(1, newImage.getEmail());
                    query.setParameter(2, null);
                    query.setParameter(3, newImage.getVisible());
                    query.setParameter(4, newImage.getType());
                    query.setParameter(5, newImage.getName());
                    query.setParameter(6, newImage.getCropX());
                    query.setParameter(7, newImage.getCropY());
                    query.setParameter(8, newImage.getCropWidth());
                    query.setParameter(9, newImage.getCropHeight());
                    query.setParameter(10, newImage.getPath());
                    query.setParameter(11, newImage.getIsProfilePic());
                    query.setParameter(12, oldID);
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
            try {
                ebeanServer.insert(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image.getImageId();
        }, executionContext);
    }

    public void removeProfilePic(String email) {
        String updateQuery = "UPDATE image SET is_profile_pic = 0 where email = ?";
        SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
        query.setParameter(1, email);
        query.execute();
    }

    public Optional<Image> getProfilePicture(String email) {
        String sql = "select image_id from image where is_profile_pic = 1 and email = ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(sql).setParameter(1, email).findList();
        if (rowList.size() < 1) {
            return null;
        } else {
            SqlRow row = rowList.get(0);
            int id = row.getInteger("image_id");
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


    /**
     * Function to get a single image of a given id
     * @param id of the required image
     * @return a single image
     */
    public Optional<Image> getImage(Integer id) {

        Image image =
                ebeanServer.find(Image.class)
                .where().eq("image_id", id)
                .findOne();

        return Optional.of(image);
    }
}
