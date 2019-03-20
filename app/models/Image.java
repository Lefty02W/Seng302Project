package models;

import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.File;
import java.sql.Blob;

@Entity
public class Image extends Model {

    @Constraints.Required
    private String email;

    @Constraints.Required
    @Id
    private Integer imageId;

    @Lob
    @Constraints.Required
    private byte[] image;

    @Constraints.Required
    private Boolean visible;

    public Image(String email, Integer imageId, byte[] image, Boolean visable){
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
