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
import views.html.createDestinations;
import views.html.destinations;
import views.html.editDestinations;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * This class is the controller for the destinations.scala.html file, it provides the route to the
 * destinations page and the method that the page uses.
 */
public class DestinationsController extends Controller {

    private MessagesApi messagesApi;
    private List<Destination> destinationsList = new ArrayList<>();
    private List<Integer> followedDestinationIds = new ArrayList<>();
    private final Form<Destination> form;
    private final DestinationRepository destinationRepository;
    private final TripDestinationsRepository tripDestinationsRepository;
    private final ProfileRepository profileRepository;
    private final PersonalPhotoRepository personalPhotoRepository;
    private final DestinationPhotoRepository destinationPhotoRepository;
    private final PhotoRepository photoRepository;
    private final Form<DestinationRequest> requestForm;
    private final DestinationTravellerTypeRepository destinationTravellerTypeRepository;
    private final TravellerTypeRepository travellerTypeRepository;
    private String destShowRoute = "/destinations/show/false";

    /**
     * Constructor for the destination controller class
     *
     * @param formFactory
     * @param messagesApi
     * @param destinationRepository
     * @param profileRepository
     */
    @Inject
    public DestinationsController(FormFactory formFactory, MessagesApi messagesApi, DestinationRepository destinationRepository,
                                  ProfileRepository profileRepository, TripDestinationsRepository tripDestinationsRepository,
                                  PersonalPhotoRepository personalPhotoRepository, DestinationPhotoRepository destinationPhotoRepository,
                                  PhotoRepository photoRepository, DestinationTravellerTypeRepository destinationTravellerTypeRepository,
                                  TravellerTypeRepository travellerTypeRepository) {
        this.form = formFactory.form(Destination.class);
        this.messagesApi = messagesApi;
        this.destinationRepository = destinationRepository;
        this.profileRepository = profileRepository;
        this.tripDestinationsRepository = tripDestinationsRepository;
        this.personalPhotoRepository = personalPhotoRepository;
        this.destinationPhotoRepository = destinationPhotoRepository;
        this.photoRepository = photoRepository;
        this.destinationTravellerTypeRepository = destinationTravellerTypeRepository;
        this.travellerTypeRepository = travellerTypeRepository;
        this.requestForm = formFactory.form(DestinationRequest.class);
    }

