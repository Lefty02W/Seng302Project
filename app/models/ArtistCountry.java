package models;

import javax.persistence.Entity;

@Entity
public class ArtistCountry {

    private Integer artistId;

    private Integer countryId;

    public ArtistCountry(Integer artistId, Integer countryId) {
        this.artistId = artistId;
        this.countryId = countryId;
    }

    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }
}
