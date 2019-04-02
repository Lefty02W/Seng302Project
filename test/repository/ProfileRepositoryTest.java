package repository;

import controllers.ProvideApplication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.Application;

public class ProfileRepositoryTest extends ProvideApplication {

    //TODO: write methods to populate db with some test data and then remove it afterwards

    private Application app;

    @Before
    public void setUp() {
        app = super.provideApplication();
    }

    /**
     * Testing the checkProfileExists method with an email that does not exist
     */
    @Test
    public void checkProfileExistsProfileDoesNotExist() {
        ProfileRepository profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean profileExists = profileRepository.checkProfileExists("kdlhafalkjdfhalkdf");
        Assert.assertFalse(profileExists);
    }

    /**
     * Testing the checkProfileExists method with an email that does exist
     */
    @Test
    public void checkProfileExistsProfileDoesExist() {
        ProfileRepository profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean profileExists = profileRepository.checkProfileExists("admin");
        Assert.assertFalse(profileExists);
    }


    /**
     * Testing the validate method with invalid email and password
     */
    @Test
    public void validateInvalidEmailAndPassword() {
        ProfileRepository profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean isValid = profileRepository.validate("sadasdsad", "dklfdhslkf");
        Assert.assertFalse(isValid);
    }

    /**
     * Testing the validate method with invalid password
     */
    @Test
    public void validateInvalidPassword() {
        ProfileRepository profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean isValid = profileRepository.validate("admin", "sadasdsadsad");
        Assert.assertFalse(isValid);
    }

    /**
     * Testing the validate method with invalid email
     */
    @Test
    public void validateInvalidEmail() {
        ProfileRepository profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean isValid = profileRepository.validate("adsadasdsadmin", "admin123");
        Assert.assertFalse(isValid);
    }

    /**
     * Testing the validate method with valid email and password
     */
    @Test
    public void validateValidData() {
        ProfileRepository profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean isValid = profileRepository.validate("admin", "admin123");
        Assert.assertTrue(isValid);
    }
}