package controllers;

import models.EventFormData;
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
    private final Form<EventFormData> eventFormDataForm;
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
        this.eventFormDataForm = formFactory.form(EventFormData.class);
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
                            destinationRepository.getAllDestinations(), eventsList, eventForm, eventFormDataForm,
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
                genreForm.ifPresent(s -> event.get().setGenreForm(s));
                ageForm.ifPresent(s -> event.get().setAgeForm(s));
                artistForm.ifPresent(s -> event.get().setArtistForm(s));

                if(checkDates(event.get())){
                    eventRepository.insert(event.get());
                } else {
                    return redirect("/events").flashing("error", "Error creating event. Start Date must be before End date");
                }
            }
            return redirect("/events").flashing("info", "Successfully added your new event");
        });
    }

    public CompletionStage<Result> search(Http.Request request){
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            if(profile.isPresent()){
                Form<EventFormData> searchEventForm = eventFormDataForm.bindFromRequest(request);
                EventFormData eventFormData = searchEventForm.get();
                try{
                    return ok(events.render(profile.get(),
                            Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), artistRepository.getAllArtists(),
                            destinationRepository.getAllDestinations(), eventRepository.searchEvent(eventFormData), eventForm, eventFormDataForm,
                            request, messagesApi.preferred(request)));
                } catch (Exception e){
                    System.out.println(e);
                }


            }
            return redirect("/events");
        });

    }

    /**
     * This method checks that the start and end dates are valid.
     * In this context valid is that the start date is prior to the end date
     * @param event the event object holding both start and end dates
     * @return a boolean holding true if the dates are valid
     */
    private boolean checkDates(Events event) {
        Date current = new Date();
        if (event.getStartDate().before(current)){
            return false;
        }
        if (event.getStartDate() != null && event.getEndDate() != null) {
            return event.getStartDate().before(event.getEndDate());
        }
        return false;
    }
}
