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
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletionStage;

/**
 * This class is the controller for the trips.scala.html file, it provides the route to the
 * trips page and the method that the page uses.
 */
public class TripsController extends Controller {

    private final ArrayList<Destination> destinationsList;
    private final ArrayList<TripDestination> currentDestinationsList;

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

    }

    /**
     *show the main trip page
     * @param request Http request
     * @return a render of the trips page
     */
    @Security.Authenticated(SecureSession.class)
    public Result show(Http.Request request) {
        currentDestinationsList.clear();
        TreeMultimap<Long, Integer> tripsMap = SessionController.getCurrentUser(request).getTrips();
        System.out.println(tripsMap.size());

        List<Integer> tripValues = new ArrayList<>(tripsMap.values());
        System.out.println(tripValues);
        return ok(trips.render(form, formTrip, destinationsList, tripValues, SessionController.getCurrentUser(request), request, messagesApi.preferred(request)));
    }


    /**
     * Shows the create destination scene to tthe user on click of the "Create Destination" button
     * @param request the http request
     * @return the result
     */
    @Security.Authenticated(SecureSession.class)
    public Result showCreate(Http.Request request) {
        Profile currentUser = SessionController.getCurrentUser(request);
        return ok(tripsCreate.render(form, formTrip, currentDestinationsList, currentUser, null, request, messagesApi.preferred(request)));
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
        if (currentDestinationsList.isEmpty()){
            currentDestinationsList.addAll(trip.getDestinations());
        }
        return ok(tripsEdit.render(tripForm, formTrip, sortByOrder(currentDestinationsList), currentUser, id, request, messagesApi.preferred(request)));
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
        TripDestination dest = currentDestinationsList.get(order - 1);
        Profile currentUser = SessionController.getCurrentUser(request);
        return ok(tripsCreate.render(form, formTrip, currentDestinationsList, currentUser, dest, request, messagesApi.preferred(request)));
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
        if(currentDestinationsList.size() >= 1) {
            if(tripDestination.getDestinationName().equals(currentDestinationsList.get(currentDestinationsList.size() - 1).getDestinationName())) {
                return redirect("/trips/create").flashing("info", "The same destination cannot be after itself in a trip");
            }
        }
        currentDestinationsList.add(tripDestination);
        tripDestination.setDestOrder(currentDestinationsList.size()); //Note this cannot be used as indexes as they start at 1 not 0
        tripDestination.setTripId(1);
        tripRepository.insertTripDestination(tripDestination);
        return redirect("/trips/create");
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

        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        if(currentDestinationsList.size() >= 1) {
            if (tripDestination.getDestinationId() == currentDestinationsList.get(currentDestinationsList.size() - 1).getDestinationId()) {
                return redirect("/trips/edit/" + id).flashing("info", "The same destination cannot be after itself in a trip");

            }
        }
        currentDestinationsList.add(tripDestination);
        tripDestination.setDestOrder(currentDestinationsList.size()); //Note this cannot be used as indexes as they start at 1 not 0
        tripDestination.setTripId(1);
        tripRepository.insertTripDestination(tripDestination);
        return redirect("/trips/edit/" + id);
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
        if (currentDestinationsList.size() < 2){
            return redirect("/trips/create").flashing("info", "A trip must have at least two destinations");
        } else {
            tripRepository.insert(trip, currentDestinationsList);

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
        if (currentDestinationsList.size() < 2){
            return redirect("/trips/edit/" + id).flashing("info", "The same destination cannot be after itself in a trip");
        } else {
            tripRepository.delete(id);
            tripRepository.insert(trip, currentDestinationsList);

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
        Integer newLocation = tripDestination.getDestOrder();
        //following code block is to test if editing a tripDestination will cause 2 of the same Destinations to be next to each other
        ArrayList<TripDestination> tempList = new ArrayList<>();
        tempList.addAll(currentDestinationsList);
        tempList.remove(oldLocation-1);
        tempList.add(newLocation-1, tripDestination);
        if (isBeside(tempList)) {
            return redirect("/trips/edit/" + tripId).flashing("info", "The same destination cannot be after itself in a trip");
        }
        if (oldLocation.equals(newLocation)) {
            currentDestinationsList.set(newLocation-1, tripDestination);
        } else {
            sortFunc(oldLocation, newLocation);
            currentDestinationsList.set(newLocation-1, tripDestination);
        }
        return redirect("/trips/edit/" + tripId);
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
        if (order != 1) {
            if (order != currentDestinationsList.size()) {
                if (currentDestinationsList.get(order - 2).getDestinationId() == currentDestinationsList.get(order).getDestinationId()) {
                    return redirect("/trips/edit/" + tripId).flashing("info", "The same destination cannot be after itself in a trip");
                }
            }
        }
        for (int i = order; i < currentDestinationsList.size(); i++){
            currentDestinationsList.get(i).setDestOrder(currentDestinationsList.get(i).getDestOrder()-1);
        }
        currentDestinationsList.remove(order-1);
        return redirect("/trips/edit/" + tripId);
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
        Integer newLocation = tripDestination.getDestOrder();
        //following code block is to test if editing a tripDestination will cause 2 of the same Destinations to be next to each other
        ArrayList<TripDestination> tempList = new ArrayList<>();
        tempList.addAll(currentDestinationsList);
        tempList.remove(oldLocation-1);
        tempList.add(newLocation-1, tripDestination);
        if (isBeside(tempList)) {
            return redirect("/trips/create").flashing("info", "The same destination cannot be after itself in a trip");
        }
        if (oldLocation.equals(newLocation)) {
            currentDestinationsList.set(newLocation-1, tripDestination);
        } else {
            sortFunc(oldLocation, newLocation);
            currentDestinationsList.set(newLocation-1, tripDestination);
        }
        return redirect("/trips/create");
    }


    /**
     * Sorts the currentDestinationsList using eachs destinations old and new location
     *
     * @param oldLocation The old destination location saved under a trip in the database
     * @param newLocation The new destination location to be saved under a trip in the database
     */
    private void sortFunc(int oldLocation, int newLocation) {
        //changes the order of any tripDests that may be indirectly affected
        TripDestination tripDest;
        for (TripDestination aCurrentDestinationsList : currentDestinationsList) {
            tripDest = aCurrentDestinationsList;
            if (tripDest.getDestOrder() > oldLocation && tripDest.getDestOrder() <= newLocation) {
                aCurrentDestinationsList.setDestOrder(tripDest.getDestOrder() - 1);

            } else if (tripDest.getDestOrder() < oldLocation && tripDest.getDestOrder() >= newLocation) {
                aCurrentDestinationsList.setDestOrder(tripDest.getDestOrder() + 1);
            }
        }
        //changes the order of the main tripDest
        currentDestinationsList.get(oldLocation-1).setDestOrder(newLocation);
        //puts everything into another list ordered
        ArrayList<TripDestination> tempList = new ArrayList<>();
        for (int i = 0; i < currentDestinationsList.size(); i++) {
            for (TripDestination aCurrentDestinationsList : currentDestinationsList) {
                if (aCurrentDestinationsList.getDestOrder() == i + 1) {
                    tempList.add(aCurrentDestinationsList);
                }
            }
        }
        //copies temp list into main list
        for (int i = 0; i < currentDestinationsList.size(); i++) {
            currentDestinationsList.set(i, tempList.get(i));
        }
    }


    /**
     * Deletes TripDests from the current destinations list and changes the order of indirectly affected TripDests
     *
     * @param request Http request
     * @param order  The integer positioning of the destinations
     * @return redirect to the show create page
     */
    @Security.Authenticated(SecureSession.class)
    public Result deleteDestination(Http.Request request, Integer order) {

        if (order != 1) {
            if (order != currentDestinationsList.size()) {
                if (currentDestinationsList.get(order - 2).getDestinationId() == currentDestinationsList.get(order).getDestinationId()) {
                    return redirect("/trips/create").flashing("info", "The same destination cannot be after itself in a trip");
                }
            }
        }
        for (int i = order; i < currentDestinationsList.size(); i++){
            currentDestinationsList.get(i).setDestOrder(currentDestinationsList.get(i).getDestOrder()-1);
        }
        currentDestinationsList.remove(order-1);
        return redirect(routes.TripsController.showCreate());
    }


    /**
     * Sorting algorithm by order of trip destinations
     *
     * @param array list of trip destinations
     * @return result ArrayList of the trip destinations in order
     */
    private ArrayList<TripDestination> sortByOrder(ArrayList<TripDestination> array) {
        ArrayList<TripDestination> result = new ArrayList<TripDestination>();
        for (int i = 0; i<array.size(); i++) {
            for (int x=0; x < array.size(); x++) {
                if (array.get(x).getDestOrder() == i+1) {
                    result.add(array.get(x));
                }
            }
        }
        return result;
    }


    /**
     * Helper function to determine if 2 destinations would be next to each other in that list
     *
     * @param array list of trip destinations
     * @return boolean
     */
    private Boolean isBeside(ArrayList<TripDestination> array) {
        if (array.size() == 1 || array.size() == 0) {
            return false;
        }
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i-1).getDestinationId() == array.get(i).getDestinationId()) {
                return true;
            }
        }
        return false;
    }

}
