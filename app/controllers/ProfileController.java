package controllers;


import com.google.common.collect.TreeMultimap;
import models.Destination;
import models.PersonalPhoto;
import models.Photo;
import models.Profile;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.Files.TemporaryFile;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import repository.*;
import utility.Thumbnail;
import views.html.profile;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private List<Destination> destinationsList = new ArrayList<>();
    private final HttpExecutionContext httpExecutionContext;
    private final ProfileRepository profileRepository;
    private final PhotoRepository photoRepository;
    private List<Photo> photoList = new ArrayList<>();
    private Photo demoProfilePicture = null;
    private static boolean showPhotoModal = false;
    private PersonalPhotoRepository personalPhotoRepository;
    private final TripRepository tripRepository;
    private final ProfileTravellerTypeRepository profileTravellerTypeRepository;
    private final String profileEndpoint = "/profile";




    /**
     * A class used to recieve information from a form for uploading an image
     */
    public static class ImageData {
        public String visible = "Private";
        String isNewProfilePicture;
    }


    @Inject
    public ProfileController(FormFactory profileFormFactory, FormFactory imageFormFactory, MessagesApi messagesApi, PersonalPhotoRepository personalPhotoRepository,
            HttpExecutionContext httpExecutionContext, ProfileRepository profileRepository, PhotoRepository photoRepository, TripRepository tripRepository, ProfileTravellerTypeRepository profileTravellerTypeRepository)
        {
            this.profileForm = profileFormFactory.form(Profile.class);
            this.imageForm = imageFormFactory.form(ImageData.class);
            this.messagesApi = messagesApi;
            this.httpExecutionContext = httpExecutionContext;
            this.profileRepository = profileRepository;
            this.photoRepository = photoRepository;
            this.personalPhotoRepository = personalPhotoRepository;
            this.tripRepository = tripRepository;
            this.profileTravellerTypeRepository = profileTravellerTypeRepository;
        }


    /**
     * Updates a profile's attributes based on what is retrieved form the form
     * @apiNote POST /profile
     * @param request Http request
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> update (Http.Request request){
        Integer profId = SessionController.getCurrentUserId(request);
        Form<Profile> currentProfileForm = profileForm.bindFromRequest(request);
        Profile profileNew = currentProfileForm.get();
        profileNew.initProfile();
        return profileRepository.update(profileNew, profId).thenApplyAsync(x -> {
            return redirect(routes.ProfileController.show()).addingToSession(request, "connected", profId.toString());
        }, httpExecutionContext.current());
    }


    /**
     * Called by either the make or remove admin buttons to update admin privilege in database.
     *
     * @param request
     * @param id The id of the user who is having admin privilege updated
     * @return Result, redrects to the travellers page.
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> updateAdmin (Http.Request request, Integer id){
        return profileRepository.updateAdminPrivelege(id).thenApplyAsync(profileId -> redirect("/travellers")
        , httpExecutionContext.current());
    }


    /**
     * Method to query the image repository to retrieve all images uploaded for a
     * logged in user.
     *
     * @param request Https request
     * @return a list of image objects
     */
    @Security.Authenticated(SecureSession.class)
    private List<Photo> getUserPhotos(Http.Request request){
        Integer profileId = SessionController.getCurrentUserId(request);
        Optional<List<Photo>> imageListTemp = personalPhotoRepository.getAllProfilePhotos(profileId);
        imageListTemp.ifPresent(photos -> photoList = photos);
        return photoList;
    }


    /**
     * Inserts an Photo object into the PhotoRepository to be stored on the database
     *
     * @apiNote GET /profile/edit/photo/:id
     * @param id Photo object containing email, id, byte array of images and visible info
     * @return a redirect to the profile page.
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> updatePrivacy (Integer id){
        return supplyAsync(() -> {
            photoRepository.updateVisibility(id);
            return redirect("/profile").flashing("success", "Visibility updated.");
        });
    }


    /**
     * Retrieves file (image) upload from the front end and converts the image into bytes
     * A new Photo object is created and has its attributes set. If the photo is a normal
     * upload it will be sent to savePhoto and the modal showPhoto is loaded. If the photo
     * is a potential new profile picture it will not be saved but will be set as the demo
     * profile picture and the modal changeProfilePicture is shown
     *
     * @param request Https request
     * @return a redirect to the profile page with a flashing response message
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> uploadPhoto(Http.Request request) {
        Http.MultipartFormData<TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<TemporaryFile> picture = body.getFile("image");
        Form<ImageData> uploadedImageForm = imageForm.bindFromRequest(request);
        ImageData imageData = uploadedImageForm.get();
        uploadedImageForm.field("isNewProfilePicture").value().ifPresent(val -> imageData.isNewProfilePicture = val);
        if (picture == null) {
            return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "No image selected."));

        }
        String fileName = picture.getFilename();
        String contentType = picture.getContentType();
        Long fileSize = picture.getFileSize();

        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/gif")) {
            return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "Invalid file type!"));
        }

        if (fileSize >= 8000000) {
            return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "File size must not exceed 8 MB!"));
        }

        TemporaryFile tempFile = picture.getRef();
        String filepath = System.getProperty("user.dir") + "/photos/personalPhotos/" + fileName;
        tempFile.copyTo(Paths.get(filepath), true);

        return supplyAsync(() -> {
            try {
                int visibility = (imageData.visible.equals("Public")) ? 1 : 0; // Set visibility
                Photo photo = new Photo("photos/personalPhotos/" + fileName, contentType, visibility, fileName);
                savePhoto(photo, SessionController.getCurrentUserId(request)); // Save photo, given a successful upload
            } catch (NullPointerException e) {
                return redirect(profileEndpoint).flashing("invalid", " Error! File not saved");
            }
            return redirect(profileEndpoint).flashing("success", fileName + " uploaded");
        });
    }


    /**
     * Method to serve an image to the frontend. Uses the image path url
     * @param id image id that is to be rendered
     * @return rendered image file to be displayed
     */
    @Security.Authenticated(SecureSession.class)
    public Result photoAt(Integer id){
        Photo image = Photo.find.byId(id);
        try {
            File imageFilePath = new File(Objects.requireNonNull(image).getPath());
            if (imageFilePath.exists()) {
                return ok(new FileInputStream(imageFilePath)).as(image.getType());
            }
            return notFound(imageFilePath.getAbsoluteFile());
        } catch(NullPointerException | IOException e) {
            return redirect(profileEndpoint); //  When there an id of a photo does not exist
        }
    }


    /**
     * Call to PhotoRepository to be insert an photo in the database
     *
     * @param photo Photo object containing email, id, byte array of photo and visible info
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    private CompletionStage<Result> savePhoto(Photo photo, int profileId){
        return photoRepository.insert(photo).thenApplyAsync(photoId -> {
            return personalPhotoRepository.insert(new PersonalPhoto(profileId, photoId))
            .thenApplyAsync(id -> {
                return id;
            });
        }).thenApply(result -> redirect("/profile"));
    }


    /**
     * Set a profile picture to the database
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> setProfilePicture(Http.Request request, Integer photoId) {
        int profileId = SessionController.getCurrentUserId(request);
        try {
            personalPhotoRepository.findByPhotoId(photoId).thenApplyAsync(photoOpt -> {
                if (photoOpt.isPresent()) {
                    personalPhotoRepository.removeProfilePic(profileId);
                    personalPhotoRepository.setProfilePic(profileId, photoId);
                }
                return photoOpt;
            });
        } catch (NullPointerException e) {
            savePhoto(demoProfilePicture, profileId);
        }
        return supplyAsync(() -> redirect(profileEndpoint).flashing("success", "Profile picture updated"));
    }


    /**
     * Unsets a a users personal photo as a profile picture
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> removeProfilePicture(Http.Request request) {
        int profileId = SessionController.getCurrentUserId(request);
        try {
            personalPhotoRepository.removeProfilePic(profileId);
        } catch (NullPointerException e) {
            savePhoto(demoProfilePicture, profileId);
        }
        return supplyAsync(() -> redirect(profileEndpoint).flashing("success", "Profile picture removed"));
    }


    /**
     * deletes the demoProfilePicture, effectively deleting any changes to the profile picture then refreshes the page
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> resetDemoProfilePicture() {
        demoProfilePicture = null;
        return supplyAsync(() -> redirect(profileEndpoint).flashing("success", "Changes cancelled"));
    }


    /**
     * Show the profile page
     * @param request The http request
     * @return a page render of the users profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> show(Http.Request request){

        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId).thenApplyAsync(profileRec -> {
            if (profileRec.isPresent()) {
                List<Photo> displayImageList = getUserPhotos(request);
                Boolean show = showPhotoModal = false;
                Optional<Photo> image = personalPhotoRepository.getProfilePicture(profId);
                Photo profilePicture;
                profilePicture = image.orElse(null);
                Profile toSend = tripRepository.setUserTrips(profileRec.get());
                TreeMultimap<Long, Integer> tripsMap = toSend.getTrips();
                List<Integer> tripValues= new ArrayList<>(tripsMap.values());
                profileRepository.getDestinations(toSend.getProfileId()).ifPresent(dests -> destinationsList = dests);
                return ok(profile.render(toSend, imageForm, displayImageList, show, tripValues, profilePicture, destinationsList, request, messagesApi.preferred(request)));
            } else {
                return redirect("/profile");
            }
        });
    }

    /**
     * Uploads a newly selected profile picture for the user, saving it in the database and
     * setting it as the user profile picture
     *
     * @param request The users request to save the photo
     * @return
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> uploadProfilePicture(Http.Request request) {
            Http.MultipartFormData<TemporaryFile> body = request.body().asMultipartFormData();
            Http.MultipartFormData.FilePart<TemporaryFile> picture = body.getFile("image");

            if (picture == null) {
                return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "No image selected."));
            }

            String fileName = picture.getFilename();
            String contentType = picture.getContentType();
            Long fileSize = picture.getFileSize();

            if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/gif")) {
                return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "Invalid file type!"));
            }

            if (fileSize >= 8000000) {
                return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "File size must not exceed 8 MB!"));
            }


            File img = picture.getRef().path().toFile();

            try {
                BufferedImage image = ImageIO.read(img );
                Image thumbnail = Thumbnail.getInstance().extract(image);

                //TODO Save 'thumbnail' object in DB.
                //TODO Save 'thumbnail' object in VM. Note this may need transformation as it is an Image object.

            } catch (IOException e) {

                return supplyAsync(() ->redirect(profileEndpoint).flashing("invalid", " Error! Thumbnail not saved"));
            }



        TemporaryFile tempFile = picture.getRef();
            String filepath = System.getProperty("user.dir") + "/photos/personalPhotos/" + fileName;
            tempFile.copyTo(Paths.get(filepath), true);


            Photo photo = new Photo("photos/personalPhotos/" + fileName, contentType, 1, fileName);

            return photoRepository.insert(photo).thenApplyAsync(photoId -> {
               return personalPhotoRepository.insert(new PersonalPhoto(SessionController.getCurrentUserId(request), photoId, 1));
            }).thenApply(id -> {
                return redirect("/profile");
            });
            //TODO Auto crop to set size
    }

    /**
     * Endpoint to handle a request from the user to delete a personal photo
     *
     * @apiNote GET /profile/photo/:photoId/delete
     * @param request
     * @param photoId
     * @return
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> deletePhoto(Http.Request request, int photoId) {
        return photoRepository.delete(photoId).thenApplyAsync(x -> redirect(profileEndpoint).flashing("success", "Photo deleted"));
    }

}

