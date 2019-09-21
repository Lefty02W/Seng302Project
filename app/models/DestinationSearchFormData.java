package models;

/**
 * Class for binding input for searching a destination
 */
public class DestinationSearchFormData {
    public String name = "";
    public Boolean isPublic = false;

    public void setName(String name) {
        this.name = name;
    }
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

}
