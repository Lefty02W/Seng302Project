package models;

/**
 * Class for getting inputs for searching functionality to Find an artist
 */
public class ArtistFormData {
    public String name = "";
    public String genre = "";
    public String country = "";
    public String followed = "";
    public String createdArtist = "";

    public void setName(String name) {
        this.name = name;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setFollowed(String followed) {
        this.followed = followed;
    }

    public void setCreatedArtist(String createdArtist) { this.createdArtist = createdArtist; }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getCountry() {
        return country;
    }

    public String getFollowed() {
        return followed;
    }

    public String getCreatedArtist() {
        return createdArtist;
    }
}
