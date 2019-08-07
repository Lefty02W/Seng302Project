package models;

import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Artist {

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
    private Map<Integer, PassportCountry> country;

    private int softDelete;

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
     * Return the travellers types as a readable list
     * @return Array of Strings of traveller types
     */
    public ArrayList<Integer> getCountryList() {
        if (country != null) {
            ArrayList<PassportCountry> countryObjects = new ArrayList<>(country.values());
            ArrayList<Integer> countryIdList = new ArrayList<>();
            for (PassportCountry type : countryObjects) {
                countryIdList.add(type.getPassportId());
            }
            return countryIdList;
        } else {
            return new ArrayList<>();
        }
    }


    //Getters and setters


    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public Integer getArtistId() {
        return artistId;
    }

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