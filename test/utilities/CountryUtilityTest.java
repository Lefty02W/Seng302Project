package utilities;

import org.junit.Assert;
import org.junit.Test;
import utility.Country;

public class CountryUtilityTest {



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


}