package repository;

import java.util.Optional;
import java.util.concurrent.CompletionStage;


/**
 * Generically typed Interface for database repository classes. CRUD methods.
 * @param <T> Object of type T for the methods to use.
 */
public interface ModelRepository<T> {


    /**
     * Generic update function to update the given object using a given id
     * @param t object of type T to be updated in the database
     * @param id id of the entry to be updated.
     * @return Optional completion stage holding the id of the entry that has been updated
     */
    CompletionStage<Optional<Integer>> update(T t, int id);

    /**
     * Generic delete function to delete an object using a given id
     * @param id id of the entry to be deleted.
     * @return Optional completion stage holding the id of the entry that has been deleted
     */
    CompletionStage<Optional<Integer>> delete(int id);

    /**
     * Generic insert function to insert the given object
     * @param t object of type T to be inserted in the database
     * @return Optional completion stage holding the id of the entry that has been created
     */
    CompletionStage<Optional<Integer>> insert(T t);

    /**
     * Generic select function to find an object using the given id
     * @param id id of the entry to be found.
     * @return Optional completion stage holding the object of type T found using the given id.
     */
    CompletionStage<Optional<T>> findById(int id);
}