    /**
     * Displays a page showing the destinations to the user
     *
     * @param request
     * @return the list of destinations
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> show(Http.Request request, boolean isPublic) {
        destinationsList.clear();
        Integer userId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(userId).thenApplyAsync(profile -> {
            if (profile.isPresent()) {
                if (isPublic) {
                    ArrayList<Destination> destListTemp = destinationRepository.getPublicDestinations();
                    try {
                        destinationsList = destListTemp;
                    } catch (NoSuchElementException e) {
                        destinationsList = new ArrayList<>();
                    }
                } else {
                    profileRepository.getDestinations(userId).ifPresent(dests -> destinationsList.addAll(dests));
                    destinationRepository.getFollowedDestinations(userId).ifPresent(follows -> destinationsList.addAll(follows));
                }
                destinationRepository.getFollowedDestinationIds(userId).ifPresent(ids -> followedDestinationIds = ids);
                destinationsList = loadCurrentUserDestinationPhotos(profile.get().getProfileId(), destinationsList);
                destinationsList = loadWorldDestPhotos(profile.get().getProfileId(), destinationsList);
                destinationsList = loadTravellerTypes(destinationsList);
                List<Photo> usersPhotos = getUsersPhotos(profile.get().getProfileId());
                return ok(destinations.render(destinationsList, profile.get(), isPublic, followedDestinationIds, usersPhotos, form, new RoutedObject<Destination>(null, false, false), requestForm, request, messagesApi.preferred(request)));
            } else {
                return redirect(destShowRoute);
            }
        });
    }


    /**
     * Endpoint method to update the privacy of a photo
     *
     *
     * @param id the photo id
     * @return Redirect to teh destinations page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> updatePhotoPrivacy(Integer id){
        return supplyAsync(() -> {
            photoRepository.updateVisibility(id);
            return redirect("/destinations/show/false").flashing("success", "Visibility updated.");
        });
    }


    /**
     * Method to follow a destination called from the destinations page and used from an endpoint
     *
     * @param profileId Id of the profile to follow destination
     * @param destId    Id of the destination to be followed
     * @param isPublic  Boolean of the destination if is public or not
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> follow(Http.Request request, Integer profileId, int destId, boolean isPublic) {
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            if (profile.isPresent()) {
                destinationRepository.followDestination(destId, profileId).ifPresent(ids -> followedDestinationIds = ids);
                destinationsList = loadCurrentUserDestinationPhotos(profileId, destinationsList);
                destinationsList = loadWorldDestPhotos(profileId, destinationsList);
                destinationsList = loadTravellerTypes(destinationsList);
                List<Photo> usersPhotos = getUsersPhotos(profile.get().getProfileId());
                return ok(destinations.render(destinationsList, profile.get(), isPublic, followedDestinationIds, usersPhotos, form, new RoutedObject<Destination>(null, false, false), requestForm, request, messagesApi.preferred(request)));
            } else {
                return redirect(destShowRoute);
            }
        });
    }


    /**
     * Endpoint method to get a destination object to the view to edit or view
     *
     * @param request the get request sent by the client
     * @param destId  the id of the destination to view
     * @return CompletionStage holding result rendering the admin  page with the desired destination
     * @apiNote GET /admin/destinations/:destId?isEdit
     */
    public CompletionStage<Result> showDestinationEdit(Http.Request request, Integer destId,  boolean isPublic) {
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            if (profile.isPresent()) {
                destinationsList = loadCurrentUserDestinationPhotos(profId, destinationsList);
                destinationsList = loadWorldDestPhotos(profId, destinationsList);
                destinationsList = loadTravellerTypes(destinationsList);
                List<Photo> usersPhotos = getUsersPhotos(profile.get().getProfileId());
                Destination currentDestination = destinationRepository.lookup(destId);
                destinationTravellerTypeRepository.getDestinationTravellerList(destId).ifPresent(currentDestination::setTravellerTypes);

                RoutedObject<Destination> toSend = new RoutedObject<>(currentDestination, true, false);
                form.fill(currentDestination);
                return ok(destinations.render(destinationsList, profile.get(), isPublic, followedDestinationIds, usersPhotos, form, toSend, requestForm, request, messagesApi.preferred(request)));
            } else {
                return redirect(destShowRoute);
            }
        });
    }

    /**
     * Method to unfollow a destination called from the destinations page and used from an endpoint
     *
     * @param profileId Id of the profile to unfollow destination
     * @param destId    Id of the destination to be unfollowed
     * @param isPublic  Boolean of the destination if is public or not
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> unfollow(Http.Request request, Integer profileId, int destId, boolean isPublic) {
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            if (profile.isPresent()) {
                Optional<ArrayList<Integer>> followedTemp = destinationRepository.unfollowDestination(destId, profileId);
                try {
                    followedDestinationIds = followedTemp.get();
                } catch (NoSuchElementException e) {
                    followedDestinationIds = new ArrayList<>();
                }
                if (!isPublic) {
                    Optional<ArrayList<Destination>> destListTemp = profileRepository.getDestinations(profileId);
                    Optional<ArrayList<Destination>> followedListTemp = destinationRepository.getFollowedDestinations(profileId);
                    try {
                        destinationsList = destListTemp.get();
                        destinationsList.addAll(followedListTemp.get());
                    } catch (NoSuchElementException e) {
                        destinationsList = new ArrayList<>();
                    }
                }

                destinationsList = loadCurrentUserDestinationPhotos(profileId, destinationsList);
                destinationsList = loadWorldDestPhotos(profileId, destinationsList);
                destinationsList = loadTravellerTypes(destinationsList);
                List<Photo> usersPhotos = getUsersPhotos(profile.get().getProfileId());
                return ok(destinations.render(destinationsList, profile.get(), isPublic, followedDestinationIds, usersPhotos, form, new RoutedObject<Destination>(null, false, false), requestForm, request, messagesApi.preferred(request)));
            } else {
                return redirect(destShowRoute);
            }
        });
    }

    /**
     * takes in a list of destinations, for each destination loads the photos which are linked to that destination and
     * owned by the current user into destination.usersPhotos
     * @param destinationsList
     * @return destinationsList
     */
    private List<Destination> loadCurrentUserDestinationPhotos(int profileId, List<Destination> destinationsList) {
        Optional<List<Photo>> imageList = personalPhotoRepository.getAllProfilePhotos(profileId);
        if (imageList.isPresent()) {
            for (Destination destination : destinationsList) {
                List<Photo> destPhotoList = new ArrayList<>();
                for (Photo photo : imageList.get()) {
                    if (destinationPhotoRepository.findByProfileIdPhotoIdDestId(profileId, photo.getPhotoId(), destination.getDestinationId()).isPresent()) {
                        destPhotoList.add(photo);
                    }
                }
                destination.setUsersPhotos(destPhotoList);
            }
            return destinationsList;
        }
        return destinationsList;
    }


    /**
     * takes in a list of destinations, for each destination loads the photos which are linked to that destination but
     * not from the current user. ie: the world destination photos
     * @param profileId, the id of the current user, will not add their destination photos to the list
     * @param destinationsList, a list of the destinations which it will be adding photos to
     * @return destinations list that was passed in
     */
    private List<Destination> loadWorldDestPhotos(int profileId, List<Destination> destinationsList) {
        Optional<List<Photo>> optionalImageList = destinationPhotoRepository.getAllDestinationPhotos();
        if (optionalImageList.isPresent()) {
            List<Photo> photoList = optionalImageList.get();
            for (Destination destination : destinationsList) {
                List<Photo> destPhotoList = new ArrayList<>();
                for (Photo photo : photoList) {
                    if (destinationPhotoRepository.isLinkedToDestByOtherUser(profileId, photo.getPhotoId(), destination.getDestinationId())) {
                        destPhotoList.add(photo);
                    }
                }
                destination.setWorldPhotos(destPhotoList);
            }
            return destinationsList;
        }
        return destinationsList;
    }


    /**
     * Takes in a list of destinations and loads (sets) traveller types into each of them.
     * Used for displaying the destination traveller types on the destination page
     * @param destinationsList
     * @return the same list of destinations
     */
    private List<Destination> loadTravellerTypes(List<Destination> destinationsList) {
        for (Destination destination: destinationsList) {
            List<TravellerType> travellerTypes = destinationRepository.getDestinationsTravellerTypes(destination.getDestinationId());
            Map<Integer, TravellerType> travellerTypesMap = new HashMap<>();
            for (TravellerType i : travellerTypes) {
                travellerTypesMap.put(i.getTravellerTypeId(), i);
            }
            destination.setTravellerTypes(travellerTypesMap);
        }
        return destinationsList;
    }

    /**
     * Gets all of the users photos
     *
     * @param id the id of the user profile
     * @return destinations, list of all user destinations
     */
    public ArrayList<Destination> getUserDestinations(int id) {
        return new ArrayList<>(Destination.find.query()
                .where()
                .eq("profile_id", id)
                .findList());
    }


    /**
     * Gets a list of all of a users photos
     *
     * @param profileId id of user to get photos for
     * @return the list of photos found
     */
    private List<Photo> getUsersPhotos(int profileId) {
        Optional<List<Photo>> imageList = personalPhotoRepository.getAllProfilePhotos(profileId);
        return imageList.orElseGet(ArrayList::new);
    }


    /**
     * Displays a page to create a destination
     *
     * @param request
     * @return redirect
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> showCreate(Http.Request request) {
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            if (profile.isPresent()) {
                Destination dest = new Destination();
                dest.setLatitude(0.0);
                dest.setLongitude(0.0);
                Form<Destination> destinationForm = form.fill(dest);
                return ok(createDestinations.render(destinationForm, profile.get(), request, messagesApi.preferred(request)));
            } else {
                return redirect(destShowRoute);
            }
        });
    }

    /**
     * This method displays the editDestinations page for the destinations to the user
     *
     * @param request
     * @param id      of the destination
     * @return redirect to destination
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> edit(Http.Request request, Integer id) {
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            if (profile.isPresent()) {
                Destination destination = new Destination();
                for (Destination dest : destinationsList) {
                    if (dest.getDestinationId() == id) {
                        destination = dest;
                        break;
                    }
                }
                Form<Destination> destinationForm = form.fill(destination);
                return ok(editDestinations.render(id, destination, destinationForm, profile.get(), request, messagesApi.preferred(request)));
            } else {
                return redirect(destShowRoute);
            }
        });
    }

    /**
     * This method updates destination in the database
     *
     * @param request
     * @param id      The ID of the destination to editDestinations.
     * @return redirect
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> update(Http.Request request, Integer id) {
        Integer userId = SessionController.getCurrentUserId(request);
        Form<Destination> destinationForm = form.bindFromRequest(request);
        String visible = destinationForm.field("visible").value().get();
        int visibility = Integer.parseInt(visible);
        Destination dest = destinationForm.value().get();
        dest.setTravellerTypesStringDest(destinationForm.field("travellerTypesForm").value().get());
        dest.initTravellerType();
        dest.setVisible(visibility);
            if (destinationRepository.checkValidEdit(dest, userId, destinationRepository.lookup(id))) {
            return supplyAsync(() -> redirect("/destinations/" + id + "/edit").flashing("failure", "This destination is already registered and unavailable to create"));
        }
        if (longLatCheck(dest)) {
            return destinationRepository.update(dest, id).thenApplyAsync(destId -> {
                if (visibility == 1 && destId.isPresent()) {
                    dest.setDestinationId(destId.get());
                    newPublicDestination(dest);
                }
                return redirect(destShowRoute).flashing("success", "Destination: " + id + " updated");
            });
        } else {
            return supplyAsync(() -> redirect("/destinations/" + id + "/edit").flashing("failure", "A destinations longitude(-180 to 180) and latitude(90 to -90) must be valid"));
        }
    }

    /**
     * Adds a new destination to the database
     *
     * @param request
     * @return redirect
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> saveDestination(Http.Request request) {
        Integer userId = SessionController.getCurrentUserId(request);
        Form<Destination> destinationForm = form.bindFromRequest(request);
        String visible = destinationForm.field("visible").value().get();
        int visibility = (visible.equals("Public")) ? 1 : 0;
        Destination destination = destinationForm.value().get();
        destination.initTravellerType();
        destination.setProfileId(userId);
        destination.setVisible(visibility);

        if (destinationRepository.checkValidEdit(destination, userId, null)) {
            return supplyAsync(() -> redirect("/destinations/show/false").flashing("failure", "This destination is already registered and unavailable to create"));
        }
        if (longLatCheck(destination)) {
            return destinationRepository.insert(destination).thenApplyAsync(destId -> {
                if (visibility == 1 && destId.isPresent()) {
                    destination.setDestinationId(destId.get());
                    newPublicDestination(destination);
                }
                return redirect(destShowRoute).flashing("success", "Destination added successfully");
            });
        } else {
            return supplyAsync(() -> redirect("/destinations/show/false").flashing("failure", "A destinations longitude(-180 to 180) and latitude(90 to -90) must be valid"));
        }
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
     * Deletes a destination in the database
     *
     * @param id ID of the destination to delete
     * @return a redirect to the destinations page
     */
    public CompletionStage<Result> delete(Http.Request request, Integer id) {
        return tripDestinationsRepository.checkDestinationExists(id).thenApplyAsync(result -> {
            if (result.isPresent()) {
                return redirect(destShowRoute).flashing("failure", "Destination: " + id +
                        " is used within the following trips: " + result.get());
            }
            destinationRepository.delete(id);
            return redirect(destShowRoute).flashing("success", "Destination: " + id + " deleted");
        });
    }

    /**
     * This function will inspect all private destinations for all users and swap any private destinations for the
     * new public destination if they are the same.
     *
     * @param newPublicDestination, the new private destination
     */
    private void newPublicDestination(Destination newPublicDestination) {
        Optional<List<Destination>> destinationList = destinationRepository.checkForSameDestination(newPublicDestination);
        if (destinationList.isPresent()) {
            for (Destination destination : destinationList.get()) {
                if (destination.getDestinationId() != newPublicDestination.getDestinationId()) {
                    destinationRepository.followDestination(newPublicDestination.getDestinationId(), destination.getProfileId());
                    Optional<List<TripDestination>> tripDestinationList = tripDestinationsRepository.getTripDestsWithDestId(destination.getDestinationId());
                    if (tripDestinationList.isPresent()) {
                        for (TripDestination tripDestination : tripDestinationList.get()) {
                            tripDestinationsRepository.editTripId(tripDestination, newPublicDestination.getDestinationId());
                        }
                    }
                    destinationRepository.delete(destination.getDestinationId());
                }
            }
        }
    }


    /**
     * Method to link photo to a given destination
     * @param photoId Id of the photo to be linked
     * @param destinationId Id of the destination that needs to be linked to a photo
     * @return completion stage result redirected to destination
     */
    public CompletionStage<Result> linkPhotoToDestination(Http.Request request, Integer photoId, Integer destinationId) {
        Integer userId = SessionController.getCurrentUserId(request);
        DestinationPhoto destinationPhoto = new DestinationPhoto(userId, photoId, destinationId);
        return destinationPhotoRepository.insert(destinationPhoto).thenApplyAsync(result -> {
            if (result.isPresent()) {
                return redirect(destShowRoute).flashing("success", "Photo was successfully linked to destination");
            }
            return redirect(destShowRoute).flashing("failure", "Photo was unsuccessfully linked to destination");
        });
    }

    /**
     * Method to un-link photo to a given destination
     * @param photoId Id of the photo to be un-linked
     * @param destinationId Id of the destination that needs to be un-linked to a photo
     * @return completion stage result redirected to destination
     */
    public CompletionStage<Result> unlinkPhotoFromDestination(Http.Request request, Integer photoId, Integer destinationId) {
        Integer userId = SessionController.getCurrentUserId(request);
        Optional<DestinationPhoto> destinationPhoto = destinationPhotoRepository.findByProfileIdPhotoIdDestId(userId, photoId, destinationId);
        return destinationPhotoRepository.delete(destinationPhoto.get().getDestinationPhotoId()).thenApplyAsync(result -> {
            if (result.isPresent()) {
                return redirect(destShowRoute).flashing("success", "Photo was successfully unlinked from destination");
            }
            return redirect(destShowRoute).flashing("failure", "Photo was unsuccessfully unlinked from destination");
        });

    }

    /**
     * Method to create the requests for changing destination traveller types once user has selected the changes \
     * they wish to make.
     * @param profileId id of the profile who is requesting the change
     * @param destinationId id of the destination which the user is trying to change
     * @param toAdd list of traveller types the user wishes to add
     * @param toRemove list of traveller type the user wishes to delete
     */
    private void createChangeRequest(Integer profileId, Integer destinationId, List<Integer> toAdd, List<Integer> toRemove){
        DestinationRequest destinationRequest = new DestinationRequest(destinationId, profileId);
        destinationRepository.createDestinationTravellerTypeChangeRequest(destinationRequest).thenApplyAsync(requestId -> {
            if (!toAdd.isEmpty()) {
                destinationRepository.travellerTypeChangesTransaction(requestId, 1, toAdd);
            }
            if (!toRemove.isEmpty()){
                destinationRepository.travellerTypeChangesTransaction(requestId, 0, toRemove);
            }
            return null;
            });
    }


    /**
     * Endpoint method for a user to create a destination edit request
     *
     * @apiNote POST /destinations/type/request
     * @param request the user request to edit the destination
     * @return CompletionStage redirecting to the destinations page
     */
    public CompletionStage<Result> createEditRequest(Http.Request request) {
        return supplyAsync(() -> {
            int profileId = SessionController.getCurrentUserId(request);
            Form<DestinationRequest> changeForm = requestForm.bindFromRequest(request);
            List<Integer> toAdd = listOfTravellerTypesToTravellerTypeId(changeForm.get().getToAddList());
            List<Integer> toRemove = listOfTravellerTypesToTravellerTypeId(changeForm.get().getToRemoveList());
            createChangeRequest(profileId,changeForm.get().getDestinationId(),toAdd,toRemove);
           return redirect("/destinations/show/false").flashing("success", "Request sent.");
        });
    }

    /**
     * Helper funciton to turn List<String> into List<Int> holding the travellerType id's of the given traveller type name
     *
     * @param names List of traveller type names
     * @return List of traveller type id's corresponding to the given names
     */
    private List<Integer> listOfTravellerTypesToTravellerTypeId(List<String> names){
        if (names.get(0).equals("")){
            return Collections.emptyList();
        } else {
            List<Integer> result = new ArrayList<>();
            for (String name : names){
                travellerTypeRepository.getTravellerTypeId(name).ifPresent(result::add);
            }
            return result;
        }
    }
}
