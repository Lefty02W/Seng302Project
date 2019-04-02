package repository;

import controllers.ProvideApplication;
import org.junit.Assert;
import org.junit.Test;

public class ProfileRepositoryTest extends ProvideApplication {

    //TODO: write methods to populate db with some test data and then remove it afterwards
    ProfileRepository profileRepository;

    /**
     * Testing the checkProfileExists method with an email that does not exist
     */
    @Test
    public void checkProfileExistsProfileDoesNotExist() {
        profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean profileExists = profileRepository.checkProfileExists("kdlhafalkjdfhalkdf@gmail.co.nz");
        Assert.assertFalse(profileExists);
    }

    /**
     * Testing the checkProfileExists method with an email that does exist
     */
    @Test
    public void checkProfileExistsProfileDoesExist() {
        profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean profileExists = profileRepository.checkProfileExists("jenny@gmail.com");
        Assert.assertTrue(profileExists);
    }


    /**
     * Testing the validate method with invalid email and password
     */
    @Test
    public void validateInvalidEmailAndPassword() {
        profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean isValid = profileRepository.validate("sadasdsad", "dklfdhslkf");
        Assert.assertFalse(isValid);
    }

    /**
     * Testing the validate method with invalid password
     */
    @Test
    public void validateInvalidPassword() {
        profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean isValid = profileRepository.validate("jenny@gmail.com", "sadasdsadsad");
        Assert.assertFalse(isValid);
    }

    /**
     * Testing the validate method with invalid email
     */
    @Test
    public void validateInvalidEmail() {
        profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean isValid = profileRepository.validate("adsadasdsadmin", "password");
        Assert.assertFalse(isValid);
    }

    /**
     * Testing the validate method with valid email and password
     */
    @Test
    public void validateValidData() {
        profileRepository = app.injector().instanceOf(ProfileRepository.class);
        boolean isValid = profileRepository.validate("jenny@gmail.com", "password");
        Assert.assertTrue(isValid);
    }

}