package utilities;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import utility.Country;

import java.io.IOException;

public class CountryUtilityTest {

    Country country = new Country();


    /**
     * Tests that a non-null object is returned
     * when retrieving all countries
     */
    @Test
    public void getAllCountries() {
        Assert.assertNotNull(country.getAllCountries());
    }


    /**
     * Check a country's name can be found
     * by using it's ISO code
     */
    @Test
    @Ignore
    public void getCountryNameByCode() {
        Assert.assertEquals(country.getCountryNameByCode("nz"), "New Zealand");
    }


    /**
     * Test if an existing country is asserted to exist
     */
    @Test
    @Ignore
    public void checkCountryExists() {
        Assert.assertTrue(country.checkExists("New Zealand"));
    }


    /**
     * Check a former existing country is not asserted as existing
     */
    @Test
    @Ignore
    public void checkFormerCcountryExists() {
        Assert.assertFalse(country.checkExists("Yugoslavia"));
    }


}