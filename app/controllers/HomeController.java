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
     * Handle default path requests
     */
    public Result index() {
        return ok(views.html.index.render());
    }


    /**
     * Display the 'edit profile' form for the current profile, filled with the user's info.
     *
     * @param email Email of the profile to edit
     */
    public CompletionStage<Result> editProfile(Http.Request request, String email) {

        return profileRepository.lookup(email).thenApplyAsync(optionalProfile -> {
            if (optionalProfile.isPresent()) {
                Profile toEditProfile = optionalProfile.get();
                Form<Profile> profileForm = formFactory.form(Profile.class).fill(toEditProfile);

                return ok(views.html.editProfileForm.render(profileForm, email));
            } else {
                return notFound("Profile not found.");
            }
        }, httpExecutionContext.current());
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
            return CompletableFuture.completedFuture(
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
     * Display the 'create destination form'.
     */
    public Result createDestination(Http.Request request) {
        Form<Destination> destinationForm = formFactory.form(Destination.class);
        return ok(views.html.createDestinationForm.render(destinationForm, request, messagesApi.preferred(request)));

    }


    /**
     * Handle the 'Create Destination Form' submission
     */
    public CompletionStage<Result> saveDestination(Http.Request request) {

        Profile user = getCurrentUser(request);
        if (user == null) {
            return CompletableFuture.completedFuture(redirectToLogin);
        }
        Form<Destination> destinationForm = formFactory.form(Destination.class).bindFromRequest(request);
        if (destinationForm.hasErrors()) {

                // This is the HTTP rendering thread context
            return CompletableFuture.completedFuture(
                    badRequest(views.html.createDestinationForm.render(
                            destinationForm, request, messagesApi.preferred(request))
                    )
            );
        }

        Destination destination = destinationForm.get();
        destination.setMember_email(user.getEmail());
        // Run insert db operation, then redirect
        return destinationRepository.insert(destination).thenApplyAsync(data -> {
            return Results.redirect(routes.HomeController.index());
        }, httpExecutionContext.current());
    }

    /**
     * Login in form
     */
    public Result login(Http.Request request) {
        Form<Login> loginForm = formFactory.form(Login.class);
        return ok(views.html.loginForm.render(loginForm, request, messagesApi.preferred(request)));
    }


    /**
     * Display all the profiles and their attributes
     * @return a rendered view displaying all the profiles in the ebean server
     */
    public Result listProfile() {
        List<Profile> profiles = Profile.find.all();
        return ok(views.html.profileForm.render(profiles)); // pass in profiles parameter wherever it needs to be displayed
    }

    /**
     * Display all the destinations and their attributes
     * @return  a rendered view displaying all the profiles in the ebean server
    */
    public Result listDestinations() {
        List<Destination> destinations = Destination.find.all();
        return ok(views.html.DisplayDestination.render(destinations));
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


    /**
     * Method to load up search form page and pass through the input form and https request for use in the listOne method
     * @param request an HTTP request that will be sent with the function call
     * @return a rendered view of the search profile form
     */
    public Result searchProfile(Http.Request request) {
        Form<SearchFormData> profileForm = formFactory.form(SearchFormData.class);
        return ok(views.html.searchProfileForm.render(profileForm, request));
    }

    /**
     * Method to load up search form page and pass through the input form and https request for use in the listPartner method
     * @param request an HTTP request that will be sent with the function call
     * @return
     */
    public Result searchPartner(Http.Request request) {
        Form<PartnerFormData> partnerForm = formFactory.form(PartnerFormData.class);
        return ok(views.html.searchPartnerForm.render(partnerForm, request));
    }

    /**
     * Display one profile based on user input (email)
     * @param request an HTTP request that will be sent with the function call
     * @return a rendered view of one profile and all its attributes
     */
    public Result listOne(Http.Request request) {
        Form<SearchFormData> profileForm = formFactory.form(SearchFormData.class).bindFromRequest(request);
        SearchFormData profileData = profileForm.get();
        Profile userProfile = Profile.find.byId(profileData.email);

        if (userProfile == null) {
            return notFound("Profile not found!");
        }
        return ok(views.html.displayProfile.render(userProfile));
    }

    /**
     * Method to search for travel partners (profiles) with a search term. The search term can be any of the following attributes:
     * nationality, gender, age range, type of traveller.
     * @param request an HTTP request that will be sent with the function call
     * @return
     */
    public Result listPartners(Http.Request request) {
        List<Profile> profiles = Profile.find.all();
        List<Profile> resultProfiles = new ArrayList<>();

        Form<PartnerFormData> partnerForm = formFactory.form(PartnerFormData.class).bindFromRequest(request);
        PartnerFormData partnerData = partnerForm.get();
        String genderTerm = partnerData.gender;

        if (!genderTerm.equals("noGender")) {
            for (Profile profile : profiles) {
                if (profile.getGender().contains(genderTerm)) {
                    resultProfiles.add(profile);
                }
            }
        } else {
            resultProfiles = profiles;
        }
        return ok(views.html.displayPartners.render(resultProfiles));
    }
}