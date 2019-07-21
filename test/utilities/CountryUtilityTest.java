package utilities;

import controllers.ProvideApplication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utility.Country;

public class CountryUtilityTest {

    Country country;

    @Before
    public void setUp() throws Exception {
        country = new Country();
    }


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
    public void getCountryNameByCode() {
        Assert.assertEquals(country.getCountryNameByCode("nz"), "New Zealand");
    }


    /**
     * Check a country can be found by name
     */
    @Test
    public void getCountryByName() {
        Assert.assertEquals(country.getCountryByName("new zealand"), "New Zealand");
    }


    /**
     * Test if an existing country is asserted to exist
     */
    @Test
    public void checkCountryExists() {
        Assert.assertTrue(country.checkExists("New Zealand"));
    }


    /**
     * Check a former existing country is not asserted as existing
     */
    @Test
    public void checkFormerCcountryExists() {
        Assert.assertFalse(country.checkExists("Yugoslavia"));
    }


}