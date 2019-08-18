package controllers;

import models.Events;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import repository.*;
import utility.Country;
import views.html.events;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class EventsController extends Controller {

    private final ProfileRepository profileRepository;
    private MessagesApi messagesApi;
    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;
    private final DestinationRepository destinationRepository;
    private final EventRepository eventRepository;
    private final Form<Events> eventForm;
    private static SimpleDateFormat dateTimeEntry = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");


    @Inject
    public EventsController(ProfileRepository profileRepository, MessagesApi messagesApi, GenreRepository genreRepository,
                            ArtistRepository artistRepository, DestinationRepository destinationRepository,
                            FormFactory formFactory, EventRepository eventRepository) {
        this.profileRepository = profileRepository;
        this.messagesApi = messagesApi;
        this.genreRepository = genreRepository;
        this.artistRepository = artistRepository;
        this.destinationRepository = destinationRepository;
        this.eventForm = formFactory.form(Events.class);
        this.eventRepository = eventRepository;
    }

    /**
     * Endpoint for landing page for Events
     *
     * @param request client request
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> show(Http.Request request){
        Integer profId = SessionController.getCurrentUserId(request);

        return profileRepository.findById(profId)
                .thenApplyAsync(profileRec -> profileRec.map(profile -> {
                    Optional<List<Events>> optionalEventsList = eventRepository.getAll();
                    List<Events> eventsList = new ArrayList<>();
                    if (optionalEventsList.isPresent()){
                        eventsList = optionalEventsList.get();
                    }
                    return ok(events.render(profile,
                            Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), artistRepository.getAllArtists(),
                            destinationRepository.getAllDestinations(), eventsList, eventForm,
                            request, messagesApi.preferred(request)));
                })
                        .orElseGet(() -> redirect("/")));
    }


    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> createEvent(Http.Request request) {
        return supplyAsync( ()-> {
            System.out.println("yeet");
            Form<Events> form = eventForm.bindFromRequest(request);
            System.out.println(form);

            Optional<Events> event = form.value();
            event.ifPresent(event1 -> {
                try {
                    event1.setStartDate(dateTimeEntry.parse(form.field("startDate").value().get()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println(event.get().getStartDate());
                eventRepository.insert(event1);
            });
            return redirect("/events");
        });
    }
}
