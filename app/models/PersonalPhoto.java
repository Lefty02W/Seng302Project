package models;

import javax.persistence.Entity;

@Entity
/**
 * Model class to hold a profile personal photo
 */
public class PersonalPhoto {


    private int personalPhotoId;
    private int profileId;
    private int photoId;
    private int isProfilePhoto;

    public PersonalPhoto(int profileId, int photoId) {
        this.profileId = profileId;
        this.photoId = photoId;
    }

    public PersonalPhoto(int profileId, int photoId, int isProfilePhoto) {
        this.profileId = profileId;
        this.photoId = photoId;
        this.isProfilePhoto = isProfilePhoto;
    }

    public int getPersonalPhotoId() {
        return personalPhotoId;
    }

    public int getProfileId() {
        return profileId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public int getIsProfilePhoto() {
        return isProfilePhoto;
    }
}

