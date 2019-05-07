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
import repository.TripRepository;
import views.html.trips;
import views.html.tripsCreate;
import views.html.tripsEdit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletionStage;

/**
 * This class is the controller for the trips.scala.html file, it provides the route to the
 * trips page and the method that the page uses.
 */
public class TripsController extends Controller {

    private final ArrayList<Destination> destinationsList;
    private final TreeMap<Integer, TripDestination> orderedCurrentDestinations;

    private MessagesApi messagesApi;
    private final Form<Trip> form;
    private final Form<TripDestination> formTrip;
    private final TripRepository tripRepository;
    private boolean showEmptyEdit = false;

    @Inject
    public TripsController(FormFactory formFactory, TripRepository tripRepository, MessagesApi messagesApi) {
        this.form = formFactory.form(Trip.class);
        this.tripRepository = tripRepository;
        this.messagesApi = messagesApi;
        this.formTrip = formFactory.form(TripDestination.class);
        this.destinationsList = new ArrayList<>();
        this.orderedCurrentDestinations = new TreeMap<>();
    }

    /**
     * Show the main trip page
     * @param request Http request
     * @return a render of the trips page
     */
    @Security.Authenticated(SecureSession.class)
    public Result show(Http.Request request) {
        showEmptyEdit = false;
        orderedCurrentDestinations.clear();
        TreeMultimap<Long, Integer> tripsMap = SessionController.getCurrentUser(request).getTrips();
        List<Integer> tripValues = new ArrayList<>(tripsMap.values());
        return ok(trips.render(form, destinationsList, tripValues, SessionController.getCurrentUser(request), request, messagesApi.preferred(request)));
    }

    /**
     * This method get all of the tripDestinations out of the orderedCurrentDestinations map
     * @return an ArrayList of the current tripDestinations
     */
    private ArrayList<TripDestination> getCurrentDestinations() {
        return new ArrayList<>(orderedCurrentDestinations.values());
    }


    /**
     * Shows the create destination scene to the user on click of the "Create Destination" button
     * @param request the http request
     * @return the result
     */
    @Security.Authenticated(SecureSession.class)
    public Result showCreate(Http.Request request) {
        Profile currentUser = SessionController.getCurrentUser(request);
        return ok(tripsCreate.render(form, formTrip, getCurrentDestinations(), currentUser, null, request, messagesApi.preferred(request)));
    }


    /**
     * Endpoint used for editing a trip
     *
     * @param request Http request
     * @param id Integer id of the trip (primary key)
     * @return a render of the editDestinations trips page
     */
    @Security.Authenticated(SecureSession.class)
    public Result showEdit(Http.Request request, Integer id) {
        Profile currentUser = SessionController.getCurrentUser(request);
        Trip trip = tripRepository.getTrip(id);
        Form<Trip> tripForm = form.fill(trip);
        if (orderedCurrentDestinations.isEmpty() && !showEmptyEdit) {
            orderedCurrentDestinations.putAll(trip.getOrderedDestiantions());
        }
        return ok(tripsEdit.render(tripForm, formTrip, getCurrentDestinations(), currentUser, id, null, request, messagesApi.preferred(request)));
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
        setDates(tripDestination, tripDestForm);
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        tripDestination.setDestOrder(orderedCurrentDestinations.size() + 1);
        if(!checkDates(tripDestination)) {
            return redirect("/trips/create").flashing("info", "The arrival date must be before the departure date");
        }
        if(orderedCurrentDestinations.size() >= 1) {
            if (orderInvalidInsert(tripDestination)) {
                return redirect("/trips/create").flashing("info", "The same destination cannot be after itself in a trip");
            }
        }
        insertTripDestination(tripDestination, orderedCurrentDestinations.size() + 1);
        return redirect("/trips/create");
    }


    /**
     * This method checks that the arrival and departure dates are valid.
     * In this context valid is that the arrival date is prior to the departure date
     * @param tripDestination the trip destination to check the dates for
     * @return a boolean holding true if the dates are valid
     */
    private boolean checkDates(TripDestination tripDestination) {
        // Possibly add check to stop overlap with other destinations in the trip
        if (tripDestination.getDeparture() != null && tripDestination.getArrival() != null) {
            return tripDestination.getArrival().getTime() < tripDestination.getDeparture().getTime();
        }
        return true;
    }


