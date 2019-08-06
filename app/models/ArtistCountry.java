package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ArtistCountry {

    @Id
    private Integer artistCountryId;

    private Integer artistId;

    private Integer countryId;

    public ArtistCountry(Integer artistId, Integer countryId) {
        this.artistId = artistId;
        this.countryId = countryId;
    }

    public Integer getArtistCountryId() {
        return artistCountryId;
    }

    public void setArtistCountryId(Integer artistCountryId) {
        this.artistCountryId = artistCountryId;
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
