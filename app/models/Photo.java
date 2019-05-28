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

    /**
     *
     * @param path relative path for the image stored in the database
     * @param contentType content type of the image
     * @param visible the privacy setting public or private
     * @param name file name of image
     */
    public Photo(String path, String contentType, Integer visible, String name) {
        this.path = path;
        this.visible = visible;
        this.contentType = contentType;
        this.name = name;
    }

    // Finder for image
    public static final Finder<Integer, Photo> find = new Finder<>(Photo.class);

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
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
