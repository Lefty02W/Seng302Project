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
import views.html.editProfile;
import views.html.profile;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
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
    private final Form<CropImageData> cropImageDataForm;
    private MessagesApi messagesApi;
    private List<Destination> destinationsList = new ArrayList<>();
    private final HttpExecutionContext httpExecutionContext;
    private final ProfileRepository profileRepository;
    private final PhotoRepository photoRepository;
    private byte[] imageBytes;
    private List<Photo> photoList = new ArrayList<>();
    private static boolean showChangeProfilePictureModal = false;
    private Photo demoProfilePicture = null;
    private static boolean showCropPhotoModal = false;
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
        String autoCropped = "true";
    }

    /**
     * a class to recieve information from a form for getting cropping image data
     */
    public static class CropImageData {
        int widthHeight;
        int cropX;
        int cropY;
    }


    @Inject
    public ProfileController(FormFactory profileFormFactory, FormFactory imageFormFactory, MessagesApi messagesApi, PersonalPhotoRepository personalPhotoRepository,
            HttpExecutionContext httpExecutionContext, ProfileRepository profileRepository, PhotoRepository photoRepository, TripRepository tripRepository, ProfileTravellerTypeRepository profileTravellerTypeRepository)
        {
            this.profileForm = profileFormFactory.form(Profile.class);
            this.imageForm = imageFormFactory.form(ImageData.class);
            this.cropImageDataForm = imageFormFactory.form(CropImageData.class);
            this.messagesApi = messagesApi;
            this.httpExecutionContext = httpExecutionContext;
            this.profileRepository = profileRepository;
            this.photoRepository = photoRepository;
            this.personalPhotoRepository = personalPhotoRepository;
            this.tripRepository = tripRepository;
            this.profileTravellerTypeRepository = profileTravellerTypeRepository;
        }


    /**
     * Method to retrieve a users profile details and return a filled form to be edited.
     *
     * @apiNote GET /profile/:profileId/edit
     * @param profileId id of the user to edit
     * @return a render of the editDestinations profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> showEdit (Integer profileId){
        return profileRepository.findById(profileId).thenApplyAsync(optionalProfile -> {
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
     * @apiNot POST /profile
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
     * @param email The email of the user who is having admin privilege updated
     * @return Result, redrects to the travellers page.
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> updateAdmin (Http.Request request, String email){
        return profileRepository.updateAdminPrivelege(email).thenApplyAsync(clickedEmail -> redirect("/travellers")
        , httpExecutionContext.current());
    }

    /**
     * Method to query the image repository to retrieve all images uploaded for a
     * logged in user.
     *
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
     * Method to convert image byte arrays into pictures and display them as the appropriate
     * content type
     *
     * @apiNote GET /profile/photo
     * @param autoCrop used as a boolean, 1 if photo is to be cropped else 0 (note that photo
     *                 will only be cropped for the profile picture)
     * @param id image id to be used as primary key to find image object
     */
    @Security.Authenticated(SecureSession.class)
    public Result displayPhotos (Integer id, Integer autoCrop) {
        Photo photo = Photo.find.byId(id);
        byte[] imageDisplay;
        if (autoCrop == 1) {
            try {
                InputStream in = new ByteArrayInputStream(photo.getImage());
                BufferedImage buffImage = ImageIO.read(in);
                buffImage = buffImage.getSubimage(photo.getCropX(), photo.getCropY(), photo.getCropWidth(), photo.getCropHeight());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(buffImage, photo.getType().split("/")[1], baos);
                baos.flush();
                imageDisplay = baos.toByteArray();
                baos.close();
            } catch (Exception e) {
                imageDisplay = Objects.requireNonNull(photo).getImage();
            }
            return ok(imageDisplay).as(photo.getType());
        }else {
            imageDisplay = Objects.requireNonNull(photo).getImage();
            return ok(imageDisplay).as(photo.getType());
        }
    }

    /**
     * A class to store information about a cropped image
     */
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

    /**
     * Auto crops the image passed in
     * @param image The image to be cropped
     * @return
     */
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
     * A new Photo object is created and has its attributes set. If the photo is a normal
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
        uploadedImageForm.field("isNewProfilePicture").value().ifPresent(val -> imageData.isNewProfilePicture = val);
        if (picture == null) {
            return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "No image selected."));

        }
        String fileName = picture.getFilename();
        String contentType = picture.getContentType();

        // Check valid content type for image
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/gif")) {
            return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "Invalid file type!"));
        }
        TemporaryFile tempFile = picture.getRef();
        File file = tempFile.path().toFile();
        return supplyAsync(() -> {
            try {
                this.imageBytes = Files.readAllBytes(file.toPath());
                int visibility = (imageData.visible.equals("Public")) ? 1 : 0; // Set visibility
                cropInfo crop = autoCrop(this.imageBytes);

                System.out.println(imageData.isNewProfilePicture);
                int isProfilePicture = (imageData.isNewProfilePicture.equals("true")) ? 1 : 0;
                Photo photo = new Photo(this.imageBytes, contentType, visibility, fileName, 0, 0, crop.getCropWidth(), crop.getCropHeight());

                if (isProfilePicture == 0) { //case not setting as the new profile picture
                    savePhoto(photo, SessionController.getCurrentUserId(request)).thenApply(result -> redirect("/profile")); // Save photo, given a successful upload
                    showPhotoModal = true;
                } else { //case photo is being set
                    if (imageData.autoCropped.equals("true")) {
                        demoProfilePicture = photo;
                        showChangeProfilePictureModal = true;
                    } else {
                        demoProfilePicture = photo;
                        showCropPhotoModal = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ok();
        }).thenApply(result -> redirect(profileEndpoint));
    }


    /**
     * Call to PhotoRepository to be insert an photo in tsavePhotohe database
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
     * saves the demo profile picture if it is not already saved to the database
     * @return a refresh to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> setProfilePicture(Http.Request request) {
        int profileId = SessionController.getCurrentUserId(request);
        personalPhotoRepository.removeProfilePic(profileId);
        // TODO this needs to hold a personalPhoto
        try {
            photoRepository.update(demoProfilePicture, demoProfilePicture.getPhotoId());
        } catch (NullPointerException e) {
            savePhoto(demoProfilePicture, profileId);
        }
        return supplyAsync(() -> redirect(profileEndpoint).flashing("success", "Profile picture updated"));
    }
    
    
    
    
    /**
     * @return a number, 1 if the default profile picture should be used on the modal and 0 if not
     */
    @Security.Authenticated(SecureSession.class)
    private Integer isDefaultProfilePicture() {
        if (demoProfilePicture == null) {
            return 1;
        }
        return 0;
    }

    /**
     * when a new profile picture is chosen but not confirmed this modal is called, it will set the
     * demo profile picture from the id, so it can be displayed on the next modal
     * @param imageId is used to retrieve the image to set it as the demoprofile picture
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> setDemoProfilePicture(Integer imageId) {
        return supplyAsync(() -> {
            showChangeProfilePictureModal = true;
            Optional<Photo> image = photoRepository.getImage(imageId);
            image.ifPresent(photo -> demoProfilePicture = photo);
            return redirect(profileEndpoint);
        });
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
     * Gives the id of the demo profile picture to be displayed on the change profile picture modal
     * @param displayCropped int, if true will display photo as a cropped photo, else will display the cropped photo
     * @return the id in an objectsavePhoto
     */
    @Security.Authenticated(SecureSession.class)
    public Result getDemoProfilePicture(Integer displayCropped) {
        if (demoProfilePicture == null) {
            return redirect(profileEndpoint).flashing("invalid", "No image selected.");
        }
        if (displayCropped == 1) {
            byte[] imageDisplay;
            try {
                InputStream in = new ByteArrayInputStream(demoProfilePicture.getImage());
                BufferedImage buffImage = ImageIO.read(in).getSubimage(demoProfilePicture.getCropX(), demoProfilePicture.getCropY(), demoProfilePicture.getCropWidth(), demoProfilePicture.getCropHeight());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(buffImage, demoProfilePicture.getType().split("/")[1], baos);
                baos.flush();
                imageDisplay = baos.toByteArray();
                baos.close();
            } catch (Exception e) {
                imageDisplay = Objects.requireNonNull(demoProfilePicture).getImage();
            }
            return ok(imageDisplay).as(demoProfilePicture.getType());
        }
        return ok(demoProfilePicture.getImage()).as(demoProfilePicture.getType());
    }


    /**
     * Takes an id and sets that photoId to be the image to be manually cropped, opens the cropping
     * and refreshes the page
     * @param imageId the image to be cropped
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> setImageToBeManuallyCropped(Integer imageId) {
        Optional<Photo> optionalImage = photoRepository.getImage(imageId);
        optionalImage.ifPresent(image -> demoProfilePicture = image);
        showCropPhotoModal = true;
        return supplyAsync(() -> redirect(profileEndpoint));
    }


    /**
     * Validation checks that the cropped image is valid, if so sets the image as the demo profile
     * picture, loads the change profile picture modal and reloads the profile page
     * @param request gives the form for the sizes of the cropped image
     * @return a redirect to the profile page with an error message if needed
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> uploadPhotoWithCroppingInfo(Http.Request request) {
        try {
            Form<CropImageData> uploadedCropImageDataForm = cropImageDataForm.bindFromRequest(request);
            CropImageData cropImageData = uploadedCropImageDataForm.get();
            if (cropImageData == null) {
                return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "No image selected."));
            }
            InputStream in = new ByteArrayInputStream(demoProfilePicture.getImage());
            BufferedImage buffImage = ImageIO.read(in);
            if ((cropImageData.widthHeight + cropImageData.cropX) <= buffImage.getWidth()) {
                if ((cropImageData.widthHeight + cropImageData.cropY) <= buffImage.getHeight()) {
                    demoProfilePicture.setCropWidth(cropImageData.widthHeight);
                    demoProfilePicture.setCropHeight(cropImageData.widthHeight);
                    demoProfilePicture.setCropX(cropImageData.cropX);
                    demoProfilePicture.setCropY(cropImageData.cropY);
                    showChangeProfilePictureModal = true;
                    return supplyAsync(() -> redirect(profileEndpoint));
                }
                return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "Cropped image exceeds original image height"));
            }
            return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "cropped image exceeds original image width"));
        } catch (IOException e) {
            showChangeProfilePictureModal = false;
            return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "Somthing went wrong while cropping the photo"));
        }
    }


    /**
     * is used to show the user the dimensions of the image they are editing
     * @return a string telling the user the width and height of the cropped image
     */
    @Security.Authenticated(SecureSession.class)
    private String getWidthHeight() {
        try {
            InputStream in = new ByteArrayInputStream(demoProfilePicture.getImage());
            BufferedImage buffImage = ImageIO.read(in);
            return "Width: " + buffImage.getWidth() + " Height: " + buffImage.getHeight();
        } catch (IOException e) {
            return "width and height is unknown";
        }
    }

    

    private String isValidSizedPhoto(Photo photo) {
        try {
            InputStream in = new ByteArrayInputStream(demoProfilePicture.getImage());
            BufferedImage buffImage = ImageIO.read(in);
            if (buffImage.getWidth() >= 200 && buffImage.getHeight() >= 200) {
                return "";
            }
            return "The selected photo has dimensions Width: " + buffImage.getWidth() + " Height: " + buffImage.getHeight() +
                    ", a photo with dimensions 200 x 200 is needed to be profile picture";
        } catch (IOException e) {
            return "The selected photo has dimensions that are too small, a photo with dimensions 200 x 200 is needed to be profile picture";
        }

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

                Optional<Photo> image = personalPhotoRepository.getProfilePicture(profId);
                Photo profilePicture;
                profilePicture = image.orElse(null);


                Integer defaultProfilePicture = isDefaultProfilePicture();
                String isTooSmall = "";
                if (defaultProfilePicture == 0) { //case a demo profile picture is to be uploaded
                    isTooSmall = isValidSizedPhoto(demoProfilePicture);
                    if (isTooSmall != "") { //case the demo Profile picture is too small to be set as
                        demoProfilePicture = null;
                        defaultProfilePicture = 1;
                        showChangeProfilePictureModal = true;
                        showCropPhotoModal = false;
                    }
                }
                // Get the current show photo modal state
                // Ensure state is false for next refresh action
                Boolean show = showPhotoModal = false;
                boolean showChangeProfile = showChangeProfilePictureModal;
                showChangeProfilePictureModal = false;
                String widthHeight = "Width and Height is unknown";
                boolean showCropPhoto = showCropPhotoModal;
                if (showCropPhoto) {
                    showCropPhotoModal = false;
                    widthHeight = getWidthHeight();
                }
                Profile toSend = tripRepository.setUserTrips(profileRec.get());
                TreeMultimap<Long, Integer> tripsMap = toSend.getTrips();
                List<Integer> tripValues= new ArrayList<>(tripsMap.values());
                profileRepository.getDestinations(toSend.getProfileId()).ifPresent(dests -> destinationsList = dests);
                return ok(profile.render(toSend, imageForm, displayImageList, show, showChangeProfile, tripValues, defaultProfilePicture, profilePicture, showCropPhoto, widthHeight, destinationsList, isTooSmall,request, messagesApi.preferred(request)));
            } else {
                return redirect("/profile");
            }
        });
    }

}

