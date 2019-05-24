package controllers;


import com.google.common.collect.TreeMultimap;
import models.Destination;
import models.Image;
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
import repository.ImageRepository;
import repository.ProfileRepository;
import views.html.editProfile;
import views.html.profile;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.nio.file.Paths;

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
    private final ImageRepository imageRepository;
    private byte[] imageBytes;
    private List<Image> imageList = new ArrayList<>();
    private static Boolean showPhotoModal = false;
    private static boolean showChangeProfilePictureModal = false;
    private Image demoProfilePicture = null;
    private static boolean showCropPhotoModal = false;
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
    public ProfileController(FormFactory profileFormFactory, FormFactory imageFormFactory, MessagesApi messagesApi, HttpExecutionContext httpExecutionContext, ProfileRepository profileRepository, ImageRepository imageRepository)
        {
            this.profileForm = profileFormFactory.form(Profile.class);
            this.imageForm = imageFormFactory.form(ImageData.class);
            this.cropImageDataForm = imageFormFactory.form(CropImageData.class);
            this.messagesApi = messagesApi;
            this.httpExecutionContext = httpExecutionContext;
            this.profileRepository = profileRepository;
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
        Profile profile = currentProfileForm.get();

        // Could improve on this
        profile.setNationalities(profile.getNationalities().replaceAll("\\s",""));
        profile.setPassports(profile.getPassports().replaceAll("\\s",""));

        return profileRepository.update(profile, SessionController.getCurrentUser(request).getPassword(),
                SessionController.getCurrentUser(request).getEmail()).thenApplyAsync(x ->  redirect(routes.ProfileController.show()).addingToSession(request, "connected", profile.getEmail())
        , httpExecutionContext.current());
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

        return profileRepository.updateAdminPrivelege(email).thenApplyAsync(clickedEmail ->
             redirect("/travellers")
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
    private List<Image> getUserPhotos(Http.Request request){
        Profile profile = SessionController.getCurrentUser(request);
        try {
            Optional<List<Image>> imageListTemp = imageRepository.getImages(profile.getEmail());
            imageList = imageListTemp.get();
        } catch (NoSuchElementException e) {
            imageList = new ArrayList<>();
        }
        return imageList;
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
        return supplyAsync(() -> redirect(profileEndpoint).flashing("success", "Visibility updated."));
    }

    //TODO: Remove if not needed in final build
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
        System.out.println(Objects.requireNonNull(image).getPath());
        return ok(Objects.requireNonNull(image).getPath());

//        Image image = Image.find.byId(id);
//        System.out.println(Objects.requireNonNull(image).getPath());
//        byte[] imageDisplay;
//        if (autoCrop == 1) {
//            try {
//                InputStream in = new ByteArrayInputStream(image.getImage());
//                BufferedImage buffImage = ImageIO.read(in);
//                buffImage = buffImage.getSubimage(image.getCropX(), image.getCropY(), image.getCropWidth(), image.getCropHeight());
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ImageIO.write(buffImage, image.getType().split("/")[1], baos);
//                baos.flush();
//                imageDisplay = baos.toByteArray();
//                baos.close();
//            } catch (Exception e) {
//                imageDisplay = Objects.requireNonNull(image).getImage();
//            }
//            return ok(imageDisplay).as(image.getType());
//        }else {
//            imageDisplay = Objects.requireNonNull(image).getImage();
//            return ok(imageDisplay).as(image.getType());
//        }
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
        System.out.println(request.body().asMultipartFormData().asFormUrlEncoded());
        System.out.println("In uploadPhoto function ****************************************************************");
        Http.MultipartFormData<TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<TemporaryFile> picture = body.getFile("image");
        Form<ImageData> uploadedImageForm = imageForm.bindFromRequest(request);
        ImageData imageData = uploadedImageForm.get();
        if (picture == null) {
            return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "No image selected."));

        }
        String fileName = picture.getFilename(); // long fileSize = picture.getFileSize();
        String contentType = picture.getContentType();

        // Check valid content type for image
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/gif")) {
            return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "Invalid file type!"));
        }
        TemporaryFile tempFile = picture.getRef();
        String filepath = System.getProperty("user.dir") + "/photos/personalPhotos/" + fileName;
        tempFile.copyTo(Paths.get(filepath), true);


        File file = tempFile.path().toFile();
        return supplyAsync(() -> {
            try {
                Profile currentUser = SessionController.getCurrentUser(request);
                this.imageBytes = Files.readAllBytes(file.toPath());
                int visibility = (imageData.visible.equals("Public")) ? 1 : 0; // Set visibility
                // Initialize Image object
//                cropInfo crop = autoCrop(this.imageBytes);
//                int isProfilePicture = (imageData.isNewProfilePicture.equals("true")) ? 1 : 0;
                Image image = new Image(currentUser.getEmail(), "photos/personalPhotos/" + fileName, contentType, visibility, fileName, 0, 0, 0, 0, 0);
                savePhoto(image); // Save photo, given a successful upload
//                if (isProfilePicture == 0) { //case not setting as the new profile picture
//                    savePhoto(image); // Save photo, given a successful upload
//                } else { //case photo is being set
//                    if (imageData.autoCropped.equals("true")) {
//                        demoProfilePicture = image;
//                        showChangeProfilePictureModal = true;
//                    } else {
//                        demoProfilePicture = image;
//                        showCropPhotoModal = true;
//                    }
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ok("File uploaded");
        }).thenApply(result -> redirect(profileEndpoint));

    }


    /**
     * Method to serve an image to the frontend. Uses the image path url
     * @param id image id that is to be rendered
     * @return rendered image file to be displayed
     */
    @Security.Authenticated(SecureSession.class)
    public Result photoAt(Integer id){
        Image image = Image.find.byId(id);
        File imageFilePath = new File(image.getPath());
        if (imageFilePath.exists()) {
            try {
                return ok(new FileInputStream(imageFilePath)).as(image.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return notFound(imageFilePath.getAbsoluteFile());
        }
        return ok();
    }


    /**
     * Call to ImageRepository to be insert an image in tsavePhotohe database
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
     * saves the demo profile picture if it is not already saved to the database
     * @return a refresh to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> setProfilePicture(Http.Request request) {
        Profile currentUser = SessionController.getCurrentUser(request);
        imageRepository.removeProfilePic(currentUser.getEmail());
        demoProfilePicture.setIsProfilePic(1);
        try {
            //Optional<Image> image = imageRepository.getImage(demoProfilePicture.getImageId());
            imageRepository.update(demoProfilePicture, demoProfilePicture.getImageId());
        } catch (NullPointerException e) {
            savePhoto(demoProfilePicture);
        }
        return supplyAsync(() -> redirect(profileEndpoint).flashing("success", "Profile picture updated"));
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
     * @param imageId is used to retrieve the image to set it as the demoprofile picture
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> setDemoProfilePicture(Integer imageId) {
        showChangeProfilePictureModal = true;
        Optional<Image> image = imageRepository.getImage(imageId);
        demoProfilePicture = image.get();
        return supplyAsync(() -> redirect(profileEndpoint));
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


//    /**
//     * Gives the id of the demo profile picture to be displayed on the change profile picture modal
//     * @param displayCropped int, if true will display photo as a cropped photo, else will display the cropped photo
//     * @return the id in an objectsavePhoto
//     */
//    @Security.Authenticated(SecureSession.class)
//    public Result getDemoProfilePicture(Integer displayCropped) {
//        if (demoProfilePicture == null) {
//            return redirect(profileEndpoint).flashing("invalid", "No image selected.");
//        }
//        if (displayCropped == 1) {
//            byte[] imageDisplay;
//            try {
//                InputStream in = new ByteArrayInputStream(demoProfilePicture.getImage());
//                BufferedImage buffImage = ImageIO.read(in).getSubimage(demoProfilePicture.getCropX(), demoProfilePicture.getCropY(), demoProfilePicture.getCropWidth(), demoProfilePicture.getCropHeight());
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ImageIO.write(buffImage, demoProfilePicture.getType().split("/")[1], baos);
//                baos.flush();
//                imageDisplay = baos.toByteArray();
//                baos.close();
//            } catch (Exception e) {
//                imageDisplay = Objects.requireNonNull(demoProfilePicture).getImage();
//            }
//            return ok(imageDisplay).as(demoProfilePicture.getType());
//        }
//        return ok(demoProfilePicture.getImage()).as(demoProfilePicture.getType());
//    }


    /**
     * Takes an id and sets that photoId to be the image to be manually cropped, opens the cropping
     * and refreshes the page
     * @param imageId the image to be cropped
     * @return a redirect to the profile page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> setImageToBeManuallyCropped(Integer imageId) {
        Optional<Image> optionalImage = imageRepository.getImage(imageId);
        demoProfilePicture = optionalImage.get();
        showCropPhotoModal = true;
        return supplyAsync(() -> redirect(profileEndpoint));
    }


//    /**
//     * Validation checks that the cropped image is valid, if so sets the image as the demo profile
//     * picture, loads the change profile picture modal and reloads the profile page
//     * @param request gives the form for the sizes of the cropped image
//     * @return a redirect to the profile page with an error message if needed
//     */
//    @Security.Authenticated(SecureSession.class)
//    public CompletionStage<Result> uploadPhotoWithCroppingInfo(Http.Request request) {
//        try {
//            Form<CropImageData> uploadedCropImageDataForm = cropImageDataForm.bindFromRequest(request);
//            CropImageData cropImageData = uploadedCropImageDataForm.get();
//            if (cropImageData == null) {
//                return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "No image selected."));
//            }
//            InputStream in = new ByteArrayInputStream(demoProfilePicture.getImage());
//            BufferedImage buffImage = ImageIO.read(in);
//            if ((cropImageData.widthHeight + cropImageData.cropX) <= buffImage.getWidth()) {
//                if ((cropImageData.widthHeight + cropImageData.cropY) <= buffImage.getHeight()) {
//                    demoProfilePicture.setCropWidth(cropImageData.widthHeight);
//                    demoProfilePicture.setCropHeight(cropImageData.widthHeight);
//                    demoProfilePicture.setCropX(cropImageData.cropX);
//                    demoProfilePicture.setCropY(cropImageData.cropY);
//                    showChangeProfilePictureModal = true;
//                    return supplyAsync(() -> redirect(profileEndpoint));
//                }
//                return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "Cropped image exceeds original image height"));
//            }
//            return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "cropped image exceeds original image width"));
//        } catch (IOException e) {
//            showChangeProfilePictureModal = false;
//            return supplyAsync(() -> redirect(profileEndpoint).flashing("invalid", "Somthing went wrong while cropping the photo"));
//        }
//    }


//    /**
//     * is used to show the user the dimensions of the image they are editing
//     * @return a string telling the user the width and height of the cropped image
//     */
//    @Security.Authenticated(SecureSession.class)
//    public String getWidthHeight() {
//        try {
//            InputStream in = new ByteArrayInputStream(demoProfilePicture.getImage());
//            BufferedImage buffImage = ImageIO.read(in);
//            return "Width: " + buffImage.getWidth() + " Height: " + buffImage.getHeight();
//        } catch (IOException e) {
//            return "width and heigh is unknown";
//        }
//    }
//
//
//
//    private String isValidSizedPhoto(Image image) {
//        try {
//            InputStream in = new ByteArrayInputStream(demoProfilePicture.getImage());
//            BufferedImage buffImage = ImageIO.read(in);
//            if (buffImage.getWidth() >= 200 && buffImage.getHeight() >= 200) {
//                return "";
//            }
//            return "The selected photo has dimensions Width: " + buffImage.getWidth() + " Height: " + buffImage.getHeight() +
//                    ", a photo with dimensions 200 x 200 is needed to be profile picture";
//        } catch (IOException e) {
//            return "The selected photo has dimensions that are too small, a photo with dimensions 200 x 200 is needed to be profile picture";
//        }
//
//    }


    /**
     * Show the profile page
     * @param request The http request
     * @return a page render of the users profile page
     */
    @Security.Authenticated(SecureSession.class)
    public Result show (Http.Request request){
        Profile currentProfile = SessionController.getCurrentUser(request);
        List<Image> displayImageList = getUserPhotos(request);
//
//        Optional<Image> image = imageRepository.getProfilePicture(currentProfile.getEmail());
//        Image profilePicture;
//        if (image == null) {
//            profilePicture = null;
//        } else {
//            profilePicture = image.get();
//        }
//
//        Integer defaultProfilePicture = isDefaultProfilePicture();
//        String isTooSmall = "";
//        if (defaultProfilePicture == 0) { //case a demo profile picture is to be uploaded
//            isTooSmall = isValidSizedPhoto(demoProfilePicture);
//            if (isTooSmall != "") { //case the demo Profile picture is too small to be set as
//                demoProfilePicture = null;
//                defaultProfilePicture = 1;
//                showChangeProfilePictureModal = true;
//                showCropPhotoModal = false;
//            }
//        }
//        // Get the current show photo modal state
//        // Ensure state is false for next refresh action
//        Boolean show = showPhotoModal = false;
//        Boolean showChangeProfile = showChangeProfilePictureModal;
//        showChangeProfilePictureModal = false;
//        String widthHeight = "Width and Height is unknown";
//        Boolean showCropPhoto = showCropPhotoModal;
//        if (showCropPhoto) {
//            showCropPhotoModal = false;
//            widthHeight = getWidthHeight();
//        }
        TreeMultimap<Long, Integer> tripsMap = SessionController.getCurrentUser(request).getTrips();
        List<Integer> tripValues= new ArrayList<>(tripsMap.values());
        Optional<ArrayList<Destination>> destListTemp = profileRepository.getDestinations(SessionController.getCurrentUser(request).getEmail());
        try {
            destinationsList = destListTemp.get();
        } catch (NoSuchElementException e) {
            destinationsList = new ArrayList<>();
        }
        return ok(profile.render(currentProfile, imageForm, displayImageList, null, null, tripValues, null, null, null, null, destinationsList, null,request, messagesApi.preferred(request)));
    }

}

