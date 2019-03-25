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
 *
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

    //to create user

    public Result save(Http.Request request){
        Form<Profile> userForm = form.bindFromRequest(request);
        System.out.println(userForm);
        Profile profile = userForm.value().get();
        profileRepository.insert(profile);
        return redirect(routes.LoginController.show());
    }

    //renders the createUser scene
    public Result show(Http.Request request) {
        return ok(createUser.render(form, request, messagesApi.preferred(request)));
    }


}
