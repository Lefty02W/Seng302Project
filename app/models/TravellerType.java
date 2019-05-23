package models;


import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TravellerType extends Model {

    @Id
    @Constraints.Required
    private int travellerTypeId;
    @Constraints.Required
    private String travellerTypeName;

    public static final Finder<String, TravellerType> find = new Finder<>(TravellerType.class);

    public TravellerType(int Id, String name) {
        travellerTypeId = Id;
        travellerTypeName = name;
    }

    public TravellerType(String travellerTypeName) {
        this.travellerTypeName = travellerTypeName;
    }

    public int getTravellerTypeId() {
        return travellerTypeId;
    }

    public void setTravellerTypeId(int travellerTypeId) {
        this.travellerTypeId = travellerTypeId;
    }

    public String getTravellerTypeName() {
        return travellerTypeName;
    }

    public void setTravellerTypeName(String travellerTypeName) {
        this.travellerTypeName = travellerTypeName;
    }
}
