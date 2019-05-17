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
import repository.TripRepository;
import views.html.admin;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class AdminController {

    private final ProfileRepository profileRepository;
    private final DestinationRepository destinationRepository;
    private final TripRepository tripRepository;
    private final Form<Profile> profileEditForm;
    private final FormFactory profileFormFactory;

    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    @Inject
    public AdminController(FormFactory profileFormFactory, HttpExecutionContext httpExecutionContext, MessagesApi messagesApi, ProfileRepository profileRepository, DestinationRepository destinationRepository, TripRepository tripRepository){
        this.profileEditForm = profileFormFactory.form(Profile.class);
        this.profileRepository = profileRepository;
        this.destinationRepository = destinationRepository;
        this.httpExecutionContext = httpExecutionContext;
        this.messagesApi = messagesApi;
        this.profileFormFactory = profileFormFactory;
        this.tripRepository = tripRepository;
    }

    /**
     * Function to delete a profile with the given email from the database using the profile controller method
     *
     * @apiNote
     * @param request
     * @param email the email of the user who is to be deleted
     * @return
     */
    public CompletionStage<Result> deleteProfile (Http.Request request, String email){
        System.out.println("yeet");
        return profileRepository.delete(email).thenApplyAsync(userEmail -> {
            return redirect("/admin");
        }, httpExecutionContext.current());
    }


    /**
     * Endpoint method to show the admin page on the site
     *
     * @apiNote /admin
     * @param request the http request
     * @return the rendered page with status ok
     */
    public Result show(Http.Request request) {
        List<Profile> profiles = Profile.find.all();
        List<Trip> trips = Trip.find.all();
        List<Destination> destinations = Destination.find.all();

        return ok(admin.render(profiles, trips, destinations, null, profileEditForm, null, request, messagesApi.preferred(request)));
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
        return ok(admin.render(profiles, trips, destinations, editProfile, profileForm, null, request, messagesApi.preferred(request)));
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
        profile.setNationalities(profile.getNationalities().replaceAll("\\s",""));
        profile.setPassports(profile.getPassports().replaceAll("\\s",""));

        return profileRepository.update(profile, profile.getPassword(),
                id).thenApplyAsync(x -> {
            return redirect("/admin");
        }, httpExecutionContext.current());
    }

    /**
     * Function to send data to the admin page with a specific users profiles, that users trips and destinations
     * and then show the page
     * @param request
     * @return a redirect to the admin page
     */
    public Result showProfile(Http.Request request, String email) {
        Profile profile = profileRepository.getProfileById(email);
        List<Profile> profiles = Profile.find.all();
        profiles.add(profile);
        List<Trip> trips = new ArrayList<>(); // TODO Needs to read the users trips
        List<Destination> destinations = destinationRepository.getUserDestinations(profile.getEmail());
        return ok(admin.render(profiles, trips, destinations, null, profileEditForm, profile, request, messagesApi.preferred(request)));
    }

    /**
     * Endpoint method to delete a trip from the database
     *
     * @apiNote /admin/trip/:tripId/delete
     * @param request
     * @param tripId
     * @return
     */
    public CompletionStage<Result> deleteTrip(Http.Request request, Integer tripId) {
        return tripRepository.delete(tripId).thenApplyAsync( x -> {
           return redirect("/admin");
        });
    }
}
