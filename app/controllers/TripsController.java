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
import java.util.Optional;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionStage;

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
        // Testing only
        Destination dest = new Destination(1, 1, "dest 1", "yeet", "NZ", "Bean Land", 12, 23);
        Destination dest1 = new Destination(2, 1, "dest 2", "yought", "USA", "Beans", 12, 23);
        ArrayList<Destination> destinations = new ArrayList<>();
        destinations.add(dest1);
        destinations.add(dest);
        currentUser.setDestinations(destinations);
        // -------
        //TODO destiantions need to be read from db when a user is
        return ok(tripsCreate.render(form, formTrip, currentDestinationsList, currentUser, request, messagesApi.preferred(request)));
    }

    public Result showEdit(Http.Request request, Integer id) {
        Profile currentUser = SessionController.getCurrentUser(request);
        // Testing only
        Destination dest = new Destination(1, 1, "dest 1", "yeet", "NZ", "Bean Land", 12, 23);
        Destination dest1 = new Destination(2, 1, "dest 2", "yought", "USA", "Beans", 12, 23);
        ArrayList<Destination> destinations = new ArrayList<>();
        destinations.add(dest1);
        destinations.add(dest);
        currentUser.setDestinations(destinations);
        Form<Trip> tripForm = form.fill(tripList.get(id));
        return ok(tripsEdit.render(tripForm, formTrip, tripList.get(id).getDestinations(), currentUser, request, messagesApi.preferred(request)));
    }

    public Result addDestination(Http.Request request){
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        System.out.println(tripDestForm);
        TripDestination trip = tripDestForm.get();
        //TODO set trips `destination` field by using destinationId field to get the destination name
        currentDestinationsList.add(trip);
        return redirect(routes.TripsController.showCreate());
    }


    public Result save(Http.Request request) {
        Form<Trip> tripForm = form.bindFromRequest(request);
        Trip trip = tripForm.get();
        if (currentDestinationsList.size() <= 1){
            return redirect(routes.TripsController.showCreate());
        } else {
            ArrayList<TripDestination> destList = new ArrayList<>();
            for (int i = 0; i < currentDestinationsList.size(); i++) {
                TripDestination tripDestination = currentDestinationsList.get(i);
                tripDestination.setTripId(trip.getId());
                tripDestination.setTripId(1);
//              System.out.println(currentDestinationsList.get(i).getDestinationId());
//              System.out.println(currentDestinationsList.get(i).getTripId());
                tripDestinationRepository.insert(tripDestination);
                //TODO save tripdestinations to a repository
            }

            Profile currentUser = SessionController.getCurrentUser(request);
            trip.setEmail(currentUser.getEmail());
            trip.setDestinations(currentDestinationsList);
            tripRepository.insert(trip);
            currentDestinationsList.clear();
            return redirect(routes.TripsController.show());
        }
    }

    /**
     * Deletes a trip in the database
     * @param tripId
     * @return
     */
    public CompletionStage<Result> delete(Integer tripId) {
        //find the trip using the trip ID
        //find the trip using the trip ID
        return tripRepository.delete(tripId).thenApplyAsync(v -> {
            return redirect(routes.TripsController.show());
        });
    }



//        for (int i = 0; !found && i < tripList.size(); i++) {
//            if (tripList.get(i).getId().equals(tripId)) {
//                found = true;
//                tripList.remove(i);
//            }
//        }
//        return redirect(routes.TripsController.show());
//    }

    public Result updateDestination() {
        return redirect(routes.TripsController.showCreate());
    }



}
