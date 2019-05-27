package models;

import javax.persistence.Entity;

@Entity
/**
 * Model class to hold a profile personal photo
 */
public class destinationPhoto {


    private int destinationPhotoId;
    private int profileId;
    private int photoId;
    private int destinationId;

    public destinationPhoto(int profileId, int photoId, int destinationId) {
        this.profileId = profileId;
        this.photoId = photoId;
        this.destinationId = destinationId;
    }

    public int getDestinationPhotoId() {
        return destinationPhotoId;
    }

    public int getProfileId() {
        return profileId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public int getDestinationId() {
        return destinationId;
    }
}

