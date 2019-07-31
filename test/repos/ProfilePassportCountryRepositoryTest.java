package repos;

import controllers.ProvideApplication;
import models.PassportCountry;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ProfilePassportCountryRepositoryTest extends ProvideApplication {

    String countryName = "Milky Way Galaxy";

    /**
     * Check that a country selected by user is inserted into db
     * if it does not already exist
     */
    @Test
    public void checkNewCountryInserted() {
        injectRepositories();
        if (passportCountryRepository.getPassportCountryId(countryName).get() != -1) {
            Assert.fail("Error: " + countryName + " exists in database.");
        }
        PassportCountry passportCountry = new PassportCountry("Milky Way Galaxy");
        profilePassportCountryRepository.insertProfilePassportCountry(passportCountry, 1);
        passportCountryRepository.getPassportCountryId("Milky Way Galaxy");

        Assert.assertTrue(passportCountryRepository.getPassportCountryId("Milky Way Galaxy").isPresent());
    }
}
