package controllers;

import com.google.common.collect.TreeMultimap;
import com.google.inject.Inject;
import models.Destination;
import models.Profile;
import models.Trip;
import models.TripDestination;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import repository.TripDestinationsRepository;
import repository.TripRepository;
import views.html.trips;
import views.html.tripsCreate;
import views.html.tripsEdit;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletionStage;

/**
 * This class is the controller for the trips.scala.html file, it provides the route to the
 * trips page and the method that the page uses.
 */
public class TripsController extends Controller {

    private final ArrayList<Destination> destinationsList;
    private final ArrayList<TripDestination> currentDestinationsList;
    private final TreeMap<Integer, TripDestination> orderedCurrentDestinations;

    private MessagesApi messagesApi;
    private final Form<Trip> form;
    private final Form<TripDestination> formTrip;
    private final TripRepository tripRepository;
    private final TripDestinationsRepository tripDestinationRepository;

    @Inject
    public TripsController(FormFactory formFactory, TripRepository tripRepository, TripDestinationsRepository tripDestinationRepository, MessagesApi messagesApi) throws ParseException {
        this.form = formFactory.form(Trip.class);
        this.tripRepository = tripRepository;
        this.tripDestinationRepository = tripDestinationRepository;
        this.messagesApi = messagesApi;
        this.formTrip = formFactory.form(TripDestination.class);
        this.destinationsList = new ArrayList<>();
        this.currentDestinationsList = new ArrayList<>();
        this.orderedCurrentDestinations = new TreeMap<>();
    }

    /**
     *show the main trip page
     * @param request Http request
     * @return a render of the trips page
     */
    @Security.Authenticated(SecureSession.class)
    public Result show(Http.Request request) {
        orderedCurrentDestinations.clear();
        TreeMultimap<Long, Integer> tripsMap = SessionController.getCurrentUser(request).getTrips();
        List<Integer> tripValues = new ArrayList<>(tripsMap.values());
        return ok(trips.render(form, formTrip, destinationsList, tripValues, SessionController.getCurrentUser(request), request, messagesApi.preferred(request)));
    }

    /**
     * This method get all of the tripDestinations out of the orderedCurrentDestinations map
     * @return an ArrayList of the current tripDestinations
     */
    private ArrayList<TripDestination> getCurrentDestinations() {
        return new ArrayList<>(orderedCurrentDestinations.values());
    }


    /**
     * Shows the create destination scene to tthe user on click of the "Create Destination" button
     * @param request the http request
     * @return the result
     */
    @Security.Authenticated(SecureSession.class)
    public Result showCreate(Http.Request request) {
        Profile currentUser = SessionController.getCurrentUser(request);
        return ok(tripsCreate.render(form, formTrip, getCurrentDestinations(), currentUser, null, request, messagesApi.preferred(request)));
    }


    /**
     * Create and show Trip edit page
     *
     * @param request Http request
     * @param id Integer id of the trip (primary key)
     * @return a render of the edit trips page
     */
    @Security.Authenticated(SecureSession.class)
    public Result showEdit(Http.Request request, Integer id) {
        Profile currentUser = SessionController.getCurrentUser(request);
        Trip trip = tripRepository.getTrip(id);
        Form<Trip> tripForm = form.fill(trip);
        if (orderedCurrentDestinations.isEmpty()){
            System.out.println(trip.getDestinations().size());
            orderedCurrentDestinations.putAll(trip.getOrderedDestiantions());
        }
        return ok(tripsEdit.render(tripForm, formTrip, getCurrentDestinations(), currentUser, id, request, messagesApi.preferred(request)));
    }


    /**
     * create edit trip destination forms and page
     *
     * @param request Http request
     * @param order The integer positioning of the destinations
     * @return a render of the create trips page
     */
    @Security.Authenticated(SecureSession.class)
    public Result editTripDestinationCreate(Http.Request request, Integer order) {
        TripDestination dest = orderedCurrentDestinations.get(order - 1);
        Profile currentUser = SessionController.getCurrentUser(request);
        return ok(tripsCreate.render(form, formTrip, getCurrentDestinations(), currentUser, dest, request, messagesApi.preferred(request)));
    }


