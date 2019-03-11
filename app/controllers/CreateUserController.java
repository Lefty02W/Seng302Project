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
public class CreateUserController extends Controller{

    private final Form<Profile> form;
    private MessagesApi messagesApi;

    @Inject
    public CreateUserController(FormFactory formFactory, MessagesApi messagesApi){
        this.form = formFactory.form(Profile.class);
        this.messagesApi = messagesApi;
    }

    //to create user

    public Result save(Http.Request request){
        //TODO timestamp1

        Form<Profile> userForm = form.bindFromRequest(request);
        Profile profile = userForm.get();
        //user.save();



        return redirect(routes.LoginController.show());

    }

    //renders the createUser scene
    public Result show(Http.Request request) {
        return ok(createUser.render(form, request, messagesApi.preferred(request)));
    }


}
