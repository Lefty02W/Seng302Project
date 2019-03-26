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
import repository.TripRepository;
import views.html.*;

import javax.inject.Inject;

import java.util.concurrent.CompletionStage;



public class ProfileController extends Controller {

    private final Form<Profile> form;
    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    private final FormFactory formFactory;
    private final ProfileRepository profileRepository;
    private final TripRepository tripRepository;
    private final SessionController sessionController;



    @Inject
    public ProfileController(FormFactory formFactory, MessagesApi messagesApi, HttpExecutionContext
            httpExecutionContext, ProfileRepository profileRepository, TripRepository tripRepository, SessionController
                             sessionController){
        this.form = formFactory.form(Profile.class);
        this.messagesApi = messagesApi;
        this.httpExecutionContext = httpExecutionContext;
        this.formFactory = formFactory;
        this.profileRepository = profileRepository;
        this.tripRepository = tripRepository;
        this.sessionController = sessionController;
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

        profileRepository.update(profile, SessionController.getCurrentUser(request).getPassword());

        //TODO redirect does not update profile displayed, have to refresh to get updated info
        return redirect(routes.ProfileController.show());

    }

    public Result show(Http.Request request) {
        Profile currentProfile = SessionController.getCurrentUser(request);
        //currentProfile.sortedTrips();
        System.out.println(currentProfile.getTrips().size());
        return ok(profile.render(currentProfile));
    }
}
