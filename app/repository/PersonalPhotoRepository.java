package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlUpdate;
import io.ebean.Transaction;
import models.PersonalPhoto;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.lang.Integer.parseInt;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class PersonalPhotoRepository implements ModelUpdatableRepository<PersonalPhoto> {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public PersonalPhotoRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext){
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
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
            String updateQuery = "UPDATE personal_photo SET profile_id = ? and photo_id = ? WHERE personal_photo_id = ?";
            Optional<Integer> value = Optional.empty();
            try{
                if (ebeanServer.find(PersonalPhoto.class).setId(id).findOne() != null) {
                    SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
                    query.setParameter(1, photo.getProfileId());
                    query.setParameter(2, photo.getPhotoId());
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
        Optional<Integer> value = Optional.empty();
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String deleteQuery = "delete * from personal_photo Where personal_photo_id = ?";
            SqlUpdate query = Ebean.createSqlUpdate(deleteQuery);
            query.setParameter(1, id);
            query.execute();
            txn.commit();
            value = Optional.of(id);
            return value;
        }, executionContext);
    }

    public CompletionStage<Optional<Integer>> insert(PersonalPhoto photo) {

    }

    public CompletionStage<Optional<PersonalPhoto>> findById(int id) {

    }

}