    /**
     * This method sets the correct date values for a newly added tripDestination
     * @param tripDestination the tripDestination to set values for
     * @param form the form holding the data values
     */
    private void setDates(TripDestination tripDestination, Form<TripDestination> form) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        Form.Field arrivalField = form.field("arrival");
        Form.Field departureField = form.field("departure");
        try {
            if (arrivalField.value().isPresent()) {
                tripDestination.setArrival(formatter.parse(arrivalField.value().get()));
            }
        } catch (ParseException e) {
            tripDestination.setArrival(null);
        }
        try {
            if (departureField.value().isPresent()) {
                tripDestination.setDeparture(formatter.parse(departureField.value().get()));
            }
        } catch (ParseException e) {
            tripDestination.setDeparture(null);
        }
    }


    /**
     * This method puts a new trip destination into the TreeMap of current TripDestinations
     * @param tripDestination the new TripDestination
     * @param order the new TripDestinations order
     */
    private void insertTripDestination(TripDestination tripDestination, int order) {
        tripDestination.setDestOrder(order);
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
    }

    /**
     * This method removes a tripDestination from the current trip map
     * @param order the destinations order
     */
    private void removeTripDestination(int order) {
        orderedCurrentDestinations.remove(order);
        TreeMap<Integer, TripDestination> destMap = new TreeMap<>();
        NavigableSet<Integer> keys = new TreeSet<>(orderedCurrentDestinations.keySet()).descendingSet();
        for (int destOrder : keys) {
            if (destOrder > order) {
                TripDestination tripDestination1 = orderedCurrentDestinations.get(destOrder);
                tripDestination1.setDestOrder(destOrder - 1);
                destMap.put(tripDestination1.getDestOrder(), tripDestination1);
            } else {
                destMap.put(destOrder, orderedCurrentDestinations.get(destOrder));
            }
        }
        orderedCurrentDestinations.clear();
        orderedCurrentDestinations.putAll(destMap);
    }


    /**
     * Saves a users newly created trip and its connected tripDestinations to the database
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
        if (orderedCurrentDestinations.size() < 2) {
            return redirect("/trips/create").flashing("info", "A trip must have at least two destinations");
        } else {
            ArrayList<TripDestination> tripDestinations = new ArrayList<>(orderedCurrentDestinations.values());
            tripRepository.insert(trip, tripDestinations);
            return redirect("/trips");
        }
    }


    /**
     * Saving the editDestinations of a trip
     *
     * @param request Http request
     * @param id Integer primary key of the trip
     * @return a redirect to the trips page
     */
    @Security.Authenticated(SecureSession.class)
    public Result saveEdit(Http.Request request, int id) {
        Form<Trip> tripForm = form.bindFromRequest(request);
        Trip trip = tripForm.get();
        Profile currentUser = SessionController.getCurrentUser(request);
        trip.setEmail(currentUser.getEmail());
        if (orderedCurrentDestinations.size() < 2){
            return redirect("/trips/"+id+"/edit").flashing("info", "A trip must have at least two destinations");
        } else {
            // TODO still needs to ideally be in a transaction
            ArrayList<TripDestination> tripDestinations = new ArrayList<>(orderedCurrentDestinations.values());
            tripRepository.delete(id);
            tripRepository.insert(trip, tripDestinations);
            // TODO put redirect inside a thenApplyAsync
            return redirect("/trips");
        }
    }


    /**
     * Deletes a trip from the database
     *
     * @param tripId integer primary key of a trip
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
     * @return redirects to the editDestinations trip page
     */
    @Security.Authenticated(SecureSession.class)
    public Result updateDestinationEdit(Http.Request request, Integer tripId, Integer oldLocation) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        int order = tripDestination.getDestOrder();
        setDates(tripDestination, tripDestForm);
        if(!checkDates(tripDestination)) {
            return redirect("/trips/" + tripId + "/edit").flashing("info", "The arrival date must be before the departure date");
        }
        TreeMap<Integer, TripDestination> tempCurrentDestMap = new TreeMap<>(orderedCurrentDestinations);
        removeTripDestination(oldLocation);
        insertTripDestination(tripDestination, tripDestination.getDestOrder());
        orderedCurrentDestinations.put(order, tripDestination);
        if (invalid()){
            orderedCurrentDestinations.clear();
            orderedCurrentDestinations.putAll(tempCurrentDestMap);
            return redirect("/trips/" + tripId + "/edit").flashing("info", "The same destination cannot be after itself in a trip");
        }
        return redirect("/trips/" + tripId + "/edit");
    }

    private boolean invalid() {
        for(int i = 1; i < orderedCurrentDestinations.size(); i++) {
            if (orderedCurrentDestinations.get(i+1) != null) {
                if(orderedCurrentDestinations.get(i).getDestinationId() == orderedCurrentDestinations.get(i+1).getDestinationId()) {
                    return true;
                }
            }
        }
        return false;
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
        return pos2Invalid || pos1Invalid;
    }




    /**
     * create editDestinations trip destination forms and page
     *
     * @param request Http request
     * @param order The integer positioning of the destinations
     * @return a render of the create trips page
     */
    @Security.Authenticated(SecureSession.class)
    public Result createTripDestinationCreate(Http.Request request, Integer order) {
        TripDestination dest = orderedCurrentDestinations.get(order);
        Profile currentUser = SessionController.getCurrentUser(request);
        return ok(tripsCreate.render(form, formTrip, getCurrentDestinations(), currentUser, dest, request, messagesApi.preferred(request)));
    }

    /**
     * create editDestinations trip destination forms and page in the editDestinations page
     *
     * @param request Http request
     * @param order The integer positioning of the destinations
     * @return a render of the create trips page
     */
    @Security.Authenticated(SecureSession.class)
    public Result editTripDestinationCreate(Http.Request request, Integer order, Integer id) {
        TripDestination dest = orderedCurrentDestinations.get(order);
        Profile currentUser = SessionController.getCurrentUser(request);
        return ok(tripsEdit.render(form, formTrip, getCurrentDestinations(), currentUser, id, dest, request, messagesApi.preferred(request)));
    }


    /**
     * Delete destinations in the editDestinations page
     *
     * @param request Http request
     * @param order The integer of the positioning of the destinations
     * @param tripId Integer primary key of a trip
     * @return redirection to the editDestinations page
     */
    @Security.Authenticated(SecureSession.class)
    public Result deleteDestinationEditTrip(Http.Request request, Integer order, Integer tripId) {
        if (orderInvalidDelete(orderedCurrentDestinations.get(order)) ) {
            return redirect("/trips/" + tripId + "/edit").flashing("info", "The same destination cannot be after itself in a trip");
        }
        removeTripDestination(order);
        showEmptyEdit = true;
        return redirect("/trips/" + tripId + "/edit");
    }

    /**
     *
     * Add extra destination to a trip being edited
     *
     * @param request Http request
     * @param id Integer id of the trip (primary key)
     * @return redirection tot he editDestinations page
     */
    @Security.Authenticated(SecureSession.class)
    public Result addDestinationEditTrip(Http.Request request, int id) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        setDates(tripDestination, tripDestForm);
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        tripDestination.setDestOrder(orderedCurrentDestinations.size() + 1);
        if(!checkDates(tripDestination)) {
            return redirect("/trips/create").flashing("info", "The arrival date must be before the departure date");
        }
        if(orderedCurrentDestinations.size() >= 1) {
            if (orderInvalidInsert(tripDestination)) {
                return redirect("/trips/"+id+"/edit").flashing("info", "The same destination cannot be after itself in a trip");
            }
        }
        insertTripDestination(tripDestination, orderedCurrentDestinations.size() + 1);
        return redirect("/trips/"+id+"/edit");
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
        setDates(tripDestination, tripDestForm);
        int order = tripDestination.getDestOrder();
        if(!checkDates(tripDestination)) {
            return redirect("/trips/create").flashing("info", "The arrival date must be before the departure date");
        }
        TreeMap<Integer, TripDestination> tempCurrentDestMap = new TreeMap<>(orderedCurrentDestinations);
        removeTripDestination(oldLocation);
        insertTripDestination(tripDestination, tripDestination.getDestOrder());
        orderedCurrentDestinations.put(order, tripDestination);
        if (invalid()){
            orderedCurrentDestinations.clear();
            orderedCurrentDestinations.putAll(tempCurrentDestMap);
            return redirect("/trips/create").flashing("info", "The same destination cannot be after itself in a trip");
        }
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
        if (orderInvalidDelete(orderedCurrentDestinations.get(order)) ) {
            return redirect("/trips/create").flashing("info", "The same destination cannot be after itself in a trip");
            }
        removeTripDestination(order);
        return redirect("/trips/create");
    }

}
