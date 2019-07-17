package repository;

import io.ebean.*;
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
     * Inserts a photo object into the ebean database server
     *
     * @param photo Photo object to insert into the database
     */
    public void insertThumbnail(Photo photo, Integer photoId){
        insert(photo).thenApplyAsync(thumbId -> {
            String qry = "INSERT INTO profile (photo_id, thumbnail_id) VALUES (?, ?)";
            SqlUpdate query = Ebean.createSqlUpdate(qry);
            query.setParameter(1, photoId);
            query.setParameter(2, thumbId);
            query.execute();
            return true;
        });
    }

    public Optional<Photo> getThumbnail(Integer id) {
        String qry = "Select thumbnail_id from thumbnail_link where photo_id = ?";
        SqlRow row = ebeanServer.createSqlQuery(qry)
                .setParameter(1, id)
                .findOne();
        Integer thumb_id = row.getInteger("thumbnail_id");
        Photo photo =
                ebeanServer.find(Photo.class)
                        .where().eq("photo_id", thumb_id)
                        .findOne();
        return Optional.ofNullable(photo);
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
