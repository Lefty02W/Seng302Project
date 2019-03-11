package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.PartnerFormData;
import models.Profile;
import models.SearchFormData;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.*;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is the controller for the travellers.scala.html file, it provides the route to the
 * travellers page
 */
public class TravellersController extends Controller {

    private final FormFactory formFactory;

    @Inject
    public TravellersController(FormFactory formFactory) {

        this.formFactory = formFactory;
    }

    /**
     * This method shows the travellers page on the screen
     * @return
     */
    public Result show() {
        List<Profile> profiles = Profile.find.all();
        return ok(travellers.render(profiles);
    }

    /**
     * Method to load up search form page and pass through the input form and https request for use in the listOne method
     * @param request an HTTP request that will be sent with the function call
     * @return a rendered view of the search profile form
     *
    public Result searchProfile(Http.Request request) {
        Form<SearchFormData> profileForm = formFactory.form(SearchFormData.class);
        return ok(views.html.searchProfileForm.render(profileForm, request));
    }*/

    /**
     * Method to load up search form page and pass through the input form and https request for use in the listPartner method
     * @param request an HTTP request that will be sent with the function call
     * @return
     *
    public Result searchPartner(Http.Request request) {
        Form<PartnerFormData> partnerForm = formFactory.form(PartnerFormData.class);
        return ok(views.html.searchPartnerForm.render(partnerForm, request));
    }*/

    /**
     * Display one profile based on user input (email)
     * @param request an HTTP request that will be sent with the function call
     * @return a rendered view of one profile and all its attributes
     *
    public Result listOne(Http.Request request) {
        Form<SearchFormData> profileForm = formFactory.form(SearchFormData.class).bindFromRequest(request);
        SearchFormData profileData = profileForm.get();
        Profile userProfile = Profile.find.byId(profileData.email);

        if (userProfile == null) {
            return notFound("Profile not found!");
        }
        return ok(views.html.displayProfile.render(userProfile));
    }*/

    /**
     * Method to search for travel partners (profiles) with a search term. The search term can be any of the following attributes:
     * nationality, gender, age range, type of traveller.
     * @param request an HTTP request that will be sent with the function call
     * @return
     *
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
    }*/
}
