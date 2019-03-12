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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CompletionStage;


public class ProfileController extends Controller {

    private final Form<Profile> form;
    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;

    private final ProfileRepository profileRepository;

    Profile testUser = new Profile("John", "James", "Smith", "yes@gmail.com", new Date(),
            "08/03/1989", "NewZealand", new Date(), "ThrillSeeker", new ArrayList<Destination>(),
            true, true, true, true, true, true , true);


    @Inject
    public ProfileController(FormFactory formFactory, MessagesApi messagesApi, HttpExecutionContext httpExecutionContext, ProfileRepository profileRepository){
        this.form = formFactory.form(Profile.class);
        this.messagesApi = messagesApi;
        this.httpExecutionContext = httpExecutionContext;
        this.profileRepository = profileRepository;
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
        Profile profile = profileForm.get();
        //TODO get profile email
        System.out.println("**********************************");
        System.out.println("User update data ready for SQL update...");
        System.out.println("Full name: " + profile.getFirstName() + " " + profile.getMiddleName() + " " + profile.getFirstName());
        System.out.println("Login info:");
        System.out.println(profile.getEmail() + " " + profile.getPassword());
        System.out.println("DOB: " + profile.getBirthDate());
        System.out.println("Nationality: " + profile.getNationality());
        System.out.println("Passport country: " + profile.getPassports());
        System.out.println("Travler type: " + profile.getTravellerTypesString());
        System.out.println("**********************************");


        return redirect(routes.ProfileController.show());

    }



    public Result show() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");

        // Bellow is for testing
        String[] nationalities = {"New Zealander", "European"};
        String[] passports = {"New Zealand", "United Kingdom"};
        String[] types = {"Backpacker", "Thrill Seeker", "Gap Year"};
        TripDestination dest1 = null;
        TripDestination dest2 = null;
        try {
            dest1 = new TripDestination("Bean Land", dateFormat.parse("04-02-19"), dateFormat.parse("16-02-19"));
            dest2 = new TripDestination("Beans", dateFormat.parse("17-02-19"), dateFormat.parse("04-03-19"));

        } catch (ParseException e){

        }
        ArrayList<TripDestination> dests = new ArrayList<>();
        dests.add(dest1);
        dests.add(dest2);
        Trip trip = new Trip(dests, "Trip to Bean Land");
        Trip trip1 = new Trip(dests, "Trip 2");
        Trip[] trips = {trip, trip1};

//        Profile profile = Profile.find.byId(email);

        return ok(profile.render(testUser, Arrays.asList(trips), Arrays.asList(types)));
    }

}
