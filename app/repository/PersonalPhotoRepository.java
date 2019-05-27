package repository;

import io.ebean.*;
import models.PersonalPhoto;
import models.Photo;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * This class provides database access methods for handling PersonalPhotos
 */
public class PersonalPhotoRepository implements ModelUpdatableRepository<PersonalPhoto> {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final PhotoRepository photoRepository;

    @Inject
    public PersonalPhotoRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext, PhotoRepository photoRepository){
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.photoRepository = photoRepository;
    }


    /**
     * Update an existing personal photo in the personal_photo table
     * Updates either the profile id or the photo id or both
     * @param photo Personal photo that has had the changes applied
     * @param id id of the personal photo entry to be updated.
     * @return an optional of the personal photo image id that has been updated
     */
    public CompletionStage<Optional<Integer>> update(PersonalPhoto photo, int id) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String updateQuery = "UPDATE personal_photo SET profile_id = ? and photo_id = ? and is_profile_picture = ? WHERE personal_photo_id = ?";
            Optional<Integer> value = Optional.empty();
            try{
                if (ebeanServer.find(PersonalPhoto.class).setId(id).findOne() != null) {
                    SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
                    query.setParameter(1, photo.getProfileId());
                    query.setParameter(2, photo.getPhotoId());
                    query.setParameter(2, photo.getIsProfilePhoto());
                    query.setParameter(3, id);
                    query.execute();
                    txn.commit();
                    value = Optional.of(id);
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }

    /**
     * Delete an existing personal photo in the personal_photo table
     * @param id id of the personal photo to be deleted.
     * @return an optional of the personal photo image id that has been deleted
     */
    public CompletionStage<Optional<Integer>> delete(int id) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String deleteQuery = "delete * from personal_photo Where personal_photo_id = ?";
            SqlUpdate query = Ebean.createSqlUpdate(deleteQuery);
            query.setParameter(1, id);
            query.execute();
            txn.commit();
            return Optional.of(id);
        }, executionContext);
    }

    /**
     * Remove profile picture from a personal photo making it just a normal personal photo
     * @param profileId Id of the user to remove profile picture
     */
    public void removeProfilePic(int profileId) {
        String updateQuery = "UPDATE personal_photo SET is_profile_photo = 0 where profile_id = ?";
        SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
        query.setParameter(1, profileId);
        query.execute();
    }

    /**
     * Set a profile picture for a given profile
     * @param profileId Id of the user to add profile picture
     */
    public void setProfilePic(int profileId, int photoId) {
        String updateQuery = "UPDATE personal_photo SET is_profile_photo = 1 where profile_id = ? AND photo_id = ?";
        SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
        query.setParameter(1, profileId);
        query.setParameter(2, photoId);
        query.execute();
    }


    /**
     * Get the profile picture of a given user
     * @param profileId id of the user to get their profile photo
     * @return an optional of the given users profile picture
     */
    public Optional<Photo> getProfilePicture(int profileId) {
        String sql = "select photo_id from personal_photo where is_profile_photo = 1 and profile_id = ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(sql).setParameter(1, profileId).findList();
        if (rowList.isEmpty()) {
            return Optional.empty();
        } else {
            SqlRow row = rowList.get(0);
            int id = row.getInteger("photo_id");
            return photoRepository.getImage(id);
        }
    }

    /**
     * Inserts a new personal photo into the database
     *
     * @param photo the personal photo to be inserted
     * @return Optional of the photo id in a completion stage
     */
    public CompletionStage<Optional<Integer>> insert(PersonalPhoto photo) {
        return supplyAsync(() -> {
            try {
                ebeanServer.insert(photo);
            } catch (Exception e) {
                e.printStackTrace();
            }

           return Optional.of(photo.getPersonalPhotoId());
        }, executionContext);
    }

    /**
     * Method to find a certain personal photo by id
     *
     * @param id id of the entry to be found.
     * @return Optional PersonalPhoto wrapped in a completion stage
     */
    public CompletionStage<Optional<PersonalPhoto>> findById(int id) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(PersonalPhoto.class).where().eq("personal_photo_id", id).findOne());
        });
    }

    /**
     * Method to find a certain personal photo by its photo id
     *
     * @param id id of the entry to be found.
     * @return Optional PersonalPhoto wrapped in a completion stage
     */
    public CompletionStage<Optional<PersonalPhoto>> findByPhotoId(int id) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(PersonalPhoto.class).where().eq("photo_id", id).findOne());
        });
    }

    /**
     * Method to find all users that are for a passed user
     *
     * @param profileId the id of the user to find photos for
     * @return Optional Map holding all personalPhotos found
     */
    public Optional<List<Photo>> getAllProfilePhotos(int profileId) {
        List<Photo> photos = new ArrayList<>();
        List<PersonalPhoto> photosFound = ebeanServer.find(PersonalPhoto.class).where().eq("profile_id", profileId).findList();
        for (PersonalPhoto photo : photosFound) {
            photoRepository.getImage(photo.getPhotoId()).ifPresent(photos::add);
        }
        return Optional.of(photos);
    }

}
