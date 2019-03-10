package controllers;


import models.Destination;
import models.Trip;
import models.TripDestination;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.*;
import views.html.helper.form;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;



public class ProfileController extends Controller {

    private final Form<User> form;
    private MessagesApi messagesApi;
    User testUser = new User("John", "James", "Smith", "yes@gmail.com", "123", "08/03/1989", "NewZealand", "newzealand", "ThrillSeeker");


    @Inject
    public ProfileController(FormFactory formFactory, MessagesApi messagesApi){
        this.form = formFactory.form(User.class);
        this.messagesApi = messagesApi;
    }

    public Result showEdit(Http.Request request){
        return ok(editProfile.render(testUser, form, request, messagesApi.preferred(request)));
    }

    public Result update(Http.Request request){
        Form<User> updateForm = form.bindFromRequest(request);
        User user = updateForm.get();
        System.out.println("**********************************");
        System.out.println("User update data ready for SQL update...");
        System.out.println("Full name: " + user.getFirst_name() + " " + user.getMiddle_name() + " " + user.getLast_name());
        System.out.println("Login info:");
        System.out.println(user.getEmail() + " " + user.getPassword());
        System.out.println("DOB: " + user.getBirth_date());
        System.out.println("Nationality: " + user.getNationality());
        System.out.println("Passport country: " + user.getPassport_country());
        System.out.println("Travler type: " + user.getTraveller_type());
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

        return ok(profile.render("John", "James", "Smith", "1989-03-08", Arrays.asList(nationalities), Arrays.asList(passports), Arrays.asList(types), "yes@gmail.com", Arrays.asList(trips)));
    }

}
