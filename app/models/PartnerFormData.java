package models;

/**
 * Class for getting form inputs for search functionality of Find a traveller partner story
 */
public class PartnerFormData {
    public String searchNationality = "";
    public String searchGender = "";
    public Integer searchAgeRange ;
    public String searchTravellerTypes = "";

    public void setSearchAgeRange(int age) {
        searchAgeRange = age;
    }
}
