package controllers;
import models.Destination;
import models.Profile;
import play.i18n.MessagesApi;

import play.data.FormFactory;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import repository.DestinationRepository;
import repository.ProfileRepository;
import views.html.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.inject.Singleton;

public class DestinationsController extends Controller {

    private MessagesApi messagesApi;
    private final List<Destination> destinationsList = new ArrayList<>();
    private final Form<Destination> form;
    private final DestinationRepository destinationRepository;
    private final SessionController sessionController = new SessionController();
    private final ProfileRepository profileRepository;


    @Inject
    public DestinationsController(FormFactory formFactory, MessagesApi messagesApi, DestinationRepository destinationRepository, ProfileRepository profileRepository) {
        this.form = formFactory.form(Destination.class);
        this.messagesApi = messagesApi;
        //this.destinationsList = Destination.find.all();
        this.destinationRepository = destinationRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Displays a page showing the destinations to the user
     * @param request
     * @return the list of destinations
     */
    public Result show(Http.Request request) {
        // to do set destinationList as get from current user query
        return ok(destinations.render(destinationsList, request, messagesApi.preferred(request)));
    }

    /**
     * Displays a page to create a destination
     * @param request
     * @return
     */
    public Result showCreate(Http.Request request) {
        Destination dest = new Destination();
        dest.setLatitude(0.0);
        dest.setLongitude(0.0);
        Form<Destination> destinationForm = form.fill(dest);
        return ok(createDestinations.render(destinationForm, request, messagesApi.preferred(request)));
    }

    /**
     * This method displays the edit page for the destinations to the user
     * @param request
     * @param id
     * @return
     */
    public Result edit(Http.Request request, Integer id) {
        Destination destination = destinationsList.get(id);
        Form<Destination> destinationForm = form.fill(destination);
        return ok(edit.render(id, destinationForm, request, messagesApi.preferred(request)));
    }

    /**
     * This method updates destination in the database
     * @param request
     * @param id
     * @return
     */
    public Result update(Http.Request request, Integer id){
        Form<Destination> destinationForm = form.bindFromRequest(request);
        Destination dest = destinationForm.get();
        destinationsList.set(id, dest);
        return redirect(routes.DestinationsController.show());
    }

    /**
     * Creates a new destination in the database
     * @param request
     * @return
     */
    public Result save(Http.Request request){
        Form<Destination> destinationForm = form.bindFromRequest(request);
        Destination dest = destinationForm.get();

       // dest.setId(destinationsList.size()+1); TODO Jade fix this
        destinationsList.add(0, dest);
        return redirect(routes.DestinationsController.show());
    }

    /**
     * Deletes a destination in the database
     * @param id
     * @return
     */
    public Result delete(Http.Request request, Integer id) {
        System.out.println("DELETED");
        Profile profile = sessionController.getCurrentUser(request);
        destinationRepository.delete(id);
        profileRepository.deleteDestination(profile.getEmail(), id);
        return redirect(routes.DestinationsController.show());

    }



    /**
     * Display the 'create destination form'.

    public Result createDestination(Http.Request request) {
        Form<Destination> destinationForm = formFactory.form(Destination.class);
        return ok(views.html.createDestinationForm.render(destinationForm, request, messagesApi.preferred(request)));

    }


    /**
     * Handle the 'Create Destination Form' submission

    public CompletionStage<Result> saveDestination(Http.Request request) {

        Profile user = getCurrentUser(request);
        if (user == null) {
            return CompletableFuture.completedFuture(redirectToLogin);
        }
        Form<Destination> destinationForm = formFactory.form(Destination.class).bindFromRequest(request);
        if (destinationForm.hasErrors()) {

            // This is the HTTP rendering thread context
            return CompletableFuture.completedFuture(
                    badRequest(views.html.createDestinationForm.render(
                            destinationForm, request, messagesApi.preferred(request))
                    )
            );
        }

        Destination destination = destinationForm.get();
        destination.setMember_email(user.getEmail());
        // Run insert db operation, then redirect
        return destinationRepository.insert(destination).thenApplyAsync(data -> {
            return Results.redirect(routes.HomeController.index());
        }, httpExecutionContext.current());
    }*/
}
