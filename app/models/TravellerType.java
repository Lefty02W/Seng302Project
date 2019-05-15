package models;


import io.ebean.Model;

import javax.persistence.Entity;

@Entity
public class TravellerType extends Model {

    private int travellerTypeId;
    private String travellerTypeName;

    public TravellerType(int Id, String name) {
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
