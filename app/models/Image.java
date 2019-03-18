package models;

import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.File;

@Entity
public class Image extends Model {

    @Constraints.Required
    private String email;

    @Constraints.Required
    @Id
    private Integer imageId;

    @Constraints.Required
    private File image;

    @Constraints.Required
    private Boolean visible;

    public  Image(String email, Integer imageId, File image, Boolean visable){
        this.email = email;
        this.imageId = imageId;
        this.image = image;
        this.visible = visable;
    }

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

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
