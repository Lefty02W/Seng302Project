package models;

import io.ebean.Finder;
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

    @Id
    private Integer imageId;

    @Lob
    @Constraints.Required
    private byte[] image;

    @Constraints.Required
    private Integer visible;

    public Image(String email, byte[] image, Integer visable){
        this.email = email;
        this.image = image;
        this.visible = visable;
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
}
