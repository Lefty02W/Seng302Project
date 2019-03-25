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
        return ok(tripsCreate.render(form, formTrip, currentDestinationsList, currentUser, null, request, messagesApi.preferred(request)));
    }

    public Result showEdit(Http.Request request, Integer id) {
        Profile currentUser = SessionController.getCurrentUser(request);
        Trip trip = tripRepository.getTrip(id);
        Form<Trip> tripForm = form.fill(trip);
        ArrayList<TripDestination> sortedTripdest = sortByOrder(trip.getDestinations());
        return ok(tripsEdit.render(tripForm, formTrip, trip.getDestinations(), currentUser, id, request, messagesApi.preferred(request)));
    }


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
        System.out.println(tripDestForm);
        TripDestination tripDestination = tripDestForm.get();
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        if(currentDestinationsList.size() >= 1) {
            if(tripDestination.getDestinationName().equals(currentDestinationsList.get(currentDestinationsList.size() - 1).getDestinationName())) {
                return redirect(routes.TripsController.showCreate());
            }
        }
        currentDestinationsList.add(tripDestination);
        tripDestination.setDestOrder(currentDestinationsList.size()); //Note this cannot be used as indexes as they start at 1 not 0
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
    public Result addDestinationEditTrip(Http.Request request, int id, int numTripdests) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        //TODO add the destination to the database and the trip
        tripDestination.setTripId(id);
        tripDestination.setDestOrder(numTripdests + 1);
        tripDestination.setDestinationId(tripDestination.getDestinationId());
        try {
            tripDestinationRepository.insert(tripDestination);
        } catch (Exception e) {
            System.out.println(e);
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
     * Updates a trip destination within the trip currently being edited
     */
    public Result updateDestinationEdit(Http.Request request, Integer oldLocation, Integer tripId) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        tripDestination.setTripId(tripId);
        tripDestination.setDestination(Destination.find.byId(Integer.toString(tripDestination.getDestinationId())));
        Trip trip = tripRepository.getTrip(tripId);
        ArrayList<TripDestination> tripDestinations = sortByOrder(trip.getDestinations());
        Integer newLocation = tripDestination.getDestOrder();
        if (oldLocation.equals(newLocation)) {
            tripDestinationRepository.insert(tripDestination);
            tripDestinationRepository.delete(tripDestinations.get(oldLocation - 1).getTripDestinationId());
        } else {
            int oldLocationId = tripDestinations.get(oldLocation-1).getTripDestinationId();
            //goes through the trips tripDests and changes the order of any tripDests that may be indirectly affected
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
            //deletes the tripDest and replaces it with the new TripDest
            tripDestinationRepository.delete(oldLocationId);
            tripDestinationRepository.insert(tripDestination);
        }
        //tripRepository.getTrip(tripId).setDestinations(sortByOrder(tripRepository.getTrip(tripId).getDestinations()));
        return redirect(routes.TripsController.showEdit(tripId));
    }


    /**
     * Updates a trip destination that is within the current trip being created
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

    public Result deleteDestination(Http.Request request, Integer id) {
        //remove destination from list
        System.out.println("Destination id is == "+id);
        //for currentDestinationsList
        //if id = id
        //.remove(currentIndex)
        return redirect(routes.TripsController.showCreate());
    }



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


}
