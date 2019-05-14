package models;


import io.ebean.Model;

import javax.persistence.Entity;

//todo delete travellerType enum and rename this to travellerType
@Entity
public class TravellerTypes extends Model {

    private int travellerTypeId;
    private String travellerTypeName;

    public TravellerTypes(int Id, String name) {
        travellerTypeId = Id;
        travellerTypeName = name;
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
