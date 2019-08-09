package repos;

import controllers.TestApplication;
import models.PassportCountry;
import org.junit.Assert;
import org.junit.Test;

public class ProfilePassportCountryRepositoryTest {

    String countryName = "Milky Way Galaxy";

    /**
     * Check that a country selected by user is inserted into db
     * if it does not already exist
     */
    @Test
    public void checkNewCountryInserted() {
        if (TestApplication.getPassportCountryRepository().getPassportCountryId(countryName).get() != -1) {
            Assert.fail("Error: " + countryName + " exists in database.");
        }
        PassportCountry passportCountry = new PassportCountry("Milky Way Galaxy");
        TestApplication.getProfilePassportCountryRepository().insertProfilePassportCountry(passportCountry, 1);
        TestApplication.getPassportCountryRepository().getPassportCountryId("Milky Way Galaxy");

        Assert.assertTrue(TestApplication.getPassportCountryRepository().getPassportCountryId("Milky Way Galaxy").isPresent());
    }
}
