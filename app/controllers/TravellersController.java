package controllers;

import models.PartnerFormData;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
//TODO Fix table in html - types field needs to wrap
//TODO Fix data format in table
//TODO Stop fields from clearing when you press search
/**
 * This class is the controller for the travellers.scala.html file, it provides the route to the
 * travellers page
 */
public class TravellersController extends Controller {


    private final Form<PartnerFormData> form;
    private MessagesApi messagesApi;

    @Inject
    public TravellersController(FormFactory formFactory, MessagesApi messagesApi) {
        this.form = formFactory.form(PartnerFormData.class);
        this.messagesApi = messagesApi;
    }

    /**
     * Function to search travellers n gender nationality, age and traveller type fields, calls search functions for
     * each field
     * @param request http request
     * @return renders traveller view with queried list of travellers
     */
    public Result search(Http.Request request){
        Form<PartnerFormData> searchForm = form.bindFromRequest(request);
        PartnerFormData searchData = searchForm.get();
        List<Profile> resultData = Profile.find.all();

        if (searchForm.get().searchGender != ""){
            resultData = listGender(resultData, searchData);
        }
        if (searchForm.get().searchNationality != ""){
            resultData = searchNat(resultData, searchData);
        }

        if (searchForm.get().searchAgeRange != null){
            resultData = searchAge(resultData, searchData);
        }

        if (searchForm.get().searchTravellerTypes != ""){
            resultData = searchTravelTypes(resultData, searchData);
        }

        return ok(travellers.render(form, resultData, ProfileController.getCurrentUser(request), request, messagesApi.preferred(request)));
    }

    /**
     * Method to search for travel partners (profiles) with a search term. The search term can be any of the following attributes:
     * nationality, gender, age range, type of traveller.
     * @param searchForm
     * @return return list of profiles
     */
    public List<Profile> listGender(List<Profile> resultData, PartnerFormData searchForm) {
        List<Profile> resultProfiles = new ArrayList<>();
        String genderTerm = searchForm.searchGender;

        if (!genderTerm.equals("")) {
            for (Profile profile : resultData) {
                if (profile.getGender().contains(genderTerm)) {
                    resultProfiles.add(profile);
                }
            }
        } else {
            resultProfiles = resultData;
        }

        return resultProfiles;
    }

    /**
     * Removes Nationalities from result list
     * @param resultData current list to return
     * @param searchData Form holding search terms
     * @return queried list including nationality search
     */
    public List<Profile> searchNat(List<Profile> resultData, PartnerFormData searchData){
        List<Profile> resultProfiles = new ArrayList<>();
        for (Profile profile: resultData){
            if (profile.getNationalities().contains(searchData.searchNationality)){
                resultProfiles.add(profile);
            }
        }

        return resultProfiles;
    }

    /**
     * Removes ages from result list
     * @param resultData current list to return
     * @param searchData Form holding search terms
     * @return queried list including age search
     */
    public List<Profile> searchAge(List<Profile> resultData, PartnerFormData searchData) {
        List<Profile> resultProfiles = new ArrayList<>();
        int travellerTypeTerm = searchData.searchAgeRange;
        Date range1;
        Date range2;

        for (Profile profile : resultData) {
            switch (travellerTypeTerm) {
                case 1: // < 18
                    Calendar calendar11 = Calendar.getInstance();
                    calendar11.add(Calendar.YEAR, -18);
                    range1 = calendar11.getTime();
                    if (profile.getBirthDate().getTime() > range1.getTime()) {
                        resultProfiles.add(profile);
                    }
                    break;
                case 2: // 18-25
                    Calendar calendar21 = Calendar.getInstance();
                    Calendar calendar22 = Calendar.getInstance();
                    calendar21.add(Calendar.YEAR, -18);
                    calendar22.add(Calendar.YEAR, -25);
                    range1 = calendar21.getTime();
                    range2 = calendar22.getTime();
                    if ((profile.getBirthDate().getTime() > range2.getTime()) && (profile.getBirthDate().getTime() < range1.getTime())) {
                        resultProfiles.add(profile);
                    }
                    break;
                case 3: // 25-35
                    Calendar calendar31 = Calendar.getInstance();
                    Calendar calendar32 = Calendar.getInstance();
                    calendar31.add(Calendar.YEAR, -25);
                    calendar32.add(Calendar.YEAR, -35);
                    range1 = calendar31.getTime();
                    range2 = calendar32.getTime();
                    if ((profile.getBirthDate().getTime() > range2.getTime()) && (profile.getBirthDate().getTime() < range1.getTime())) {
                        resultProfiles.add(profile);
                    }
                    break;
                case 4: // 35-50
                    Calendar calendar41 = Calendar.getInstance();
                    Calendar calendar42 = Calendar.getInstance();
                    calendar41.add(Calendar.YEAR, -35);
                    calendar42.add(Calendar.YEAR, -50);
                    range1 = calendar41.getTime();
                    range2 = calendar42.getTime();
                    if ((profile.getBirthDate().getTime() > range2.getTime()) && (profile.getBirthDate().getTime() < range1.getTime())) {
                        resultProfiles.add(profile);
                    }
                    break;
                case 5: // 50-65
                    Calendar calendar51 = Calendar.getInstance();
                    Calendar calendar52 = Calendar.getInstance();
                    calendar51.add(Calendar.YEAR, -50);
                    calendar52.add(Calendar.YEAR, -65);
                    range1 = calendar51.getTime();
                    range2 = calendar52.getTime();
                    if ((profile.getBirthDate().getTime() < range2.getTime()) && (profile.getBirthDate().getTime() > range1.getTime())) {
                        resultProfiles.add(profile);
                    }
                    break;
                case 6: // 65+
                    Calendar calendar61 = Calendar.getInstance();
                    calendar61.add(Calendar.YEAR, -65);
                    range1 = calendar61.getTime();
                    if (profile.getBirthDate().getTime() < range1.getTime()) {
                        resultProfiles.add(profile);
                    }
                    break;
            }
        }

        return resultProfiles;
    }

    /**
     * Removes traveller types from result list
     * @param resultData current list to return
     * @param searchData Form holding search terms
     * @return queried list including traveller types search
     */
    public List<Profile> searchTravelTypes(List<Profile> resultData, PartnerFormData searchData){
        List<Profile> resultProfiles = new ArrayList<>();
        String travellerTypeTerm = searchData.searchTravellerTypes;

        for (Profile profile : resultData) {
            if (profile.getTravellerTypes().contains(travellerTypeTerm)) {
                resultProfiles.add(profile);
            }
        }

        return resultProfiles;
    }

    /**
     * This method shows the travellers page on the screen
     * @return
     */
    public Result show(Http.Request request) {
        List<Profile> profiles = Profile.find.all();

        return ok(travellers.render(form, profiles, ProfileController.getCurrentUser(request), request, messagesApi.preferred(request)));
    }
}