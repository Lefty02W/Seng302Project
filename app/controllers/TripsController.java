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
import views.html.trips;
import views.html.tripsCreate;
import views.html.tripsEdit;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TripsController extends Controller {

    private final ArrayList<Destination> destinationsList;
    private final ArrayList<TripDestination> currentDestinationsList;
    private final ArrayList<Trip> tripList;
    private MessagesApi messagesApi;
    private final Form<Trip> form;
    private final Form<TripDestination> formTrip;

    @Inject
    public TripsController(FormFactory formFactory, MessagesApi messagesApi) throws ParseException {
        this.form = formFactory.form(Trip.class);
        this.formTrip = formFactory.form(TripDestination.class);
        this.messagesApi = messagesApi;
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
        return ok(trips.render(form, formTrip, destinationsList, tripList, request, messagesApi.preferred(request)));
    }

    public Result showCreate(Http.Request request) {
        Profile currentUser = ProfileController.getCurrentUser(request);
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
        Profile currentUser = ProfileController.getCurrentUser(request);
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
        String tripName = tripForm.get().getName();
        if (currentDestinationsList.size() <= 1){
            return redirect(routes.TripsController.showCreate());
        } else {
            ArrayList<TripDestination> destList = new ArrayList<>();
            for (int i = 0; i < currentDestinationsList.size(); i++) {
                destList.add(currentDestinationsList.get(i));
            }
            Trip trip = new Trip(destList, tripName);
            trip.setId(tripList.size());
            tripList.add(trip);
            currentDestinationsList.clear();
            return redirect(routes.TripsController.show());
        }
    }

    /**
     * Deletes a trip in the database
     * @param tripId
     * @return
     */
    public Result delete(Integer tripId) {
        System.out.println(tripId);
        //find the trip using the trip ID
        boolean found = false;
        for (int i = 0; !found && i < tripList.size(); i++) {
            if (tripList.get(i).getId().equals(tripId)) {
                found = true;
                tripList.remove(i);
            }
        }
        return redirect(routes.TripsController.show());
    }


}
