package models;

import play.data.validation.Constraints;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MappedSuperclass
public class Admin extends Profile {

    @Id
    private Integer admin_id;

    @Constraints.Required
    private Integer profile_id;

    @Constraints.Required
    private final Integer is_master;

    /**
     * Admin Constructor takes attributes from the profile class and requires
     * @param firstName
     * @param lastName
     * @param email
     * @param password
     * @param birthDate
     * @param passports
     * @param gender
     * @param timeCreated
     * @param nationalities
     * @param travellerTypes
     * @param trips
     * @param roles
     * @param admin_id
     * @param profile_id
     * @param is_master
     */
    public Admin(String firstName, String lastName, String email, String password, Date birthDate, String passports,
                 String gender, Date timeCreated, String nationalities, String travellerTypes, ArrayList<Trip> trips,
                 List<String> roles, Integer admin_id, Integer profile_id, Integer is_master) {
        super(firstName, lastName, email, password, birthDate, passports, gender, timeCreated, nationalities, travellerTypes, trips);
        this.admin_id = admin_id;
        this.profile_id = profile_id;
        this.is_master = is_master;
    }


    public Integer getAdmin_id() {
        return admin_id;
    }

    public Integer getProfile_id() {
        return profile_id;
    }

    public Integer getIs_master() {
        return is_master;
    }

}
