package repository;

import io.ebean.*;
import models.*;
import play.db.ebean.EbeanConfig;
import play.db.ebean.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;

import static java.lang.Math.abs;
import static java.util.concurrent.CompletableFuture.supplyAsync;


/**
 * A destination repository that executes database operations in a different
 * execution context handles all interactions with the destination table .
 */
public class DestinationRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final RolesRepository rolesRepository;
    private final TravellerTypeRepository travellerTypeRepository;
    private final DestinationTravellerTypeRepository destinationTravellerTypeRepository;

    /**
     * A Constructor which links to the ebeans database
     *
     * @param ebeanConfig
     * @param executionContext
     */
    @Inject
    public DestinationRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext,
                                 RolesRepository roleRepository, TravellerTypeRepository travellerTypeRepository,
                                 DestinationTravellerTypeRepository destinationTravellerTypeRepository) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.rolesRepository = roleRepository;
        this.travellerTypeRepository = travellerTypeRepository;
        this.destinationTravellerTypeRepository = destinationTravellerTypeRepository;
    }

    /**
     * Returns a specific destination
     *
     * @param destID The ID of the destination to return
     * @return
     */
    public Destination lookup(int destID) {
        Destination destination = ebeanServer.find(Destination.class).setId(destID).findOne();
        Map<Integer, TravellerType> types = new HashMap<>();
        for (TravellerType type : getDestinationsTravellerTypes(destination.getDestinationId())) {
            types.put(type.getTravellerTypeId(), type);
        }
        destination.setTravellerTypes(types);
        return destination;
    }

    /**
     * Get the users destination list
     *
     * @param id the id of the user profile
     * @return destinations, list of all user destinations
     */
    public List<Destination> getUserDestinations(int id) {
        return new ArrayList<>(ebeanServer.find(Destination.class)
                .where()
                .eq("profile_id", id)
                .eq("soft_delete", 0)
                .findList());
    }

    /**
     * Get the all of the public destinations
     *
     * @param rowOffset - The row to being retrieving results from. Used for paginatino
     * @return destinations, list of all public destinations
     */
    public List<Destination> getPublicDestinations(Integer rowOffset) {
        return new ArrayList<>(ebeanServer.find(Destination.class)
                .setMaxRows(7)
                .setFirstRow(rowOffset)
                .where()
                .eq("visible", 1)
                .eq("soft_delete", 0)
                .findList());
    }

    /**
     * Get the all of the public destinations
     *
     * @return destinations, list of all public destinations
     */
    public List<Destination> getPublicDestinationsNotOwned(Integer userId) {
        return new ArrayList<>(ebeanServer.find(Destination.class)
                .where()
                .eq("visible", 1)
                .eq("soft_delete", 0)
                .ne("profile_id", userId)
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
            // Adding traveller types of destinations to the database
            for (String travellerTypeName : dest.getTravellerTypesList()) {
                destinationTravellerTypeRepository
                        .insertDestinationTravellerType(new TravellerType(travellerTypeName), dest.getDestinationId());
            }

            return Optional.of(dest.getDestinationId());
        }, executionContext);
    }

    /**
     * Method to check if a passed destination to be delete is within a treasure hunt or trip or event
     *
     * @param destinationId the id of the destination to check
     * @return the result of the check with and optional id of the treasure hunt or trip which contains the destination within a completion stage
     */
    public CompletionStage<Boolean> checkDestinationExists(int destinationId) {
        return supplyAsync(
                () -> {
                    boolean inTrips =
                            ebeanServer.find(TripDestination.class).where()
                                    .eq("destination_id", destinationId).exists();
                    boolean inHunts =
                            ebeanServer.find(TreasureHunt.class).where()
                                    .eq("destination_id", destinationId)
                                    .eq("soft_delete", 0).exists();
                   boolean inEvents = ebeanServer.find(Events.class).where()
                           .eq("destination_id", destinationId)
                           .eq("soft_delete", 0).exists();
                   return inEvents || inTrips || inHunts;
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
     *
     * @param destId     The ID of the destination to soft delete
     * @param softDelete Boolean, true if is to be deleted, false if cancel a delete
     * @return
     */
    public CompletionStage<Integer> setSoftDelete(int destId, int softDelete) {
        return supplyAsync(() -> {
            try {
                Destination targetDest = ebeanServer.find(Destination.class).setId(destId).findOne();
                if (targetDest != null) {
                    targetDest.setSetSoftDelete(softDelete);
                    targetDest.update();
                    return 1;
                } else {
                    return 0;
                }
            } catch (Exception e) {
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
                    targetDestination.setTravellerTypes(newDestination.getTravellerTypes());
                    targetDestination.update();
                    txn.commit();
                    value = Optional.of(targetDestination.getDestinationId());
                    destinationTravellerTypeRepository.removeAll(Id);
                    for (String travellerTypeName : newDestination.getTravellerTypesList()) {
                        destinationTravellerTypeRepository.insertDestinationTravellerType(new TravellerType(travellerTypeName), Id);
                    }
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
    private Optional<Integer> updateProfileId(Destination newDestination, Integer destinationId) {
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
                .eq("soft_delete", 0)
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
        return getFollowedDestinationIds(profileId, 0);
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
        return getFollowedDestinationIds(profileId, 0);
    }

    /**
     * Checks to see if destination is owned by an admin, if true this is its first follower, will change
     * ownership to admins and set the previous owner to follow destination
     *
     * @param destId the id of the destination
     */
    public void setOwnerAsAdmin(int destId) {
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
     * @param rowOffset The row to begin getting data from. This is for pagination
     * @param limit     The page limit for the query. This is used to prevent collation of private
     *                  and followed destinations causing incorrect page size.
     * @return Optional array list of destinations followed by the user
     */
    public Optional<ArrayList<Destination>> getFollowedDestinations(int profileId, Integer rowOffset, Integer limit) {
        String updateQuery = "Select D.destination_id, D.profile_id, D.name, D.type, D.country, D.district, D.latitude, D.longitude, D.visible " +
                "from follow_destination JOIN destination D on follow_destination.destination_id = D.destination_id " +
                "where follow_destination.profile_id = ? and D.soft_delete = 0 LIMIT ? OFFSET ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(updateQuery).setParameter(1, profileId)
                .setParameter(2, limit)
                .setParameter(3, rowOffset).findList();
        ArrayList<Destination> destList = new ArrayList<>(getDestinationsFromSqlRow(rowList));

        return Optional.of(destList);
    }

    /**
     * Method to get a users owned + followed destinations
     * @param profileId user Id to search for their owned destinations
     * @return Optional array list of destinations either owned or followed by the user
     */
    public ArrayList<Destination> getAllFollowedOrOwnedDestinations(int profileId) {
        ArrayList<Destination> destinationList = new ArrayList<>(ebeanServer.find(Destination.class)
                .where()
                .eq("soft_delete", 0)
                .eq("profile_id", profileId)
                .findList());

        String updateQuery = "Select D.destination_id, D.profile_id, D.name, D.type, D.country, D.district, D.latitude, D.longitude, D.visible " +
                "from follow_destination JOIN destination D on follow_destination.destination_id = D.destination_id " +
                "where follow_destination.profile_id = ? and D.soft_delete = 0";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(updateQuery).setParameter(1, profileId).findList();
        destinationList.addAll(getDestinationsFromSqlRow(rowList));
        return destinationList;
    }

    /**
     * Method returns all followed destinations ids from a user
     *
     * @param profileId User id for the user followed destinations
     * @param rowOffset - Row to begin getting data from. Used in pagination
     * @return Optional array list of integers of the followed destination ids
     */
    public Optional<ArrayList<Integer>> getFollowedDestinationIds(int profileId, int rowOffset) {
        String updateQuery = "Select destination_id from follow_destination where profile_id = ? LIMIT 7 OFFSET ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(updateQuery).setParameter(1, profileId)
                .setParameter(2, rowOffset).findList();
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

        if (previousDestination != null && !destinations.isEmpty() &&
                destinations.get(0).getName().equals(previousDestination.getName()) &&
                destinations.get(0).getType().equals(previousDestination.getType()) &&
                destinations.get(0).getCountry().equals(previousDestination.getCountry())) {
            return false;
        }

        return !destinations.isEmpty() || !publicDestinations.isEmpty();
    }

    /**
     * Method called from addRequest method to add the changes made in a request to the actions table
     *
     * @param destinationChange Object that holds the following attributes to be inserted into the database:
     *                          travellerTypeId: Id of the traveller type the user wants to add or remove.
     *                          action: tinyInt 1 if the user wants to add traveller type, 0 if user wants to remove traveller type.
     *                          requestId: Integer id of the request the user is making, links the changes to a request.
     * @return Integer CompletionStage of the id from the new change after the change is inserted into the
     * destination_change table
     */
    private CompletionStage<Integer> addDestinationChange(DestinationChange destinationChange) {
        return supplyAsync(() -> {
            ebeanServer.insert(destinationChange);
            return destinationChange.getId();
        }, executionContext);
    }


    /**
     * Method to remove the traveller type destination request from the destination changes database table
     *
     * @param changeId the database id of the change to delete
     * @return completion stage
     */
    public CompletionStage<Integer> deleteDestinationChange(int changeId) {
        return supplyAsync(
                () -> {
                    ebeanServer.find(DestinationChange.class).where().eq("id", changeId).delete();
                    return 1;
                });
    }

    /**
     * Accept destination change request
     * calls add traveller type method if the request is to add or calls remove traveller type method if the request is
     * to remove traveller type
     *
     * @param changeId the destination change to be performed
     */
    public CompletionStage<Integer> acceptDestinationChange(int changeId) {
        return getDestinationChange(changeId)
                .thenApplyAsync(changeOpt -> {
                    if (changeOpt.isPresent()) {
                        return getDestinationRequest(changeOpt.get().getRequestId())
                                .thenApplyAsync(requestOpt -> {
                                    if (changeOpt.get().getAction() == 1) {
                                        addDestinationTravellerType(changeOpt.get().getTravellerTypeId(), requestOpt.get().getDestinationId());
                                    } else {
                                        removeDestinationTravellerType(changeOpt.get().getTravellerTypeId(), requestOpt.get().getDestinationId());
                                    }

                                    return 1;
                                });
                    }
                    return 1;
                })
                .thenApplyAsync(x -> {
                    deleteDestinationChange(changeId);
                    return 1;
                });

    }


    /**
     * Helper function to wrap Destination changes in a transaction
     *
     * @param requestId
     * @param toAdd     Boolean true if the traveller type is to be added
     *                  False if traveller type is to be removed
     * @param changes   List of changes to be wrapped in a transaction
     */
    @Transactional
    public void travellerTypeChangesTransaction(Integer requestId, Integer toAdd, List<Integer> changes) {
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
     * @param destinationRequest object holding destinationId and profileId required for inserting the change into the
     *                           changes table
     */
    public CompletionStage<Integer> createDestinationTravellerTypeChangeRequest(DestinationRequest destinationRequest) {
        return supplyAsync(() -> {
            ebeanServer.insert(destinationRequest);
            return destinationRequest.getId();
        }, executionContext);
    }


    /**
     * Update method to add traveller types to a destination
     *
     * @param travellerTypeId id of the traveller type that will be added to the destination
     * @param destinationId   id of the destination that the traveller type will be added to
     */
    private CompletionStage<Void> addDestinationTravellerType(int travellerTypeId, int destinationId) {
        DestinationTravellerType destinationTravellerType = new DestinationTravellerType(destinationId, travellerTypeId);
        return supplyAsync(() -> {
            ebeanServer.insert(destinationTravellerType);
            return null;
        }, executionContext);
    }

    /**
     * Update method to remove traveller type on a destination
     *
     * @param travellerTypeId id of the traveller type that will be added to the destination
     * @param destinationId   id of the destination that the traveller type will be added to
     */
    private CompletionStage<Void> removeDestinationTravellerType(int travellerTypeId, int destinationId) {
        return supplyAsync(() -> {
            ebeanServer
                    .find(DestinationTravellerType.class)
                    .where()
                    .eq("destinationId", destinationId)
                    .eq("travellerTypeId", travellerTypeId)
                    .delete();
            return null;
        });
    }


    /**
     * Method to get all destinationChanges with content such as profileId, destination and travellerTypes
     *
     * @return result, a list of destinationChanges
     */
    public List<DestinationChange> getAllDestinationChanges() {

        //Getting Destinationchanges out of the database
        List<DestinationChange> result = DestinationChange.find.query().where()
                .findList();

        for (DestinationChange destinationChange : result) {
            DestinationRequest destinationRequest = DestinationRequest.find.query().where()
                    .eq("id", destinationChange.getRequestId())
                    .findOne();

            Profile profile = Profile.find.query().where()
                    .eq("profile_id", destinationRequest.getProfileId())
                    .findOne();
            destinationChange.setEmail(profile.getEmail());

            Destination destination = lookup(destinationRequest.getDestinationId());
            destinationChange.setDestination(destination);

            TravellerType travellerType = TravellerType.find.query().where()
                    .eq("traveller_type_id", destinationChange.getTravellerTypeId())
                    .findOne();
            destinationChange.setTravellerType(travellerType);
        }
        return result;
    }

    public List<TravellerType> getDestinationsTravellerTypes(int destinationId) {
        String sql = "select traveller_type_id from destination_traveller_type where destination_id = ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(sql).setParameter(1, destinationId).findList();
        if (rowList.isEmpty()) {
            return new ArrayList<>();
        } else {
            ArrayList<TravellerType> travellerTypes = new ArrayList<>();
            for (SqlRow row : rowList) {
                int id = row.getInteger("traveller_type_id");
                TravellerType travellerType = travellerTypeRepository.getById(id);
                if (!travellerTypes.contains(travellerType)) {
                    travellerTypes.add(travellerType);
                }
            }
            return travellerTypes;
        }
    }

    /**
     * Method used to get a DestinationChange object from the database using a passed id
     *
     * @param changeId the id of the change to retrieve
     * @return CompletionStage containing the found DestinationChange
     */
    private CompletionStage<Optional<DestinationChange>> getDestinationChange(int changeId) {
        return supplyAsync(
                () -> Optional.ofNullable(
                        ebeanServer.find(DestinationChange.class).where().eq("id", changeId).findOne()),
                executionContext);
    }

    /**
     * Method to get a destination request object using a request id
     */
    private CompletionStage<Optional<DestinationRequest>> getDestinationRequest(int requestId) {
        return supplyAsync(
                () -> Optional.ofNullable(
                        ebeanServer.find(DestinationRequest.class).where().eq("id", requestId).findOne()),
                executionContext);
    }

    /**
     * Get the all of the destinations
     *
     * @return destinations, list of all Destinations
     */
    public List<Destination> getAllDestinations() {
        return new ArrayList<>(ebeanServer.find(Destination.class)
                .where()
                .eq("soft_delete", 0)
                .findList());
    }

    /**
     * Method to get the number of destination requests
     * Used for pagination
     *
     * @return int number of requests
     */
    public int getNumDestRequests() {
        return ebeanServer.find(DestinationChange.class).findCount();
    }

    /**
     * Method to find number of destinations
     * Used for pagination
     *
     * @return int number of destinations found
     */
    public int getNumDestinations() {
        return ebeanServer.find(Destination.class).where().eq("soft_delete", 0).findCount();
    }


    /**
     * Method to get number of public destinations
     * Used for pagination
     *
     * @return number of public destinations
     */
    public int getNumPublicDestinations() {
        return ebeanServer.find(Destination.class).where().eq("soft_delete", 0).eq("visible", 1).findCount();
    }


    /**
     * Method to get count of all private and followed destinations
     *
     * @param profileId - The id of the profile to get its followed destinations
     */
    public int getNumPrivateDestinations(int profileId) {
        String query = "SELECT DISTINCT destination.destination_id FROM follow_destination INNER JOIN destination ON destination.destination_id = follow_destination.destination_id " +
                "WHERE follow_destination.profile_id = ? AND destination.soft_delete = 0";
        List<SqlRow> countRow = ebeanServer.createSqlQuery(query).setParameter(1, profileId).findList();
        int count = 0;
        if (!countRow.isEmpty()) {
            count = countRow.size();
        }
        return count + ebeanServer.find(Destination.class).where().eq("soft_delete", 0).eq("profile_id", profileId).eq("visible", 1).findCount();
    }


    /**
     * Get a page of destinations
     *
     * @param offset   offset of destinations to get
     * @param pageSize max number of destinations to get
     * @return destinations found
     */
    public List<Destination> getDestinationPage(Integer offset, int pageSize) {
        return new ArrayList<>(ebeanServer.find(Destination.class)
                .setFirstRow(offset)
                .setMaxRows(pageSize)
                .where()
                .eq("soft_delete", 0)
                .findList());
    }

    /**
     * Finds a page of destination changes
     *
     * @param offset   offset to find
     * @param pageSize limit of changes to find
     * @return changes found
     */
    public List<DestinationChange> getDestRequestPage(Integer offset, int pageSize) {
        List<DestinationChange> requests = ebeanServer.find(DestinationChange.class).setFirstRow(offset).setMaxRows(pageSize).findList();
        for (DestinationChange destinationChange : requests) {
            DestinationRequest destinationRequest = ebeanServer.find(DestinationRequest.class).setId(destinationChange.getRequestId()).findOne();
            destinationChange.setEmail(ebeanServer.find(Profile.class).where().eq("profile_id", destinationRequest.getProfileId()).findOne().getEmail());
            destinationChange.setDestination(ebeanServer.find(Destination.class).where().eq("destination_id", destinationRequest.getDestinationId()).findOne());
            destinationChange.setTravellerType(ebeanServer.find(TravellerType.class).where().eq("traveller_type_id", destinationChange.getTravellerTypeId()).findOne());
        }
        return requests;
    }


    /**
     * Function to take in a list of SqlRow and convert to destinations
     * @param rowList List of SqlRow to be converted
     * @return List of destinations that has been converted from sql row
     */
    private List<Destination> getDestinationsFromSqlRow(List<SqlRow> rowList) {
        List<Destination> destinations = new ArrayList<>();

        for (SqlRow row : rowList) {
            Destination destination = new Destination();
            destination.setDestinationId(row.getInteger("destination_id"));
            destination.setProfileId(row.getInteger("profile_id"));
            destination.setName(row.getString("name"));
            destination.setType(row.getString("type"));
            destination.setCountry(row.getString("country"));
            destination.setDistrict(row.getString("district"));
            destination.setLatitude(row.getDouble("latitude"));
            destination.setLongitude(row.getDouble("longitude"));
            destination.setVisible(row.getBoolean("visible") ? 1 : 0);
            destinations.add(destination);
        }

        return destinations;
    }

    /**
     * Database method to search for a destination of a given name
     *
     * @param name   - The destination name
     * @param offset - The offset from which to start returning results
     * @return The destinations found by the search query
     */
    public List<Destination> searchDestinations(String name, Integer offset, Boolean isPublic, Integer profileId) {
        if (isPublic) {
            return new ArrayList<>(ebeanServer.find(Destination.class)
                    .setFirstRow(offset)
                    .setMaxRows(7)
                    .where()
                    .like("name", name)
                    .eq("visible", 1)
                    .findList());
        }


        List<Destination> privateList = new ArrayList<>(ebeanServer.find(Destination.class)
                .setFirstRow(offset)
                .setMaxRows(7)
                .where()
                .eq("soft_delete", 0)
                .eq("profile_id", profileId)
                .like("name", name)
                .eq("visible", 0)
                .findList());

        Integer limit = abs(7 - privateList.size());
        String query = "Select D.destination_id, D.profile_id, D.name, D.type, D.country, D.district, D.latitude, D.longitude, D.visible " +
                "from follow_destination JOIN destination D on follow_destination.destination_id = D.destination_id " +
                "where follow_destination.profile_id = ? and D.soft_delete = 0 LIMIT ? OFFSET ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(query)
                .setParameter(1, profileId)
                .setParameter(2, limit)
                .setParameter(3, offset)
                .findList();

        privateList.addAll(getDestinationsFromSqlRow(rowList));

        return privateList;
    }


    /**
     * Database method to get the destination search result size
     * This is for the size value in the destination search pagination
     *
     * @param name - Name of the destination the user is searching for
     * @return The count of destinations matching/containing the query name in their name
     */
    public Integer getNumSearchDestinations(String name, Boolean isPublic, Integer profileId) {
        if (isPublic) {
            return ebeanServer.find(Destination.class)
                    .setMaxRows(100)
                    .where()
                    .eq("visible", 1)
                    .like("name", name)
                    .findCount();
        }

        Integer privateCount = ebeanServer.find(Destination.class)
                .where()
                .eq("soft_delete", 0)
                .eq("profile_id", profileId)
                .like("name", name)
                .eq("visible", 0)
                .findCount();

        String query = "Select D.destination_id, D.profile_id, D.name, D.type, D.country, D.district, D.latitude, D.longitude, D.visible " +
                "from follow_destination JOIN destination D on follow_destination.destination_id = D.destination_id " +
                "where follow_destination.profile_id = ? and D.soft_delete = 0";
        privateCount += ebeanServer.createSqlQuery(query)
                .setParameter(1, profileId)
                .findList().size();

        return privateCount;

    }
}

