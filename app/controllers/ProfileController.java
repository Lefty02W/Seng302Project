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

import javax.inject.Inject;
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
    private final Form<Image> imageForm;
    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    private final FormFactory profileFormFactory;
    private final FormFactory imageFormFactory;
    private final ProfileRepository profileRepository;
    private final ImageRepository imageRepository;
    private byte[] imageBytes;

    @Inject
    public ProfileController(FormFactory profileFormFactory, FormFactory imageFormFactory, MessagesApi messagesApi, HttpExecutionContext httpExecutionContext, ProfileRepository profileRepository, ImageRepository imageRepository){
        this.profileForm = profileFormFactory.form(Profile.class);
        this.imageForm = imageFormFactory.form(Image.class);
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

    public Result savePhoto(Http.Request request){
        System.out.println(this.imageBytes);
        Profile currentUser = getCurrentUser(request);
        Image image = new Image(null, null, null, true);
        image.setEmail(currentUser.getEmail());
//        image.setImageId(1);
        image.setImage(this.imageBytes);
        image.setVisible(true); // For public or private
        imageRepository.insert(image);
        System.out.println("DONE " + image.getVisible());
        return ok();
    }

    public void displayPhotos(){
        //Todo
    }

    public Result uploadPhoto(Http.Request request) {

        Http.MultipartFormData<TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<TemporaryFile> picture = body.getFile("image");

        if (picture != null) {
            String fileName = picture.getFilename();
            long fileSize = picture.getFileSize();
            String contentType = picture.getContentType();

            TemporaryFile tempFile = picture.getRef();
            File file = tempFile.path().toFile();
//            tempFile.copyTo(Paths.get("public/images/" + fileName), true); // Can change to appropriate folder
            //TODO: Convert image file into byte array and save image to the database
            try {
                this.imageBytes = Files.readAllBytes(file.toPath());
                savePhoto(request);
            } catch (IOException e) {
                System.out.print(e);
            }

            // Successful upload
            return redirect(routes.ProfileController.show());
        } else {

            return badRequest().flashing("error", "Missing file");
        }
    }

    public Result show(Http.Request request) {
        Profile currentProfile = getCurrentUser(request);
        //TODO change to read from db (the trips)
        currentProfile.setTrips(new ArrayList<Trip>());
        return ok(profile.render(currentProfile, imageForm, request, messagesApi.preferred(request)));
    }
}
