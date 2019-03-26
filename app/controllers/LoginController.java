package controllers;


import models.Profile;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;

import play.mvc.Http;
import play.mvc.Http.CookieBuilder;
import play.mvc.Result;

import repository.ProfileRepository;
import views.html.*;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 *
 */
public class LoginController extends Controller {

    private final Form<Login> form;
    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    private final ProfileRepository profileRepository;

    public static class Login {
        public String email;
        public String password;

    }

    @Inject
    public LoginController(FormFactory formFactory, ProfileRepository profileRepository, HttpExecutionContext httpExecutionContext, MessagesApi messagesApi){
        this.form = formFactory.form(Login.class);
        this.profileRepository = profileRepository;
        this.httpExecutionContext = httpExecutionContext;
        this.messagesApi = messagesApi;
    }

    public CompletionStage<Result> login(Http.Request request){

        Form<Login> loginForm = form.bindFromRequest(request);
        Login login = loginForm.get();
        if (checkUser(login.email, login.password)){
            // Validate the login credentials
            Login loginData = loginForm.get();
            CompletionStage<Optional<Profile>> profileOptional = profileRepository.lookup(loginData.email);
            return profileRepository.lookup(loginData.email).thenCombineAsync(profileOptional, (profiles, profile) -> {
                if (profile.isPresent()) {
                    Profile currentUser = profile.get();
                    return redirect(routes.ProfileController.show()).addingToSession(request, "connected", currentUser.getEmail());
                }
                return notFound("Login failed");
            }, httpExecutionContext.current());

        } else {
            //TODO show incorrect user login on the front end
            System.out.println("Incorrect login Data please try again");
            return supplyAsync(() -> redirect(routes.LoginController.show()));
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
            
