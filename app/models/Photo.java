package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Photo class containing all the attributes of an image object.
 * photoId - Auto incrementing primary key
 * image - A Blob (Binary Large Object) byte array of the file converted into bytes.
 * visible - A tinyInt, 1 or 0 meaning 'public' or 'private' access.
 * contentType - The extension of a file uploaded (image/png, image/gif etc.).
 * name - The name of the uploaded file.
 */
@Entity
public class Photo extends Model {


    @Id
    private Integer photoId;

    @Constraints.Required
    private String path;

    @Constraints.Required
    private Integer visible;

    @Constraints.Required
    private String contentType;

    @Constraints.Required
    private String name;

    //this causes issues when signing in a new user as it searches for images in repo with feature cropx, cropy ect
    @Constraints.Required
    private int cropX;

    @Constraints.Required
    private int cropY;

    @Constraints.Required
    private int cropWidth;

    @Constraints.Required
    private int cropHeight;

    private Integer isProfilePic;

    /**
     * Constructor for image
     * @param email
     * @param path
     * @param contentType
     * @param visible
     * @param name
     */
    public Photo(String path, String contentType, Integer visible, String name, int cropX, int cropY, int cropWidth, int cropHeight, int isProfilePic) {
        this.path = path;
        this.visible = visible;
        this.contentType = contentType;
        this.name = name;
        this.cropX = cropX;
        this.cropY = cropY;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        this.isProfilePic = isProfilePic;
    }

    // Finder for image
    public static final Finder<Integer, Photo> find = new Finder<>(Photo.class);

    public Integer getIsProfilePic() { return isProfilePic; }

    public void setIsProfilePic(Integer isProfilePic) { this.isProfilePic = isProfilePic; }



    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public String getType() { return contentType; }

    public void setType(String contentType) { this.contentType = contentType; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setpath(String path) {
        this.path = path;
    }

    public int getCropX() {
        return cropX;
    }

    public void setCropX(int cropX) {
        this.cropX = cropX;
    }

    public int getCropY() {
        return cropY;
    }

    public void setCropY(int cropY) {
        this.cropY = cropY;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }


    /**
     * Method to test if the image visibility is 1 or 0 and returns a string 'Public' or 'Private'
     * respectively. Used only for display on the frontend.
     *
     * @param visibility Integer specifying the saved content type of an image
     * @return a String 'Public' or 'Private'
     */
    public String displayVisibility(Integer visibility) {
        if(visibility == 1) {
            return "Public";
        }
        return "Private";
    }
}
