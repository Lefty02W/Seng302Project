package repository;

import controllers.ProvideApplication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.Application;

public class ProfileRepositoryTest extends ProvideApplication {

    private Application app;

    @Before
    public void setUp() {
        app = super.provideApplication();
    }

    @Test
    public void checkProfileExistsProfileDoesNotExist() {
        ProfileRepository profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean profileExists = profileRepository.checkProfileExists("kdlhafalkjdfhalkdf");
        Assert.assertNull(profileExists);
    }
}