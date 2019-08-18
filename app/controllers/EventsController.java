package controllers;

import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import repository.ArtistRepository;
import repository.GenreRepository;
import repository.ProfileRepository;
import utility.Country;
import views.html.events;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class EventsController extends Controller {

    private final ProfileRepository profileRepository;
    private MessagesApi messagesApi;
    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;

    @Inject
    public EventsController(ProfileRepository profileRepository, MessagesApi messagesApi, GenreRepository genreRepository, ArtistRepository artistRepository){
        this.profileRepository = profileRepository;
        this.messagesApi = messagesApi;
        this.genreRepository = genreRepository;
        this.artistRepository = artistRepository;
    }

    /**
     * Endpoint for landing page for Events
     *
     * @param request client request
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> show(Http.Request request){
        Integer profId = SessionController.getCurrentUserId(request);

        return profileRepository.findById(profId)
                .thenApplyAsync(profileRec -> profileRec.map(profile -> ok(events.render(profile,
                        Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), artistRepository.getAllArtists(),
                        request, messagesApi.preferred(request))))
                .orElseGet(() -> redirect("/")));
    }
}
