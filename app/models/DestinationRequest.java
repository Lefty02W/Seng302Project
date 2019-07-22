package models;

import io.ebean.Finder;
import io.ebean.Model;
import repository.TravellerTypeRepository;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class containing attributes for generating an entry in the Destination request linking table linking a request with a
 * profile, required for creating request changes
 */
@Entity
public class DestinationRequest extends Model{

    @Id
    private Integer id;

    private Integer destinationId;

    private Integer profileId;

    @Transient
    private String toAdd;

    @Transient
    private String toRemove;


    public DestinationRequest(Integer destinationId, Integer profileId){
        this.destinationId = destinationId;
        this.profileId = profileId;
    }

    // Finder for destinationRequest
    public static final Finder<String, DestinationRequest> find = new Finder<>(DestinationRequest.class);

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

    public String getToAdd() {
        return toAdd;
    }

    public void setToAdd(String toAdd) {
        this.toAdd = toAdd;
    }

    public String getToRemove() {
        return toRemove;
    }

    public void setToRemove(String toRemove) {
        this.toRemove = toRemove;
    }

    public List<String> getToAddList(){
        try{
            String myString = getToAdd();
            List<String> result = convertToList(myString);
            return result;
        } catch (Exception e){
            return Collections.emptyList();
        }

    }

    public List<String> getToRemoveList(){
        try{
            return convertToList(getToRemove());
        } catch (Exception e){
            return Collections.emptyList();
        }
    }

    private List<String> convertToList(String string){
        return Arrays.asList(string.split(","));
    }
}
