package controllers;

import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import repository.*;
import roles.RestrictAnnotation;
import utility.Country;
import views.html.event;
import views.html.events;
import views.html.viewArtist;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private final Form<Events> eventEditForm;
    private final Form<EventFormData> eventFormDataForm;
    private final AttendEventRepository attendEventRepository;
    private static SimpleDateFormat dateTimeEntry = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    private String successEvent = "Successfully added your new event";
    private String errorEventDate = "Error creating event: Start date must be before end date and the start date must not be in the past.";
    private String errorEventAdmin = "Error creating event: You must be a verified artist admin.";
    private String errorEventUnknown = "Error creating event: Unknown error";
    private String adminEventURL = "/admin/events/0";
    private String eventURL = "/events/0";


    @Inject
    public EventsController(ProfileRepository profileRepository, MessagesApi messagesApi, GenreRepository genreRepository,
                            ArtistRepository artistRepository, DestinationRepository destinationRepository,
                            FormFactory formFactory, EventRepository eventRepository, AttendEventRepository attendEventRepository) {
        this.profileRepository = profileRepository;
        this.messagesApi = messagesApi;
        this.genreRepository = genreRepository;
        this.artistRepository = artistRepository;
        this.destinationRepository = destinationRepository;
        this.eventForm = formFactory.form(Events.class);
        this.eventEditForm = formFactory.form(Events.class);
        this.eventFormDataForm = formFactory.form(EventFormData.class);
        this.eventRepository = eventRepository;
        this.attendEventRepository = attendEventRepository;
    }




    /**
     * Helper function to set up pagination object
     *
     * @param offset current offset
     * @param maxSize max number of items to paginate
     * @param pageSize page size
     * @return PaginationHelper object
     */
    private PaginationHelper initPagination(int offset, int maxSize, int pageSize) {
        PaginationHelper paginationHelper = new PaginationHelper(offset, offset, offset, 0, true, true, maxSize);
        paginationHelper.alterNext(pageSize);
        paginationHelper.alterPrevious(pageSize);
        paginationHelper.checkButtonsEnabled();
        return paginationHelper;
    }

    /**
     * Endpoint landing page for editing an event
     *
     * @param request client request
     * @param eventId The ID of the event to edit
     * @return
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> showEventEdit(Http.Request request, Integer eventId, Integer offset) {
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId)
                .thenApplyAsync(profileRec -> profileRec.map(profile -> {
                    List<Events> eventsList = eventRepository.getPage(offset);
                    Events editEvent = eventRepository.lookup(eventId);
                    RoutedObject<Events> toSend = new RoutedObject<>(editEvent, true, false);
                    return ok(events.render(profile,
                            Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), artistRepository.getAllVerfiedArtists(),
                            destinationRepository.getAllDestinations(), eventsList, eventForm, toSend,
                            eventFormDataForm, artistRepository.isArtistAdmin(profId), initPagination(offset, eventRepository.getNumEvents(), 8), null,
                            request, messagesApi.preferred(request)));
                }).orElseGet(() -> redirect("/")));
    }

    /**
     * Endpoint to show the edit dialog for an event from the artists page
     *
     * @param request http request
     * @param artistId id of the artist who owns the event
     * @param eventId id of the event to edit
     * @return render of the artist page with the event to edit
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> editArtistEvent(Http.Request request, Integer artistId, Integer eventId) {
        Integer profId = SessionController.getCurrentUserId(request);
        Artist artist = artistRepository.getArtistById(artistId);
        if (artist == null) {
            return supplyAsync(() -> redirect("/artists"));
        }
        return profileRepository.findById(profId)
                .thenApplyAsync(profileOpt -> profileOpt.map(profile ->
                        ok(viewArtist.render(profile, artist, eventRepository.getArtistEventsPage(artistId, 0), Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), 1,
                                initPagination(0, eventRepository.getNumArtistEvents(artistId), 8), profileRepository.getAllEbeans(), destinationRepository.getAllDestinations(),
                                artistRepository.getAllVerfiedArtists(), new RoutedObject<Events>(eventRepository.lookup(eventId), true, false), eventEditForm, request, messagesApi.preferred(request))))
                        .orElseGet(() -> redirect("/artists/" + artistId + eventURL)));
    }


    /**
     * Endpoint for landing page for Events
     *
     * @param request client requests
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
                            Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), artistRepository.getAllVerfiedArtists(),
                            destinationRepository.getAllDestinations(), eventsList, eventForm, new RoutedObject<Events>(null, false, false),
                            eventFormDataForm, artistRepository.isArtistAdmin(profId), paginationHelper, null,
                            request, messagesApi.preferred(request)));
                }).orElseGet(() -> redirect("/")));
    }


    /**
     * Helper function to extract into an event object
     * @param userId User id of the creator of this event
     * @param values form of the incoming data to be put into a event object
     * @return event object with all values inside
     */
    protected static Events setValues(Integer userId, Form<Events> values){
        Events event = values.get();
        Integer destinationId = null;
        String startDate = null;
        String endDate = null;
        int ageRestriction = 0;

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
        Optional<String> ageSring = values.field("ageForm").value();
        if (ageSring.isPresent()) {
            ageRestriction = Integer.parseInt(ageSring.get());
        }
        event.setDestinationId(destinationId);
        event.setAgeRestriction(ageRestriction);
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
     * @param request request containing the edit object
     * @param id The id of the event to edit.
     * @return redirect back to the event page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> editEvent(Http.Request request, Integer id) {
        Form<Events> form = eventForm.bindFromRequest(request);
        System.out.println(form);
        Events event = setValues(SessionController.getCurrentUserId(request), form);
        System.out.println("something is going wrong here");
        if (event.getStartDate().after(event.getEndDate())){
            return supplyAsync(() -> redirect(eventURL).flashing("error", "Error: Start date cannot be after end date."));
        }
        return eventRepository.update(id, event).thenApplyAsync(x -> redirect(eventURL).flashing("info",  event.getEventName() + " has been updated."));
    }


    /**
     * Endpoint to allow and artist to edit their events
     *
     * @param request http request
     * @param eventId id of event to edit
     * @param artistId id of artist that is editing event
     * @return redirect to the artists page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> editEventFromArtist(Http.Request request, Integer artistId, Integer eventId) {
        System.out.println("in editEventFromArtist");
        Form<Events> form = eventEditForm.bindFromRequest(request);
        System.out.println(form);
        System.out.println("test");
        Events event = setValues(SessionController.getCurrentUserId(request), form);
        if (event.getStartDate().after(event.getEndDate())){
            return supplyAsync(() -> redirect("/artists/" + artistId + eventURL).flashing("error", "Error: Start date cannot be after end date."));
        }
        return eventRepository.update(eventId, event).thenApplyAsync(x -> redirect("/artists/" + artistId +eventURL).flashing("success", "Event has been updated."));
    }


    /**
     * Endpoint method to create an event as an artist admin.
     * @param request Http Request
     * @return Redirect to the admin event URL
     */
    @RestrictAnnotation()
    public CompletionStage<Result> createAdminEvent(Http.Request request) {
        return supplyAsync(() -> {
            int profId = SessionController.getCurrentUserId(request);
            Optional<Events> optEvent = createEvent(request);

            if (!optEvent.isPresent()) {
                return redirect(adminEventURL).flashing("error", errorEventUnknown);
            }
            if (!artistRepository.isArtistAdmin(profId)) {
                return redirect(adminEventURL).flashing("error", errorEventAdmin);
            }
            if (checkDates(optEvent.get())) {
                eventRepository.insert(optEvent.get());
            } else {
                return redirect(adminEventURL).flashing("error", errorEventDate);
            }

            return redirect(adminEventURL).flashing("info", successEvent);
        });
    }

    /**
     * Endpoint method to create an event as an artist
     * @param request Http Request
     * @param id Id of the artist that is creating an event
     * @return Redirect to artists page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> createArtistEvent(Http.Request request, int id){
        return supplyAsync(() -> {
            Integer profId = SessionController.getCurrentUserId(request);
            String url = "/artists/" + Integer.toString(id) + eventURL;

            Optional<Events> optEvent = createEvent(request);

            if (!optEvent.isPresent()) {
                return redirect(url).flashing("error", errorEventUnknown);
            }

            if (!artistRepository.isAdminOfGivenArtist(profId, id)) {
                return redirect(url).flashing("error", errorEventAdmin);
            }

            if (!artistRepository.isVerifiedArtist(id)) {
                return redirect(url).flashing("error", "Error creating event: Your artist must be verified before creating events.");
            }
            if (checkDates(optEvent.get())) {
                eventRepository.insert(optEvent.get());
            } else {
                return redirect(url).flashing("error", errorEventDate);
            }

            return redirect(url).flashing("info", successEvent);
        });
    }

    /**
     * End point to create an event as a user.
     * @param request Http request.
     * @return Redirect to the event page.
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> createUserEvent(Http.Request request) {

        return supplyAsync(() -> {
            Integer profId = SessionController.getCurrentUserId(request);
            Optional<Events> optEvent = createEvent(request);

            if (!optEvent.isPresent()) {
                return redirect(eventURL).flashing("error", errorEventUnknown);
            }

            if (!artistRepository.isArtistAdmin(profId)) {
                return redirect(eventURL).flashing("error", errorEventAdmin);
            }

            if (checkDates(optEvent.get())) {
                eventRepository.insert(optEvent.get());
            } else {
                return redirect(eventURL).flashing("error", errorEventDate);
            }
            return redirect(eventURL).flashing("info", successEvent);
        });



    }


    /**
     * Endpoint method to allow a user to create an event
     *
     * @param request request to create event
     * @return redirect to the events page with newly created event
     */
    public Optional<Events> createEvent(Http.Request request) {
            Form<Events> form = eventForm.bindFromRequest(request);
            Optional<Events> event = form.value();
            if (event.isPresent()) {
                Optional<String> startDate = form.field("startDate").value();
                Optional<String> endDate = form.field("endDate").value();
                Optional<String> genreForm = form.field("genreForm").value();
                Optional<String> genreFormEvent = form.field("genreFormEvent").value();
                Optional<String> ageForm = form.field("ageForm").value();
                Optional<String> artistForm = form.field("artistForm").value();
                Optional<String> ticketPrice = form.field("ticketPrice").value();
                Optional<String> ticketLink = form.field("ticketLink").value();

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
                genreFormEvent.ifPresent(s -> event.get().setGenreForm(s));
                ageForm.ifPresent(s -> event.get().setAgeRestriction(Integer.parseInt(s)));
                artistForm.ifPresent(s -> event.get().setArtistForm(s));
                event.get().setTicketPrice(-1.0);
                ticketPrice.ifPresent(price -> event.get().setTicketPrice(Double.parseDouble(price)));
                ticketLink.ifPresent(link -> event.get().setTicketLink(link));
            }
            return event;
    }


    /**
     * Method that takes in search values defined from the user and passes them into an sql search function to get
     * a refined list of artists
     *
     * @param request request to search that contains SearchFormData
     * @return a redirect to the events page, displaying the refined list of events
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> search(Http.Request request, Integer offset){
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            if(profile.isPresent()){
                Form<EventFormData> searchEventForm = eventFormDataForm.bindFromRequest(request);
                EventFormData eventFormData = searchEventForm.get();
                if(eventFormData.getAgeRestriction().equals("") && eventFormData.getArtistName().equals("") &&
                eventFormData.getDestinationId().equals("") && eventFormData.getEventName().equals("") && eventFormData.getEventType().equals("") &&
                eventFormData.getGenre().equals("") && eventFormData.getStartDate().equals("") && !eventFormData.getAttending().equals("on")) {
                    return redirect(eventURL).flashing("error", "Please enter at least one search filter.");
                }

                List<Events> eventsList = eventRepository.searchEvent(eventFormData, offset);
                if(!eventsList.isEmpty() || offset > 0){
                    PaginationHelper paginationHelper = new PaginationHelper(offset, offset, offset, 0, true, true, eventRepository.getNumEvents());
                    paginationHelper.alterNext(8);
                    paginationHelper.alterPrevious(8);
                    paginationHelper.checkButtonsEnabled();

                    return ok(events.render(profile.get(),
                            Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), artistRepository.getAllVerfiedArtists(),
                            destinationRepository.getAllDestinations(), eventsList, eventForm, new RoutedObject<Events>(null, false, false),
                            eventFormDataForm, artistRepository.isArtistAdmin(profId), paginationHelper, eventFormData,
                            request, messagesApi.preferred(request)));
                } else {
                    return redirect(eventURL).flashing("error", "No results found.");
                }
            }
            return redirect(eventURL).flashing("error", "No events match your search");
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
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> deleteEvent(Http.Request request, Integer artistId, Integer eventId) {
        return eventRepository.deleteEvent(eventId).thenApplyAsync(code -> redirect("/artists/" + artistId + eventURL));
    }

    /**
     * Endpoint method to attend an event with the given eventID
     * @param request http request
     * @param eventId event id
     * @return redirects back to event page
     */
    @Security.Authenticated(SecureSession.class)
    public Result attendEvent(Http.Request request, Integer eventId) {
        AttendEvent attendEvent = new AttendEvent(eventId, SessionController.getCurrentUserId(request));
        attendEventRepository.insert(attendEvent);
        return redirect(eventURL).flashing("info", "Attending event: " + eventRepository.lookup(attendEvent.getEventId()).getEventName());
    }

    /**
     * Endpoint method to withdraw from an event with the given eventID
     * @param request http request
     * @param eventId event id
     * @return redirects back to event page
     */
    @Security.Authenticated(SecureSession.class)
    public Result leaveEvent(Http.Request request, Integer eventId) {
        attendEventRepository.delete(attendEventRepository.getAttendEventId(eventId, SessionController.getCurrentUserId(request)));
        return redirect(eventURL).flashing("info", "No longer going to event");
    }

    /**
     * Endpoint method to withdraw from an event with the given eventID and to redirect to the profile page
     * Used for the "Don't attend" functionality from the profile page "Upcoming Events" tab
     * @param request http request
     * @param eventId event id
     * @return redirects back to the users profile page
     */
    @Security.Authenticated(SecureSession.class)
    public Result leaveEventFromProfile(Http.Request request, Integer eventId) {
        attendEventRepository.delete(attendEventRepository.getAttendEventId(eventId, SessionController.getCurrentUserId(request)));
        return redirect("/profile").flashing("success", "No longer going to event: " + eventRepository.lookup(eventId).getEventName());
    }

    /**
     * Endpoint to view a specific event
     *
     * @param request client request to view event
     * @param id the id of the event to view
     * @return rendered event page for selected event
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> viewEvent(Http.Request request, Integer id) {
        Integer profId = SessionController.getCurrentUserId(request);
        return eventRepository.getEvent(id)
                .thenApplyAsync(optEvent -> {
                    Optional<Profile> profileOpt = Optional.ofNullable(profileRepository.getProfileByProfileId(profId));
                    if (optEvent.isPresent()) {

                        return ok(event.render(profileOpt.get(), optEvent.get(), profileRepository.getAllProfileByIdList(optEvent.get().getEventAttendees()), request, messagesApi.preferred(request)));
                    } else {
                        return redirect("/events/0").flashing("info", "Error retrieving event or profile");
                    }
                });
    }


}
