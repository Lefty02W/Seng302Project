package models;

import javax.persistence.Entity;

/**
 * Model to hold the link from an artist to the artists profile picture
 */
@Entity
public class ArtistProfilePhoto {

    private int artistId;
    private int photoId;

    public ArtistProfilePhoto(int artistId, int photoId) {
        this.artistId = artistId;
        this.photoId = photoId;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }
}
