package repository;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Generically typed Interface for database repository classes that are updatable.
 * @param <T> Object of type T for the methods to use.
 */

public interface ModelUpdatableRepository<T> {

    /**
     * Generic update function to update the given object using a given id
     * @param t object of type T to be updated in the database
     * @param id id of the entry to be updated.
     * @return Optional completion stage holding the id of the entry that has been updated
     */
    CompletionStage<Optional<Integer>> update(T t, int id);

}
