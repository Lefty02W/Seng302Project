package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlUpdate;
import io.ebean.Transaction;
import models.Photo;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
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
     * @param newPhoto New image with recent updates to be finalized in the database
     * @param oldID Id of the image to be changed
     * @return
     */
    public CompletionStage<Optional<String>> update(Photo newPhoto, int oldID) {

        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String updateQuery = "UPDATE photo SET image = ?, visible = ?, content_type = ?, " +
                    "name = ?, crop_x = ?, crop_y = ?, crop_width = ?, crop_height = ?, " +
                    "is_profile_pic = ? WHERE photo_id = ?";
            Optional<String> value = Optional.empty();
            try {
                if (ebeanServer.find(Photo.class).setId(oldID).findOne() != null) {
                    SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
                    query.setParameter(1, newPhoto.getPath());
                    query.setParameter(2, newPhoto.getVisible());
                    query.setParameter(3, newPhoto.getType());
                    query.setParameter(4, newPhoto.getName());
                    query.setParameter(5, newPhoto.getCropX());
                    query.setParameter(6, newPhoto.getCropY());
                    query.setParameter(7, newPhoto.getCropWidth());
                    query.setParameter(8, newPhoto.getCropHeight());
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
     * Inserts an photo object into the ebean database server
     *
     * @param photo Photo object to insert into the database
     * @return the photo id
     */
    public CompletionStage<Integer> insert(Photo photo){
        return supplyAsync(() -> {
            ebeanServer.insert(photo);
            return photo.getPhotoId();
        }, executionContext);
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
     * Function to get a single image of a given id
     * @param id of the required image
     * @return a single image
     */
    public Optional<Photo> getImage(Integer id) {

        Photo photo =
                ebeanServer.find(Photo.class)
                        .where().eq("photo_id", id)
                        .findOne();

        return Optional.ofNullable(photo);
    }
}
