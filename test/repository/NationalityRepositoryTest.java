package repository;

import controllers.ProvideApplication;
import models.Nationality;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class NationalityRepositoryTest extends ProvideApplication {

    private Optional<Integer> nationId;

    @Before
    public void setUp() {
        //System.out.println(nationalityRepository.getAll());
    }

    @Test
    public void delete() {}

    @Test
    public void deleteInvalidId() {}

    @Test
    public void insert() {
        int nationalityId = -1;
        nationalityRepository.insert(new Nationality("BeanLand"))
                .thenApplyAsync(i -> {
                    System.out.println(i.get());
                    return i;
                });
        System.out.println(nationalityId);
    }

    @Test
    public void findById() {}

    @Test
    public void findByIdInvalidId() {}
}