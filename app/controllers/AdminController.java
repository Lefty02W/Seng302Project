package controllers;

import io.ebean.DuplicateKeyException;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import repository.*;
import roles.RestrictAnnotation;
import utility.Country;
import views.html.admin;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.lang.Integer.parseInt;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;


/**
 * This class provides the api endpoint functionality for the admin page of the site
 */
@RestrictAnnotation()
public class AdminController {

    private final ProfileRepository profileRepository;
    private final DestinationRepository destinationRepository;
    private final TripRepository tripRepository;
    private final Form<Profile> profileEditForm;
    private final Form<Destination> destinationEditForm;
    private final Form<Profile> profileCreateForm;
    private final TreasureHuntRepository treasureHuntRepository;
    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    private final TreasureHuntController treasureHuntController;
    private final ArtistController artistController;
    private final Form<TreasureHunt> huntForm;
    private final UndoStackRepository undoStackRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final Form<Artist> artistForm;
    private final int pageSize = 8;
    private String adminEndpoint = "/admin";
    private RolesRepository rolesRepository;

    @Inject
    public AdminController(FormFactory formFactory, HttpExecutionContext httpExecutionContext,
                           MessagesApi messagesApi, ProfileRepository profileRepository, DestinationRepository
                                   destinationRepository, TripRepository tripRepository,
                           RolesRepository rolesRepository,
                           TreasureHuntRepository treasureHuntRepository, TreasureHuntController treasureHuntController,
                           ArtistController artistController, UndoStackRepository undoStackRepository, ArtistRepository artistRepository,
                           FormFactory artistProfileFormFactory, GenreRepository genreRepository) {
        this.profileEditForm = formFactory.form(Profile.class);
        this.profileRepository = profileRepository;
        this.destinationRepository = destinationRepository;
        this.httpExecutionContext = httpExecutionContext;
        this.messagesApi = messagesApi;
        this.tripRepository = tripRepository;
        this.profileCreateForm = formFactory.form(Profile.class);
        this.destinationEditForm = formFactory.form(Destination.class);
        this.rolesRepository = rolesRepository;
        this.treasureHuntRepository = treasureHuntRepository;
        this.huntForm = formFactory.form(TreasureHunt.class);
        this.treasureHuntController = treasureHuntController;
        this.artistController = artistController;
        this.undoStackRepository = undoStackRepository;
        this.artistRepository = artistRepository;
        this.artistForm = artistProfileFormFactory.form(Artist.class);
        this.genreRepository = genreRepository;
    }


    private PaginationHelper initialisePaginatior(int offset, int maxSize) {
        PaginationHelper paginationHelper = new PaginationHelper(offset, offset, offset, 2, true, true, maxSize);
        paginationHelper.alterNext(pageSize);
        paginationHelper.alterPrevious(pageSize);
        paginationHelper.checkButtonsEnabled();
        return paginationHelper;
    }

