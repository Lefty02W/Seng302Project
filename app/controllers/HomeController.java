package controllers;

//import models.DeleteDestinations;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import repository.DestinationRepository;
import repository.ProfileRepository;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Manage a database of profiles
 */
public class HomeController extends Controller {

    private final FormFactory formFactory;
    private final HttpExecutionContext httpExecutionContext;
    private final MessagesApi messagesApi;
    private final ProfileRepository profileRepository;
    private final DestinationRepository destinationRepository;

    @Inject
    public HomeController(FormFactory formFactory,
                          ProfileRepository profileRepository,
                          HttpExecutionContext httpExecutionContext,
                          MessagesApi messagesApi,
                          DestinationRepository destinationRepository) {

        this.profileRepository = profileRepository;
        this.formFactory = formFactory;
        this.httpExecutionContext = httpExecutionContext;
        this.messagesApi = messagesApi;
        this.destinationRepository = destinationRepository;
    }



    /**
     * Handle the 'edit profile form' submission
     * @param request The http request which contains form data from the edit profile form.
     * If the password field is empty it will not be updated.
     */
    public CompletionStage<Result> updateProfile(Http.Request request) throws PersistenceException {
        Form<Profile> profileForm = formFactory.form(Profile.class).bindFromRequest(request);
        String password = profileForm.rawData().get("password");
        Profile updatedProfile = profileForm.get();

        if (profileForm.hasErrors()) {
            return CompletableFuture.completedFuture(
                notFound("Profile form had errors."+ profileForm.errors()));

        }else if(!updatedProfile.travellerTypeIsPicked()) {
            return CompletableFuture.completedFuture(
                    notFound("Please select at least 1 traveller type."));
        }

        // Run update operation and then flash and then redirect
        return profileRepository.update(updatedProfile, password).thenApplyAsync(data -> {
            // This is the HTTP rendering thread context
            return this.index();
        }, httpExecutionContext.current());

    }

    /**
     * Display the 'sign up form'.
     */
    public Result create(Http.Request request) {
        Form<Profile> profileForm = formFactory.form(Profile.class);
        return ok(views.html.signUpForm.render(profileForm, request, messagesApi.preferred(request)));

    }


    /**
     * Save the submitted profile from the sign up form
     * @param request The http request.
     * @return rendered view of results
     */
    public CompletionStage<Result> save(Http.Request request) {
        Form<Profile> profileForm = formFactory.form(Profile.class).bindFromRequest(request);
        if (profileForm.hasErrors()) {
            return CompletableFuture.completedFuSystem.out.println(user.getFirst_name() + " " + user.getMiddle_name() + " " + user.getLast_name());
        System.out.println("Login Cridentials:");
        System.out.println(user.getEmail() + " " + user.getPassword());
        System.out.println("DOB: " + user.getBirth_date());
        System.out.println("Gender: " + user.getGender());
        System.out.println("Nationality: " + user.getNationality());
        System.out.println("Passport country: " + user.getPassport_country());
        Date createDate = new Date();
        System.out.println("Date of creation: " + createDate);
ture(
                    badRequest(views.html.signUpForm.render(profileForm, request, messagesApi.preferred(request))));
        }

        Profile profile = profileForm.get();
        Profile duplicate = Profile.find.byId(profile.getEmail());
        if (duplicate != null) {
            return CompletableFuture.completedFuture(badRequest("This email already exists"));
        } else if(!profile.travellerTypeIsPicked()) {
            return CompletableFuture.completedFuture(badRequest(
                    views.html.signUpForm.render(profileForm, request, messagesApi.preferred(request))));
        }

        return profileRepository.insert(profile).thenApplyAsync(data -> {
            return Results.redirect(routes.HomeController.index());
        }, httpExecutionContext.current());
    }


    private Result redirectToLogin = Results.redirect(
            routes.HomeController.login()
    );




    /**
     * Login in form
     */
    public Result login(Http.Request request) {
        Form<Login> loginForm = formFactory.form(Login.class);
        return ok(views.html.loginForm.render(loginForm, request, messagesApi.preferred(request)));
    }


    /**
     * Validate login submission
     */
    public CompletionStage<Result> validate(Http.Request request) {
        Form<Login> loginForm = formFactory.form(Login.class).bindFromRequest(request);

        if (loginForm.hasErrors()) {
            return CompletableFuture.completedFuture(
                    badRequest(views.html.loginForm.render(loginForm, request, messagesApi.preferred(request))));
        } else {
            Login login = loginForm.get();
            // Run update operation and then flash and then redirect
            CompletionStage<Optional<Profile>> profileOptional = profileRepository.lookup(login.email);
            return profileRepository.lookup(login.email).thenCombineAsync(profileOptional, (profiles, profile) -> {
                Profile user = null;
                if (!profile.isPresent()) {
                    return notFound("Login failed");
                }
                user = profile.get();

                if (user.checkPassword(login.password)) {
                    return ok(views.html.displayProfile.render(user))
                            .addingToSession(request, "connected", user.getEmail());
                    //return Results.redirect(routes.HomeController.index());
                }
                return notFound("Login failed");
            }, httpExecutionContext.current());
        }
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


    /**
     * Display the profile of logged in user
     * @param request
     * @return
     */
    public Result showCurrentUser(Http.Request request) {
        Optional<String> connected = request.session().getOptional("connected");
        String email;
        if (connected.isPresent()) {
            email = connected.get();
        } else {
            Form<Login> loginForm = formFactory.form(Login.class);
            return badRequest(views.html.loginForm.render(loginForm, request, messagesApi.preferred(request)));
        }
        Profile profile = Profile.find.byId(email);
        return ok(views.html.displayProfile.render(profile));
    }

    /**
     * Log out a user by removing the related session object
     * @param request
     * @return Result - the result of the web page to show
     */
    public Result logout(Http.Request request) {
        Optional<String> connected = request.session().getOptional("connected");
        String email;
        if (connected.isPresent()) {
            email = connected.get();
        } else {
            return Results.redirect(routes.HomeController.index());
        }
        return ok(email).removingFromSession(request, "connected", email);
    }

    public static class Login {
        public String email;
        public String password;

    }


}