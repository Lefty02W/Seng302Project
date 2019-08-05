package models;

import javax.persistence.Id;

public class ArtistProfile {

    @Id
    private Integer artistProfileId;

    private Integer artistId;

    private Integer profileId;

    /**
     * Traditional constructor used for retrieving object from DB
     * @param artistId The artist id the profile is being added to
     * @param profileId The profile id to add
     * */
    public ArtistProfile(Integer artistId, Integer profileId){
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
