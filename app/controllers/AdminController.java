package controllers;

import com.google.common.collect.TreeMultimap;
import models.Destination;
import models.Profile;
import play.data.Form;
import play.data.FormFactory;
import models.Trip;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import repository.DestinationRepository;
import repository.ProfileRepository;
import repository.TripRepository;
import scala.Int;
import views.html.admin;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;


import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class AdminController {

    private final ProfileRepository profileRepository;
    private final DestinationRepository destinationRepository;
    private final Form<Profile> profileEditForm;
    private final FormFactory profileFormFactory;

    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    @Inject
    public AdminController(FormFactory profileFormFactory, HttpExecutionContext httpExecutionContext, MessagesApi messagesApi, ProfileRepository profileRepository, DestinationRepository destinationRepository){
        this.profileEditForm = profileFormFactory.form(Profile.class);
        this.profileRepository = profileRepository;
        this.destinationRepository = destinationRepository;
        this.httpExecutionContext = httpExecutionContext;
        this.messagesApi = messagesApi;
        this.profileFormFactory = profileFormFactory;
    }

    /**
     * Function to delete a profile with the given email from the database using the profile controller method
     * @param request
     * @param email the email of the user who is to be deleted
     * @return
     */
    private CompletionStage<Result> deleteProfile (Http.Request request, String email){
        return profileRepository.delete(email).thenApplyAsync(userEmail -> {
            return redirect("/admin");
        }, httpExecutionContext.current());
    }

    /**
     * Function to send data to the admin page with all users profiles, trips and destinations and then show the page
     * @param request
     * @return a redirect to the admin page
     */
    public Result show(Http.Request request) {
        List<Profile> profiles = Profile.find.all();
        List<Trip> trips = Trip.find.all();
        List<Destination> destinations = Destination.find.all();

        return ok(admin.render(profiles, trips, destinations, null, profileEditForm, request, messagesApi.preferred(request)));
    }


    /**
     * Create model for editing a users profile in the admin page
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
        return ok(admin.render(profiles, trips, destinations, editProfile, profileForm, request, messagesApi.preferred(request)));
    }

    /**
     * Updates a profile's attributes based on what is retrieved form the form via the admin
     *
     * @param request Http requestRequest
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
     * @param request        TreeMultimap<Long, Integer> tripsMap = profile.getTrips();
     * @return a redirect to the admin page
     */
    public Result showProfile(Http.Request request, String profileId) {
        Profile profile = profileRepository.getProfileById(profileId);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        List<Trip> trips = new ArrayList<>(profile.getTripsMap().values());
        List<Destination> destinations = destinationRepository.getUserDestinations(profile.getEmail());
        return ok(admin.render(profiles, trips, destinations, null, profileEditForm, request, messagesApi.preferred(request)));
    }
}
