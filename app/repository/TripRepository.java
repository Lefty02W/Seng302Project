package repository;

import com.google.common.collect.TreeMultimap;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Model;
import io.ebean.Transaction;
import models.Destination;
import models.Profile;
import models.Trip;
import models.TripDestination;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;
import repository.TripDestinationsRepository;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A trip repository that executes database operations in a different
 * execution context handles all interactions with the trip table .
 */
public class TripRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final TripDestinationsRepository tripDestinationsRepository;
    private final DestinationRepository destinationRepository;
    private final ProfileRepository profileRepository;

    @Inject
    public TripRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext, TripDestinationsRepository tripDestinationsRepository, ProfileRepository profileRepository, DestinationRepository destinationRepository) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.tripDestinationsRepository = tripDestinationsRepository;
        this.destinationRepository = destinationRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * insert trip destination into the database
     * @param tripDestination
     * @return trip id
     */
    public CompletionStage<Integer> insertTripDestination(TripDestination tripDestination) {
        return supplyAsync(() -> {
            ebeanServer.insert(tripDestination);
            return tripDestination.getTripId();
        }, executionContext);
    }


    /**
     * insert trip into database
     * @param trip
     * @param tripDestinations
     */
    public void insert(Trip trip, ArrayList<TripDestination> tripDestinations) {
        ebeanServer.insert(trip);
        System.out.println("yote");
        for (TripDestination tripDestination : tripDestinations) {
                tripDestination.setTripId(trip.getId());
                Destination dest = destinationRepository.lookup(tripDestination.getDestinationId());
                //if(dest.getVisible() == 1 && !dest.getUserEmail().equals("admin@admin.com")) {
                if (dest.getVisible() == 1) {
                   // makeAdmin(dest);
                    System.out.println("Make Admin");
                }
            ebeanServer.insert(tripDestination);
        }
    }

    private void makeAdmin(Destination destination) {
        destinationRepository.followDesination(destination.getDestinationId(), destination.getProfileId());
        Destination targetDestination = ebeanServer.find(Destination.class).setId(destination.getDestinationId()).findOne();
        Optional<Integer> profileId = profileRepository.getAdminId();
        if (profileId.isPresent()) {
            targetDestination.setProfileId(profileId.get());
            targetDestination.update();
        }
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
                return tripOptional.map(p -> p.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }


    /**
     * Takes in a user and sets ups the users trips from the database
     * @param currentUser User that gets trips set
     * @return currentUser user after trips have been set
     */
    public Profile setUserTrips(Profile currentUser) {
        TreeMultimap<Long, Integer> trips = TreeMultimap.create();
        TreeMap <Integer, Trip> tripMap = new TreeMap<>();
        // Getting the trips out of the database
        List<Trip> result = Trip.find.query().where()
                .eq("profile_id", currentUser.getProfileId())
                .findList();
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
        // Returning the trips found
        currentUser.setTrips(trips);
        currentUser.setTripMaps(tripMap);
        return currentUser;
    }



    /**
     * code to return trip from id
     * @param tripId
     * @return
     */
    public Trip getTrip(int tripId) {
        // Getting the trips out of the database
        List<Trip> result = Trip.find.query().where()
                .eq("trip_id", tripId)
                .findList();

        Trip trip = result.get(0);

        ArrayList<TripDestination> tripDestinations = new ArrayList<>();
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
        trip.setOrderedDestiantions(orderedDestiantions);
        return trip;
    }

}
