package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    @Lob
    @Constraints.Required
    private byte[] image;

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

    @Transient
    private Map<Integer, Integer> destinationMap;


    /**
     * Constructor for image
     * @param image
     * @param contentType
     * @param visible
     * @param name
     */
    public Photo(byte[] image, String contentType, Integer visible, String name, int cropX, int cropY, int cropWidth, int cropHeight) {
        this.image = image;
        this.visible = visible;
        this.contentType = contentType;
        this.name = name;
        this.cropX = cropX;
        this.cropY = cropY;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        this.destinationMap = new HashMap<>();
    }

    // Finder for image
    public static final Finder<Integer, Photo> find = new Finder<>(Photo.class);



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

    public String getType() {
        return contentType;
    }

    public void setType(String contentType) {
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
     * destinationMap is used to see if a photo is in a destination for a particular user. This is used for choosing
     * to add another photo to a destination or taking a photo off a destination. This map will be sent to the destinations page
     *
     * This method will add the destinationId as the key and isTrue as the value to the map
     * @param destinationId the id of a destination
     * @param isTrue boolean for if the photo is shown for that destination
     * @return
     */
    public boolean putInDestinationMap(Integer destinationId, Integer isTrue) {
        if (isTrue != 0 && isTrue != 1) {
            return false;
        }
        try {
            destinationMap.put(destinationId, isTrue);
        } catch (NullPointerException e) {
            destinationMap = new HashMap<>();
            destinationMap.put(destinationId, isTrue);
        }
        return true;
    }

    public Map<Integer, Integer> getDestinationMap() {
        try {
            return destinationMap;
        } catch (NullPointerException e) {
            destinationMap = new HashMap<>();
            return destinationMap;
        }
    }

    public void clearDestinationMap() {
        try {
            destinationMap.clear();
        } catch (NullPointerException e) {
            destinationMap = new HashMap<>();
        }
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
