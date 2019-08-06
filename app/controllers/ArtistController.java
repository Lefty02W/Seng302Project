package controllers;

import models.Artist;
import models.ArtistProfile;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.ArtistRepository;
import repository.GenreRepository;
import repository.ProfileRepository;
import utility.Country;
import views.html.artists;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.lang.Integer.parseInt;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * This class is the controller for processing front end artist profile related functionality
 */
public class ArtistController extends Controller {

    private final Form<Artist> artistForm;
    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;
    private final ArtistRepository artistRepository;
    private final ProfileRepository profileRepository;
    private final GenreRepository genreRepository;


    @Inject
    public ArtistController(FormFactory artistProfileFormFactory, HttpExecutionContext httpExecutionContext,
                            MessagesApi messagesApi, ArtistRepository artistRepository, ProfileRepository profileRepository,
                            GenreRepository genreRepository){
        this.artistForm = artistProfileFormFactory.form(Artist.class);
        this.httpExecutionContext = httpExecutionContext;
        this.messagesApi = messagesApi;
        this.artistRepository = artistRepository;
        this.profileRepository = profileRepository;
        this.genreRepository = genreRepository;
    }


    /**
     * Endpoint for landing page for artists
     *
     * @param request client request
     * @return CompletionStage rendering artist page
     */
    public CompletionStage<Result> show(Http.Request request) {
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId)
                .thenApplyAsync(profileRec -> profileRec.map(profile -> ok(artists.render(profile, genreRepository.getAllGenres(), profileRepository.getAll(), Country.getInstance().getAllCountries(), request, messagesApi.preferred(request)))).orElseGet(() -> redirect("/profile")));

    }


    /**
     * Method to call repository method to save an Artist profile to the database
     * grabs the artistProfile object required for the insert
     * @param request
     * @return
     */
    public CompletionStage<Result> createArtist(Http.Request request){
        Form<Artist> artistProfileForm = artistForm.bindFromRequest(request);
        Optional<Artist> artistOpt = artistProfileForm.value();
        if (artistOpt.isPresent()){
            Artist artist = artistOpt.get();
            return artistRepository.checkDuplicate(artist.getArtistName()).thenApplyAsync(x -> {
                if (!x) {
                    artistRepository.insert(artist);

                    Optional<String> optionalProfiles = artistProfileForm.field("adminForm").value();

                    //Insert ArtistProfiles for new Artist.
                    for (String profileIdString: optionalProfiles.toString().split(",")){
                        Integer profileId = parseInt(profileIdString);
                        ArtistProfile artistProfile = new ArtistProfile(profileId, artist.getArtistId());
                        artistRepository.insertProfileLink(artistProfile);
                    }
                    System.out.println("Added artist");
                    return redirect("/profile").flashing("info", "Artist Profile : " + artist.getArtistName() + " created");
                } else {
                    return redirect("/profile").flashing("info", "Artist with the name " + artist.getArtistName() + " already exists!");
                }
            });
        }
        return supplyAsync(() -> redirect("/profile").flashing("info", "Artist Profile save failed"));
    }


    /**
     * Allows a memeber of an artist to leave an artist
     *
     * @param request client request to leave artist
     * @return CompletionStage holding redirect to TODO set page when my artist page is there
     */
    public CompletionStage<Result> leaveArtist(Http.Request request, int artistId) {
        return artistRepository.removeProfileFromArtist(artistId, SessionController.getCurrentUserId(request))
                .thenApplyAsync(x -> redirect("/artist")); //TODO update redirect when my artist page is present
    }
}
