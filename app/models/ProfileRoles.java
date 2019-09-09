package models;

import javax.persistence.Entity;

/**
 * Model to hold link from profile to a row
 */
@Entity
public class ProfileRoles {

    private int profileRoleId;
    private int profileId;
    private int roleId;

    public ProfileRoles(int profileRoleId, int profileId, int roleId) {
        this.profileRoleId = profileRoleId;
        this.profileId = profileId;
        this.roleId = roleId;
    }
}
