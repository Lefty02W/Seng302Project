package controllers;

import models.*;
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


import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletionStage;

import play.libs.Files.TemporaryFile;

import static java.util.concurrent.CompletableFuture.supplyAsync;


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
    public ProfileController(FormFactory profileFormFactory, FormFactory imageFormFactory, MessagesApi messagesApi, HttpExecutionContext httpExecutionContext, ProfileRepository profileRepository, ImageRepository imageRepository) {
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

    public Result update(Http.Request request) {
        Form<Profile> currentProfileForm = profileForm.bindFromRequest(request);
        Profile profile = currentProfileForm.get();

        profileRepository.update(profile, getCurrentUser(request).getPassword());

        //TODO redirect does not update profile displayed, have to refresh to get updated info
        return redirect(routes.ProfileController.show());

    }

    /**
     * Get the currently logged in user
     *
     * @param request Https request
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
     * Call to ImageRepository to be insert an image in the database
     *
     * @param image Image object containing email, id, byte array of image and visible info
     * @return a redirect to the profile page
     */
    public Result savePhoto(Image image) {
        imageRepository.insert(image);
        return redirect(routes.ProfileController.show());
    }


    /**
     * Method to convert image byte arrays into pictures and display them as the appropriate
     * content type
     *
     * @param id image id to be used as primary key to find image object
     */
    public Result displayPhotos(Integer id) {
        Image image = Image.find.byId(id);
        return ok(Objects.requireNonNull(image).getImage()).as(image.getType());
    }


    /**
     * Retrieves file (image) upload from the front end and converts the image into bytes
     * A new Image object is created and has its attributes set. This image is then sent
     * to savePhoto.
     *
     * @param request
     * @return
     */
    public CompletionStage<Result> uploadPhoto(Http.Request request) {
        Http.MultipartFormData<TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<TemporaryFile> picture = body.getFile("image");

        Form<ImageData> uploadedImageForm = imageForm.bindFromRequest(request);
        ImageData imageData = uploadedImageForm.get();

        if (picture == null) {
            System.out.println("No image found.");
            return supplyAsync(() -> redirect("/profile").flashing("noImage", "No image selected."));

        }

        String fileName = picture.getFilename(); // long fileSize = picture.getFileSize();
        String contentType = picture.getContentType();

        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/gif")) {
            return supplyAsync(() -> redirect("/profile").flashing("invalidImage", "Invalid file type!"));
        }

        TemporaryFile tempFile = picture.getRef();
        File file = tempFile.path().toFile();

        try {
            this.imageBytes = Files.readAllBytes(file.toPath());
            Image image = new Image(null, null, null, null, null); // Initialize Image object
            Profile currentUser = getCurrentUser(request);
            image.setEmail(currentUser.getEmail());
            image.setType(contentType);
            image.setName(fileName);
            image.setImage(this.imageBytes);
            if (imageData.visible != null) {
                image.setVisible(1); // For public (true)
            } else {
                image.setVisible(0); // For private (false)
            }
            savePhoto(image); // Successful upload
        } catch (IOException e) {
            System.out.print(e);
        }


        return supplyAsync(() -> redirect("/profile").flashing("success", "Image uploaded."));
    }


    /**
     * Inserts an Image object into the ImageRepository to be stored on the database
     *
     * @param id Image object containing email, id, byte array of image and visible info
     * @return a redirect to the profile page.
     */
    public Result updatePrivacy(Integer id) {
        System.out.println("CALL TO UPDATE");
        try {
            imageRepository.updateVisibility(id);
        } catch (Exception e) {
            System.out.println(e);
        }
        return redirect(routes.ProfileController.show());
    }

    /**
     * Method to query the image repository to retrieve all images uploaded for a
     * logged in user.
     *
     * @param request Https request
     * @return a list of image objects
     */
    public List<Image> getUserPhotos(Http.Request request) {
        Profile profile = getCurrentUser(request);
        Optional<List<Image>> imageListTemp = imageRepository.getImages(profile.getEmail());
        try {
            imageList = imageListTemp.get();
        } catch (NoSuchElementException e) {
            imageList = new ArrayList<Image>();
        }


        return imageList;
    }

    public Result show(Http.Request request) {
        Profile currentProfile = getCurrentUser(request);
        //TODO change to read from db (the trips)
        currentProfile.setTrips(new ArrayList<Trip>());
        List<Image> displayImageList = getUserPhotos(request);
        return ok(profile.render(currentProfile, imageForm, displayImageList, request, messagesApi.preferred(request)));
    }
}
