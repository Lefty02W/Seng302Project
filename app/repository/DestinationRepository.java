package repository;

import io.ebean.*;
import models.Destination;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;


/**
 * A destination repository that executes database operations in a different
 * execution context handles all interactions with the destination table .
 */
public class DestinationRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;


    /**
     * A Constructor which links to the ebeans database
     *
     * @param ebeanConfig
     * @param executionContext
     */
    @Inject
    public DestinationRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Returns a specific destination
     *
     * @param destID The ID of the destination to return
     * @return
     */
    public Destination lookup(int destID) {
        return ebeanServer.find(Destination.class).setId(destID).findOne();
    }

    /**
     * Get the users destination list
     *
     * @param id the id of the user profile
     * @return destinations, list of all user destinations
     */
    public ArrayList<Destination> getUserDestinations(int id) {
        return new ArrayList<>(Destination.find.query()
                .where()
                .eq("profile_id", id)
                .findList());
    }

    /**
     * Get the all of the public destinations
     *
     * @return destinations, list of all public destinations
     */
    public ArrayList<Destination> getPublicDestinations() {
        return new ArrayList<>(Destination.find.query()
                .where()
                .eq("visible", 1)
                .findList());
    }


    /**
     * Inserts a new destination to the database.
     *
     * @param dest The destination to insert
     * @return
     */
    public CompletionStage<String> insert(Destination dest) {
        return supplyAsync(() -> {
            ebeanServer.insert(dest);
            return String.format("Destination %s added", dest.getName());
        }, executionContext);
    }

    /**
     * Deletes a destination from the database
     *
     * @param destID The ID of the destination to delete
     * @return
     */
    public CompletionStage<Optional<String>> delete(int destID) {
        return supplyAsync(() -> {
            try {
                final Optional<Destination> destinationOptional = Optional.ofNullable(ebeanServer.find(Destination.class)
                        .setId(destID).findOne());
                destinationOptional.ifPresent(Model::delete);
                return Optional.of(String.format("Destination %s deleted", destinationOptional.map(p -> p.getName())));
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    /**
     * Updates a destination in the database
     *
     * @param newDestination The new info to change the destination to
     * @param Id             The ID of the destination to editDestinations
     * @return
     */
    public CompletionStage<Optional<String>> update(Destination newDestination, Integer Id) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<String> value = Optional.empty();
            try {
                Destination targetDestination = ebeanServer.find(Destination.class).setId(Id).findOne();
                if (targetDestination != null) {
                    targetDestination.setName(newDestination.getName());
                    targetDestination.setType(newDestination.getType());
                    targetDestination.setCountry(newDestination.getCountry());
                    targetDestination.setDistrict(newDestination.getDistrict());
                    targetDestination.setLatitude(newDestination.getLatitude());
                    targetDestination.setLongitude(newDestination.getLongitude());
                    targetDestination.setVisible(newDestination.getVisible());
                    targetDestination.update();
                    txn.commit();
                    value = Optional.of(String.format("Destination %s edited", newDestination.getDestinationId()));
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }

    /**
     * class to check if destination is already available to user
     * return true if already in else false
     */
    public boolean checkValid(Destination destination, int id) {
        Destination destinations = (Destination.find.query()
                .where()
                .eq("name", destination.getName())
                .eq("type", destination.getType())
                .eq("country", destination.getCountry())
                .eq("profile_id", id)
                .findOne());
        Destination publicDestinations = (Destination.find.query()
                .where()
                .eq("name", destination.getName())
                .eq("type", destination.getType())
                .eq("country", destination.getCountry())
                .eq("visible", "1")
                .findOne());
        if (publicDestinations != null) {
            return true;
        } else if (destinations != null) {
            return true;
        } else {
            return false;
        }
    }

    public Optional<ArrayList<Integer>> followDesination(int destId, int profileId) {
        String updateQuery = "INSERT into follow_destination(profile_id, destination_id) values (?, ?)";
        SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
        query.setParameter(1, profileId);
        query.setParameter(2, destId);
        query.execute();
        return getFollowedDestinationIds(profileId);
    }

    public Optional<ArrayList<Integer>> unfollowDesination(int destId, int profileId) {
        String updateQuery = "DELETE from follow_destination where profile_id = ? and destination_id =  ?";
        SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
        query.setParameter(1, profileId);
        query.setParameter(2, destId);
        query.execute();
        return getFollowedDestinationIds(profileId);
    }

    public Optional<ArrayList<Destination>> getFollowedDesinations(int profileId) {
        String updateQuery = "Select D.destination_id, D.profile_id, D.name, D.type, D.country, D.district, D.latitude, D.longitude, D.visible " +
                "from follow_destination JOIN destination D on follow_destination.destination_id = D.destination_id where follow_destination.profile_id = ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(updateQuery).setParameter(1, profileId).findList();
        ArrayList<Destination> destList = new ArrayList<>();
        Destination destToAdd;
        for (SqlRow aRowList : rowList) {
            destToAdd = new Destination();
            destToAdd.setDestinationId(aRowList.getInteger("destination_id"));
            destToAdd.setProfileId(aRowList.getInteger("profile_id"));
            destToAdd.setName(aRowList.getString("name"));
            destToAdd.setType(aRowList.getString("type"));
            destToAdd.setCountry(aRowList.getString("country"));
            destToAdd.setDistrict(aRowList.getString("district"));
            destToAdd.setLatitude(aRowList.getDouble("latitude"));
            destToAdd.setLongitude(aRowList.getDouble("longitude"));
            destToAdd.setVisible(aRowList.getBoolean("visible") ? 1 : 0);
            destList.add(destToAdd);
        }
        return Optional.of(destList);
    }

    public Optional<ArrayList<Integer>> getFollowedDestinationIds(int profileId) {
        String updateQuery = "Select destination_id from follow_destination where profile_id = ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(updateQuery).setParameter(1, profileId).findList();
        ArrayList<Integer> destIdList = new ArrayList<>();
        for (SqlRow aRowList : rowList) {
            int id = aRowList.getInteger("destination_id");
            destIdList.add(id);
        }
        return Optional.of(destIdList);
    }

    public Optional<ArrayList<Destination>> getAdminDestinations() {
        ArrayList<Destination> destList = new ArrayList<>();
        String selectQuery = "Select * from destination where profile_id IN (select profile_id from admin)";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(selectQuery).findList();
        Destination destToAdd;
        for (SqlRow aRowList : rowList) {
            destToAdd = new Destination();
            destToAdd.setDestinationId(aRowList.getInteger("destination_id"));
            destToAdd.setProfileId(aRowList.getInteger("profile_id"));
            destToAdd.setName(aRowList.getString("name"));
            destToAdd.setType(aRowList.getString("type"));
            destToAdd.setCountry(aRowList.getString("country"));
            destToAdd.setDistrict(aRowList.getString("district"));
            destToAdd.setLatitude(aRowList.getDouble("latitude"));
            destToAdd.setLongitude(aRowList.getDouble("longitude"));
            destToAdd.setVisible(aRowList.getBoolean("visible") ? 1 : 0);
            destList.add(destToAdd);
        }
        return Optional.of(destList);
    }


    /**
     * class to check if destination is already available to user
     * return true if already in else false
     */
    public boolean checkValidEdit(Destination destination, int  profileId, int id) {
        Destination destinations = (Destination.find.query()
                .where()
                .eq("name", destination.getName())
                .eq("type", destination.getType())
                .eq("country", destination.getCountry())
                .eq("profile_id", profileId)
                .findOne());
        Destination publicDestination = (Destination.find.query()
                .where()
                .eq("name", destination.getName())
                .eq("type", destination.getType())
                .eq("country", destination.getCountry())
                .eq("visible", "1")
                .findOne());
        if (publicDestination != null) {
            if (publicDestination.getDestinationId() != id) {
                return true;
            }
        }

        if (destinations != null) {
            if (destinations.getDestinationId() != id) {
                return true;
            }
        }

        return false;
    }
}
