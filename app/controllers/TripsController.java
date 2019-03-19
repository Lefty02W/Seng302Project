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
    private final ArrayList<Trip> tripList;

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
        this.tripList = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        TripDestination dest1 = new TripDestination(1, dateFormat.parse("05-03-18"), dateFormat.parse("15-03-18"));
        TripDestination dest2 = new TripDestination(2, dateFormat.parse("15-03-18"), dateFormat.parse("05-04-18"));
        ArrayList<TripDestination> dest = new ArrayList<>();
        dest.add(dest1);
        dest.add(dest2);
        Trip trip = new Trip(dest, "Example Trip");
        trip.setId(0);
        tripList.add(trip);

    }

    public Result show(Http.Request request) {
        //TODO Handle null dates
        ArrayList<Trip> tripsList = tripRepository.getUsersTrips(SessionController.getCurrentUser(request));

        return ok(trips.render(form, formTrip, destinationsList, tripsList, request, messagesApi.preferred(request)));
    }

    public Result showCreate(Http.Request request) {
        Profile currentUser = SessionController.getCurrentUser(request);
        setUsersDestinations(currentUser);
        return ok(tripsCreate.render(form, formTrip, currentDestinationsList, currentUser, request, messagesApi.preferred(request)));
    }

    public Result showEdit(Http.Request request, Integer id) {
        Profile currentUser = SessionController.getCurrentUser(request);
        setUsersDestinations(currentUser);
        Trip trip = tripRepository.getTrip(id);
        Form<Trip> tripForm = form.fill(trip);
        return ok(tripsEdit.render(tripForm, formTrip, trip.getDestinations(), currentUser, id, request, messagesApi.preferred(request)));
    }

    /**
     * adds a destination for the create a trip page
     */
    public Result addDestination(Http.Request request) {
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination tripDestination = tripDestForm.get();
        //TODO set trips `destination` field by using destinationId field to get the destination name
        //TODO Crashes when no info present
        currentDestinationsList.add(tripDestination);
        return redirect(routes.TripsController.showCreate());
    }

    /**
     * adds a destination for the edit a trip page
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


    public Result save(Http.Request request) {
        Form<Trip> tripForm = form.bindFromRequest(request);
        Trip trip = tripForm.get();
        Profile currentUser = SessionController.getCurrentUser(request);
        trip.setEmail(currentUser.getEmail());
        if (currentDestinationsList.size() < 2){
            return redirect(routes.TripsController.showCreate());
        } else {

            tripRepository.insert(trip, currentDestinationsList);


            for (int i = 0; i < currentDestinationsList.size(); i++) {
                TripDestination tripDestination = currentDestinationsList.get(i);
                tripDestination.setTripId(trip.getId());
            }


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



    public Result updateDestination() {
        return redirect(routes.TripsController.showCreate());
    }

    public Result deleteDestination(Http.Request request, Integer id) {
        //remove destination from list
        System.out.println("Destination id is == "+id);
        //for currentDestinationsList
        //if id = id
        //.remove(currentIndex)
        return redirect(routes.TripsController.showCreate());
    }

    public void setUsersDestinations(Profile currentUser) {

        //following code only resets the users destinations to their actual destinations
        List<Destination> tempDestinationList = Destination.find.query()
                .where()
                .eq("user_email", currentUser.getEmail())
                .findList();
        ArrayList<Destination> tempArraylist = new ArrayList<Destination>();
        for (int i = 0; i < tempDestinationList.size(); i++) {
            tempArraylist.add(tempDestinationList.get(i));
        }
        currentUser.setDestinations(tempArraylist);
        //if current user has no destinations
        if (currentUser.getDestinations().size() == 0) {
            Destination dest = new Destination(1, "noot", "dest 1", "yeet", "NZ", "Bean Land", 12, 23);
            Destination dest1 = new Destination(2, "noot", "dest 2", "yought", "USA", "Beans", 12, 23);
            ArrayList<Destination> destinations = new ArrayList<>();
            destinations.add(dest1);
            destinations.add(dest);
            currentUser.setDestinations(destinations);
        }
    }

}
