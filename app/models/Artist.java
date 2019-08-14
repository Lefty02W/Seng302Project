package models;

import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.*;

@Entity
public class Artist extends Model {

    @Id
    private Integer artistId;

    @Constraints.Required
    private String artistName;

    @Constraints.Required
    private String biography;

    private String facebookLink;

    private String instagramLink;

    private String spotifyLink;

    private String twitterLink;

    private String websiteLink;

    private Collection<Profile> adminsList;

    private String members;

    @Transient
    private List<MusicGenre> genreList = new ArrayList<>();

    private int softDelete;

    private int verified;

    @Transient
    private Map<Integer, PassportCountry> country;

    @Transient
    private String genreFrom;

    @Transient
    private String adminForm;

    @Transient
    private String countries;


    public Artist() {

    }

    /**
     * Traditional constructor used for retrieving object from DB
     * @param artistId
     * @param artistName
     * @param biography
     * @param facebookLink
     * @param instagramLink
     * @param spotifyLink
     * @param twitterLink
     * @param websiteLink
     */
    public Artist(Integer artistId, String artistName, String biography, String facebookLink, String instagramLink, String spotifyLink, String twitterLink, String websiteLink, int softDelete, List<MusicGenre> genreList) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.biography = biography;
        this.facebookLink = facebookLink;
        this.instagramLink = instagramLink;
        this.spotifyLink = spotifyLink;
        this.twitterLink = twitterLink;
        this.websiteLink = websiteLink;
        this.softDelete = softDelete;
        this.genreList = genreList;
    }

    /**
     * Traditional constructor used for retrieving object from DB
     * @param artistId
     * @param artistName
     * @param biography
     * @param facebookLink
     * @param instagramLink
     * @param spotifyLink
     * @param twitterLink
     * @param websiteLink
     * @param adminsList
     * @param country
     */
    public Artist(Integer artistId, String artistName, String biography, String facebookLink, String instagramLink, String spotifyLink, String twitterLink, String websiteLink, Collection<Profile> adminsList, Map<Integer, PassportCountry> country, int softDelete) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.biography = biography;
        this.facebookLink = facebookLink;
        this.instagramLink = instagramLink;
        this.spotifyLink = spotifyLink;
        this.twitterLink = twitterLink;
        this.websiteLink = websiteLink;
        this.adminsList = adminsList;
        this.country = new HashMap<>();
        this.softDelete = softDelete;
    }
    /**
     * A function to turn the destination class created by the create destination form. It is required to turn the
     * , separated strings into maps.
     */
    public void initCountry() {
        this.country = new HashMap<>();

        if (countries != null) {
            int i = 1;
            for (String countryString : (countries.split(","))) {
                PassportCountry countryName = new PassportCountry(i, countryString);
                this.country.put(countryName.getPassportId(), countryName);
                i++;
            }
        }
    }

    /**
     * Return the countries as a readable list
     * Used to generate a list of countries that are to be
     * inserted into the artist_country database
     *
     * @return Array of Strings of country names
     */
    public ArrayList<String> getCountryList() {
        if (country != null) {
            ArrayList<PassportCountry> countryObjects = new ArrayList<>(country.values());
            ArrayList<String> countryList = new ArrayList<>();
            for (PassportCountry passportCountry : countryObjects) {
                countryList.add(passportCountry.getPassportName());
            }
            return countryList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Method that gets the current artists countries from a list
     * and concatenates them into a string for display
     *
     * @return a string of countries to be displayed
     */
    public String getCountryListString() {
        ArrayList<String> listOfCountries = getCountryList();
        String countryNameStrings = "";
        for (String countryName : listOfCountries) {
            countryNameStrings += countryName + ", ";
        }
        return countryNameStrings;
    }

    //Getters and setters

    public void setGenre(List<MusicGenre> genre) { this.genreList = genre;}

    public List<MusicGenre> getGenreList() {
        return genreList;
    }

    public int getVerified() {
        return verified;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public Integer getArtistId() {
        return artistId;
    }

    public String getCountrys() {return countries;}

    public String getGenre() {return genreFrom;}

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public String getSpotifyLink() {
        return spotifyLink;
    }

    public void setSpotifyLink(String spotifyLink) {
        this.spotifyLink = spotifyLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public Collection<Profile> getAdminsList() {
        return adminsList;
    }

    public void setAdminsList(Collection<Profile> adminsList) {
        this.adminsList = adminsList;
    }

    public Map<Integer, PassportCountry> getCountry() {
        return country;
    }

    public void setCountry(Map<Integer, PassportCountry> country) {
        this.country = country;
    }

    public int getSoftDelete() {
        return softDelete;
    }

    public void setSoftDelete(int softDelete) {
        this.softDelete = softDelete;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }
}