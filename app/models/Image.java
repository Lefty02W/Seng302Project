package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.Constraint;
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

    @Constraints.Required
    private String type;

    @Constraints.Required
    private String name;

    public Image(String email, byte[] image, String type, Integer visable, String name){
        this.email = email;
        this.image = image;
        this.type = type;
        this.visible = visable;
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
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String displayVisibility(Integer visibility) {
        if(visibility == 1) {
            return "Public";
        } else {
            return "Private";
        }
    }

}
