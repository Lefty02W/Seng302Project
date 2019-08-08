package models;

import javax.persistence.Entity;

@Entity
public class ArtistProfile {

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


    public Integer getAPArtistId() {
        return artistId;
    }

    public Integer getAPProfileId() {
        return profileId;
    }
}
