package repository;

import com.google.common.collect.TreeMultimap;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Model;
import io.ebean.SqlRow;
import models.Destination;
import models.Profile;
import models.Trip;
import models.TripDestination;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A trip repository that executes database operations in a different
 * execution context handles all interactions with the trip table .
 */
public class TripRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final DestinationRepository destinationRepository;

    @Inject
    public TripRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext, DestinationRepository destinationRepository) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.destinationRepository = destinationRepository;
    }


    /**
     * insert trip into database
     * @param trip Trip object to be inserted in the database
     * @param tripDestinations List of trip destinations to be inserted that are inside the trip
     */
    public void insert(Trip trip, List<TripDestination> tripDestinations) {
        ebeanServer.insert(trip);
        for (TripDestination tripDestination : tripDestinations) {
                tripDestination.setTripId(trip.getId());
                Destination dest = destinationRepository.lookup(tripDestination.getDestinationId());
                if (dest.getVisible() == 1) {
                    destinationRepository.setOwnerAsAdmin(dest.getDestinationId());
                }
            ebeanServer.insert(tripDestination);
        }
    }


    /**
     * Gets 9 of the passed user trips starting from a passed offset
     *
     * @param profileId database id of profile
     * @param offset offset for trips to retrieve
     * @return Optional list of ids of the trips found
     */
    public Optional<List<Integer>> getUserTripIds(int profileId, int offset) {
        return Optional.of(ebeanServer.find(Trip.class).setMaxRows(9).setFirstRow(offset).where().eq("profile_id", profileId).findIds());
    }


    /**
     * Removes a trip from the database
     * @param tripID the id of the trip to remove
     * @return the completionStage
     */
    public CompletionStage<Optional<Integer>> delete(int tripID) {
        return supplyAsync(() -> {
            try {
                final Optional<Trip> tripOptional = Optional.ofNullable(ebeanServer.find(Trip.class).setId(tripID).findOne());
                tripOptional.ifPresent(Model::delete);
                return tripOptional.map(Trip::getId);
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    /**
     * sets soft delete for a Trip which eather deletes it or
     * undoes the delete
     * @param tripId The ID of the trip to soft delete
     * @return
     */
    public CompletionStage<Integer> setSoftDelete(int tripId, int softDelete) {
        return supplyAsync(() -> {
            try {
                Trip targetTrip = ebeanServer.find(Trip.class).setId(tripId).findOne();
                if (targetTrip != null) {
                    targetTrip.setSoftDelete(softDelete);
                    targetTrip.update();
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
     * Takes in a user and sets ups the users trips from the database
     * @param currentUser User that gets trips set
     * @return currentUser user after trips have been set
     */
    public Profile setUserTrips(Profile currentUser) {
        // Getting the trips out of the database
        List<Trip> result = Trip.find.query()
                .where()
                .eq("profile_id", currentUser.getProfileId())
                .eq("soft_delete",0)
                .findList();
        return populateTrips(currentUser, result);
    }

    private Profile populateTrips(Profile profile, List<Trip> result) {
        TreeMultimap<Long, Integer> trips = TreeMultimap.create();
        TreeMap <Integer, Trip> tripMap = new TreeMap<>();
        for (Trip trip : result) {
            ArrayList<TripDestination> tripDestinations = new ArrayList<>();
            // Getting the tripDestinations out of the database for each trip returned
            List<TripDestination> tripDests = TripDestination.find.query()
                    .where()
                    .eq("trip_id", trip.getId())
                    .findList();
            for (TripDestination tripDest : tripDests) {
                // Getting the destinations for each tripDestination
                List<Destination> destinations = Destination.find.query()
                        .where()
                        .eq("destination_id", tripDest.getDestinationId())
                        .findList();
                tripDest.setDestination(destinations.get(0));
                tripDestinations.add(tripDest);
            }
            trip.setDestinations(tripDestinations);
            trips.put(trip.getFirstDate(), trip.getId());
            tripMap.put(trip.getId(), trip);
        }
        profile.setTrips(trips);
        profile.setTripMaps(tripMap);
        return profile;
    }

    /**
     * code to return trip from id
     * @param tripId Id of the trip to be selected
     * @return Trip object taken from the database
     */
    public Trip getTrip(int tripId) {
        // Getting the trips out of the database
        List<Trip> result = Trip.find.query().where()
                .eq("trip_id", tripId)
                .eq("soft_delete",0)
                .findList();

        Trip trip = result.get(0);

        TreeMap<Integer, TripDestination> orderedDestiantions = new TreeMap<>();
        List<TripDestination> tripDests = TripDestination.find.query()
                .where()
                .eq("trip_id", tripId)
                .findList();

        for (TripDestination tripDest : tripDests) {
            // Getting the destinations for each tripDestination
            List<Destination> destinations = Destination.find.query()
                    .where()
                    .eq("destination_id", tripDest.getDestinationId())
                    .findList();
            tripDest.setDestination(destinations.get(0));
            orderedDestiantions.put(tripDest.getDestOrder(), tripDest);
        }
        trip.setOrderedDestinations(orderedDestiantions);
        return trip;
    }

    /**
     * Method to get all trips
     *
     * @return List of all trips
     */
    public List<Trip> getAll() {
        String selectQuery = "SELECT * FROM trip WHERE soft_delete = 0;";
        List<SqlRow> rows = ebeanServer.createSqlQuery(selectQuery).findList();
        List<Trip> allTrips = new ArrayList<>();
        for (SqlRow row : rows) {
            allTrips.add(getTrip(row.getInteger("trip_id")));

        }
        return allTrips;
    }

    /**
     * Gets a subset of the trips from the database
     * used for pagination on the admin page
     *
     * @param offset amount to offset query by
     * @param amount amount of trips to get
     * @return List of trips found
     */
    public List<Trip> getPaginateTrip(int offset, int amount) {
        List<Trip> trips = new ArrayList<>();
        List<Integer> tripIds = ebeanServer.find(Trip.class).setMaxRows(amount).setFirstRow(offset).where().eq("soft_delete", 0).findIds();
        for (int id : tripIds) {
            trips.add(getTrip(id));
        }
        return trips;
    }

    /**
     * Get ten of the users trips
     * @return trip list
     */
    public Profile getTenTrips(Profile currentUser) {
        List<Trip> result = Trip.find.query()
                .setMaxRows(10)
                .where()
                .eq("profile_id", currentUser.getProfileId())
                .eq("soft_delete",0)
                .findList();
        return populateTrips(currentUser, result);
    }

    /**
     * Finds the number of trips in the database
     * Used for pagination purposes
     * @return int of number found
     */
    public int getNumTrips() {
        return ebeanServer.find(Trip.class).where().eq("soft_delete", 0).findCount();
    }
}
