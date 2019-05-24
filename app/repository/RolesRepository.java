package repository;

import io.ebean.*;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Repository to handle database access calls.
 * For managing interactions with the roles link and roles table.
 * @author George
 */
public class RolesRepository {

    private final EbeanServer ebeanServer;

    @Inject
    public RolesRepository(EbeanConfig ebeanConfig) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    /**
     * Retrieves the role of a user based on the role id
     *
     * @param roleId The ID of the role to retrieve name of
     * @return An optional string type of the role's name
     */
    private Optional<String> getRoleById(Integer roleId) {
        String query = "SELECT role_name FROM roles WHERE role_id = ?";
        SqlRow rowList = ebeanServer.createSqlQuery(query).setParameter(1, roleId).findOne();
        String role = rowList.getString("role_name");


        return Optional.ofNullable(role);
    }


    /**
     * Gets the role of a user profile based on the profile id
     *
     * @param profileId The ID of the profile to retrieve its roles
     * @return An optional string list of the profile roles
     */
    public Optional<List<String>> getProfileRoles(Integer profileId) {
        String sql = ("SELECT role_id FROM profile_roles WHERE profile_id = ?");
        List<SqlRow> rowList = ebeanServer.createSqlQuery(sql).setParameter(1, profileId).findList();
        List<String> roles = new ArrayList<>();
        for (SqlRow row : rowList) {
            //Retrieve role as name by id
            Optional<String> role = getRoleById(row.getInteger("role_id"));
            if (role.isPresent()) { //Ensure role exists before adding to list
                roles.add(role.get());
            }
        }


        return Optional.of(roles);
    }


    /**
     * Retrieves the ID of a role based on its name
     *
     * @param roleName
     * @return The role ID if a matching role name exists on database
     */
    private Optional<Integer> getRoleFromName(String roleName) {
        Integer roleId;
        String query = "SELECT FROM roles WHERE role_name = ?";
        SqlRow row = ebeanServer.createSqlQuery(query).setParameter(1, roleName).findOne();
        roleId = row.getInteger("role_id");

        return Optional.ofNullable(roleId);
    }


    /**
     * Add a role for a user based on the role name
     *
     * @param profileId The ID of the profile to add role for
     * @param roleName  The name of the role to add to user
     */
    public void setProfileRole(Integer profileId, String roleName) {
        Optional<Integer> role = getRoleFromName(roleName);
        Integer roleId;
        // If the role does not exist, stop
        if (role.isPresent()) {
            roleId = role.get();
        } else {
            return;
        }
        Transaction transaction = ebeanServer.beginTransaction();
        String queryString = "INSERT INTO profile_roles(profile_id, role_id) VALUES(?, ?)";
        try {
            SqlUpdate query = Ebean.createSqlUpdate(queryString);
            query.setParameter(1, profileId);
            query.setParameter(2, roleId);
        } finally {
            transaction.end();
        }
    }

}
