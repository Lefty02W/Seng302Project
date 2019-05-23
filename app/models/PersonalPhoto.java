package models;

public class PersonalPhoto {


    private int personalPhotoId;
    private int profileId;
    private int photoId;
    private int isProfilePicture;

    public PersonalPhoto(int personalPhotoId, int profileId, int photoId, int isProfilePicture) {
        this.personalPhotoId = personalPhotoId;
        this.profileId = profileId;
        this.photoId = photoId;
        this.isProfilePicture = isProfilePicture;
    }

    public PersonalPhoto(int profileId, int photoId) {
        this.profileId = profileId;
        this.photoId = photoId;
    }

    public int getProfileId() {
        return profileId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public int getIsProfilePicture() {
        return isProfilePicture;
    }
}

