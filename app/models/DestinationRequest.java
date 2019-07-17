package models;

import io.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Class containing attributes for generating an entry in the Destination request linking table linking a request with a
 * profile, required for creating request changes
 */
@Entity
public class DestinationRequest extends Model{

    @Id
    private Integer id;

    @Constraints.Required
    private Integer destinationId;

    @Constraints.Required
    private Integer profileId;

    public DestinationRequest(Integer destinationId, Integer profileId){
        this.destinationId = destinationId;
        this.profileId = profileId;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }
}
