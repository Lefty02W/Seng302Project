package controllers;


import models.Profile;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;

import play.mvc.Http;
import play.mvc.Result;

import repository.ProfileRepository;
import views.html.*;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 *
 */
public class LoginController extends Controller {

    private final Form<Login> form;
    private MessagesApi messagesApi;
    private final ProfileRepository profileRepository;

    public static class Login {
        public String email;
        public String password;

    }

    @Inject
    public LoginController(FormFactory formFactory, ProfileRepository profileRepository, MessagesApi messagesApi){
        this.form = formFactory.form(Login.class);
        this.profileRepository = profileRepository;
        this.messagesApi = messagesApi;
    }

    public Result login(Http.Request request){

        Form<Login> loginForm = form.bindFromRequest(request);
        Login login = loginForm.get();
        if (checkUser(login.email, login.password)){
            //TODO create profile cookie
            return redirect(routes.ProfileController.show());
        } else {
            //TODO show error message on this redirect
            return redirect(routes.LoginController.show());
        }
    }

    private boolean checkUser(String email, String password){
        if (profileRepository.checkProfileExists(email)) {
            return profileRepository.validate(email, password);
        } else {
            return false;
        }

    }


    public Result show(Http.Request request) {
        return ok(login.render(form, request, messagesApi.preferred(request)));
    }

}
            
