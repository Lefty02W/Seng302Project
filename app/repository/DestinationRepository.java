package repository;

import io.ebean.*;
import models.*;
import play.db.ebean.EbeanConfig;
import play.db.ebean.Transactional;

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
    private final RolesRepository rolesRepository;


    /**
     * A Constructor which links to the ebeans database
     *
     * @param ebeanConfig
     * @param executionContext
     */
    @Inject
    public DestinationRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext, RolesRepository roleRepository) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.rolesRepository = roleRepository;
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
        return new ArrayList<>(ebeanServer.find(Destination.class)
                .where()
                .eq("profile_id", id)
                .eq("soft_delete",0)
                .findList());
    }

    /**
     * Get the all of the public destinations
     *
     * @return destinations, list of all public destinations
     */
    public ArrayList<Destination> getPublicDestinations() {
        return new ArrayList<>(ebeanServer.find(Destination.class)
                .where()
                .eq("visible", 1)
                .eq("soft_delete",0)
                .findList());
    }


    /**
     * Inserts a new destination to the database.
     *
     * @param dest The destination to insert
     * @return
     */
    public CompletionStage<Optional<Integer>> insert(Destination dest) {
        return supplyAsync(() -> {
            ebeanServer.insert(dest);
            return Optional.of(dest.getDestinationId());
        }, executionContext);
    }

    /**
     * Method to check if a passed destination to be delete is within a treasure hunt or trip
     *
     * @param destinationId the id of the destination to check
     * @return the result of the check with and optional id of the treasure hunt or trip which contains the destination within a completion stage
     */
    public CompletionStage<Optional<String>> checkDestinationExists(int destinationId) {
        return supplyAsync(
                () -> {
                    List<Integer> foundIds =
                            ebeanServer
                                    .find(TripDestination.class)
                                    .where()
                                    .eq("destination_id", destinationId)
                                    .select("tripId")
                                    .findSingleAttributeList();
                    if (foundIds.isEmpty()) {
                        List<Integer> foundIds2 =
                                    ebeanServer
                                            .find(TreasureHunt.class)
                                            .where()
                                            .eq("destination_id", destinationId)
                                            .select("treasureHuntId")
                                            .findSingleAttributeList();
                        if (foundIds2.isEmpty()) return Optional.empty();
                        else return Optional.of("treasure hunts: " + foundIds2);
                    } else {
                        return Optional.of("trips: " + foundIds);
                    }
                },
                executionContext);
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
                return Optional.of(String.format("Destination %s deleted", destinationOptional.map(Destination::getName)));
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    /**
     * sets soft delete for a destination which eather deletes it or
     * undoes the delete
     * @param destId The ID of the destination to soft delete
     * @param delete Boolean, true if is to be deleted, false if cancel a delete
     * @return
     */
    public CompletionStage<Integer> setSoftDelete(int destId, boolean delete) {
        return supplyAsync(() -> {
            try {
                Destination targetDest = ebeanServer.find(Destination.class).setId(destId).findOne();
                if (targetDest != null) {
                    if (delete) {
                        targetDest.setSetSoftDelete(1);
                    } else {
                        targetDest.setSetSoftDelete(0);
                    }
                    targetDest.update();
                    return 1;
                } else {
                    return 0;
                }
            } catch(Exception e) {
                return 0;
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
    public CompletionStage<Optional<Integer>> update(Destination newDestination, Integer Id) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Integer> value = Optional.empty();
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
                    value = Optional.of(targetDestination.getDestinationId());
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }


    /**
     * Update function to change only the profileId of a destination since the other update cannot handle this
     * Preconditions: The newDestinations profileId is a valid profileId
     *
     * @param newDestination
     * @param destinationId
     * @return
     */
    public Optional<Integer> updateProfileId(Destination newDestination, Integer destinationId) {
        Transaction txn = ebeanServer.beginTransaction();
        Optional<Integer> value = Optional.empty();
        try {
            Destination targetDestination = ebeanServer.find(Destination.class).setId(destinationId).findOne();
            if (targetDestination != null) {
                targetDestination.setProfileId(newDestination.getProfileId());
                targetDestination.update();
                txn.commit();
                value = Optional.of(destinationId);
            }
        } finally {
            txn.end();
        }
        return value;
    }

    /**
     * Checks to see if a user has any destinations that are the same as the destination1 passed in
     *
     * @param destination1 the destination
     * @return Optional destination list, if there is a destination the same as destination1 then that destination will be
     * returned
     */
    public Optional<List<Destination>> checkForSameDestination(Destination destination1) {
        List<Destination> destinations = (Destination.find.query()
                .where()
                .eq("name", destination1.getName())
                .eq("type", destination1.getType())
                .eq("country", destination1.getCountry())
                .eq("soft_delete",0)
                .findList());
        return Optional.of(destinations);
    }

    /**
     * Method to follow a destination for a user
     *
     * @param destId    Id of the entered destination
     * @param profileId Id of the entered profile
     * @return Optional array of integers of the followed users id
     */
    public Optional<ArrayList<Integer>> followDestination(int destId, int profileId) {
        String updateQuery = "INSERT into follow_destination(profile_id, destination_id) values (?, ?)";
        SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
        query.setParameter(1, profileId);
        query.setParameter(2, destId);
        query.execute();
        setOwnerAsAdmin(destId);
        return getFollowedDestinationIds(profileId);
    }

    /**
     * Method to allow a user to unfollow a given destination
     *
     * @param destId    Id of the destination to be unfollowed
     * @param profileId Id of the user that wants to unfollow a destination
     * @return Optional list of integers for the followed destination ids
     */
    public Optional<ArrayList<Integer>> unfollowDestination(int destId, int profileId) {
        String updateQuery = "DELETE from follow_destination where profile_id = ? and destination_id =  ?";
        SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
        query.setParameter(1, profileId);
        query.setParameter(2, destId);
        query.execute();
        return getFollowedDestinationIds(profileId);
    }

    /**
     * Checks to see if destination is owned by an admin, if true this is its first follower, will change
     * ownership to admins and set the previous owner to follow destination
     *
     * @param destId the id of the destination
     */
    private void setOwnerAsAdmin(int destId) {
        Destination destination = lookup(destId);
        int profileId = destination.getProfileId();
        Optional<Integer> optionalAdminId = rolesRepository.getIdFromRole("global_admin");
        if (optionalAdminId.isPresent()) {
            int adminId = optionalAdminId.get();
            if (destination.getProfileId() != adminId) {
                destination.setProfileId(adminId);
                updateProfileId(destination, destination.getDestinationId());
                followDestination(destination.getDestinationId(), profileId);
            }
        }
    }

    /**
     * Method returns all of the users followed destinations
     *
     * @param profileId User if of the followed destinations to return
     * @return Optional array list of destinations followed by the user
     */
    public Optional<ArrayList<Destination>> getFollowedDestinations(int profileId) {
        String updateQuery = "Select D.destination_id, D.profile_id, D.name, D.type, D.country, D.district, D.latitude, D.longitude, D.visible " +
                "from follow_destination JOIN destination D on follow_destination.destination_id = D.destination_id where follow_destination.profile_id = ? and D.soft_delete = 0";
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




    /**
     * Method returns all followed destinations ids from a user
     *
     * @param profileId User id for the user followed destinations
     * @return Optional array list of integers of the followed destination ids
     */
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

    /**
     * class to check if destination is already available to user
     * return true if already in else false
     */
    public boolean checkValidEdit(Destination destination, int profileId, Destination previousDestination) {
        List<Destination> destinations = (Destination.find.query()
                .where()
                .eq("name", destination.getName())
                .eq("type", destination.getType())
                .eq("country", destination.getCountry())
                .eq("profile_id", profileId)
                .findList());

        List<Destination> publicDestinations = (Destination.find.query()
                .where()
                .eq("name", destination.getName())
                .eq("type", destination.getType())
                .eq("country", destination.getCountry())
                .eq("visible", 1)
                .findList());

        if (previousDestination != null && !destinations.isEmpty() && destinations.get(0).getName().equals(previousDestination.getName()) && destinations.get(0).getType().equals(previousDestination.getType()) && destinations.get(0).getCountry().equals(previousDestination.getCountry())) {
            return false;
        }

        return !destinations.isEmpty() || !publicDestinations.isEmpty();
    }

    /**
     * Method called from addRequest method to add the changes made in a request to the actions table
     * @param destinationChange Object that holds the following attributes to be inserted into the database:
     *   travellerTypeId: Id of the traveller type the user wants to add or remove.
     *   action: tinyInt 1 if the user wants to add traveller type, 0 if user wants to remove traveller type.
     *   requestId: Integer id of the request the user is making, links the changes to a request.
     * @return Integer CompletionStage of the id from the new change after the change is inserted into the
     *  destination_changes table
     */
    public CompletionStage<Integer> addDestinationChange(DestinationChange destinationChange){
        return supplyAsync(() -> {
            ebeanServer.insert(destinationChange);
            return destinationChange.getId();
        }, executionContext);
    }

    /**
     * Helper function to wrap Destination changes in a transaction
     * @param requestId
     * @param toAdd Boolean true if the traveller type is to be added
     *              False if traveller type is to be removed
     * @param changes List of changes to be wrapped in a transaction
     */
    @Transactional
    public void travellerTypeChangesTransaction(Integer requestId, Integer toAdd, List<Integer> changes){
        try (Transaction transaction = ebeanServer.beginTransaction()) {
            for (Integer travellerTypeId : changes) {
                DestinationChange destinationChange = new DestinationChange(travellerTypeId, toAdd, requestId);
                addDestinationChange(destinationChange);
            }
            transaction.commit();
        }
    }

    /**
     * Method to lodge a traveller type change request
     * Inserts request into the destination_request Table
     *
     * @param  destinationRequest object holding destinationId and profileId required for inserting the change into the
     *                            changes table
     */
    public CompletionStage<Integer> createDestinationTravellerTypeChangeRequest(DestinationRequest destinationRequest){
        return supplyAsync(() -> {
            ebeanServer.insert(destinationRequest);
            return destinationRequest.getId();
        }, executionContext);
    }


    /**
     * Update method to add traveller types to a destination
     *
     * @param travellerTypeId id of the traveller type that will be added to the destination
     * @param destinationId id of the destination that the traveller type will be added to
     */
    public void addDestinaionTravellerType(int travellerTypeId, int destinationId){
        // TODO: 15/07/19 implement method and change method signatur to return id of the added traveller type.
    }


    /**
     * Method to get all destinationChanges with content such as email, destination and travellerTypes
     * @return result, a list of destinationChanges
     */
    public List<DestinationChange> getAllDestinationChanges() {

                //Getting Destinationchanges out of the database
                List<DestinationChange> result = DestinationChange.find.query().where()
                        .findList();

            for (DestinationChange destinationchanges : result) {
                DestinationRequest destinationRequest = DestinationRequest.find.query().where()
                        .eq("id", destinationchanges.getRequestId())
                        .findOne();

                Profile profile = Profile.find.query().where()
                        .eq("profile_id", destinationRequest.getProfileId())
                        .findOne();
                destinationchanges.setEmail(profile.getEmail());

                Destination destination = lookup(destinationRequest.getDestinationId());
                destinationchanges.setDestination(destination);

                TravellerType travellerType = TravellerType.find.query().where()
                        .eq("traveller_type_id", destinationchanges.getTravellerTypeId())
                        .findOne();
                destinationchanges.setTravellerType(travellerType);
            }
            return result;
    }

    /**
     * Get the all of the destinations
     *
     * @return destinations, list of all Destinations
     */
    public ArrayList<Destination> getAllDestinations() {
        return new ArrayList<>(ebeanServer.find(Destination.class)
                .where()
                .eq("soft_delete", 0)
                .findList());
    }
}