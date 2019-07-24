package utilities;

import controllers.ProvideApplication;
import models.PassportCountry;
import models.Profile;
import org.junit.Assert;
import org.junit.Test;
import utility.Country;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountryUtilityTest extends ProvideApplication {


    /**
     * Tests that a non-null object is returned
     * when retrieving all countries
     */
    @Test
    public void getAllCountries() {
        Assert.assertNotNull(Country.getInstance().getAllCountries());
    }


    /**
     * Check a country's name can be found
     * by using it's ISO code
     */
    @Test
    public void getCountryNameByCode() {
        Assert.assertEquals("New Zealand", Country.getInstance().getCountryNameByCode("nz"));
    }


    /**
     * Test if an existing country is asserted to exist
     */
    @Test
    public void checkCountryExists() {
        Assert.assertTrue(Country.getInstance().checkExists("New Zealand"));
    }


    /**
     * Check a former existing country is not asserted as existing
     */
    @Test
    public void checkFormerCountryExists() {
        Assert.assertFalse(Country.getInstance().checkExists("Yugoslavia"));
    }


    /**
     * Check if a user's outdated countries are correctly retrieved
     */
    @Test
    public void checkUserOutdatedCountries() {
        injectRepositories();

        List<String> testCountries = new ArrayList<String>();
        testCountries.add("Yugoslavia");
        testCountries.add("Czechoslovakia");
        testCountries.add("Ottoman Empire");
        testCountries.add("East Germany");

        Map<Integer, PassportCountry> testMap = new HashMap<>();
        for (int i = 0; i < testCountries.size(); i++) {
            testMap.put(i, new PassportCountry(i, testCountries.get(i)));
        }

        Profile profile = profileRepository.getProfileByProfileId(1);
        profile.setPassports(testMap);

        Assert.assertEquals(testCountries, Country.getInstance().getUserOutdatedCountries(profile));
    }

}