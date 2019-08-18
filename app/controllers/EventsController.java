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
import java.util.Date;
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
            Form<Events> form = eventForm.bindFromRequest(request);
            Optional<Events> event = form.value();
            if (event.isPresent()) {
                Optional<String> startDate = form.field("startDate").value();
                Optional<String> endDate = form.field("endDate").value();
                Optional<String> genreForm = form.field("genreForm").value();
                Optional<String> ageForm = form.field("ageForm").value();
                Optional<String> artistForm = form.field("artistForm").value();

                if (startDate.isPresent()) {
                    try {
                        event.get().setStartDate(dateTimeEntry.parse(startDate.get()));
                    } catch (ParseException e) {
                        event.get().setStartDate(new Date());
                    }
                }
                if (endDate.isPresent()) {
                    try {
                        event.get().setEndDate(dateTimeEntry.parse(endDate.get()));
                    } catch (ParseException e) {
                        event.get().setEndDate(new Date());
                    }
                }
                if (genreForm.isPresent()) {
                    event.get().setGenreForm(genreForm.get());
                }
                if (ageForm.isPresent()) {
                    event.get().setAgeForm(ageForm.get());
                }
                if (artistForm.isPresent()) {
                    event.get().setArtistForm(artistForm.get());
                }
                eventRepository.insert(event.get());

                if(checkDates(event.get())){
                    eventRepository.insert(event.get());
                } else {
                    return redirect("/events").flashing("error", "Error creating event. Start Date must be before End date");
                }
            }
            return redirect("/events").flashing("info", "Successfully added your new event");
        });
    }

    /**
     * This method checks that the start and end dates are valid.
     * In this context valid is that the start date is prior to the end date
     * @param event the event object holding both start and end dates
     * @return a boolean holding true if the dates are valid
     */
    private boolean checkDates(Events event) {
        // Possibly add check to stop overlap with other destinations in the trip
        if (event.getStartDate() != null && event.getEndDate() != null) {
            return event.getStartDate().before(event.getEndDate());
        }
        return false;
    }
}
