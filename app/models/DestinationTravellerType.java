package models;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DestinationTravellerType {

    @Id
    private int id;

    private int destinationId;

    private int travellerTypeId;

    public DestinationTravellerType(int destinationId, int travellerTypeId) {
        this.destinationId = destinationId;
        this.travellerTypeId = travellerTypeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public int getTravellerTypeId() {
        return travellerTypeId;
    }

    public void setTravellerTypeId(int travellerTypeId) {
        this.travellerTypeId = travellerTypeId;
    }
}
