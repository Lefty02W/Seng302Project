package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import repository.ArtistRepository;
import repository.GenreRepository;
import repository.PassportCountryRepository;
import repository.ProfileRepository;
import utility.Country;
import views.html.artists;
import views.html.viewArtist;

import javax.inject.Inject;
import java.util.List;
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
    private final PassportCountryRepository passportCountryRepository;
    private final GenreRepository genreRepository;
    private final Form<ArtistFormData> searchForm;


    @Inject
    public ArtistController(FormFactory artistProfileFormFactory, HttpExecutionContext httpExecutionContext,
                            MessagesApi messagesApi, ArtistRepository artistRepository, ProfileRepository profileRepository,
                            PassportCountryRepository passportCountryRepository,
                            GenreRepository genreRepository){
        this.artistForm = artistProfileFormFactory.form(Artist.class);
        this.httpExecutionContext = httpExecutionContext;
        this.messagesApi = messagesApi;
        this.artistRepository = artistRepository;
        this.profileRepository = profileRepository;
        this.passportCountryRepository = passportCountryRepository;
        this.genreRepository = genreRepository;
        this.searchForm = artistProfileFormFactory.form(ArtistFormData.class);
    }


    /**
     * Endpoint for landing page for artists
     *
     * @param request client request
     * @return CompletionStage rendering artist page
     */
    public CompletionStage<Result> show(Http.Request request) {
        Integer profId = SessionController.getCurrentUserId(request);

        //Start with page = 0
        List<Artist> artistList = artistRepository.getPagedArtists(0);
        return profileRepository.findById(profId)
                .thenApplyAsync(profileRec -> profileRec.map(profile -> ok(artists.render(searchForm, profile, genreRepository.getAllGenres(), profileRepository.getAll(), Country.getInstance().getAllCountries(),  artistList, request, messagesApi.preferred(request)))).orElseGet(() -> redirect("/profile")));

    }

    /**
     * Method for internal artist paging using AJAX requests within jQuery of artists.scala.
     * @param page
     * @return
     */
    public Result pageArtist(Integer page) {
        List<Artist> artists = artistRepository.getPagedArtists(page);
        JsonNode node = Json.toJson(artists);
        return ok(node);
    }

    /**
     * Endpoint for searching an artist
     * @param request client request
     * @return CompletionStage rendering artist page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> search(Http.Request request){
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
                    if (profile.isPresent()) {
                        Form<ArtistFormData> searchArtistForm = searchForm.bindFromRequest(request);
                        ArtistFormData formData = searchArtistForm.get();
                        searchArtistForm.field("name").value().ifPresent(formData::setName);
                        searchArtistForm.field("genre").value().ifPresent(formData::setGenre);
                        searchArtistForm.field("country").value().ifPresent(formData::setCountry);
                        searchArtistForm.field("followed").value().ifPresent(formData::setFollowed);
                        return ok(artists.render(searchForm, profile.get(), genreRepository.getAllGenres(), profileRepository.getAll(), Country.getInstance().getAllCountries(), artistRepository.searchArtist(formData.name, formData.genre, formData.country, formData.followed), request, messagesApi.preferred(request)));
                    } else {
                        return redirect("/artists");
                    }
        });
    }

    /**
     * Endpoint for landing page for viweing details of artists
     *
     * @param request client request
     * @return CompletionStage rendering artist page
     */
    public CompletionStage<Result> showDetailedArtists(Http.Request request, Integer artistID) {
        Integer profId = SessionController.getCurrentUserId(request);
        Artist artist = artistRepository.getArtistById(artistID);
        if (artist == null) {
            return profileRepository.findById (profId).thenApplyAsync(profile -> redirect("/artists"));
        }
        return profileRepository.findById(profId)
                .thenApplyAsync(profileRec -> profileRec.map(profile ->
                        ok(viewArtist.render(profile, artist, request, messagesApi.preferred(request))))
                        .orElseGet(() -> redirect("/profile")));
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
            artist.initCountry();
            Optional<String> optionalMembers = artistProfileForm.field("members").value();
            optionalMembers.ifPresent(artist::setMembers);
            return artistRepository.checkDuplicate(artist.getArtistName()).thenApplyAsync(x -> {
                if (!x) {
                    artistRepository.insert(artist).thenApplyAsync(artistId -> {
                        Optional<String> optionalProfiles = artistProfileForm.field("adminForm").value();
                        if (optionalProfiles.isPresent() && !optionalProfiles.get().isEmpty()) {
                            for (String profileIdString : optionalProfiles.get().split(",")) {
                                Integer profileId = parseInt(profileIdString);
                                ArtistProfile artistProfile = new ArtistProfile(artistId, profileId);
                                artistRepository.insertProfileLink(artistProfile);
                            }
                        }
                        artistRepository.insertProfileLink(new ArtistProfile(artistId, SessionController.getCurrentUserId(request)));
                        Optional<String> optionalGenres = artistProfileForm.field("genreForm").value();
                        if (optionalGenres.isPresent() && !optionalGenres.get().isEmpty()) {
                            for (String genre : optionalGenres.get().split(",")) {
                                genreRepository.insertArtistGenre(artistId, parseInt(genre));
                            }
                        }
                        saveArtistCountries(artist, artistProfileForm);

                        return null;
                    });
                    return redirect("/artists").flashing("info", "Artist Profile : " + artist.getArtistName() + " ready to be approved by admin");
                } else {
                    return redirect("/artists").flashing("error", "Artist with the name " + artist.getArtistName() + " already exists!");
                }
            });
        }
        return supplyAsync(() -> redirect("/artists").flashing("error", "Artist Profile save failed"));
    }

    /**
     * Method to get the country data for an artist to then save in the linking table
     * @param artist the countries are getting added too
     * @param artistProfileForm form holding he artistFormData needed to get out the country list
     */
    void saveArtistCountries(Artist artist, Form<Artist> artistProfileForm) {
        Optional<String> optionalCountries = artistProfileForm.field("countries").value();
        if(optionalCountries.isPresent()){
            for (String country: optionalCountries.get().split(",")) {
                Optional<Integer> countryObject = passportCountryRepository.getPassportCountryId(country);
                if (countryObject.isPresent()) {
                    ArtistCountry artistCountry = new ArtistCountry(artist.getArtistId(), countryObject.get());
                    artistRepository.addCountrytoArtistCountryTable(artistCountry);
                } else {
                    PassportCountry passportCountry = new PassportCountry(country);
                    passportCountryRepository.insert(passportCountry).thenApplyAsync(id -> {
                        if (id.isPresent()) {
                            ArtistCountry artistCountry = new ArtistCountry(artist.getArtistId(), id.get());
                            artistRepository.addCountrytoArtistCountryTable(artistCountry);
                        }
                        return null;
                    });
                }
            }
        }
    }

    /**
     * Method for user to delete their artist profile
     * @param request client request to delete an artist
     * @param artistId id of the artist profile that will be deleted
     * @return redirect to artist page with success flash
     */
    public CompletionStage<Result> deleteArtist(Http.Request request, Integer artistId){
        return artistRepository.deleteArtist(artistId)
                .thenApplyAsync(x -> redirect("/artists").flashing("info", "Artist was successfully deleted"));
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

    /**
     * A helper function to set the changes in an artist edit request from the artist form
     *
     * @param artistId the id of the artist to be edited
     * @param values the form of artist information that was filled in by the user
     * @return an artist object with newly set values from the user artist form
     */
    Artist setValues(Integer artistId, Form<Artist> values){
        Artist artist = values.get();

        artist.initCountry();
        artist.setCountry(artist.getCountry());
        artist.setArtistId(artistId);

        return artist;
    }
}
