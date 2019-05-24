package controllers;

import models.Destination;
import models.Profile;
import models.Trip;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import repository.DestinationRepository;
import repository.ProfileRepository;
import repository.TripDestinationsRepository;
import repository.TripRepository;
import views.html.admin;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;


/**
 * This class provides the api endpoint functionality for the admin page of the site
 */
public class AdminController {

    private final ProfileRepository profileRepository;
    private final DestinationRepository destinationRepository;
    private final TripRepository tripRepository;
    private final Form<Profile> profileEditForm;
    private final Form<Profile> profileCreateForm;
    private final TripDestinationsRepository tripDestinationsRepository;

    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;

    private String adminEndpoint = "/admin";

    @Inject
    public AdminController(FormFactory profileFormFactory, HttpExecutionContext httpExecutionContext,
                           MessagesApi messagesApi, ProfileRepository profileRepository, DestinationRepository
                                       destinationRepository, TripRepository tripRepository, TripDestinationsRepository
                           tripDestinationsRepository) {
        this.profileEditForm = profileFormFactory.form(Profile.class);
        this.profileRepository = profileRepository;
        this.destinationRepository = destinationRepository;
        this.httpExecutionContext = httpExecutionContext;
        this.messagesApi = messagesApi;
        this.tripRepository = tripRepository;
        this.tripDestinationsRepository = tripDestinationsRepository;
        this.profileCreateForm = profileFormFactory.form(Profile.class);
    }

    /**
     * Function to delete a profile with the given email from the database using the profile controller method
     *
     * @apiNote
     * @param request
     * @param id the id of the user who is to be deleted
     * @return
     */
    public CompletionStage<Result> deleteProfile (Http.Request request, String id){


        return profileRepository.delete(Integer.parseInt(id)).thenApplyAsync(userEmail -> redirect(adminEndpoint)
        , httpExecutionContext.current());
    }


    /**
     * Endpoint method to show the admin page on the site
     *
     * @apiNote /admin
     * @param request the http request
     * @return the rendered page with status ok
     */
    public CompletionStage<Result> show(Http.Request request) {
        return supplyAsync(() -> {
            List<Profile> profiles = Profile.find.all();
            List<Trip> trips = Trip.find.all();
            List<Destination> destinations = Destination.find.all();

            return ok(admin.render(profiles, trips, destinations, null, profileEditForm, null, profileCreateForm, request, messagesApi.preferred(request)));
        });
    }


    /**
     * Create model for editing a users profile in the admin page
     *
     * @apiNote
     * @param request
     * @param id of the profile to be edited
     * @return a redirect to the admin page
     */
    public Result showEditProfile(Http.Request request, String id) {
        List<Profile> profiles = Profile.find.all();
        List<Trip> trips = Trip.find.all();
        List<Destination> destinations = Destination.find.all();

        Profile editProfile = profileRepository.getProfileById(id);
        Form<Profile> profileForm = profileEditForm.fill(editProfile);
        return ok(admin.render(profiles, trips, destinations, editProfile, profileForm, null, profileCreateForm, request, messagesApi.preferred(request)));
    }

    /**
     * Updates a profile's attributes based on what is retrieved form the form via the admin
     *
     * @param request Http requestRequest
     * @apiNote
     * @param request Http request
     * @return a redirect to the profile page
     */
    public CompletionStage<Result> update (Http.Request request, String id){
        Form<Profile> currentProfileForm = profileEditForm.bindFromRequest(request);
        Profile profile = currentProfileForm.get();
        profile.setNationalities(profile.getNationalities());
        profile.setPassports(profile.getPassports());

        return profileRepository.update(profile, Integer.parseInt(id))
                .thenApplyAsync(x -> redirect(adminEndpoint)
        , httpExecutionContext.current());
    }

    /**
     * Function to send data to the admin page with a specific users profiles, that users trips and destinations
     * and then show the page
     * @param request
     * @return a redirect to the admin page
     */
    public Result showProfile(Http.Request request, String email) {
        Profile profile = profileRepository.getProfileById(email);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        List<Trip> trips = new ArrayList<>(); // TODO Needs to read the users trips
        List<Destination> destinations = destinationRepository.getUserDestinations(profile.getProfileId());
        return ok(admin.render(profiles, trips, destinations, null, profileEditForm, null, profileCreateForm, request, messagesApi.preferred(request)));
    }


    /**
     * Method to allow an admin to create a new user profile
     *
     * @apiNote /admin/profile/create
     * @param request
     * @return
     */
    public CompletionStage<Result> createProfile(Http.Request request) {
            Form<Profile> profileForm = profileCreateForm.bindFromRequest(request);
            Profile profile = profileForm.get();
            //TODO Update when drop downs implemented

            return profileRepository.insert(profile)
                    .thenApplyAsync(email -> redirect(adminEndpoint)
                    );
    }

    /**
     * Endpoint method to delete a trip from the database
     *
     * @apiNote /admin/trip/:tripId/delete
     * @param request the http request
     * @param tripId the id of the trip to delete
     * @return a redirect to /admin
     */
    public CompletionStage<Result> deleteTrip(Http.Request request, Integer tripId) {
        return tripRepository.delete(tripId).thenApplyAsync( x -> redirect(adminEndpoint)
                   .flashing(
                           "info",
                           "Trip: " + tripId + " deleted")
        );
    }

    /**
     * Endpoint method allowing an admin to view a selected trip
     *
     * @apiNote /admin/trips/:tripId
     * @param request the request sent to view the trip
     * @param tripId the id of the trip to view
     * @return the admin page rendered with the view trip modal with status ok
     */
    public CompletionStage<Result> viewTrip(Http.Request request, Integer tripId) {
        return supplyAsync(() -> {
            Trip trip = tripRepository.getTrip(tripId);
            List<Profile> profiles = Profile.find.all();
            List<Trip> trips = Trip.find.all();
            List<Destination> destinations = Destination.find.all();

            return ok(admin.render(profiles, trips, destinations, null, profileEditForm, trip, profileCreateForm, request, messagesApi.preferred(request)));
        });
    }


    /**
     * Endpoint method to delete a destination from the database
     *
     * @apiNote /admin/destinations/:destId/delete
     * @param request the heep request
     * @param destId the id of the destination to delete
     * @return a redirect to /admin
     */
//    public CompletionStage<Result> deleteDestination(Http.Request request, Integer destId) {
//        return tripDestinationsRepository
//            .checkDestinationExists(destId)
//            .thenApplyAsync(
//                result -> {
//                  if (result.isPresent()) {
//                      return redirect(adminEndpoint)
//                          .flashing(
//                              "error",
//                              "Destination: "
//                                  + destId
//                                  + " is used within the following trips: "
//                                  + result.get());
//                  }
//                  destinationRepository.delete(destId);
//                  return redirect(adminEndpoint)
//                          .flashing(
//                                  "info",
//                                  "Destination: "
//                                          + destId
//                                          + " deleted");
//                });
//    }

}
