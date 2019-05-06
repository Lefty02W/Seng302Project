package controllers;

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
import repository.DestinationRepository;
import repository.ProfileRepository;
import repository.TripRepository;
import views.html.createDestinations;
import views.html.login;
import views.html.destinations;
import views.html.editDestinations;
import controllers.LoginController.Login;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * This class is the controller for the destinations.scala.html file, it provides the route to the
 * destinations page and the method that the page uses.
 */
public class DestinationsController extends Controller {

    private MessagesApi messagesApi;
    private List<Destination> destinationsList = new ArrayList<>();
    private final Form<Destination> form;
    private final Form<Profile> userForm;
    private final Form<Login> loginForm;
    private final DestinationRepository destinationRepository;
    private final ProfileRepository profileRepository;
    private final TripRepository tripRepository;
    private String destShowRoute = "/destinations/show/false";

    /**
     * Constructor for the destination controller class
     *
     * @param formFactory
     * @param messagesApi
     * @param destinationRepository
     * @param profileRepository
     */
    @Inject
    public DestinationsController(FormFactory formFactory, MessagesApi messagesApi, DestinationRepository destinationRepository,
                                  ProfileRepository profileRepository, TripRepository tripRepository) {
        this.form = formFactory.form(Destination.class);
        this.userForm = formFactory.form(Profile.class);
        this.loginForm = formFactory.form(Login.class);
        this.messagesApi = messagesApi;
        this.destinationRepository = destinationRepository;
        this.profileRepository = profileRepository;
        this.tripRepository = tripRepository;
    }

    /**
     * Displays a page showing the destinations to the user
     *
     * @param request
     * @return the list of destinations
     */
    @Security.Authenticated(SecureSession.class)
    public Result show(Http.Request request, boolean isPublic) {
        if (isPublic) {
            ArrayList<Destination> destListTemp = destinationRepository.getPublicDestinations();
            try {
                destinationsList = destListTemp;
            } catch (NoSuchElementException e) {
                destinationsList = new ArrayList<>();
            }
        } else{
            Profile user = SessionController.getCurrentUser(request);
            Optional<ArrayList<Destination>> destListTemp = profileRepository.getDestinations(user.getEmail());
            try {
                destinationsList = destListTemp.get();
            } catch (NoSuchElementException e) {
                destinationsList = new ArrayList<>();
            }
        }
        return ok(destinations.render(destinationsList, SessionController.getCurrentUser(request), request, messagesApi.preferred(request)));
    }

    /**
     * Displays a page to create a destination
     *
     * @param request
     * @return
     */
    @Security.Authenticated(SecureSession.class)
    public Result showCreate(Http.Request request) {
        Destination dest = new Destination();
        dest.setLatitude(0.0);
        dest.setLongitude(0.0);
        Form<Destination> destinationForm = form.fill(dest);
        return ok(createDestinations.render(destinationForm, request, messagesApi.preferred(request)));
    }

    /**
     * This method displays the editDestinations page for the destinations to the user
     *
     * @param request
     * @param id
     * @return
     */
    @Security.Authenticated(SecureSession.class)
    public Result edit(Http.Request request, Integer id) {
        Destination destination = new Destination();
        for (Destination dest : destinationsList) {
            if (dest.getDestinationId() == id) {
                destination = dest;
                break;
            }
        }
        System.out.println("This: "+destination.getVisible());
        Form<Destination> destinationForm = form.fill(destination);
        return ok(editDestinations.render(id, destination, destinationForm, request, messagesApi.preferred(request)));
    }

    /**
     * This method updates destination in the database
     *
     * @param request
     * @param id      The ID of the destination to editDestinations.
     * @return
     */
    @Security.Authenticated(SecureSession.class)
    public Result update(Http.Request request, Integer id) {
        Profile user = SessionController.getCurrentUser(request);
        Form<Destination> destinationForm = form.bindFromRequest(request);
        String visible = destinationForm.field("visible").value().get();
        int visibility = (visible.equals("Public")) ? 1 : 0;
        Destination dest = destinationForm.value().get();
        dest.setVisible(visibility);
        if(destinationRepository.checkValid(dest, user.getEmail())) {
            return redirect("/destinations/create").flashing("info", "This destination is already registered and unavailable to create");
        }
        if(longLatCheck(dest)){
            destinationRepository.update(dest, id);
            return redirect(destShowRoute);
        } else {
            return redirect("/destinations/create").flashing("info", "A destinations longitude(-180 to 180) and latitude(90 to -90) must be valid");
        }
    }

    /**
     * Adds a new destination to the database
     *
     * @param request
     * @return
     */
    @Security.Authenticated(SecureSession.class)
    public Result saveDestination(Http.Request request) {
        Profile user = SessionController.getCurrentUser(request);
        if (user == null) {
            return ok(login.render(loginForm, userForm, request, messagesApi.preferred(request)));
        }
        Form<Destination> destinationForm = form.bindFromRequest(request);
        String visible = destinationForm.field("visible").value().get();
        int visibility = (visible.equals("Public")) ? 1 : 0;
        Destination destination = destinationForm.value().get();
        destination.setUserEmail(user.getEmail());
        destination.setVisible(visibility);
        if(destinationRepository.checkValid(destination, user.getEmail())) {
            return redirect("/destinations/create").flashing("info", "This destination is already registered and unavailable to create");
        }
        if(longLatCheck(destination)){
            destinationRepository.insert(destination);
            return redirect(destShowRoute);
        } else {
            return redirect("/destinations/edit").flashing("info", "A destinations longitude(-180 to 180) and latitude(90 to -90) must be valid");
        }
    }


    private boolean longLatCheck(Destination destination) {
        if (destination.getLatitude() > 90 || destination.getLatitude() < -90) {
            return false;
        }
        if (destination.getLongitude() > 180 || destination.getLongitude() < -180) {
                return false;
        }
        return true;
    }


    /**
     * Deletes a destination in the database
     * @param id ID of the destination to delete
     * @return
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> delete(Http.Request request, Integer id) {
        Profile profile = SessionController.getCurrentUser(request);

        return supplyAsync(() -> {
            // Get all trips
            List<Trip> trips = Trip.find.query()
                    .where()
                    .eq("email", profile.getEmail())
                    .findList();

            // Iterate through each trip
            // and get it's destinations
            for (Trip trip : trips) {
                List<TripDestination> destinations = TripDestination.find.query()
                        .where()
                        .eq("trip_id", trip.getId())
                        .findList();

                // Iterate over each destination
                for (TripDestination destination : destinations) {
                    // Cannot delete a destination if there is match
                    // Since it in a trip
                    if (destination.getDestinationId() == id) {
                        return redirect("/destinations/show/false").flashing("failure",
                                "Destination cannot be deleted as it is part of a trip");
                    }
                }
            }
            destinationRepository.delete(id);


            return redirect("/destinations/show/false").flashing("success", "Destination Deleted");
        });

    }

}
