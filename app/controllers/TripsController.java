package controllers;

import models.Destination;
import models.Profile;
import play.data.Form;
import play.i18n.MessagesApi;
import com.google.inject.Inject;
import models.Trip;
import models.TripDestination;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.TripDestinationsRepository;
import repository.TripRepository;
import scala.xml.Null;
import views.html.trips;
import views.html.tripsCreate;
import views.html.tripsEdit;
import java.util.*;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.CompletionStage;



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
     * @param request
     * @return
     */
    public Result show(Http.Request request) {
        currentDestinationsList.clear();
        ArrayList<Trip> tripsList = SessionController.getCurrentUser(request).getTrips();
        return ok(trips.render(form, formTrip, destinationsList, tripsList, request, messagesApi.preferred(request)));
    }


    /**
     * Shows the create destination scene to tthe user on click of the "Create Destination" button
     * @param request the http request
     * @return the result
     */
    public Result showCreate(Http.Request request) {
        Profile currentUser = SessionController.getCurrentUser(request);
        return ok(tripsCreate.render(form, formTrip, currentDestinationsList, currentUser, null, request, messagesApi.preferred(request)));
    }

    /**
     * create and show  edit page
     * @param request
     * @param id
     * @return
     */
    public Result showEdit(Http.Request request, Integer id) {
        Profile currentUser = SessionController.getCurrentUser(request);
        Trip trip = tripRepository.getTrip(id);
        Form<Trip> tripForm = form.fill(trip);
        ArrayList<TripDestination> sortedTripdest = sortByOrder(trip.getDestinations());
        return ok(tripsEdit.render(tripForm, formTrip, trip.getDestinations(), currentUser, id, request, messagesApi.preferred(request)));
    }

    /**
     * create edit trip destination forms and page
     * @param request
     * @param order
     * @return
     */
    public Result editTripDestinationCreate(Http.Request request, Integer order) {
        TripDestination dest = currentDestinationsList.get(order - 1);
        Profile currentUser = SessionController.getCurrentUser(request);
        return ok(tripsCreate.render(form, formTrip, currentDestinationsList, currentUser, dest, request, messagesApi.preferred(request)));
    }

    /**
     * Adds a destination to the trip being created
     * @param request the http request
     * @return the result
     */
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
     * add extra destination to a trip being edited
     * @param request
     * @param id
     * @return redirection tot he editpage
     */
    public Result addDestinationEditTrip(Http.Request request, int id, int numTripdests) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        ArrayList<TripDestination> tripDestArray = tripRepository.getTrip(id).getDestinations();
        if (tripDestArray.get(tripDestArray.size()-1).getDestinationId() != tripDestination.getDestinationId()) {
            tripDestination.setTripId(id);
            tripDestination.setDestOrder(numTripdests + 1);
            try {
                tripDestinationRepository.insert(tripDestination);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return redirect(routes.TripsController.showEdit(id));
    }


    /**
     * Saves a users newly created trip and its connected tripDestinations ot the database
     * @param request the http request
     * @return the result
     */
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
     * updates a trips name
     * @param request
     * @param id
     * @return
     */
    public Result updateName(Http.Request request, int id) {
        Form<Trip> tripForm = form.bindFromRequest(request);
        Trip tempTrip = tripForm.get();
        tripRepository.updateName(id, tempTrip.getName());
        return redirect("/trips");
    }

    /**
     * Deletes a trip in the database
     * @param tripId
     * @return
     */
    public CompletionStage<Result> delete(Integer tripId) {
        return tripRepository.delete(tripId).thenApplyAsync(v -> {
            return redirect("/trips");
        });
    }

    /**
     * Updates a trip destination within the trip currently being edited
     * @param request
     * @param tripId
     * @param oldLocation
     * @returnredirections to the edit page
     */
    public Result updateDestinationEdit(Http.Request request, Integer tripId, Integer oldLocation) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        tripDestination.setTripId(tripId);
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        Trip trip = tripRepository.getTrip(tripId);
        ArrayList<TripDestination> tripDestinations = sortByOrder(trip.getDestinations());
        Integer newLocation = tripDestination.getDestOrder();

        //following code block is to test if editing a tripDestination will cause 2 of the same Destinations to be next to each other
        ArrayList<TripDestination> tempList = new ArrayList<>();
        tempList.addAll(tripDestinations);
        tempList.remove(oldLocation-1);
        tempList.add(newLocation-1, tripDestination);
        if (isBeside(tempList)) {
            return redirect(routes.TripsController.showEdit(tripId));
        }

        //goes through the trips tripDests and changes the order of any tripDests that may be indirectly affected
        int oldLocationId = tripDestinations.get(oldLocation-1).getTripDestinationId();
        if (!oldLocation.equals(newLocation)) {

            for (int i = 0; i < tripDestinations.size(); i++) {
                if (tripDestinations.get(i).getDestOrder() > oldLocation && tripDestinations.get(i).getDestOrder() <= newLocation) {
                    tripDestinationRepository.updateOrder(tripDestinations.get(i).getTripDestinationId(),
                            tripDestinations.get(i).getDestOrder()-1);
                }
                else if (tripDestinations.get(i).getDestOrder() < oldLocation && tripDestinations.get(i).getDestOrder() >= newLocation) {
                    tripDestinationRepository.updateOrder(tripDestinations.get(i).getTripDestinationId(),
                            tripDestinations.get(i).getDestOrder()+1);
                }
            }

        }

        tripDestinationRepository.delete(oldLocationId);
        tripDestinationRepository.insert(tripDestination);
        return redirect(routes.TripsController.showEdit(tripId));
    }

    /**
     * delete destinations in the edit page
     * @param request
     * @param order
     * @param tripId
     * @return redirection to the edit page
     */
    public Result deleteDestinationEditTrip(Http.Request request, Integer order, Integer tripId) {
        Trip trip = tripRepository.getTrip(tripId);
        ArrayList<TripDestination> tripDestinations = sortByOrder(trip.getDestinations());

        //following code block is to test if editing a tripDestination will cause 2 of the same Destinations to be next to each other
        ArrayList<TripDestination> tempList = new ArrayList<>();
        tempList.addAll(tripDestinations);
        tempList.remove(order-1);
        if (isBeside(tempList)) {
            return redirect(routes.TripsController.showEdit(tripId));
        }

        if (tripDestinations.size() > 2) {
            for (int i= order; i < tripDestinations.size(); i++) {
                tripDestinationRepository.updateOrder(tripDestinations.get(i).getTripDestinationId(),
                        tripDestinations.get(i).getDestOrder()-1);
            }
            tripDestinationRepository.delete(tripDestinations.get(order-1).getTripDestinationId());
        }
        return redirect(routes.TripsController.showEdit(tripId));
    }



    /**
     * Updates a trip destination that is within the current trip being created
     * @param request
     * @param oldLocation
     * @return redirection to the create trips page
     */
    public Result updateDestination(Http.Request request, Integer oldLocation) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        Integer newLocation = tripDestination.getDestOrder();
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
     * @param oldLocation
     * @param newLocation
     */
    private void sortFunc(int oldLocation, int newLocation) {
        //changes the order of any tripDests that may be indirectly affected
        TripDestination tripDest;
        for (int i = 0; i < currentDestinationsList.size(); i++) {
            tripDest = currentDestinationsList.get(i);
            if (tripDest.getDestOrder() > oldLocation && tripDest.getDestOrder() <= newLocation) {
                currentDestinationsList.get(i).setDestOrder(tripDest.getDestOrder() - 1);

            }
            else if (tripDest.getDestOrder() < oldLocation && tripDest.getDestOrder() >= newLocation) {
                currentDestinationsList.get(i).setDestOrder(tripDest.getDestOrder() + 1);
            }
        }
        //changes the order of the main tripDest
        currentDestinationsList.get(oldLocation-1).setDestOrder(newLocation);
        //puts everything into another list ordered
        ArrayList<TripDestination> tempList = new ArrayList<>();
        for (int i = 0; i < currentDestinationsList.size(); i++) {
            for (int x= 0; x < currentDestinationsList.size(); x++) {
                if (currentDestinationsList.get(x).getDestOrder() == i+1) {
                    tempList.add(currentDestinationsList.get(x));
                }
            }
        }
        System.out.println(tempList.size());
        //copies temp list into main list
        for (int i = 0; i < currentDestinationsList.size(); i++) {
            currentDestinationsList.set(i, tempList.get(i));
        }
    }

    /**
     * eletes TripDests from the current destinations list and changes the order of indirectly affected TripDests
     * @param request
     * @param order
     * @return redirect to the show create page
     */
    public Result deleteDestination(Http.Request request, Integer order) {

        if (order != 1) {
            if (order != currentDestinationsList.size()) {
                System.out.println("-qe12-31231 re---------------------");
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
     * sorting algorithm by order of trip destinations
     * @param array list of trip destinations
     * @return temp
     */
    public ArrayList<TripDestination> sortByOrder(ArrayList<TripDestination> array) {
        ArrayList<TripDestination> temp = new ArrayList<TripDestination>();
        for (int i = 0; i<array.size(); i++) {
            for (int x=0; x < array.size(); x++) {
                if (array.get(x).getDestOrder() == i+1) {
                    temp.add(array.get(x));
                }
            }
        }
        return temp;
    }


    /**
     * helper function to determine if 2 destinations would be next to each other in that list
     * @param array list of trip destinations
     * @return boolean
     */
    public Boolean isBeside(ArrayList<TripDestination> array) {
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
