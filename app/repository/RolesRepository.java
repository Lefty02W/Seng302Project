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
    private final DatabaseExecutionContext context;
    private final EbeanConfig config;

    @Inject
    public RolesRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext context) {
        this.config = ebeanConfig;
        this.context = context;
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
     * Removes role from user using sql query
     * @param userId id of the user who's role is changing
     */
    public void removeRole(Integer userId){
        Transaction txn = ebeanServer.beginTransaction();
        String query = "DELETE FROM profile_roles WHERE profile_id = ?";
        SqlUpdate deleteQuery = Ebean.createSqlUpdate(query);
        deleteQuery.setParameter(1, userId);
        deleteQuery.execute();
        txn.commit();
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
     * @param roleName The name of the role get ID of
     * @return The role ID if a matching role name exists on database
     */
    private Optional<Integer> getRoleFromName(String roleName) {
        Integer roleId = null;
        String query = "SELECT * FROM roles WHERE role_name = ?";
        SqlRow row = ebeanServer.createSqlQuery(query).setParameter(1, roleName).findOne();
        if (row != null) {
            roleId = row.getInteger("role_id");
        }

        return Optional.ofNullable(roleId);
    }

    /**
     * Retrieves a list of profile ids for a given role name.
     *
     * @param roleName The name of the role get ID of
     * @return The role ID if a matching role name exists on database
     */
    public List<Integer> getProfileIdFromRoleName(String roleName) {
        Optional<Integer> roleId = getRoleFromName(roleName);
        Optional<Integer> roleIdGlobal = getRoleFromName("global_admin");

        List<Integer> profileIds = new ArrayList<>();

        if(roleId.isPresent() && roleIdGlobal.isPresent()){

            String query = "SELECT DISTINCT profile_id FROM profile_roles WHERE role_id = ? OR role_id = ?";
            List<SqlRow> rows = ebeanServer.createSqlQuery(query).setParameter(1, roleId.get()).setParameter(2, roleIdGlobal.get()).findList();

            for(SqlRow row: rows ) {

                profileIds.add(row.getInteger("profile_id"));

            }
        }
        return profileIds;
    }


    /**
     * Retrieves a list of profile ids for a given role name.
     *
     * @param roleName The name of the role get ID of
     * @return The role ID if a matching role name exists on database
     */
    public List<Integer> getExitingProfileIdsFromRoleName(String roleName) {
        Optional<Integer> roleId = getRoleFromName(roleName);
        Optional<Integer> roleIdGlobal = getRoleFromName("global_admin");

        List<Integer> profileIds = new ArrayList<>();

        if(roleId.isPresent() && roleIdGlobal.isPresent()){

            String query = "SELECT DISTINCT profile_id FROM profile_roles WHERE role_id = ? AND soft_delete = 0 OR role_id = ? AND soft_delete = 0";
            List<SqlRow> rows = ebeanServer.createSqlQuery(query).setParameter(1, roleId.get()).setParameter(2, roleIdGlobal.get()).findList();

            for(SqlRow row: rows ) {

                profileIds.add(row.getInteger("profile_id"));

            }
        }
        return profileIds;
    }



    /**
     * Add a role for a user based on the role name
     *
     * @param profileId The ID of the profile to add role for
     * @param roleName  The name of the role to add to user
     */
    public void setProfileRole(Integer profileId, String roleName) {
        Optional<Integer> role = getRoleFromName(roleName);
        // If the role does not exist, stop
        role.ifPresent(integer -> addProfileRole(profileId, integer));
    }




    /**
     * Add a role to a profile given the profile ID and role ID
     * @param profileId The ID of the profile to set role for
     * @param roleId The ID of the role to link to profile
     */
    private void addProfileRole(Integer profileId, Integer roleId) {
        Transaction transaction = ebeanServer.beginTransaction();
        String queryString = "INSERT INTO profile_roles(profile_id, role_id) VALUES (?, ?)";
        try {
            SqlUpdate query = Ebean.createSqlUpdate(queryString);
            query.setParameter(1, profileId);
            query.setParameter(2, roleId);
            query.setGetGeneratedKeys(true); // Need to set the ID of the generated key
            query.execute();
            transaction.commit();
        } finally {
            transaction.end();
        }
    }


    /**
     * Method to get single id back from a selected role main use in finding global admin and uses optionals
     * @param role String of role name to find
     * @return optional of user id that is in use of the role
     */
    public Optional<Integer> getIdFromRole(String role) {
        Optional<Integer> roleId = getRoleFromName(role);
        List<Integer> profileIds = new ArrayList<>();
        if (roleId.isPresent()) {
            String query = "SELECT DISTINCT profile_id FROM profile_roles WHERE role_id = ?";
            List<SqlRow> rows = ebeanServer.createSqlQuery(query).setParameter(1, roleId.get()).findList();
            for (SqlRow row : rows) {

                profileIds.add(row.getInteger("profile_id"));

            }
        }
        return Optional.ofNullable(profileIds.get(0));
    }
}