    /**
     * Endpoint for admin to view all user trips
     *
     * @apiNote GET /admin/trips/:offset
     * @param request client http request
     * @param offset pagination offset
     * @return CompletionStage result of admin page
     */
    public CompletionStage<Result> showTrips(Http.Request request, Integer offset) {
        return supplyAsync(() -> {
            return ok(admin.render(new ArrayList<Profile>(), new ArrayList<Profile>(), tripRepository.getPaginateTrip(offset, pageSize), new RoutedObject<Destination>(null, false, false),
                    new ArrayList<Destination>(), new RoutedObject<Profile>(null, false, false), profileEditForm,
                    null, profileCreateForm, null, new ArrayList<DestinationChange>(), new ArrayList<TreasureHunt>(),
                    new RoutedObject<TreasureHunt>(null, false, false), Country.getInstance().getAllCountries(),
                    undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), new ArrayList<Artist>(), new ArrayList<Artist>(),
                    new RoutedObject<Artist>(null, true, false), genreRepository.getAllGenres(), initialisePaginatior(offset, tripRepository.getNumTrips()), request, messagesApi.preferred(request)));
        });
    }

    /**
     * Endpoint for admin to view all user profiles
     *
     * @apiNote GET /admin/profiles/:offset
     * @param request client http request
     * @param offset pagination offset
     * @return CompletionStage result of admin page
     */
    public CompletionStage<Result> showProfiles(Http.Request request, Integer offset) {
        return supplyAsync(() -> ok(admin.render(profileRepository.getAll(), new ArrayList<Profile>(), new ArrayList<Trip>(), new RoutedObject<Destination>(null, false, false),
                new ArrayList<Destination>(), new RoutedObject<Profile>(null, false, false), profileEditForm,
                null, profileCreateForm, null, new ArrayList<DestinationChange>(), new ArrayList<TreasureHunt>(),
                new RoutedObject<TreasureHunt>(null, false, false), Country.getInstance().getAllCountries(),
                undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), new ArrayList<Artist>(), new ArrayList<Artist>(),
                new RoutedObject<Artist>(null, true, false), genreRepository.getAllGenres(), new PaginationHelper(0, 0, 0, 1,true, true, 0), request, messagesApi.preferred(request))));
    }

    /**
     * Endpoint for admin to view all admins
     *
     * @apiNote GET /admin/admins/:offset
     * @param request client http request
     * @param offset pagination offset
     * @return CompletionStage result of admin page
     */
    public CompletionStage<Result> showAdmins(Http.Request request, Integer offset) {
        return supplyAsync(() -> ok(admin.render(new ArrayList<Profile>(), getAdmins(), new ArrayList<Trip>(), new RoutedObject<Destination>(null, false, false),
                new ArrayList<Destination>(), new RoutedObject<Profile>(null, false, false), profileEditForm,
                null, profileCreateForm, null, new ArrayList<DestinationChange>(), new ArrayList<TreasureHunt>(),
                new RoutedObject<TreasureHunt>(null, false, false), Country.getInstance().getAllCountries(),
                undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), new ArrayList<Artist>(), new ArrayList<Artist>(),
                new RoutedObject<Artist>(null, true, false), genreRepository.getAllGenres(), new PaginationHelper(0, 0, 0, 0,true, true, 0), request, messagesApi.preferred(request))));
    }

    /**
     * Endpoint for admin to view all user destinations
     *
     * @apiNote GET /admin/destinations/:offset
     * @param request client http request
     * @param offset pagination offset
     * @return CompletionStage result of admin page
     */
    public CompletionStage<Result> showDestinations(Http.Request request, Integer offset) {
        return supplyAsync(() -> ok(admin.render(new ArrayList<Profile>(), new ArrayList<Profile>(), new ArrayList<Trip>(), new RoutedObject<Destination>(null, false, false),
                destinationRepository.getAllDestinations(), new RoutedObject<Profile>(null, false, false), profileEditForm,
                null, profileCreateForm, null, new ArrayList<DestinationChange>(), new ArrayList<TreasureHunt>(),
                new RoutedObject<TreasureHunt>(null, false, false), Country.getInstance().getAllCountries(),
                undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), new ArrayList<Artist>(), new ArrayList<Artist>(),
                new RoutedObject<Artist>(null, true, false), genreRepository.getAllGenres(), new PaginationHelper(0, 0, 0, 3,true, true, 0), request, messagesApi.preferred(request))));
    }

    /**
     * Endpoint for admin to view all user destination requests
     *
     * @apiNote GET /admin/destinations/requests/:offset
     * @param request client http request
     * @param offset pagination offset
     * @return CompletionStage result of admin page
     */
    public CompletionStage<Result> showDestinationRequests(Http.Request request, Integer offset) {
        return supplyAsync(() -> ok(admin.render(new ArrayList<Profile>(), new ArrayList<Profile>(), new ArrayList<Trip>(), new RoutedObject<Destination>(null, false, false),
                new ArrayList<Destination>(), new RoutedObject<Profile>(null, false, false), profileEditForm,
                null, profileCreateForm, null, destinationRepository.getAllDestinationChanges(), new ArrayList<TreasureHunt>(),
                new RoutedObject<TreasureHunt>(null, false, false), Country.getInstance().getAllCountries(),
                undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), new ArrayList<Artist>(), new ArrayList<Artist>(),
                new RoutedObject<Artist>(null, true, false), genreRepository.getAllGenres(), new PaginationHelper(0, 0, 0, 4,true, true, 0), request, messagesApi.preferred(request))));
    }

    /**
     * Endpoint for admin to view all user treasure hunts
     *
     * @apiNote GET /admin/hunts/:offset
     * @param request client http request
     * @param offset pagination offset
     * @return CompletionStage result of admin page
     */
    public CompletionStage<Result> showHunts(Http.Request request, Integer offset) {
        return supplyAsync(() -> ok(admin.render(new ArrayList<Profile>(), new ArrayList<Profile>(), new ArrayList<Trip>(), new RoutedObject<Destination>(null, false, false),
                new ArrayList<Destination>(), new RoutedObject<Profile>(null, false, false), profileEditForm,
                null, profileCreateForm, null, new ArrayList<DestinationChange>(), treasureHuntRepository.getAllTreasureHunts(),
                new RoutedObject<TreasureHunt>(null, false, false), Country.getInstance().getAllCountries(),
                undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), new ArrayList<Artist>(), new ArrayList<Artist>(),
                new RoutedObject<Artist>(null, true, false), genreRepository.getAllGenres(), new PaginationHelper(0, 0, 0, 5,true, true, 0), request, messagesApi.preferred(request))));
    }

    /**
     * Endpoint for admin to view all artists
     *
     * @apiNote GET /admin/artists/:offset
     * @param request client http request
     * @param offset pagination offset
     * @return CompletionStage result of admin page
     */
    public CompletionStage<Result> showArtists(Http.Request request, Integer offset) {
        return supplyAsync(() -> ok(admin.render(new ArrayList<Profile>(), new ArrayList<Profile>(), new ArrayList<Trip>(), new RoutedObject<Destination>(null, false, false),
                new ArrayList<Destination>(), new RoutedObject<Profile>(null, false, false), profileEditForm,
                null, profileCreateForm, null, new ArrayList<DestinationChange>(), new ArrayList<TreasureHunt>(),
                new RoutedObject<TreasureHunt>(null, false, false), Country.getInstance().getAllCountries(),
                undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), new ArrayList<Artist>(), artistRepository.getAllArtists(),
                new RoutedObject<Artist>(null, true, false), genreRepository.getAllGenres(), new PaginationHelper(0, 0, 0, 6,true, true, 0), request, messagesApi.preferred(request))));
    }

    /**
     * Endpoint for admin to view all artist requests
     *
     * @apiNote GET /admin/artists/requests/:offset
     * @param request client http request
     * @param offset pagination offset
     * @return CompletionStage result of admin page
     */
    public CompletionStage<Result> showArtistRequests(Http.Request request, Integer offset) {
        return supplyAsync(() -> ok(admin.render(new ArrayList<Profile>(), new ArrayList<Profile>(), new ArrayList<Trip>(), new RoutedObject<Destination>(null, false, false),
                new ArrayList<Destination>(), new RoutedObject<Profile>(null, false, false), profileEditForm,
                null, profileCreateForm, null, new ArrayList<DestinationChange>(), new ArrayList<TreasureHunt>(),
                new RoutedObject<TreasureHunt>(null, false, false), Country.getInstance().getAllCountries(),
                undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), artistRepository.getInvalidArtists(), new ArrayList<Artist>(),
                new RoutedObject<Artist>(null, true, false), genreRepository.getAllGenres(), new PaginationHelper(0, 0, 0, 7,true, true, 0), request, messagesApi.preferred(request))));
    }


    /**
     * Function to check if the long and lat are valid
     *
     * @param destination destination to check lat and long values
     * @return Boolean true if longitude and latitude are valid
     */
    private boolean longLatCheck(Destination destination) {
        if (destination.getLatitude() > 90 || destination.getLatitude() < -90) {
            return false;
        }
        return !(destination.getLongitude() > 180 || destination.getLongitude() < -180);
    }


    /**
     * Function to delete a profile with the given email from the database using the profile controller method
     *
     * @param request the request sent from the client
     * @param id      the id of the user who is to be deleted
     * @return a redirect to the admin page
     * @apiNote
     */
    public CompletionStage<Result> deleteProfile(Http.Request request, Integer id) {
        if (rolesRepository.getProfileIdFromRoleName("global_admin").contains(id)) {

            return supplyAsync(() -> (redirect("/admin").flashing("error",
                    "Global admin cannot be deleted.")));
        }
        undoStackRepository.addToStack(new UndoStack("profile", id, SessionController.getCurrentUserId(request)));
        return profileRepository.setSoftDelete(id, 1).thenApplyAsync(userEmail -> redirect("/admin").flashing("info",
                "Profile deleted successfully"));
    }


    /**
     * Endpoint method to retrieve profile data for the admin to view
     *
     * @param request the request sent from the client to view a given profile
     * @param id      the id of the profile to view
     * @return CompletionStage holding either a redirect or ok to the /admin page
     * @apiNote GET /admin/profile/:id/view
     */
    public CompletionStage<Result> viewProfile(Http.Request request, Integer id) {
        return profileRepository.findById(id).thenApplyAsync(profOpt -> {
            if (profOpt.isPresent()) {
                List<DestinationChange> destinationChangeList = destinationRepository.getAllDestinationChanges();
                List<Artist> artistList = artistRepository.getInvalidArtists();
                return ok(admin.render(profileRepository.getAll(), getAdmins(), Trip.find.all(), new RoutedObject<Destination>(null, false, false),
                        Destination.find.all(), new RoutedObject<Profile>(profOpt.get(), false, true), profileEditForm,
                        null, profileCreateForm, null, destinationChangeList, treasureHuntRepository.getAllTreasureHunts(),
                        new RoutedObject<TreasureHunt>(null, false, false), Country.getInstance().getAllCountries(),
                        undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), artistList, artistRepository.getAllArtists(),
                        new RoutedObject<Artist>(null, true, false), genreRepository.getAllGenres(), new PaginationHelper(0, 0, 0, 1,true, true, 0), request, messagesApi.preferred(request)));
            } else {
                return redirect("/admin");
            }
        });
    }


    /**
     * Create model for editing a users profile in the admin page
     *
     * @param request
     * @param id      of the profile to be edited
     * @return a redirect to the admin page
     * @apiNote
     */
    public CompletionStage<Result> showEditProfile(Http.Request request, Integer id) {
        return profileRepository.findById(id).thenApplyAsync(profileOpt -> {
            List<Profile> profiles = profileRepository.getAll();
            List<Trip> trips = tripRepository.getAll();
            List<Destination> destinations = destinationRepository.getAllDestinations();
            if (profileOpt.isPresent()) {
                Form<Profile> profileForm = profileEditForm.fill(profileOpt.get());
                List<DestinationChange> destinationChangeList = destinationRepository.getAllDestinationChanges();
                List<Artist> artistList = artistRepository.getInvalidArtists();
                return ok(admin.render(profiles, getAdmins(), trips, new RoutedObject<Destination>(null, false, false),
                        destinations, new RoutedObject<Profile>(profileOpt.get(), true, false), profileForm, null,
                        profileCreateForm, null, destinationChangeList, treasureHuntRepository.getAllTreasureHunts(),
                        new RoutedObject<TreasureHunt>(null, false, false), Country.getInstance().getAllCountries(),
                        undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), artistList, artistRepository.getAllArtists(),
                        new RoutedObject<Artist>(null, true, false), genreRepository.getAllGenres(), new PaginationHelper(0, 0, 0, 1,true, true, 0), request, messagesApi.preferred(request)));
            } else {
                return redirect("/admin").flashing("info", "User profile not found");
            }
        });

    }


    /**
     * Returns list of all the admins in the system
     *
     * @return list of all the admins in the system
     */
    private List<Profile> getAdmins() {
        List<Integer> adminIdList = rolesRepository.getProfileIdFromRoleName("admin");
        List<Profile> adminProfiles = new ArrayList<>();
        for (Integer id : adminIdList) {
            Profile profile = profileRepository.getExistingProfileByProfileId(id);
            if (profile != null) {
                rolesRepository.getProfileRoles(profile.getProfileId()).ifPresent(profile::setRoles);
                adminProfiles.add(profile);
            }
        }
        return adminProfiles;
    }


    /**
     * Endpoint method to show the admin page on the site
     *
     * @param request the http request
     * @return the rendered page with status ok
     * @apiNote /admin
     */
    public CompletionStage<Result> show(Http.Request request) {
        return supplyAsync(() -> {
            List<Profile> profiles = profileRepository.getAll();
            List<Trip> trips = tripRepository.getAll();
            List<Destination> destinations = destinationRepository.getAllDestinations();
            List<DestinationChange> destinationChangeList = destinationRepository.getAllDestinationChanges();
            List<UndoStack> adminUndoStackList = undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request));
            List<Artist> artistList = artistRepository.getInvalidArtists();
            return ok(admin.render(profiles, getAdmins(), trips, new RoutedObject<Destination>(null, false, false),
                    destinations, new RoutedObject<Profile>(null, false, false), profileEditForm,
                    null, profileCreateForm, null, destinationChangeList, treasureHuntRepository.getAllTreasureHunts(),
                    new RoutedObject<TreasureHunt>(null, false, false), Country.getInstance().getAllCountries(),
                    undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), artistList, artistRepository.getAllArtists(),
                    new RoutedObject<Artist>(null, true, false), genreRepository.getAllGenres(), new PaginationHelper(0, 0, 0, 3,true, true, 0), request, messagesApi.preferred(request)));
        });
    }


    /**
     * Updates a profile's attributes based on what is retrieved form the form via the admin
     *
     * @param request Http requestRequest
     * @param request Http request
     * @return a redirect to the profile page
     * @apiNote
     */
    public CompletionStage<Result> updateProfile(Http.Request request, Integer id) {
        Form<Profile> currentProfileForm = profileEditForm.bindFromRequest(request);
        Profile profile = currentProfileForm.get();
        profile.initProfile();
        profile.setNationalities(profile.getNationalities());
        profile.setPassports(profile.getPassports());

        return profileRepository.update(profile, id)
                .thenApplyAsync(x -> redirect(adminEndpoint)
                        , httpExecutionContext.current());
    }


    /**
     * Method to allow an admin to create a new user profile
     *
     * @param request
     * @return
     * @apiNote /admin/profile/create
     */
    public CompletionStage<Result> createProfile(Http.Request request) {
        Form<Profile> profileForm = profileCreateForm.bindFromRequest(request);
        Profile profile = profileForm.get();
        profile.initProfile();

        return profileRepository.insert(profile)
                .thenApplyAsync(email -> redirect(adminEndpoint)
                );
    }


    /**
     * Endpoint method to delete a trip from the database
     *
     * @param request the http request
     * @param tripId  the id of the trip to delete
     * @return a redirect to /admin
     * @apiNote /admin/trip/:tripId/delete
     */
    public CompletionStage<Result> deleteTrip(Http.Request request, Integer tripId) {
        undoStackRepository.addToStack(new UndoStack("trip", tripId, SessionController.getCurrentUserId(request)));
        System.out.println("yeet");
        return tripRepository.setSoftDelete(tripId, 1).thenApplyAsync(x -> redirect("/admin/trips/0")
                .flashing(
                        "info",
                        "Trip: " + tripId + " deleted")
        );
    }


    /**
     * Endpoint method allowing an admin to view a selected trip
     *
     * @param request the request sent to view the trip
     * @param tripId  the id of the trip to view
     * @return the admin page rendered with the view trip modal with status ok
     * @apiNote /admin/trips/:tripId/view
     */
    public CompletionStage<Result> viewTrip(Http.Request request, Integer tripId) {
        return supplyAsync(() -> {
            Trip trip = tripRepository.getTrip(tripId);
            return ok(admin.render(new ArrayList<Profile>(), new ArrayList<Profile>(), tripRepository.getPaginateTrip(0, pageSize), new RoutedObject<Destination>(null, false, false),
                    new ArrayList<Destination>(), new RoutedObject<Profile>(null, false, false), profileEditForm, trip,
                    profileCreateForm, null, new ArrayList<DestinationChange>(), new ArrayList<TreasureHunt>(),
                    new RoutedObject<TreasureHunt>(null, false, false), Country.getInstance().getAllCountries(),
                    undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), new ArrayList<Artist>(), new ArrayList<Artist>(),
                    new RoutedObject<Artist>(null, true, false), genreRepository.getAllGenres(), initialisePaginatior(0, tripRepository.getNumTrips()), request, messagesApi.preferred(request)));
        });
    }


    /**
     * Endpoint method allowing an admin to make another use an admin
     *
     * @param userId the id of the user to promote
     * @return the admin page rendered with the new admin
     * @apiNote /admin/:userId/admin
     */
    public Result makeAdmin(Integer userId) {
        String roleName = "admin";
        try {

            rolesRepository.setProfileRole(userId, roleName);
        } catch (DuplicateKeyException e) {

            return redirect("/admin").flashing("error",
                    "User already has this role.");
        }

        return redirect(adminEndpoint);
    }


    /**
     * Endpoint method allowing an admin to remove another use an admin
     *
     * @param userId the id of the user to promote
     * @return the admin page rendered with the admin removed
     * @apiNote /admin/:userId/admin/remove
     */
    public Result removeAdmin(Integer userId) {
        rolesRepository.removeRole(userId);
        return redirect(adminEndpoint);
    }


    /**
     * Endpoint method to delete a destination from the database
     *
     * @param request the heep request
     * @param destId  the id of the destination to delete
     * @return a redirect to /admin
     * @apiNote /admin/destinations/:destId/delete
     */
    public CompletionStage<Result> deleteDestination(Http.Request request, Integer destId) {
        return destinationRepository
                .checkDestinationExists(destId)
                .thenApplyAsync(
                        result -> {
                            if (result.isPresent()) {
                                return redirect(adminEndpoint)
                                        .flashing(
                                                "error",
                                                "Destination: "
                                                        + destId
                                                        + " is used within the following "
                                                        + result.get());
                            }
                            undoStackRepository.addToStack(new UndoStack("destination", destId, SessionController.getCurrentUserId(request)));
                            destinationRepository.setSoftDelete(destId, 1);
                            return redirect(adminEndpoint)
                                    .flashing(
                                            "info",
                                            "Destination: "
                                                    + destId
                                                    + " deleted");
                        });
    }


    /**
     * Endpoint method to get a destination object to the view to edit or view
     *
     * @param request the get request sent by the client
     * @param destId  the id of the destination to view
     * @param isEdit  boolean holding if the request is for an edit operation
     * @return CompletionStage holding result rendering the admin  page with the desired destination
     * @apiNote GET /admin/destinations/:destId?isEdit
     */
    public CompletionStage<Result> showDestination(Http.Request request, Integer destId, Boolean isEdit) {
        return supplyAsync(() -> {
            List<Profile> profiles = profileRepository.getAll();
            List<Trip> trips = Trip.find.all();
            List<Destination> destinations = destinationRepository.getAllDestinations();
            Destination currentDestination = destinationRepository.lookup(destId);
            RoutedObject<Destination> toSend = new RoutedObject<>(currentDestination, isEdit, !isEdit);
            if (isEdit) destinationEditForm.fill(currentDestination);
            List<DestinationChange> destinationChangeList = destinationRepository.getAllDestinationChanges();
            List<Artist> artistList = artistRepository.getInvalidArtists();
            return ok(admin.render(profiles, getAdmins(), trips, toSend, destinations, new RoutedObject<Profile>(null, true, false),
                    profileEditForm, null, profileCreateForm, destinationEditForm, destinationChangeList,
                    treasureHuntRepository.getAllTreasureHunts(), new RoutedObject<TreasureHunt>(null, false, false),
                    Country.getInstance().getAllCountries(), undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)),
                    artistList, artistRepository.getAllArtists(), new RoutedObject<Artist>(null, true, false),
                    genreRepository.getAllGenres(), new PaginationHelper(0, 0, 0, 3,true, true, 0), request, messagesApi.preferred(request)));
        });
    }


    /**
     * Endpoint method to save an admins edit of a destination
     *
     * @param request the clients request
     * @param destId  the id of teh destination to edit
     * @return CompletionStage holding redirect to the "/admin" page
     * @apiNote POST /admin/destinations/:destId
     */
    public CompletionStage<Result> editDestination(Http.Request request, Integer destId) {
        Form<Destination> destForm = destinationEditForm.bindFromRequest(request);
        Destination destination = destForm.get();
        Optional<String> destFormString = destForm.field("travellerTypesStringDest").value();
        destFormString.ifPresent(destination::setTravellerTypesStringDest);
        destination.initTravellerType();
        if (longLatCheck(destination)) {
            destinationRepository.update(destination, destId);
            return supplyAsync(() -> redirect(adminEndpoint).flashing("info", "Destination " + destination.getName() + " was edited successfully."));
        } else {
            return supplyAsync(() -> redirect(adminEndpoint).flashing("error", "A destinations longitude (-180 to 180) and latitude (90 to -90) must be valid"));
        }
    }


    /**
     * Endpoint method for an admin to add a new destination for a user
     *
     * @param request the client request to add a destination
     * @return CompletionStage holding redirect to the /admin page
     * @apiNote POST /admin/destinations
     */
    public CompletionStage<Result> addDestination(Http.Request request) {
        Form<Destination> destForm = destinationEditForm.bindFromRequest(request);
        String visible = destForm.field("visible").value().get();
        int visibility = (visible.equals("Public")) ? 1 : 0;
        Destination destination = destForm.value().get();
        destination.initTravellerType();
        destination.setVisible(visibility);

        return destinationRepository.insert(destination).thenApplyAsync(string -> redirect("/admin").flashing("info", "Destination " + destination.getName() + " added successfully"));
    }


    /**
     * Calls the destination repository method deleteDestinationChange to remove the selected change request once the
     * admin confirms the delete.
     * Method redirects to admin page with the a success message displayed
     *
     * @param request  http request
     * @param changeId Id of the change request the admin is removing
     * @return redirect with flashing success message
     */
    public CompletionStage<Result> rejectDestinationRequest(Http.Request request, Integer changeId) {
        return destinationRepository.deleteDestinationChange(changeId)
                .thenApplyAsync(x ->
                        redirect("/admin").flashing("info", "Destination change request successfully rejected")
                );
    }

    /**
     * Endpoint method for the admin to accept a change request on a destination
     *
     * @param request  request sent by admin to accept change
     * @param changeId database id of the change to accept
     * @return CompletionStage holding redirect to the admin page
     * @apiNote GET /admin/destinations/:id/request/accept
     */
    public CompletionStage<Result> acceptDestinationRequest(Http.Request request, Integer changeId) {
        return destinationRepository.acceptDestinationChange(changeId)
                .thenApplyAsync(x -> {
                    return redirect("/admin").flashing("info", "Destination change successfully accepted");
                });
    }


    /**
     * Endpoint method for the admin to create a new treasure hunt
     *
     * @param request the admins create request
     * @return CompletionStage redirecting back to the admin page
     */
    public CompletionStage<Result> createHunt(Http.Request request) {
        return supplyAsync(
                () -> {
                    Form<TreasureHunt> filledForm = huntForm.bindFromRequest(request);
                    Optional<TreasureHunt> huntOpt = filledForm.value();
                    if (huntOpt.isPresent()) {
                        TreasureHunt treasureHunt = huntOpt.get();
                        String destinationId = null;
                        String startDate = null;
                        String endDate = null;
                        int profileId = -1;
                        if (filledForm.field("endDate").value().isPresent()) {
                            endDate = filledForm.field("endDate").value().get();
                        }
                        if (filledForm.field("startDate").value().isPresent()) {
                            startDate = filledForm.field("startDate").value().get();
                        }
                        if (filledForm.field("destinationId").value().isPresent()) {
                            destinationId = filledForm.field("destinationId").value().get();
                        }
                        if (filledForm.field("profileId").value().isPresent()) {
                            profileId = Integer.parseInt(filledForm.field("profileId").value().get());
                        }
                        if (profileId != -1) {
                            treasureHunt.setTreasureHuntProfileId(profileId);
                        }
                        treasureHunt.setDestinationIdString(destinationId);
                        treasureHunt.setStartDateString(startDate);
                        treasureHunt.setEndDateString(endDate);

                        if (treasureHunt.getStartDate().after(treasureHunt.getEndDate())) {
                            return redirect(adminEndpoint).flashing("error", "Error: Start date cannot be after end date.");
                        }

                        treasureHuntRepository.insert(treasureHunt);
                    }
                    return redirect(adminEndpoint).flashing("info", "Treasure Hunt has been created.");
                });
    }


    /**
     * Endpoint method to handle a admin  request to edit a previously made treasure hunt
     *
     * @param request the admin request holding the treasure hunt form
     * @param id      Id of the treasure hunt to be edited
     * @return CompletionStage redirecting back to the treasure hunts page
     * @apiNote /admin/hunts/:id/edit
     */
    public CompletionStage<Result> editTreasureHunt(Http.Request request, Integer id) {
        Form<TreasureHunt> treasureHuntForm = huntForm.bindFromRequest(request);
        Integer profileId = SessionController.getCurrentUserId(request);
        Optional<String> treasureHuntFormString = treasureHuntForm.field("profileId").value();
        if (treasureHuntFormString.isPresent()) {
            profileId = Integer.parseInt(treasureHuntFormString.get());
        }
        TreasureHunt treasureHunt = treasureHuntController.setValues(profileId, treasureHuntForm);
        return supplyAsync(() -> {
            if (treasureHunt.getStartDate().after(treasureHunt.getEndDate())) {
                return redirect(adminEndpoint).flashing("error", "Error: Start date cannot be after end date.");
            }
            treasureHuntRepository.update(treasureHunt, id);
            return redirect(adminEndpoint).flashing("info", "Treasure Hunt has been updated.");
        });
    }


    /**
     * Endpoint method to get a hunt object to the view to edit
     *
     * @param request the get request sent by the client
     * @param id      the id of the treasure hunt to view
     * @return CompletionStage holding result rendering the admin  page with the desired hunt
     * @apiNote GET /admin/hunts/:id/edit/show
     */
    public CompletionStage<Result> showEditHunt(Http.Request request, Integer id) {
        return supplyAsync(() -> {
            List<DestinationChange> destinationChangeList = destinationRepository.getAllDestinationChanges();
            TreasureHunt hunt = treasureHuntRepository.lookup(id);
            List<Artist> artistList = artistRepository.getInvalidArtists();
//            return ok(admin.render(profileRepository.getAll(), getAdmins(), tripRepository.getAll(), new RoutedObject<Destination>(null, false, false),             destinationRepository.getAllDestinations(),
//                    new RoutedObject<Profile>(null, true, false), profileEditForm, null, profileCreateForm, destinationEditForm, destinationChangeList, treasureHuntRepository.getAllTreasureHunts(), new RoutedObject<TreasureHunt>(hunt, true, true),
//                    undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), request, messagesApi.preferred(request)));

            return ok(admin.render(profileRepository.getAll(), getAdmins(), tripRepository.getAll(),
                    new RoutedObject<Destination>(null, false, false), destinationRepository.getAllDestinations(),
                    new RoutedObject<Profile>(null, true, false), profileEditForm, null,
                    profileCreateForm, destinationEditForm, destinationChangeList, treasureHuntRepository.getAllTreasureHunts(),
                    new RoutedObject<TreasureHunt>(hunt, true, true), Country.getInstance().getAllCountries(),
                    undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)), artistList,
                    artistRepository.getAllArtists(), new RoutedObject<Artist>(null, true, false),
                    genreRepository.getAllGenres(), new PaginationHelper(0, 0, 0, 5,true, true, 0), request, messagesApi.preferred(request)));
        });
    }


    /**
     * Endpoint method for an admin to delete a treasure hunt
     *
     * @param request the admin request
     * @param id      the id of the treasure hunt to delete
     * @return CompletionStage holding redirect to the admin page
     */
    public CompletionStage<Result> deleteHunt(Http.Request request, Integer id) {
        undoStackRepository.addToStack(new UndoStack("treasure_hunt", id, SessionController.getCurrentUserId(request)));
        return treasureHuntRepository.setSoftDelete(id, 1)
                .thenApplyAsync(x -> redirect("/admin").flashing("info", "Treasure Hunt: " + id + " was deleted"));
    }

    /**
     * Endpoint method of an admin to undo a delete
     *
     * @param request the admin request
     * @return CompletionStage holding redirect to the admin page
     * @apiNote GET /admin/undo/
     */
    public CompletionStage<Result> undoTopOfStack(Http.Request request) {
        Integer profileId = SessionController.getCurrentUserId(request);
        return undoStackRepository.undoItemOnTopOfStack(profileId)
                .thenApplyAsync(x -> {
                    if (x == 1) {
                        return redirect("/admin").flashing("info", "Deletion is undone");
                    } else {
                        return redirect("/admin").flashing("info", "No changes to undo");
                    }
                });
    }


    /**
     * Endpoint method allowing the admin to verify an artist creation request
     *
     * @param request  the request sent from admin client
     * @param artistId the database id of the artist to verify
     * @return CompletionStage redirecting to admin page with flashing holding result of action
     * @apiNote POST /admin/artist/verify
     */
    public CompletionStage<Result> verifyArtist(Http.Request request, Integer artistId) {
        return artistRepository.setArtistAsVerified(artistId)
                .thenApplyAsync(x -> redirect("/admin").flashing("info", "Artist: " + artistId + " verified"));
    }


    /**
     * Endpoint method allowing the admin to decline an artist creation request
     *
     * @param request  the request sent from admin client
     * @param artistId the database id of the artist to verify
     * @return CompletionStage redirecting to admin page with flashing holding result of action
     * @apiNote POST /admin/artist/decline
     */
    public CompletionStage<Result> declineArtist(Http.Request request, Integer artistId) {
        return artistRepository.deleteArtist(artistId)
                .thenApplyAsync(x -> redirect("/admin").flashing("info", "Artist: " + artistId + " declined"));
    }


    /**
     * Method to call repository to save an artist profile to the database
     *
     * @param request
     * @return redirect to the
     */
    public CompletionStage<Result> createArtist(Http.Request request) {
        Form<Artist> artistProfileForm = artistForm.bindFromRequest(request);
        Optional<Artist> artistOpt = artistProfileForm.value();
        if (artistOpt.isPresent()) {
            Artist artist = artistOpt.get();
            artist.initCountry();
            return artistRepository.checkDuplicate(artist.getArtistName()).thenApplyAsync(duplicate -> {
                if (!duplicate) {
                    artistRepository.insert(artist).thenApplyAsync(artistId -> {

                        Optional<String> optionalProfiles = artistProfileForm.field("adminForm").value();
                        if (optionalProfiles.isPresent()) {
                            //Insert ArtistProfiles for new Artist.
                            for (String profileIdString : optionalProfiles.get().split(",")) {
                                Integer profileId = parseInt(profileIdString);
                                ArtistProfile artistProfile = new ArtistProfile(artistId, profileId);
                                artistRepository.insertProfileLink(artistProfile);
                            }
                        }

                        artistRepository.insertProfileLink(new ArtistProfile(SessionController.getCurrentUserId(request), artistId));
                        Optional<String> optionalGenres = artistProfileForm.field("genreForm").value();
                        if (optionalGenres.isPresent()) {
                            if (!optionalGenres.get().isEmpty()) {
                                for (String genre : optionalGenres.get().split(",")) {
                                    genreRepository.insertArtistGenre(artistId, parseInt(genre));
                                }
                            }
                        }

                        artistController.saveArtistCountries(artist, artistProfileForm);
                        return null;

                    });
                    return redirect("/admin").flashing("info", "Artist Profile : " + artist.getArtistName() + " created");
                } else {
                    return redirect("/admin").flashing("info", "Artist with the name " + artist.getArtistName() + " already exists!");
                }
            });
        }
        return supplyAsync(() -> redirect("/admin").flashing("info", "Artist Profile save failed"));
    }


    /**
     * Method to soft delete artist, append to stack and redirect to the admin page.
     *
     * @param request
     * @return redirect to admin page with flashing
     */
    public CompletionStage<Result> deleteArtist(Http.Request request, Integer artistId) {
        undoStackRepository.addToStack(new UndoStack("artist", artistId, SessionController.getCurrentUserId(request)));
        return artistRepository.setSoftDelete(artistId, 1).thenApplyAsync(x -> redirect(adminEndpoint)
                .flashing("info", "Artist: " + artistId + " deleted")
        );
    }

    /**
     * Method to render a page containing a routed object with a specific artist object
     * Used to load a particular artist object when editing an artist
     *
     * @param request
     * @param id      the id of the artist
     * @return a render of the page with the specified artist
     */
    public CompletionStage<Result> showEditArtist(Http.Request request, Integer id) {
        return supplyAsync(() -> {
            List<DestinationChange> destinationChangeList = destinationRepository.getAllDestinationChanges();
            Artist artist = artistRepository.getArtistById(id);
            artist = artistRepository.populateArtist(artist);
            if (artist.getGenreList() == null) {
                artist.setGenre(new ArrayList<>());
            }
            return ok(admin.render(profileRepository.getAll(), getAdmins(), tripRepository.getAll(),
                    new RoutedObject<Destination>(null, false, false), destinationRepository.getAllDestinations(),
                    new RoutedObject<Profile>(null, true, false), profileEditForm, null,
                    profileCreateForm, destinationEditForm, destinationChangeList, treasureHuntRepository.getAllTreasureHunts(),
                    new RoutedObject<TreasureHunt>(null, true, false), Country.getInstance().getAllCountries(),
                    undoStackRepository.getUsersStack(SessionController.getCurrentUserId(request)),
                    artistRepository.getInvalidArtists(), artistRepository.getAllArtists(),
                    new RoutedObject<Artist>(artist, true, true), genreRepository.getAllGenres(),
                    new PaginationHelper(0, 0, 0, 6,true, true, 0), request, messagesApi.preferred(request)));
        });
    }

    /**
     * Endpoint method to handle a admin request to edit an artist
     *
     * @param request the admin request holding the artist form
     * @param id      Id of the artist to be edited
     * @return CompletionStage redirecting back to the admin page
     * @apiNote /admin/artist/:id/edit
     */
    public CompletionStage<Result> editArtist(Http.Request request, Integer id) {
        Form<Artist> artistProfileForm = artistForm.bindFromRequest(request);
        Integer artistId = SessionController.getCurrentUserId(request);
        Integer currentUserId = SessionController.getCurrentUserId(request);
        Optional<String> artistFormString = artistProfileForm.field("artistId").value();
        if (artistFormString.isPresent()) {
            artistId = Integer.parseInt(artistFormString.get());
        }

        Artist artist = artistController.setValues(artistId, artistProfileForm);
        return supplyAsync(() -> {
            artistRepository.editArtistProfile(id, artist, artistProfileForm, currentUserId);
            return redirect(adminEndpoint).flashing("info", "Artist " + artist.getArtistName() + " has been updated.");
        });
    }
}
