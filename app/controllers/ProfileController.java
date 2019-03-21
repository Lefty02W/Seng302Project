package controllers;

import models.*;
import play.api.mvc.MultipartFormData;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.ImageRepository;
import repository.ProfileRepository;
import views.html.*;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletionStage;

import play.libs.Files.TemporaryFile;


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

    /**
     * To get Image data upon upload
     */
    public static class ImageData {
        public String visible;
    }

    @Inject
    public ProfileController(FormFactory profileFormFactory, FormFactory imageFormFactory, MessagesApi messagesApi, HttpExecutionContext httpExecutionContext, ProfileRepository profileRepository, ImageRepository imageRepository){
        this.profileForm = profileFormFactory.form(Profile.class);
        this.imageForm = imageFormFactory.form(ImageData.class);
        this.messagesApi = messagesApi;
        this.httpExecutionContext = httpExecutionContext;
        this.profileFormFactory = profileFormFactory;
        this.imageFormFactory = imageFormFactory;
        this.profileRepository = profileRepository;
        this.imageRepository = imageRepository;
    }


    public CompletionStage<Result> showEdit(String email) {




        return profileRepository.lookup(email).thenApplyAsync(optionalProfile -> {
            if (optionalProfile.isPresent()) {
                Profile toEditProfile = optionalProfile.get();
                //TODO Form is not auto filling
                Form<Profile> currentProfileForm = profileForm.fill(toEditProfile);
                return ok(editProfile.render(toEditProfile, currentProfileForm));

            } else {
                return notFound("Profile not found.");
            }
        }, httpExecutionContext.current());
    }

    public Result update(Http.Request request){
        Form<Profile> currentProfileForm = profileForm.bindFromRequest(request);
        Profile profile = currentProfileForm.get();

        profileRepository.update(profile, getCurrentUser(request).getPassword());

        //TODO redirect does not update profile displayed, have to refresh to get updated info
        return redirect(routes.ProfileController.show());

    }

    /**
     * Get the currently logged in user
     * @param request
     * @return Web page showing connected user's email
     */
    public Profile getCurrentUser(Http.Request request) {
        Optional<String> connected = request.session().getOptional("connected");
        String email;
        if (connected.isPresent()) {
            email = connected.get();
            return Profile.find.byId(email);
        } else {
            return null;
        }
    }

    /**
     * Inserts an Image object into the ImageRepository to be stored on the database
     * @param image Image object containing email, id, byte array of image and visible info
     * @return
     */
    public Result savePhoto(Image image){
        imageRepository.insert(image);
        return redirect(routes.ProfileController.show());
    }

    /**
     * Method to convert image byte arrays into pictures and display them
     * @param request
     */
    public void displayPhotos(Http.Request request){
        // TODO: Work on converting each image binary array into an picture and display them
        List<Image> userPhotos = getUserPhotos(request);
        ArrayList<BufferedImage> imageList = new ArrayList<>();
        for(Image photo: userPhotos) {
            imageList.add(convertByteToImage(photo.getImage()));
        }

        // For testing. Delete later
        for(BufferedImage image1 : imageList) {
            System.out.println("Image to display: " + image1);
        }
    }

    /**
     * Method to convert a given byte array into a buffered image file
     * @param byteArray the file's byte array value
     * @return a BufferedImage object of the byte array
     */
    public BufferedImage convertByteToImage(byte[] byteArray) {
        try {
            return ImageIO.read(new ByteArrayInputStream(byteArray));
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("Error converting image");
        return null;
    }

    /**
     * Retrieves file (image) upload from the front end and converts the image into bytes
     * A new Image object is created and has its attributes set. This image is then sent
     * to savePhoto.
     * @param request
     * @return
     */
    public Result uploadPhoto(Http.Request request) {
        Http.MultipartFormData<TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<TemporaryFile> picture = body.getFile("image");

        Form<ImageData> uploadedImageForm = imageForm.bindFromRequest(request);
        ImageData imageData = uploadedImageForm.get();


        if (picture != null) {
//            String fileName = picture.getFilename();
//            long fileSize = picture.getFileSize();
//            String contentType = picture.getContentType();

            TemporaryFile tempFile = picture.getRef();
            File file = tempFile.path().toFile();
//            tempFile.copyTo(Paths.get("public/images/" + fileName), true); // Can change to appropriate folder
            try {
                this.imageBytes = Files.readAllBytes(file.toPath());
                Image image = new Image(null, null, null); // Initialize Image object
                Profile currentUser = getCurrentUser(request);
                image.setEmail(currentUser.getEmail());
                image.setImage(this.imageBytes);
                if(imageData.visible != null){
                    image.setVisible(1); // For public (true)
                } else {
                    image.setVisible(0); // For private (false)
                }
                savePhoto(image);
            } catch (IOException e) {
                System.out.print(e);
            }
            // Successful upload
            return redirect(routes.ProfileController.show());
        } else {
            System.out.println("No image found.");
            return redirect(routes.ProfileController.show());
        }
    }

    /**
     * Method to retrieve all uploaded profile images from the database for a logged in user
     * @param request
     * @return
     */
    public List<Image> getUserPhotos(Http.Request request) {
        Profile profile = getCurrentUser(request);
        Optional<List<Image>> imageListTemp = imageRepository.getImages(profile.getEmail());
        try {
            imageList = imageListTemp.get();
        } catch(NoSuchElementException e) {
            imageList = new ArrayList<Image>();
        }

        // For testing. Delete later
        for(Image image : imageList) {
            System.out.println("ID: " + image.getImageId() + " Image: " + image.getImage() + " Visible: " + image.getVisible());
        }
        return imageList;
    }

    public Result show(Http.Request request) {
        Profile currentProfile = getCurrentUser(request);
        //TODO change to read from db (the trips)
        currentProfile.setTrips(new ArrayList<Trip>());
        displayPhotos(request); // For Testing if images are able to be retrieved from db. Delete later.
        return ok(profile.render(currentProfile, imageForm, request, messagesApi.preferred(request)));
    }
}
