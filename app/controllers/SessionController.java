package controllers;

import com.google.common.collect.TreeMultimap;
import io.ebean.Expr;
import models.Destination;
import models.Profile;
import models.Trip;
import models.TripDestination;
import play.mvc.Http;
import repository.ProfileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

/**
 * This class manages sessions cookies
 * @author George, ajl190, jma361
 */
public class SessionController {

    /**
     * Get the currently logged in user
     * @param request
     * @return Web page showing connected user's email
     */
    static Profile getCurrentUser(Http.Request request) {
        Optional<String> connected = request.session().getOptional("connected");
        String stringId;
        if (connected.isPresent()) {
            stringId = connected.get();
            int id = Integer.parseInt(stringId);
            Profile profile = Profile.find.byId(stringId);
            profile.setDestinations(getUserDestinations(profile.getProfileId()));
            getUsersTrips(profile);
            return profile;
        } else {
            return null;
        }
    }


    /**
     * Get the users destination list
     * @param email
     * @return destinations, list of all user destination
     */
    private static ArrayList<Destination> getUserDestinations(Integer id) {
        ArrayList<Destination> destinations = new ArrayList<>(Destination.find.query()
                .where()
                .eq("profile_id", id)
                .findList());

        ArrayList<Destination> publicDestinations = new ArrayList<>(Destination.find.query()
                .where()
                .not(Expr.eq("profile_id", id))
                .eq("visible", "1")
                .findList());
        destinations.addAll(publicDestinations);

        return destinations;
    }


    /**
     * Gets an ArrayList of trips from the database that related to a passed profile
     * @param currentUser the profile to get rips for
     * @return the trips found for the passed profile
     */
    private static void getUsersTrips(Profile currentUser) {
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
    }
}
