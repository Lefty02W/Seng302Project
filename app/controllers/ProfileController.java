package controllers;


import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.ProfileRepository;
import views.html.*;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletionStage;


public class ProfileController extends Controller {

    private final Form<Profile> form;
    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    private final FormFactory formFactory;
    private final ProfileRepository profileRepository;



    @Inject
    public ProfileController(FormFactory formFactory, MessagesApi messagesApi, HttpExecutionContext httpExecutionContext, ProfileRepository profileRepository){
        this.form = formFactory.form(Profile.class);
        this.messagesApi = messagesApi;
        this.httpExecutionContext = httpExecutionContext;
        this.formFactory = formFactory;
        this.profileRepository = profileRepository;
    }


    public CompletionStage<Result> showEdit(String email) {
        //TODO data not updating until refresh after edit

        return profileRepository.lookup(email).thenApplyAsync(optionalProfile -> {
            if (optionalProfile.isPresent()) {
                Profile toEditProfile = optionalProfile.get();
                Form<Profile> profileForm = form.fill(toEditProfile);
                return ok(editProfile.render(toEditProfile, profileForm));

            } else {
                return notFound("Profile not found.");
            }
        }, httpExecutionContext.current());
    }

    public Result update(Http.Request request){
        Form<Profile> profileForm = form.bindFromRequest(request);
        Profile profile = profileForm.get();

        profileRepository.update(profile, getCurrentUser(request).getPassword());

        //TODO redirect does not update profile displayed, have to refresh to get updated info
        return redirect(routes.ProfileController.show());

    }


    public Result updateAdmin(Http.Request request, boolean isAdmin){
        Form<Profile> profileForm = form.bindFromRequest(request);
        Profile profile = profileForm.get();

        profileRepository.updateAdminPrivelege(profile.getEmail(), isAdmin);

        //TODO redirect does not update profile displayed, have to refresh to get updated info
        return redirect(routes.TravellersController.show());

    }


    /**
     * Get the currently logged in user
     * @param request
     * @return Web page showing connected user's email
     */
    public static Profile getCurrentUser(Http.Request request) {
        Optional<String> connected = request.session().getOptional("connected");
        String email;
        if (connected.isPresent()) {
            email = connected.get();
            return Profile.find.byId(email);
        } else {
            return null;
        }
    }

    public Result show(Http.Request request) {
        Profile currentProfile = getCurrentUser(request);
        //TODO change to read from db (the trips)
        currentProfile.setTrips(new ArrayList<Trip>());
        return ok(profile.render(currentProfile));
    }
}
