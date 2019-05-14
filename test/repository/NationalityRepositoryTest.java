package repository;

import controllers.ProvideApplication;
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
    public void update() {}

    @Test
    public void delete() {}

    @Test
    public void deleteInvalidId() {}

    @Test
    public void insert() {}

    @Test
    public void findById() {}

    @Test
    public void findByIdInvalidId() {}
}