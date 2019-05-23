package repository;

import io.ebean.*;
import models.Photo;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
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


    public CompletionStage<Optional<String>> update(Photo newPhoto, int oldID) {

        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String updateQuery = "UPDATE image SET email = ?, image = ?, visible = ?, content_type = ?, " +
                    "name = ?, crop_x = ?, crop_y = ?, crop_width = ?, crop_height = ?, " +
                    "is_profile_pic = ? WHERE image_id = ?";
            Optional<String> value = Optional.empty();
            try {
                if (ebeanServer.find(Photo.class).setId(oldID).findOne() != null) {
                    SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
                    query.setParameter(1, newPhoto.getEmail());
                    query.setParameter(2, newPhoto.getImage());
                    query.setParameter(3, newPhoto.getVisible());
                    query.setParameter(4, newPhoto.getType());
                    query.setParameter(5, newPhoto.getName());
                    query.setParameter(6, newPhoto.getCropX());
                    query.setParameter(7, newPhoto.getCropY());
                    query.setParameter(8, newPhoto.getCropWidth());
                    query.setParameter(9, newPhoto.getCropHeight());
                    query.setParameter(10, newPhoto.getIsProfilePic());
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
     * Inserts an photo object into the ebean database server
     *
     * @param photo Photo object to insert into the database
     * @return the photo id
     */
    public CompletionStage<Integer> insert(Photo photo){
        return supplyAsync(() -> {
            try {
                ebeanServer.insert(photo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return photo.getImageId();
        }, executionContext);
    }

    public void removeProfilePic(String email) {
        String updateQuery = "UPDATE image SET is_profile_pic = 0 where email = ?";
        SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
        query.setParameter(1, email);
        query.execute();
    }

    public Optional<Photo> getProfilePicture(int profileId) {
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
     * Update image visibility in database using Photo model object,
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
                Photo targetPhoto = ebeanServer.find(Photo.class).setId(id).findOne();
                if (targetPhoto != null) {
                    if(targetPhoto.getVisible() == 1) {
                        targetPhoto.setVisible(0); // Public to private
                    } else {
                        targetPhoto.setVisible(1); // Private to public
                    }
                    targetPhoto.update();
                    txn.commit();
                    value = Optional.of(targetPhoto.getVisible());
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
    public Optional<List<Photo>> getImages(int profileId) {
        try {
            List<Photo> photoList =
                    ebeanServer.find(Photo.class)
                            .where().eq("profile_id", profileId)
                            .findList();
            return Optional.of(photoList);
        } catch (PersistenceException e) {
            List<Photo> photoList = new ArrayList<>();
            return Optional.of(photoList);
        }

    }


    /**
     * Function to get a single image of a given id
     * @param id of the required image
     * @return a single image
     */
    public Optional<Photo> getImage(Integer id) {

        Photo photo =
                ebeanServer.find(Photo.class)
                .where().eq("image_id", id)
                .findOne();

        return Optional.of(photo);
    }
}
