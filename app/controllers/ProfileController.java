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
import scala.util.Try;
import views.html.editProfile;
import views.html.profile;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.*;
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
    private final Form<selectDemoProfilePictureData> selectImageForm;
    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    private final FormFactory profileFormFactory;
    private final FormFactory imageFormFactory;
    private final ProfileRepository profileRepository;
    private final ImageRepository imageRepository;
    private byte[] imageBytes;
    private List<Image> imageList = new ArrayList<>();
    private static Boolean showPhotoModal = false;
    private static boolean showChangeProfilePictureModal = false;
    private final TripRepository tripRepository;
    private Image demoProfilePicture = null;
    private Image profilePicture = null;



    public static class selectDemoProfilePictureData {
        public String autoCrop = "False";
        public int photoId;
    }

    /**
     * To get Image data upon upload
     */
    public static class ImageData {
        public String visible = "Private";
        public String isNewProfilePicture;
    }


    @Inject
    public ProfileController(FormFactory profileFormFactory, FormFactory imageFormFactory, MessagesApi messagesApi, HttpExecutionContext httpExecutionContext, ProfileRepository profileRepository, ImageRepository imageRepository, TripRepository tripRepository)
        {
            this.profileForm = profileFormFactory.form(Profile.class);
            this.imageForm = imageFormFactory.form(ImageData.class);
            this.selectImageForm = imageFormFactory.form(selectDemoProfilePictureData.class);
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
    @Security.Authenticated(SecureSession.class)
    private Result savePhoto(Image image){
        imageRepository.insert(image);
        return redirect(routes.ProfileController.show());
    }


    /**
     * Method to convert image byte arrays into pictures and display them as the appropriate
     * content type
     * @param autoCrop used as a boolean, 1 if photo is to be cropped else 0 (note that photo
     *                 will only be cropped for the profile picture)
     * @param id image id to be used as primary key to find image object
     */
    @Security.Authenticated(SecureSession.class)
    public Result displayPhotos (Integer id, Integer autoCrop) {
        Image image = Image.find.byId(id);
        if (autoCrop == 1) {
            byte[] imageDisplay;
            try {
                InputStream in = new ByteArrayInputStream(image.getImage());
                BufferedImage buffImage = ImageIO.read(in).getSubimage(image.getCropX(), image.getCropY(), image.getCropWidth(), image.getCropHeight());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(buffImage, image.getType().split("/")[1], baos);
                baos.flush();
                imageDisplay = baos.toByteArray();
                baos.close();
            } catch (Exception e) {
                imageDisplay = Objects.requireNonNull(image).getImage();
            }
            return ok(imageDisplay).as(image.getType());
        }else {
            return ok(image.getImage()).as(image.getType());
        }
    }

    private class cropInfo {
        private int cropHeight;
        private int cropWidth;

        public cropInfo(int cropHeight, int cropWidth) {
            this.cropHeight = cropHeight;
            this.cropWidth = cropWidth;
        }

        public void setCropHeight(int cropHeight) { this.cropHeight = cropHeight; }

        public void setCropWidth(int cropWidth) { this.cropWidth = cropWidth; }

        public int getCropHeight() { return cropHeight; }

        public int getCropWidth() { return cropWidth; }
    }

    private cropInfo autoCrop(byte[] image) {
        cropInfo crop = new cropInfo(100, 100);
        try {
            InputStream in = new ByteArrayInputStream(this.imageBytes);
            BufferedImage buffImage = ImageIO.read(in);
            crop.setCropWidth(buffImage.getWidth());
            crop.setCropHeight(buffImage.getHeight());
            if (crop.getCropWidth() < crop.getCropHeight()) {
                crop.setCropHeight(crop.getCropWidth());
            } else {
                crop.setCropWidth(crop.getCropHeight());
            }
        } catch (Exception e) {
            crop.setCropHeight(100);
            crop.setCropWidth(100);
        }
        return crop;
    }


    /**
     * Retrieves file (image) upload from the front end and converts the image into bytes
     * A new Image object is created and has its attributes set. If the photo is a normal
     * upload it will be sent to savePhoto and the modal showPhoto is loaded. If the photo
     * is a potential new profile picture it will not be saved but will be set as the demo
     * profile picture and the modal changeProfilePicture is shown
     *
     * @param request Https request
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> uploadPhoto(Http.Request request) {
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
                cropInfo crop = autoCrop(this.imageBytes);

                Image image = new Image(currentUser.getEmail(), this.imageBytes, contentType, visibility, fileName, 0, 0, crop.getCropWidth(), crop.getCropHeight());
                int isProfilePicture = (imageData.isNewProfilePicture.equals("true")) ? 1 : 0;
                if (isProfilePicture == 0) { //case not setting as the new profile picture
                    savePhoto(image); // Save photo, given a successful upload
                    showPhotoModal = true;
                } else {
                    demoProfilePicture = image;
                    showChangeProfilePictureModal = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Redirect user to profile page to show state change
            return ok();
        }).thenApply(result -> redirect("/profile"));
    }


    /**
     * @return a number, 1 if the default profile picture should be used on the modal and 0 if not
     */
    @Security.Authenticated(SecureSession.class)
    public Integer isDefaultProfilePicture() {
        if (demoProfilePicture == null) {
            return 1;
        }
        return 0;
    }

    /**
     * when a new profile picture is chosen but not confirmed this modal is called, it will set the
     * demo profile picture from the id, so it can be displayed on the next modal
     * @param request gives a form with the photo id and an int autoCrop which acts as a boolean
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> setDemoProfilePicture(Http.Request request) {
        Form<selectDemoProfilePictureData> selectedImageForm = selectImageForm.bindFromRequest(request);
        selectDemoProfilePictureData selectedImageData = selectedImageForm.get();
        int id = selectedImageData.photoId;
        int autoCrop = (selectedImageData.autoCrop.equals("True")) ? 1 : 0;
        showChangeProfilePictureModal = true;
        Optional<Image> image = imageRepository.getImage(id);
        demoProfilePicture = image.get();
        if (autoCrop == 1) {
            cropInfo cropInfo = autoCrop(demoProfilePicture.getImage());
            demoProfilePicture.setCropHeight(cropInfo.getCropHeight());
            demoProfilePicture.setCropWidth(cropInfo.getCropWidth());
        } else {
            try {
                InputStream in = new ByteArrayInputStream(demoProfilePicture.getImage());
                BufferedImage buffImage = ImageIO.read(in);
                demoProfilePicture.setCropHeight(buffImage.getHeight());
                demoProfilePicture.setCropWidth(buffImage.getWidth());
            }catch (IOException e) {
                System.out.println(e);
            }
        }
        return supplyAsync(() -> redirect("/profile"));
    }


    /**
     * deletes the demoProfilePicture, effectively deleting any changes to the profile picture then refreshes the page
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> resetDemoProfilePicture() {
        demoProfilePicture = null;
        return supplyAsync(() -> redirect("/profile").flashing("success", "Changes cancelled"));
    }

    /**
     * saves the demo profile picture if it is not already saved to the database
     * @return a refresh to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> setProfilePicture() {
        try {
            Optional<Image> image = imageRepository.getImage(demoProfilePicture.getImageId());
        } catch (NullPointerException e) {
            savePhoto(demoProfilePicture);
        }
        profilePicture = demoProfilePicture;
        return supplyAsync(() -> redirect("/profile").flashing("success", "Profile picture updated"));
    }

    /**
     * Gives the id of the demo profile picture to be displayed on the change profile picture modal
     * @return the id in an object and a refresh to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public Result getDemoProfilePicture() {
        byte[] imageDisplay;
            try {
                InputStream in = new ByteArrayInputStream(demoProfilePicture.getImage());
                BufferedImage buffImage = ImageIO.read(in).getSubimage(0, 0, demoProfilePicture.getCropWidth(), demoProfilePicture.getCropHeight());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(buffImage, demoProfilePicture.getType().split("/")[1], baos);
                baos.flush();
                imageDisplay = baos.toByteArray();
                baos.close();
            } catch (Exception e) {
                System.out.println(e);
                imageDisplay = Objects.requireNonNull(demoProfilePicture).getImage();
            }
            return ok(imageDisplay).as(demoProfilePicture.getType());
    }


    /**
     * Gives the id of the profile picture to be displayed
     * @return the id in an object and a refresh to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public Result getProfilePictureId() {
        if (profilePicture == null) {
            return ok();
        }
        return ok(Objects.requireNonNull(profilePicture).getImage()).as(profilePicture.getType());
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
    @Security.Authenticated(SecureSession.class)
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
        Boolean showChangeProfile = showChangeProfilePictureModal;
        showChangeProfilePictureModal = false;
        TreeMultimap<Long, Integer> tripsMap = SessionController.getCurrentUser(request).getTrips();
        List<Integer> tripValues= new ArrayList<>(tripsMap.values());
        Integer defaultProfilePicture = isDefaultProfilePicture();
        return ok(profile.render(currentProfile, imageForm, displayImageList, show, showChangeProfile, tripValues, defaultProfilePicture, profilePicture, request, messagesApi.preferred(request)));
    }

}

