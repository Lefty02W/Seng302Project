package controllers;

import models.EventFormData;
import models.Events;
import models.PaginationHelper;
import models.RoutedObject;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.lang.Integer.parseInt;
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
    public CompletionStage<Result> show(Http.Request request, Integer offset){
        Integer profId = SessionController.getCurrentUserId(request);

        return profileRepository.findById(profId)
                .thenApplyAsync(profileRec -> profileRec.map(profile -> {
                    List<Events> eventsList = eventRepository.getPage(offset);
                    PaginationHelper paginationHelper = new PaginationHelper(offset, offset, offset, 0, true, true, eventRepository.getNumEvents());
                    paginationHelper.alterNext(8);
                    paginationHelper.alterPrevious(8);
                    paginationHelper.checkButtonsEnabled();
                    return ok(events.render(profile,
                            Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), artistRepository.getAllArtists(),
                            destinationRepository.getAllDestinations(), eventsList, eventForm, new RoutedObject<Events>(null, false, false),
                            eventFormDataForm, artistRepository.isArtistAdmin(profId), paginationHelper,
                            request, messagesApi.preferred(request)));
                }).orElseGet(() -> redirect("/")));
    }


    /**
     * Helper function to extract into an event object
     * @param userId User id of the creator of this event
     * @param values form of the incoming data to be put into a event object
     * @return event object with all values inside
     */
    Events setValues(Integer userId, Form<Events> values){
        Events event = values.get();
        Integer destinationId = null;
        String startDate = null;
        String endDate = null;

        Optional<String> endDateString = values.field("endDate").value();
        if (endDateString.isPresent()) {
            endDate = endDateString.get();
        }
        Optional<String> startDateString = values.field("startDate").value();
        if (startDateString.isPresent()) {
            startDate = startDateString.get();
        }
        Optional<String> destinationIdString = values.field("destinationId").value();
        if (destinationIdString.isPresent()) {
            destinationId = parseInt(destinationIdString.get());
        }

        event.setDestinationId(destinationId);
        try {
            event.setStartDate(dateTimeEntry.parse(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            event.setEndDate(dateTimeEntry.parse(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return event;
    }

    /**
     * Function to edit a event.
     * @param request
     * @param id The id of the event to edit.
     * @return redirect back to the event page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> editEvent(Http.Request request, Integer id) {
        Form<Events> form = eventForm.bindFromRequest(request);
        Events event = setValues(SessionController.getCurrentUserId(request), form);
        if (event.getStartDate().after(event.getEndDate())){
            return supplyAsync(() -> redirect("/events").flashing("error", "Error: Start date cannot be after end date."));
        }
        return eventRepository.update(id, event).thenApplyAsync(x -> redirect("/events").flashing("success", "Event has been updated."));

    }


    /**
     * Endpoint method to allow a user to create an event
     *
     * @param request request to create event
     * @return redirect to the events page with newly created event
     */
    public CompletionStage<Result> createEvent(Http.Request request) {
        return supplyAsync( ()-> {
            Form<Events> form = eventForm.bindFromRequest(request);
            int profId = SessionController.getCurrentUserId(request);
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

                if(!artistRepository.isArtistAdmin(profId)){
                    return redirect("/events").flashing("error", "Error creating event: You must be a verified artist admin.");
                }

                if(checkDates(event.get())){
                    eventRepository.insert(event.get());
                } else {
                    return redirect("/events").flashing("error", "Error creating event: Start date must be before end date");
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
                List<Events> eventsList = eventRepository.searchEvent(eventFormData);
                if(!eventsList.isEmpty()){
                    PaginationHelper paginationHelper = new PaginationHelper(0, 0, 0, 0, true, true, eventsList.size());
                    paginationHelper.alterNext(8);
                    paginationHelper.alterPrevious(8);
                    paginationHelper.checkButtonsEnabled();
                    return ok(events.render(profile.get(),
                            Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), artistRepository.getAllArtists(),
                            destinationRepository.getAllDestinations(), eventsList, eventForm, new RoutedObject<Events>(null, false, false),
                            eventFormDataForm, artistRepository.isArtistAdmin(profId), paginationHelper,
                            request, messagesApi.preferred(request)));
                }
            }
            return redirect("/events").flashing("error", "No events match your search");
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

    /**
     * Endpoint method to delete an event
     *
     * @param request request
     * @param artistId id of artist who owns event
     * @param eventId id of event to delete
     * @return redirect back to artist page
     */
    public CompletionStage<Result> deleteEvent(Http.Request request, Integer artistId, Integer eventId) {
        return eventRepository.deleteEvent(eventId).thenApplyAsync(code -> redirect("/artists/" + artistId + "/events/0"));
    }

}
