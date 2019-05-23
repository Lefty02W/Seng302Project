package repository;

import models.PersonalPhoto;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class PersonalPhotoRepository implements ModelUpdatableRepository<PersonalPhoto> {

    public CompletionStage<Optional<Integer>> update(PersonalPhoto photo, int id) {

    }

    public CompletionStage<Optional<Integer>> delete(int id) {

    }

    public CompletionStage<Optional<Integer>> insert(PersonalPhoto phot) {

    }

    public CompletionStage<Optional<PersonalPhoto>> findById(int id) {

    }

}
