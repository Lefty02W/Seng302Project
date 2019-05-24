package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlRow;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Repository to handle database access calls.
 * For managing interactions with the roles link and roles table.
 */
public class RolesRepository {

    private final EbeanServer ebeanServer;

    @Inject
    public RolesRepository(EbeanConfig ebeanConfig) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    /**
     * Retrieves the role of a user based on the role id
     * @param roleId The ID of the role to retrieve name of
     * @return An optional string type of the role's name
     */
    public Optional<String> getRoleById(Integer roleId) {
        String query = "select role_name from roles where role_id = ?";
        SqlRow rowList = ebeanServer.createSqlQuery(query).setParameter(1, roleId).findOne();
        String role = rowList.getString("role_name");


        return Optional.of(role);
    }


    /**
     * Gets the role of a user profile based on the profile id
     * @param profileId The ID of the profile to retrieve its roles
     * @return An optional string list of the profile roles
     */
    public Optional<List<String>> getProfileRoles(Integer profileId) {
        String sql = ("select role_id from profile_roles where profile_id = ?");
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

}
