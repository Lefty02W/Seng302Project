package controllers;

import models.Profile;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.ProfileRepository;
import views.html.createUser;

import javax.inject.Inject;


/**
 * This class is the controller for the createUser.scala.html file, it provides the route to the
 * createUser page and the method that the page uses.
 */
public class CreateUserController extends Controller{

    private final Form<Profile> form;
    private MessagesApi messagesApi;
    private final ProfileRepository profileRepository;

    @Inject
    public CreateUserController(FormFactory formFactory, ProfileRepository profileRepository, MessagesApi messagesApi){
        this.form = formFactory.form(Profile.class);
        this.profileRepository = profileRepository;
        this.messagesApi = messagesApi;
    }

    /**
     * Save user into the database
     * @param request
     * @return redirect to login
     */
    public Result save(Http.Request request){
        Form<Profile> userForm = form.bindFromRequest(request);
        Profile profile = userForm.value().get();
        profileRepository.insert(profile);
        return redirect("/").flashing("info", "Profile: " + profile.getFirstName() + " " + profile.getLastName() + " created");
    }


    /**
     * render createUser page
     * @param request
     * @return rendered create user page
     */
    public Result show(Http.Request request) {
        return ok(createUser.render(form, request, messagesApi.preferred(request)));
    }
}
