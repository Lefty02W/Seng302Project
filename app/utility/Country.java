package utility;

import play.mvc.Http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Country {

    private String baseURL = "https://restcountries.eu/rest/v2/";

    public Country() {
    }


    /**
     * Get a list of all countries from the RESTCountries api
     *
     * @return
     */
    public List<String> getAllCountries() {
        List<String> countries = new ArrayList<>();
        try {
            URL url = new URL(baseURL + "all?fields=name;");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                countries.add(line);
            }
        } catch (IOException exception) {
            return countries;
        }


        return countries;
    }


    /**
     * Get a country's name from its ISO code
     * @param code - the ISO code
     * @return name - Name of the country
     */
    public String getCountryNameByCode(String code) {
        String name = "";


        return name;
    }


    /**
     * Check if a country exists
     * @param name - Name of country to check
     * @return boolean - True if exists, false otherwise
     */
    public boolean checkExists(String name) {
        boolean exists = true;

        return exists;
    }

}
