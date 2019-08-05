package models;

import javax.persistence.Id;

public class ArtistProfile {

    @Id
    private Integer artistProfileId;

    private Integer artistId;

    private Integer profileId;


    ArtistProfile(Integer artistId, Integer profileId){
        this.artistId = artistId;
        this.profileId = profileId;
    }

    public Integer getArtistProfileId() {
        return artistProfileId;
    }

    public Integer getAPArtistId() {
        return artistId;
    }

    public Integer getAPProfileId() {
        return profileId;
    }
}
