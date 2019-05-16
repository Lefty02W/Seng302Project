package controllers;

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
import repository.ProfileRepository;
import views.html.admin;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;


import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class AdminController {

    private final ProfileRepository profileRepository;
    private final Form<Profile> profileEditForm;
    private final FormFactory profileFormFactory;

    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    @Inject
    public AdminController(FormFactory profileFormFactory, HttpExecutionContext httpExecutionContext, MessagesApi messagesApi, ProfileRepository profileRepository){
        this.profileEditForm = profileFormFactory.form(Profile.class);
        this.profileRepository = profileRepository;
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
     * @return
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
}
