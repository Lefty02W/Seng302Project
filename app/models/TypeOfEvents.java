package models;

import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TypeOfEvents {

    @Id
    private int typeId;

    @Constraints.Required
    private String typeName;

    /**
     * Traditional constructor for TypeOfEvents. Used when retrieving an event type from the database
     * @param typeId id of the event type name (Primary Key)
     * @param typeName String: name of the event type
     */
    public TypeOfEvents(int typeId, String typeName){
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }
}
