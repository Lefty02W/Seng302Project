package models;

import javax.persistence.Entity;

/**
 * Model to hold the link from an artist to the artists profile picture
 */
@Entity
public class ArtistProfilePhoto {

    private int artistId;
    private int personalPhotoId;

    public ArtistProfilePhoto(int artistId, int personalPhotoId) {
        this.artistId = artistId;
        this.personalPhotoId = personalPhotoId;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public int getPersonalPhotoId() {
        return personalPhotoId;
    }

    public void setPersonalPhotoId(int personalPhotoId) {
        this.personalPhotoId = personalPhotoId;
    }
}
