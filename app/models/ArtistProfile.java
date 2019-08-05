package models;

import play.data.validation.Constraints;

import javax.persistence.Id;
import java.util.Collection;
import java.util.Map;

public class ArtistProfile {

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

    private Collection<Profile> membersList;

    private Map<Integer, PassportCountry> country;

    private int softDelete;

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
     * @param membersList
     * @param country
     */
    public ArtistProfile(Integer artistId, String artistName, String biography, String facebookLink, String instagramLink, String spotifyLink, String twitterLink, String websiteLink, Collection<Profile> membersList, Map<Integer, PassportCountry> country, int softDelete) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.biography = biography;
        this.facebookLink = facebookLink;
        this.instagramLink = instagramLink;
        this.spotifyLink = spotifyLink;
        this.twitterLink = twitterLink;
        this.websiteLink = websiteLink;
        this.membersList = membersList;
        this.country = country;
        this.softDelete = softDelete;
    }

    //Getters and setters

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

    public Collection<Profile> getMembersList() {
        return membersList;
    }

    public void setMembersList(Collection<Profile> membersList) {
        this.membersList = membersList;
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
}
