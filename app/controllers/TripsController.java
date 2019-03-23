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
import java.util.concurrent.TimeUnit;

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
     *
     * @param request
     * @return
     */
    public Result show(Http.Request request) {
        currentDestinationsList.clear();
        ArrayList<Trip> tripsList = tripRepository.getUsersTrips(SessionController.getCurrentUser(request));
        return ok(trips.render(form, formTrip, destinationsList, tripsList, request, messagesApi.preferred(request)));
    }


    /**
     * Shows the create destination scene to tthe user on click of the "Create Destination" button
     * @param request the http request
     * @return the result
     */
    public Result showCreate(Http.Request request) {
        Profile currentUser = SessionController.getCurrentUser(request);
        return ok(tripsCreate.render(form, formTrip, currentDestinationsList, currentUser, request, messagesApi.preferred(request)));
    }

    public Result showEdit(Http.Request request, Integer id) {
        Profile currentUser = SessionController.getCurrentUser(request);
        Trip trip = tripRepository.getTrip(id);
        Form<Trip> tripForm = form.fill(trip);
        return ok(tripsEdit.render(tripForm, formTrip, trip.getDestinations(), currentUser, id, request, messagesApi.preferred(request)));
    }

    /**
     * Adds a destination to the trip being created
     * @param request the http request
     * @return the result
     */
    public Result addDestination(Http.Request request) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        System.out.println(tripDestForm);
        TripDestination tripDestination = tripDestForm.get();
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        currentDestinationsList.add(tripDestination);
        tripDestination.setOrder(currentDestinationsList.size()); //Note this cannot be used as indexes as they start at 1 not 0
        tripDestination.setTripId(1);
        tripRepository.insertTripDestination(tripDestination);
        return redirect(routes.TripsController.showCreate());
    }

    /**
     *
     * @param request
     * @param id
     * @return
     */
    public Result addDestinationEditTrip(Http.Request request, int id) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        //TODO add the destination to the database and the trip
        tripDestination.setTripId(id);
        tripDestination.setDestinationId(tripDestination.getDestinationId());
        tripDestinationRepository.insert(tripDestination);
        return redirect(routes.TripsController.showEdit(id));
    }

    /**
     * Updates a trip destination within the trip currenty being edited
     * @param request
     * @param oldLocation
     * @return
     */
    public Result updateDestinationEdit(Http.Request request, Integer oldLocation) {
        //TODO implement this
        return null;
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
            return redirect(routes.TripsController.showCreate());
        } else {
            tripRepository.insert(trip, currentDestinationsList);

            return redirect(routes.TripsController.show());
        }
    }

    public Result updateName(Http.Request request, int id) {
        Form<Trip> tripForm = form.bindFromRequest(request);
        Trip tempTrip = tripForm.get();
        tripRepository.updateName(id, tempTrip.getName());
        return redirect(routes.TripsController.show());
    }

    /**
     * Deletes a trip in the database
     * @param tripId
     * @return
     */
    public CompletionStage<Result> delete(Integer tripId) {
        return tripRepository.delete(tripId).thenApplyAsync(v -> {
            return redirect(routes.TripsController.show());
        });
    }


    /**
     * Updates a trip destination that is within the current trip being created
     * @param request the request
     * @param oldLocation the index of the destiantion to edit
     * @return
     */
    public Result updateDestination(Http.Request request, Integer oldLocation) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        Integer newLocation = tripDestination.getOrder();
        if (oldLocation.equals(newLocation)) {
            currentDestinationsList.set(newLocation-1, tripDestination);
        } else {
            sortFunc(oldLocation, newLocation);
            currentDestinationsList.set(newLocation-1, tripDestination);
        }
        return redirect(routes.TripsController.showCreate());
    }

    /**
     * Sorts the currentDestinationsList
     */
    private void sortFunc(int oldLocation, int newLocation) {
        //changes the order of any tripDests that may be indirectly affected
        TripDestination tripDest;
        for (int i = 0; i < currentDestinationsList.size(); i++) {
            tripDest = currentDestinationsList.get(i);
            if (tripDest.getOrder() > oldLocation && tripDest.getOrder() <= newLocation) {
                currentDestinationsList.get(i).setOrder(tripDest.getOrder() - 1);

            }
            else if (tripDest.getOrder() < oldLocation && tripDest.getOrder() >= newLocation) {
                currentDestinationsList.get(i).setOrder(tripDest.getOrder() + 1);
            }
        }
        //changes the order of the main tripDest
        currentDestinationsList.get(oldLocation-1).setOrder(newLocation);
        //puts everything into another list ordered
        ArrayList<TripDestination> tempList = new ArrayList<>();
        for (int i = 0; i < currentDestinationsList.size(); i++) {
            for (int x= 0; x < currentDestinationsList.size(); x++) {
                if (currentDestinationsList.get(x).getOrder() == i+1) {
                    tempList.add(currentDestinationsList.get(x));
                    break;
                }
            }
        }
        //copies temp list into main list
        for (int i = 0; i < currentDestinationsList.size(); i++) {
            currentDestinationsList.set(i, tempList.get(i));
        }
    }

    public Result deleteDestination(Http.Request request, Integer id) {
        //remove destination from list
        System.out.println("Destination id is == "+id);
        //for currentDestinationsList
        //if id = id
        //.remove(currentIndex)
        return redirect(routes.TripsController.showCreate());
    }


}
