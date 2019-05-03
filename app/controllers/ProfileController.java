package controllers;


import com.google.common.collect.TreeMultimap;
import models.Image;
import models.Profile;
import models.Trip;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.Files.TemporaryFile;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import repository.ImageRepository;
import repository.ProfileRepository;
import repository.TripRepository;
import views.html.editProfile;
import views.html.profile;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;


/**
 * This class is the controller for the profiles.scala.html file, it provides the route to the
 * profiles page
 */
public class ProfileController extends Controller {

    private final Form<Profile> profileForm;
    private final Form<ImageData> imageForm;
    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    private final FormFactory profileFormFactory;
    private final FormFactory imageFormFactory;
    private final ProfileRepository profileRepository;
    private final ImageRepository imageRepository;
    private byte[] imageBytes;
    private List<Image> imageList = new ArrayList<>();
    private static Boolean showPhotoModal = false;
    private final TripRepository tripRepository;
    private Image demoProfilePicture = null;



    /**
     * To get Image data upon upload
     */
    public static class ImageData {
        private String visible;
        private String isNewProfilePicture;
    }


    @Inject
    public ProfileController(FormFactory profileFormFactory, FormFactory imageFormFactory, MessagesApi messagesApi, HttpExecutionContext httpExecutionContext, ProfileRepository profileRepository, ImageRepository imageRepository, TripRepository tripRepository)
        {
            this.profileForm = profileFormFactory.form(Profile.class);
            this.imageForm = imageFormFactory.form(ImageData.class);
            this.messagesApi = messagesApi;
            this.httpExecutionContext = httpExecutionContext;
            this.profileFormFactory = profileFormFactory;
            this.imageFormFactory = imageFormFactory;
            this.profileRepository = profileRepository;
            this.tripRepository = tripRepository;
            this.imageRepository = imageRepository;

        }


    /**
     * Method to retrieve a users profile details and return a filled form to be edited.
     *
     * @param email String of the users email
     * @return a render of the editDestinations profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> showEdit (String email){
        return profileRepository.lookup(email).thenApplyAsync(optionalProfile -> {
            if (optionalProfile.isPresent()) {
                Profile toEditProfile = optionalProfile.get();
                Form<Profile> currentProfileForm = profileForm.fill(toEditProfile);
                return ok(editProfile.render(toEditProfile, currentProfileForm));

            } else {
                return notFound("Profile not found.");
            }
        }, httpExecutionContext.current());
    }


    /**
     * Updates a profile's attributes based on what is retrieved form the form
     *
     * @param request Http request
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> update (Http.Request request){
        Form<Profile> currentProfileForm = profileForm.bindFromRequest(request);
    System.out.println(currentProfileForm);
        Profile profile = currentProfileForm.get();

        // Could improve on this
        profile.setNationalities(profile.getNationalities().replaceAll("\\s",""));
        profile.setPassports(profile.getPassports().replaceAll("\\s",""));

        return profileRepository.update(profile, SessionController.getCurrentUser(request).getPassword(),
                SessionController.getCurrentUser(request).getEmail()).thenApplyAsync(x -> {
            return redirect(routes.ProfileController.show()).addingToSession(request, "connected", profile.getEmail());
        }, httpExecutionContext.current());
    }


    /**
     * Called by either the make or remove admin buttons to update admin privilege in database.
     *
     * @param request
     * @param email The email of the user who is having admin privilege updated
     * @return Result, redrects to the travellers page.
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> updateAdmin (Http.Request request, String email){

        return profileRepository.updateAdminPrivelege(email).thenApplyAsync(clickedEmail -> {
            return redirect("/travellers");
        }, httpExecutionContext.current());
    }


    /**
     * Call to ImageRepository to be insert an image in the database
     *
     * @param image Image object containing email, id, byte array of image and visible info
     * @return a redirect to the profile page
     */
    private Result savePhoto(Image image){
        imageRepository.insert(image);
        return redirect(routes.ProfileController.show());
    }


    /**
     * Method to convert image byte arrays into pictures and display them as the appropriate
     * content type
     *
     * @param id image id to be used as primary key to find image object
     */
    public Result displayPhotos (Integer id){
        Image image = Image.find.byId(id);
        return ok(Objects.requireNonNull(image).getImage()).as(image.getType());
    }


