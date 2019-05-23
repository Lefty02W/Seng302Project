package models;

public class PersonalPhoto {


    private int personalPhotoId;
    private int profileId;
    private int photoId;

    public PersonalPhoto(int personalPhotoId, int profileId, int photoId) {
        this.personalPhotoId = personalPhotoId;
        this.profileId = profileId;
        this.photoId = photoId;
    }

    public PersonalPhoto(int profileId, int photoId) {
        this.profileId = profileId;
        this.photoId = photoId;
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
}

