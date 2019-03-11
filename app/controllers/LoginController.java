package controllers;


import models.Profile;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;

import play.mvc.Http;
import play.mvc.Result;

import views.html.*;

import javax.inject.Inject;

/**
 *
 */
public class LoginController extends Controller {

    private final Form<Profile> form;
    private MessagesApi messagesApi;

    @Inject
    public LoginController(FormFactory formFactory, MessagesApi messagesApi){
        this.form = formFactory.form(Profile.class);
        this.messagesApi = messagesApi;
    }

    public Result login(Http.Request request){
        Form<Profile> loginForm = form.bindFromRequest(request);
        Profile profile = loginForm.get();
        if (checkUser(profile.getEmail(), profile.getPassword())){
            return redirect(routes.ProfileController.show());
        } else {
            //TODO show incorrect user information error message
            System.out.println("Incorrect login Data please try again");
            //return ok(login.render(form, request, messagesApi.preferred(request)));
            return redirect(routes.LoginController.show());
        }
    }

    public Boolean checkUser(String email, String password){
        //TODO implement user check from database
        if ((email.equals("yes@gmail.com")) && (password.equals("123"))){
            System.out.print("Logging in user");
            return true;
        }
        return false;
    }


    public Result show(Http.Request request) {
        return ok(login.render(form, request, messagesApi.preferred(request)));
    }

}
            
