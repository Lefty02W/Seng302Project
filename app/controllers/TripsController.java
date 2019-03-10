package controllers;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class
TripsController extends Controller {

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
        TripDestination dest1 = new TripDestination("Example dest1", dateFormat.parse("05-03-18"), dateFormat.parse("15-03-18"));
        TripDestination dest2 = new TripDestination("Example dest2", dateFormat.parse("15-03-18"), dateFormat.parse("05-04-18"));
        ArrayList<TripDestination> dest = new ArrayList<>();
        dest.add(dest1);
        dest.add(dest2);
        Trip trip = new Trip(dest, "Example Trip");
        trip.setId(0);
        tripList.add(trip);

    }

    public Result show(Http.Request request) {
        return ok(trips.render(form, formTrip, destinationsList, tripList, request, messagesApi.preferred(request)));
    }

    public Result showCreate(Http.Request request) {
        return ok(tripsCreate.render(form, formTrip, currentDestinationsList, request, messagesApi.preferred(request)));
    }

    public Result showEdit(Http.Request request, Integer id) {
        Form<Trip> tripForm = form.fill(tripList.get(id));
        return ok(tripsEdit.render(tripForm, formTrip, tripList.get(id).getDestinations(), request, messagesApi.preferred(request)));
    }

    public Result addDestination(Http.Request request){
        Form<TripDestination> tripDestForm = formTrip.bindFromRequest(request);
        TripDestination trip = tripDestForm.get();
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


}
