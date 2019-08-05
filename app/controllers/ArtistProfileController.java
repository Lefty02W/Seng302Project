package controllers;

import models.ArtistProfile;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;

/**
 * This class is the controller for processing front end artist profile related functionality
 */
public class ArtistProfileController extends Controller {

    private final Form<ArtistProfile> artistForm;
    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    private final ArtistProfileRepository artistProfileRepository;


    @Inject
    public ArtistProfileController(FormFactory artistProfileFormFactory, HttpExecutionContext httpExecutionContext, MessagesApi messagesApi){
        this.artistForm = artistProfileFormFactory.form(ArtistProfile.class);
        this.httpExecutionContext = httpExecutionContext;
        this.messagesApi = messagesApi;
    }


    /**
     * Method to call repository method to save an Artist profile to the database
     * grabs the artistProfile object required for the insert
     * @param request
     * @return
     */
    public Result createArtistProfile(Http.Request request){
        Form<ArtistProfile> artistProfileForm = artistForm.bindFromRequest(request);
        Optional<ArtistProfile> artistOpt = artistProfileForm.value();
        if (artistOpt.isPresent()){
            ArtistProfile artistProfile = artistOpt.get();
            artistProfileRepository.insert(artistProfile);
            return redirect("/profile").flashing("info", "Artist Profile : " + artistProfile.getArtistName() + " created");
        }
        return  redirect("/profile").flashing("info", "Artist Profile save failed");
    }
}
