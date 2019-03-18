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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletionStage;


public class ProfileController extends Controller {

    private final Form<Profile> form;
    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    private final ProfileRepository profileRepository;
    private final TripRepository tripRepository;



    @Inject
    public ProfileController(FormFactory formFactory, MessagesApi messagesApi, HttpExecutionContext httpExecutionContext, ProfileRepository profileRepository, TripRepository tripRepository){
        this.form = formFactory.form(Profile.class);
        this.messagesApi = messagesApi;
        this.httpExecutionContext = httpExecutionContext;
        this.profileRepository = profileRepository;
        this.tripRepository = tripRepository;
    }


    public CompletionStage<Result> showEdit(String email) {

        return profileRepository.lookup(email).thenApplyAsync(optionalProfile -> {
            if (optionalProfile.isPresent()) {
                Profile toEditProfile = optionalProfile.get();
                Form<Profile> profileForm = form.fill(toEditProfile);

                return ok(editProfile.render(profileForm));

            } else {
                return notFound("Profile not found.");
            }
        }, httpExecutionContext.current());
    }

    public Result update(Http.Request request){
        Form<Profile> profileForm = form.bindFromRequest(request);
        System.out.println(profileForm);
        Profile profile = profileForm.get();
        //TODO get profile email

        return redirect(routes.ProfileController.show());

    }

    public Result show(Http.Request request) {
        Profile currentProfile = SessionController.getCurrentUser(request);
        //TODO xhange to read from db
        currentProfile.setTrips(tripRepository.getUsersTrips(currentProfile));
        //currentProfile.sortedTrips();
        System.out.println(currentProfile.getTrips().size());
        return ok(profile.render(currentProfile));
    }

}
