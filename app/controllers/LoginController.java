package controllers;

import models.Profile;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.ProfileRepository;
import utility.Country;
import views.html.login;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * This class is the controller for the login.scala.html file, it provides the route to the
 * login page and the method that the page uses.
 */
public class LoginController extends Controller {

    private final Form<Login> loginForm;
    private final Form<Profile> profileForm;
    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    private final ProfileRepository profileRepository;

    public static class Login {
        public String email;
        public String password;

    }

    @Inject
    public LoginController(FormFactory formFactory, FormFactory profileFormFactory, ProfileRepository profileRepository, HttpExecutionContext httpExecutionContext, MessagesApi messagesApi){
        this.loginForm = formFactory.form(Login.class);
        this.profileForm = profileFormFactory.form(Profile.class);
        this.profileRepository = profileRepository;
        this.httpExecutionContext = httpExecutionContext;
        this.messagesApi = messagesApi;
    }

    /**
     * Function to authenticate a login
     * @param request the users login request
     * @return either login failed  with incorrect info or successful login and go to user  page
     */
    public CompletionStage<Result> login(Http.Request request){
        Form<Login> currentLoginForm = loginForm.bindFromRequest(request);
        Login login = currentLoginForm.get();
        if (checkUser(login.email, login.password)){
            // Validate the login credentials
            Login loginData = currentLoginForm.get();
            CompletionStage<Optional<Profile>> profileOptional = profileRepository.lookupEmail(loginData.email);
            return profileRepository.lookupEmail(loginData.email).thenCombineAsync(profileOptional, (profiles, profile) -> {
                if (profile.isPresent()) {
                    Profile currentUser = profile.get();
                    return redirect(routes.ProfileController.show()).addingToSession(request, "connected", currentUser.getProfileId().toString());
                }
                return redirect("/").flashing("warning", "Profile has been deleted!");
            }, httpExecutionContext.current());

        } else {
            return supplyAsync(() -> redirect("/").flashing("info", "Login details incorrect, please try again."));
        }
    }


    /**
     * Check if user exists
     * @param email Email of the user to validate login
     * @param password Password of the user to validate login
     * @return true for existing user or false if not existing
     */
    private boolean checkUser(String email, String password){
        if (profileRepository.checkProfileExists(email)) {
            return profileRepository.validate(email, password);
        } else {
            return false;
        }

    }

    /**
     * Save user into the database
     * @param request users request to create a profile
     * @return redirect to login
     */
    public CompletionStage<Result> save(Http.Request request){
        Form<Profile> userForm = profileForm.bindFromRequest(request);
        Optional<Profile> profOpt = userForm.value();
        try{
            if (profOpt.isPresent()) {
                Profile profile = profOpt.get();
                profile.initProfile();
                String email = profile.getEmail();
                if (profileRepository.isEmailTakenSignup(email)) {
                    return supplyAsync(()-> redirect("/").flashing("warning", "Error: Email already taken"));
                }
                if (!isEmailValid(email)) {
                    return supplyAsync(()-> redirect("/").flashing("warning", "Error: Please enter a valid email"));
                }

                return profileRepository.insert(profile).thenApplyAsync(profileIdOptional ->
                        redirect(routes.ProfileController.show()).addingToSession(request, "connected", profileIdOptional.get().toString()));

            }
        }catch (Exception e){
            return supplyAsync(()-> redirect("/").flashing("info", "Profile save failed"));
        }
        return supplyAsync(()-> redirect("/").flashing("info", "Profile save failed"));

    }

    public static boolean isEmailValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    /**
     * create the login page
     * @param request users request to show login page/logout
     * @return rendered login page login
     */
    public Result show(Http.Request request) {
        return ok(login.render(loginForm, profileForm, Country.getInstance().getAllCountries(), request, messagesApi.preferred(request)));
    }

}
            
