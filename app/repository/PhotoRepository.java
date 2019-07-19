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
            try {
                ebeanServer.insert(photo);
            } catch (Exception e) {
                e.printStackTrace();
            }

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
            String qry = "INSERT INTO thumbnail_link (photo_id, thumbnail_id) VALUES (?, ?)";
            try {
                SqlUpdate query = Ebean.createSqlUpdate(qry);
                query.setParameter(1, photoId);
                query.setParameter(2, thumbId);
                query.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    /**
     * Method to delete a photo from the database using a passed photoId
     *
     * @param id The id of the photo to delete
     */
    public CompletionStage<Integer> delete(int id) {
         return supplyAsync(() -> {
            Photo.find.deleteById(id);
            return 1;
         }, executionContext);
    }


    /**
     * Delete a thumbnail from the database given the id
     * @param thumbnailID - ID of the thumbnail image to delete
     * @return
     */
    public CompletionStage<Void> deleteThumbnail(Integer thumbnailID) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String deleteQuery = "delete from thumbnail_link where thumbnail_id = ?";
            SqlUpdate query = Ebean.createSqlUpdate(deleteQuery);
            query.setParameter(1, thumbnailID);
            query.execute();
            txn.commit();
            return null;
        }, executionContext);
    }


    /**
     * Update a given thumbnail photo's path and name in the database
     * @param photo - The photo as a thumbnail to be updated
     */
    public CompletionStage<Void> updateThumbnail(Photo photo) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String updateQuery = "UPDATE photo SET path = ? and name = ? where photo_id = ?";
            SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
            query.setParameter(1, photo.getPath());
            query.setParameter(2, photo.getName());
            query.setParameter(3, photo.getPhotoId());
            query.execute();
            txn.commit();
            return null;
        }, executionContext);
    }

    public CompletionStage<String> getImageType(Integer photoId){
        return supplyAsync(() -> {
          SqlQuery query = Ebean.createSqlQuery("SELECT content_type FROM photo WHERE photo_id = ?");
          query.setParameter(1, photoId);
          SqlRow row = query.findOne();
          if (!row.isEmpty()){
              return row.getString("content_type");
          }
          return "failed";
        });
    }

    /**
     * Removes a thumbnail from the database using a photoId
     *
     * @param photoId the photoId
     */
    public void deleteCurrentThumbnail(Integer photoId) {
        supplyAsync(() -> {
            SqlUpdate query = Ebean.createSqlUpdate("delete from thumbnail_link where photo_id = ?");
            query.setParameter(1, photoId);
            query.execute();
            return null;
        }, executionContext);
    }
}
