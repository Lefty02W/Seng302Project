package models;

/**
 * Class for getting inputs for searching functionality to Find an artist
 */
public class ArtistFormData {
    public String name = "";
    public String genre = "";
    public String country = "";
    public String followed = "";

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

}
