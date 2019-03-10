package controllers;

import models.User;
import play.data.*;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;

import play.mvc.Http;
import play.mvc.Result;

import views.html.*;
import views.html.helper.form;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 *
 */
public class CreateUserController extends Controller{

    private final Form<User> form;
    private MessagesApi messagesApi;

    @Inject
    public CreateUserController(FormFactory formFactory, MessagesApi messagesApi){
        this.form = formFactory.form(User.class);
        this.messagesApi = messagesApi;
    }

    //to create user

    public Result save(Http.Request request){
        //TODO timestamp1

        Form<User> userForm = form.bindFromRequest(request);
        User user = userForm.get();
        //user.save();



        return redirect(routes.LoginController.show());

    }

    //renders the createUser scene
    public Result show(Http.Request request) {
        return ok(createUser.render(form, request, messagesApi.preferred(request)));
    }


}