    /**
     * Adds a destination to the trip being created
     *
     * @param request the http request
     * @return the result
     */
    @Security.Authenticated(SecureSession.class)
    public Result addDestination(Http.Request request) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        if(orderedCurrentDestinations.size() >= 1) {
            if (orderInvalidDelete(tripDestination)) {
                return redirect("/trips/create").flashing("info", "The same destination cannot be after itself in a trip");
            }
        }
        insertTripDestination(tripDestination, orderedCurrentDestinations.size());
        tripRepository.insertTripDestination(tripDestination); // TODO should this be here - tf reeee
        return redirect("/trips/create");
    }


    /**
     * This method puts a new trip destination into the TreeMap of current TripDestinations
     * @param tripDestination the new TripDestination
     * @param order the new TripDestinations order
     */
    private void insertTripDestination(TripDestination tripDestination, int order) {
        tripDestination.setDestOrder(order);
        System.out.println("Before update order: " + orderedCurrentDestinations);
        if (orderedCurrentDestinations.containsKey(order)) {
            NavigableSet<Integer> keys = new TreeSet<>(orderedCurrentDestinations.keySet()).descendingSet();
            for (int destOrder : keys) {
                if (destOrder >= order) {
                    TripDestination tripDestination1 = orderedCurrentDestinations.get(destOrder);
                    tripDestination1.setDestOrder(destOrder + 1);
                    orderedCurrentDestinations.remove(destOrder);
                    orderedCurrentDestinations.put(tripDestination1.getDestOrder(), tripDestination1);
                }
            }
            orderedCurrentDestinations.put(order, tripDestination);
        } else {
            orderedCurrentDestinations.put(order, tripDestination);
        }
        System.out.println("After update order: " + orderedCurrentDestinations);
    }

    /**
     * This method removes a tripDestination from the current trip map
     * @param order the destinations order
     */
    private void removeTripDestination(int order) {
        orderedCurrentDestinations.remove(order);
        NavigableSet<Integer> keys = new TreeSet<>(orderedCurrentDestinations.keySet()).descendingSet();
        for (int destOrder : keys) {
            if (destOrder > order) {
                TripDestination tripDestination1 = orderedCurrentDestinations.get(destOrder);
                tripDestination1.setDestOrder(destOrder - 1);
                orderedCurrentDestinations.remove(destOrder);
                orderedCurrentDestinations.put(tripDestination1.getDestOrder(), tripDestination1);
            }
        }
    }


    /**
     * Add extra destination to a trip being edited
     *
     * @param request Http request
     * @param id Integer id of the trip (primary key)
     * @return redirection tot he edit page
     */
    @Security.Authenticated(SecureSession.class)
    public Result addDestinationEditTrip(Http.Request request, int id, int numTripdests) {
        // TODO ConcurrentModificationException when order is edited
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        tripDestination.setDestOrder(orderedCurrentDestinations.size() + 1);
        if(orderedCurrentDestinations.size() >= 1) {
            if (orderInvalidInsert(tripDestination)) {
                return redirect("/trips/"+id+"/edit").flashing("info", "The same destination cannot be after itself in a trip");
            }
        }
        insertTripDestination(tripDestination, orderedCurrentDestinations.size() + 1);
        return redirect("/trips/"+id+"/edit");
    }


    /**
     * Saves a users newly created trip and its connected tripDestinations ot the database
     *
     * @param request the http request
     * @return the result
     */
    @Security.Authenticated(SecureSession.class)
    public Result save(Http.Request request) {
        Form<Trip> tripForm = form.bindFromRequest(request);
        Trip trip = tripForm.get();
        Profile currentUser = SessionController.getCurrentUser(request);
        trip.setEmail(currentUser.getEmail());
        if (orderedCurrentDestinations.size() < 2){
            return redirect("/trips/create").flashing("info", "A trip must have at least two destinations");
        } else {
            ArrayList<TripDestination> tripDestinations = new ArrayList<>(orderedCurrentDestinations.values());
            tripRepository.insert(trip, tripDestinations);

            return redirect("/trips");
        }
    }


    /**
     * Updates the trip after the destinations have been selected
     *
     * @param request Http request
     * @param id Integer primary key of the trip
     * @return a redirect to the trips page
     */
    @Security.Authenticated(SecureSession.class)
    public Result updateName(Http.Request request, int id) {
        Form<Trip> tripForm = form.bindFromRequest(request);
        Trip trip = tripForm.get();
        Profile currentUser = SessionController.getCurrentUser(request);
        trip.setEmail(currentUser.getEmail());
        if (orderedCurrentDestinations.size() < 2){
            return redirect("/trips/"+id+"/edit").flashing("info", "The same destination cannot be after itself in a trip");
        } else {
            tripRepository.delete(id); //TODO needs to be removed
            ArrayList<TripDestination> tripDestinations = new ArrayList<>(orderedCurrentDestinations.values());
            tripRepository.insert(trip, tripDestinations);
            // TODO put redirect inside a thenApplyAsync
            return redirect("/trips");
        }
    }


    /**
     * Deletes a trip in the database
     *
     * @param tripId Integer primary key of a trip
     * @return a redirect to the trips page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> delete(Integer tripId) {
        return tripRepository.delete(tripId).thenApplyAsync(v -> {
            return redirect("/trips");
        });
    }


    /**
     * Updates a trip destination within the trip currently being edited
     *
     * @param request Http request
     * @param tripId Integer primary key of a trip
     * @param oldLocation The old destination location saved under a trip
     * @return redirects to the edit trip page
     */
    @Security.Authenticated(SecureSession.class)
    public Result updateDestinationEdit(Http.Request request, Integer tripId, Integer oldLocation) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));

        if(orderedCurrentDestinations.size() >= 1) {
            if (orderInvalidDelete(tripDestination)) {
                if (orderInvalidInsert(tripDestination)) {
                    return redirect("/trips/" + tripId + "/edit").flashing("info", "The same destination cannot be after itself in a trip");
                }
            }
        }
        removeTripDestination(oldLocation);
        insertTripDestination(tripDestination, tripDestination.getDestOrder());
        return redirect("/trips/"+tripId+"/edit");
    }

    /**
     * This checks if a tripDestination can be deleted from its current position
     * @param tripDestination the tripDestination to check
     * @return boolean holding true if order is invalid
     */
    private boolean orderInvalidDelete(TripDestination tripDestination) {
        int order = tripDestination.getDestOrder();
        TripDestination tripDestination1 = orderedCurrentDestinations.get(order + 1);
        TripDestination tripDestination2 = orderedCurrentDestinations.get(order - 1);
        if (tripDestination1 != null && tripDestination2 != null) {
            return tripDestination1.getDestinationId() == tripDestination2.getDestinationId();

        }
        return false;
    }

    /**
     * This method checks that a tripDestination can be inserted at its new position
     * @param tripDestination the tripDestination to check
     * @return boolean holding true if the position is invalid
     */
    private boolean orderInvalidInsert(TripDestination tripDestination) {
        System.out.println("Order: " + tripDestination.getDestOrder());
        System.out.println(orderedCurrentDestinations);
        int order = tripDestination.getDestOrder();
        TripDestination tripDestination1 = orderedCurrentDestinations.get(order);
        TripDestination tripDestination2 = orderedCurrentDestinations.get(order - 1);
        boolean pos1Invalid = false;
        boolean pos2Invalid = false;
        if (tripDestination1 != null) {
            pos1Invalid = tripDestination.getDestinationId() == tripDestination1.getDestinationId();

        }
        if (tripDestination2 != null) {
            pos2Invalid = tripDestination.getDestinationId() == tripDestination2.getDestinationId();
        }
        System.out.println(pos2Invalid);
        System.out.println(pos1Invalid);
        return pos2Invalid || pos1Invalid;
    }

    /**
     * Delete destinations in the edit page
     *
     * @param request Http request
     * @param order The integer of the positioning of the destinations
     * @param tripId Integer primary key of a trip
     * @return redirection to the edit page
     */
    @Security.Authenticated(SecureSession.class)
    public Result deleteDestinationEditTrip(Http.Request request, Integer order, Integer tripId) {
        if (orderInvalidDelete(orderedCurrentDestinations.get(order)) ) {
            return redirect("/trips/"+tripId+"/edit").flashing("info", "The same destination cannot be after itself in a trip");
        }
        removeTripDestination(order);
        return redirect("/trips/"+tripId+"/edit");
    }


    /**
     * Updates a trip destination that is within the current trip being created
     *
     * @param request Http request
     * @param oldLocation The old destination location saved under a trip
     * @return redirection to the create trips page
     */
    @Security.Authenticated(SecureSession.class)
    public Result updateDestination(Http.Request request, Integer oldLocation) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        int newLocation = tripDestination.getDestOrder();

        if(orderedCurrentDestinations.size() >= 1) {
            if (orderInvalidDelete(tripDestination)) {
                return redirect("/trips/create").flashing("info", "The same destination cannot be after itself in a trip");
            }
        }
        insertTripDestination(tripDestination, tripDestination.getDestOrder());
        return redirect("/trips/create");
    }

    /**
     * Deletes a tripDestination from the current destinations list and changes the order of indirectly affected tripDestinations
     *
     * @param request Http request
     * @param order the integer positioning of the destinations
     * @return redirect to the show create page
     */
    @Security.Authenticated(SecureSession.class)
    public Result deleteDestination(Http.Request request, Integer order) {
        TripDestination tripDestination1 = orderedCurrentDestinations.get(order - 1);
        TripDestination tripDestination2 = orderedCurrentDestinations.get(order + 1);
        if (tripDestination1 != null && tripDestination2 != null) {
            if (tripDestination1.getDestinationId() == tripDestination2.getDestinationId()) {
                return redirect("/trips/create").flashing("info", "The same destination cannot be after itself in a trip");
            }
        }
        orderedCurrentDestinations.remove(order);
        return redirect("/trips/create");
    }

}
