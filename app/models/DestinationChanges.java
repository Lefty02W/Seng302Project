package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * DestinationChanges class containing all attributes of a change for inserting the change object into the
 * destination_changes database table
 *
 * id - Primary Key
 * travellerType - int foreign key referencing travellerTypeId from travellerType table
 * action - tinyInt; 1 for add 0 for remove.
 * requestId - int Foreign Key referencing requestId from destinationRequest table
 */
@Entity
public class DestinationChanges extends Model {

    @Id
    private Integer id;

    @Constraints.Required
    private Integer travellerTypeId;

    @Constraints.Required
    private Integer action;

    @Constraints.Required
    private Integer requestId;
    @Transient
    private String email;
    @Transient
    private Destination destination;
    @Transient
    private TravellerType travellerType;

    public DestinationChanges(Integer travellerTypeId, Integer action, Integer requestId) {
        this.travellerTypeId = travellerTypeId;
        this.action = action;
        this.requestId = requestId;
    }

    // Finder for destination
    public static final Finder<String, DestinationChanges> find = new Finder<>(DestinationChanges.class);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTravellerTypeId() {
        return travellerTypeId;
    }

    public void setTravellerTypeId(Integer travellerTypeId) {
        this.travellerTypeId = travellerTypeId;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getEmail() { return email;}

    public void setEmail(String email) { this.email = email;}

    public Destination getDestination() { return destination;}

    public void setDestination(Destination destination) { this.destination = destination;}

    public TravellerType getTravellerType() {return travellerType;}

    public void setTravellerType(TravellerType travellerType) {
        this.travellerType = travellerType;
    }
}
