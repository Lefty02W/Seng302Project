package models;

import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * UndoStack class containing all the attributes for inserting an undo object into the undo_stack database table
 * table required to keep the state of each admins stack so we can process hard deletes even if there is a system
 * failure
 *
 * entryId - Primary Key
 * item_type - string referencing the object type name from an enum of object types
 * objectId - id of the object to be removed from the system
 * profileId - profile id of the admin user that made the delete
 */
public class UndoStack extends Model {

    @Id
    private int entryId;

    @Constraints.Required
    private String item_type;

    @Constraints.Required
    private int objectId;

    @Constraints.Required
    private int profileId;

    public UndoStack(String item_type, int objectId, int profileId){
        this.item_type = item_type;
        this.objectId = objectId;
        this.profileId = profileId;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }
}
