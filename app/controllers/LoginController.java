package controllers;


import models.User;
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

    private final Form<User> form;
    private MessagesApi messagesApi;

    @Inject
    public LoginController(FormFactory formFactory, MessagesApi messagesApi){
        this.form = formFactory.form(User.class);
        this.messagesApi = messagesApi;
    }

    public Result login(Http.Request request){
        Form<User> loginForm = form.bindFromRequest(request);
        User user = loginForm.get();
        if (checkUser(user.getEmail(), user.getPassword())){
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
            