    /**
     * Retrieves file (image) upload from the front end and converts the image into bytes
     * A new Image object is created and has its attributes set. This image is then sent
     * to savePhoto. Logic is also used so that if the newly uploaded photo is a new
     * profile picture then it will set it as a profile picture
     *
     * @param request Https request
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> uploadPhoto (Http.Request request){
        Http.MultipartFormData<TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<TemporaryFile> picture = body.getFile("image");

        Form<ImageData> uploadedImageForm = imageForm.bindFromRequest(request);
        ImageData imageData = uploadedImageForm.get();

        if (picture == null) {
            return supplyAsync(() -> redirect("/profile").flashing("invalid", "No image selected."));

        }

        String fileName = picture.getFilename(); // long fileSize = picture.getFileSize();
        String contentType = picture.getContentType();

        // Check valid content type for image
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/gif")) {
            return supplyAsync(() -> redirect("/profile").flashing("invalid", "Invalid file type!"));
        }

        TemporaryFile tempFile = picture.getRef();
        File file = tempFile.path().toFile();

        return supplyAsync(() -> {
            try {
                Profile currentUser = SessionController.getCurrentUser(request);
                this.imageBytes = Files.readAllBytes(file.toPath());
                int visibility = (imageData.visible.equals("Public")) ? 1 : 0; // Set visibility
                // Initialize Image object
                Image image = new Image(currentUser.getEmail(), this.imageBytes, contentType,
                        visibility, fileName);
                savePhoto(image); // Save photo, given a successful upload
                int isProfilePicture = (imageData.isNewProfilePicture.equals("true")) ? 1 : 0;
                if (isProfilePicture == 0) { //case not setting as the new profile picture
                    showPhotoModal = true;
                } else {
                    //TODO set the picture as the new profile picture and show modal/redirect to appropriate place on page
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Redirect user to profile page to show state change
            return ok();
        }).thenApply(result -> redirect("/profile").flashing("success", "Image uploaded."));
    }

    /**
     * Selects an image and sets it as the demo profile picture so that a user can see what that
     * picture may look like if it was truly set as the profile picture
     * @param request https reuquest
     * @return a redirect to the profile page
     */
    public Image getDemoProfilePicture(Http.Request request) {
        if (demoProfilePicture == null) {
            resetDemoProfilePicture(request);
        }
        return demoProfilePicture;
    }

    /**
     * resets the demo profile picture to the original profile picture
     * @param request
     */
    public void resetDemoProfilePicture(Http.Request request) {
        Profile currentUser = SessionController.getCurrentUser(request);
        Optional<List<Image>> imagesList  = imageRepository.getImages(currentUser.getEmail());
        List<Image> usersImages = imagesList.get();
        this.demoProfilePicture = usersImages.get(0);
    }


    /**
     * Inserts an Image object into the ImageRepository to be stored on the database
     *
     * @param id Image object containing email, id, byte array of images and visible info
     * @return a redirect to the profile page.
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> updatePrivacy (Integer id){
        try {
            imageRepository.updateVisibility(id);
            showPhotoModal = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return supplyAsync(() -> redirect("/profile").flashing("success", "Visibility updated."));
    }

    /**
     * Method to query the image repository to retrieve all images uploaded for a
     * logged in user.
     *
     * @param request Https request
     * @return a list of image objects
     */
    private List<Image> getUserPhotos(Http.Request request){
        Profile profile = SessionController.getCurrentUser(request);
        try {
            Optional<List<Image>> imageListTemp = imageRepository.getImages(profile.getEmail());
            imageList = imageListTemp.get();
        } catch (NoSuchElementException e) {
            imageList = new ArrayList<Image>();
        }
        return imageList;
    }


    /**
     * Show the profile page
     * @param request The http request
     * @return a page render of the users profile page
     */
    @Security.Authenticated(SecureSession.class)
    public Result show (Http.Request request){
        Profile currentProfile = SessionController.getCurrentUser(request);
        List<Image> displayImageList = getUserPhotos(request);
        // Get the current show photo modal state
        // Ensure state is false for next refresh action
        Boolean show = showPhotoModal = false;
        TreeMultimap<Long, Integer> tripsMap = SessionController.getCurrentUser(request).getTrips();
        List<Integer> tripValues= new ArrayList<>(tripsMap.values());
        Image demoProfilePicture = getDemoProfilePicture(request);
        return ok(profile.render(currentProfile, imageForm, displayImageList, show, tripValues, demoProfilePicture, request, messagesApi.preferred(request)));
    }

}

