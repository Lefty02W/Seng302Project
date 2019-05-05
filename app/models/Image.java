package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Image class containing all the attributes of an image object.
 * email - The email of the user the image is linked to.
 * imageId - Auto incrementing primary key
 * image - A Blob (Binary Large Object) byte array of the file converted into bytes.
 * visible - A tinyInt, 1 or 0 meaning 'public' or 'private' access.
 * contentType - The extension of a file uploaded (image/png, image/gif etc.).
 * name - The name of the uploaded file.
 */
@Entity
public class Image extends Model {

    @Constraints.Required
    private String email;

    @Id
    private Integer imageId;

    @Constraints.Required
    private String path;

    @Constraints.Required
    private Integer visible;

    @Constraints.Required
    private String contentType;

    @Constraints.Required
    private String name;


    /**
     * Constructor for image
     * @param email
     * @param path
     * @param contentType
     * @param visible
     * @param name
     */
    public Image(String email, String path, String contentType, Integer visible, String name){
        this.email = email;
        this.path = path;
        this.contentType = contentType;
        this.visible = visible;
        this.name = name;
    }

    // Finder for image
    public static final Finder<Integer, Image> find = new Finder<>(Image.class);

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
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
