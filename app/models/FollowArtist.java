package models;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import play.data.validation.Constraints;

@Entity
public class FollowArtist extends Model {

    @Id
    private int artistFollowId;

    @Constraints.Required
    private int profileId;

    @Constraints.Required
    private int artistId;

    /**
     * Tarditional constructor used for retrieving followArtist object from the database
     * @param artistFollowId primary key id of the table entry
     * @param profileId id of the profile who follows a given artist
     * @param artistId id of the artist that the given profile follows
     */
    public FollowArtist(int artistFollowId, int profileId, int artistId){
        this.artistFollowId = artistFollowId;
        this.profileId = profileId;
        this.artistId = artistId;
    }

    public int getArtistFollowId() {
        return artistFollowId;
    }

    public void setArtistFollowId(int artistFollowId) {
        this.artistFollowId = artistFollowId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }
}
