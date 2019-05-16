package controllers;

import models.Destination;
import models.Profile;
import models.Trip;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import views.html.admin;

import javax.inject.Inject;
import java.util.List;

import static play.mvc.Results.ok;

public class AdminController {

    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public AdminController(HttpExecutionContext httpExecutionContext, MessagesApi messagesApi){
        this.httpExecutionContext = httpExecutionContext;
        this.messagesApi = messagesApi;
    }


    public Result show(Http.Request request) {
        List<Profile> profiles = Profile.find.all();
        List<Trip> trips = Trip.find.all();
        List<Destination> destinations = Destination.find.all();

        return ok(admin.render(profiles, trips, destinations, request, messagesApi.preferred(request)));
    }
}
