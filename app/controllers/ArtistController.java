package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import repository.*;
import utility.Country;
import views.html.artists;
import views.html.viewArtist;

import javax.inject.Inject;
import java.util.ArrayList;
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
    private final ArtistRepository artistRepository;
    private final ProfileRepository profileRepository;
    private final PassportCountryRepository passportCountryRepository;
    private final GenreRepository genreRepository;
    private final EventRepository eventRepository;
    private final Form<ArtistFormData> searchForm;
    private final DestinationRepository destinationRepository;


    @Inject
    public ArtistController(FormFactory artistProfileFormFactory, MessagesApi messagesApi,
                            ArtistRepository artistRepository, ProfileRepository profileRepository,
                            PassportCountryRepository passportCountryRepository,
                            GenreRepository genreRepository, EventRepository eventRepository,
                            DestinationRepository destinationRepository){
        this.artistForm = artistProfileFormFactory.form(Artist.class);
        this.messagesApi = messagesApi;
        this.artistRepository = artistRepository;
        this.profileRepository = profileRepository;
        this.passportCountryRepository = passportCountryRepository;
        this.genreRepository = genreRepository;
        this.searchForm = artistProfileFormFactory.form(ArtistFormData.class);
        this.eventRepository = eventRepository;
        this.destinationRepository = destinationRepository;
    }

    /**
     * Endpoint for landing page for artists
     *
     * @param request client request
     * @return CompletionStage rendering artist page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> show(Http.Request request) {
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId)
                .thenApplyAsync(profileRec -> profileRec.map(profile -> ok(artists.render(searchForm, profile,
                        genreRepository.getAllGenres(), profileRepository.getAllEbeans(),
                        Country.getInstance().getAllCountries(),  artistRepository.getPagedArtists(0),
                        artistRepository.getFollowedArtists(profId), artistRepository.getAllUserArtists(profId),
                        request, messagesApi.preferred(request)))).orElseGet(() -> redirect("/profile")));

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
                        int followed = 0;
                        int created = 0;
                        Form<ArtistFormData> searchArtistForm = searchForm.bindFromRequest(request);
                        ArtistFormData formData = searchArtistForm.get();
                        searchArtistForm.field("name").value().ifPresent(formData::setName);
                        searchArtistForm.field("genre").value().ifPresent(formData::setGenre);
                        searchArtistForm.field("country").value().ifPresent(formData::setCountry);
                        searchArtistForm.field("followed").value().ifPresent(formData::setFollowed);
                        searchArtistForm.field("created").value().ifPresent(formData::setCreatedArtist);
                        if(formData.followed.equals("on")) {
                            followed = 1;
                        }

                        if(formData.createdArtist.equals("on")) {
                            created = 1;
                        }
                        if(formData.name.equals("") && formData.country.equals("") && formData.genre.equals("") && followed == 0 && created == 0) {
                            return redirect("/artists").flashing("error", "Please enter at least one search filter.");
                        }

                        searchForm.fill(formData);
                        return ok(artists.render(searchForm, profile.get(), genreRepository.getAllGenres(), profileRepository.getAllEbeans(), Country.getInstance().getAllCountries(), artistRepository.searchArtist(formData.name, formData.genre, formData.country, followed, created, profId), artistRepository.getFollowedArtists(profId), artistRepository.getAllUserArtists(profId), request, messagesApi.preferred(request)));
                    } else {
                        return redirect("/artists");
                    }
        });
    }

    /**
     * Endpoint for landing page for viewing details of artists
     *
     * @param request client request
     * @return CompletionStage rendering artist page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> showDetailedArtists(Http.Request request, Integer artistId) {
        Integer profId = SessionController.getCurrentUserId(request);
        Artist artist = artistRepository.getArtistById(artistId);
        if (artist == null) {
            return profileRepository.findById (profId).thenApplyAsync(profile -> redirect("/artists"));
        }
        return profileRepository.findById(profId)
                .thenApplyAsync(profileRec -> profileRec.map(profile ->
                        ok(viewArtist.render(profile, artist, new ArrayList<Events>(),
                                Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), 0,
                                new PaginationHelper(), profileRepository.getAllEbeans(), destinationRepository.getAllFollowedOrOwnedDestinations(profId),
                                artistRepository.getAllUserArtists(profId), new RoutedObject<Events>(null, false, false), null, request, messagesApi.preferred(request))))
                        .orElseGet(() -> redirect("/profile")));
    }

    /**
     * Endpoint to view an artists events
     *
     * @param request request
     * @param id id of artist to view
     * @param offset offset of page of events to view
     * @return rendered artist page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> showArtistEvents(Http.Request request, Integer id, Integer offset) {
        Integer profId = SessionController.getCurrentUserId(request);
        Artist artist = artistRepository.getArtistById(id);
        if (artist == null) {
            return supplyAsync(() -> redirect("/artists"));
        }
        PaginationHelper paginationHelper = new PaginationHelper(offset, offset, offset, 1, true, true, eventRepository.getNumArtistEvents(id));
        paginationHelper.alterNext(8);
        paginationHelper.alterPrevious(8);
        paginationHelper.checkButtonsEnabled();
        return profileRepository.findById(profId)
                .thenApplyAsync(profileOpt -> profileOpt.map(profile ->
                        ok(viewArtist.render(profile, artist, eventRepository.getArtistEventsPage(id, offset), Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), 1,
                                paginationHelper, profileRepository.getAllEbeans(), destinationRepository.getAllFollowedOrOwnedDestinations(profId),
                                artistRepository.getAllUserArtists(profId), new RoutedObject<Events>(null, false, false), null, request, messagesApi.preferred(request))))
                        .orElseGet(() -> redirect("/profile")));
    }

    /**
     * Endpoint to view an artists members
     *
     * @param request request
     * @param id id of artist to view
     * @return rendered artist page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> showArtistMembers(Http.Request request, Integer id) {
        Integer profId = SessionController.getCurrentUserId(request);
        Artist artist = artistRepository.getArtistById(id);
        if (artist == null) {
            return supplyAsync(() -> redirect("/artists"));
        }
        return profileRepository.findById(profId)
                .thenApplyAsync(profileOpt -> profileOpt.map(profile ->
                        ok(viewArtist.render(profile, artist, new ArrayList<Events>(),
                                Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), 2,
                                new PaginationHelper(), profileRepository.getAllEbeans(), destinationRepository.getAllFollowedOrOwnedDestinations(profId),
                                artistRepository.getAllUserArtists(profId), new RoutedObject<Events>(null, false, false), null, request, messagesApi.preferred(request))))
                        .orElseGet(() -> redirect("/profile")));
    }


    /**
     * Method to call repository method to save an Artist profile to the database
     * grabs the artistProfile object required for the insert
     * @param request client request
     * @return returns completion stage with the result of the redirect
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> createArtist(Http.Request request){
        Form<Artist> artistProfileForm = artistForm.bindFromRequest(request);
        Optional<Artist> artistOpt = artistProfileForm.value();
        if (artistOpt.isPresent()){
            Artist artist = artistOpt.get();
            artist.initCountry();
            Optional<String> optionalMembers = artistProfileForm.field("members").value();
            optionalMembers.ifPresent(artist::setMembers);
            if (artist.getFacebookLink() != null && !artist.getFacebookLink().isEmpty()) {
                if (!artist.getFacebookLink().contains("www.facebook.com/")) {
                    return supplyAsync(() -> redirect("/artists").flashing("error", "Invalid Facebook Link provided"));
                }
            }
            if (artist.getInstagramLink() != null && !artist.getInstagramLink().isEmpty()) {
                if (!artist.getInstagramLink().contains("www.instagram.com/")) {
                    return supplyAsync(() -> redirect("/artists").flashing("error", "Invalid Instagram Link provided"));
                }
            }
            if (artist.getSpotifyLink() != null && !artist.getSpotifyLink().isEmpty()) {
                if (!artist.getSpotifyLink().contains(".spotify.com/")) {
                    return supplyAsync(() -> redirect("/artists").flashing("error", "Invalid Spotify Link provided"));
                }
            }
            if (artist.getTwitterLink() != null && !artist.getTwitterLink().isEmpty()) {
                if (!artist.getTwitterLink().contains("twitter.com/")) {
                    return supplyAsync(() -> redirect("/artists").flashing("error", "Invalid Twitter Link provided"));
                }
            }

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
                        } else {
                            artistRepository.insertProfileLink(new ArtistProfile(artistId, SessionController.getCurrentUserId(request)));
                        }
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
        if (optionalCountries.isPresent()) {
            for (String country : optionalCountries.get().split(",")) {
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
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> deleteArtist(Http.Request request, Integer artistId){
        return artistRepository.deleteArtist(artistId)
                .thenApplyAsync(x -> redirect("/artists").flashing("info", "Artist was successfully deleted"));
    }


    /**
     * Method for user to unfollow an artist profile
     * @param request client request to unfollow an artist
     * @param artistId id of the artist profile that will be unfollowed
     * @return redirect to artist page with success flash
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> unfollowArtist(Http.Request request, Integer artistId){
        return artistRepository.unfollowArtist(artistId, SessionController.getCurrentUserId(request))
                .thenApplyAsync(x -> redirect("/artists").flashing("info", "Artist unfollowed"));
    }

    /**
     * Method for user to follow an artist profile
     * @param request client request to follow an artist
     * @param artistId id of the artist profile that will be followed
     * @return redirect to artist page with success flash
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> followArtist(Http.Request request, Integer artistId){
        return artistRepository.followArtist(artistId, SessionController.getCurrentUserId(request))
                .thenApplyAsync(x -> redirect("/artists").flashing("info", "Artist followed"));
    }

    /**
     * Allows a member of an artist to leave an artist
     *
     * @param request client request to leave artist
     * @return CompletionStage holding redirect to artist page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> leaveArtist(Http.Request request, int artistId) {
        return artistRepository.removeProfileFromArtist(artistId, SessionController.getCurrentUserId(request))
                .thenApplyAsync(x -> redirect("/artists"));
    }

    /**
     * Method to edit an artists information
     *
     * @param request https request containing the artist form
     * @param id artist id that is going to be edited
     * @return redirect to the artist page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> editArtist(Http.Request request, Integer id) {
        Form<Artist> artistProfileForm = artistForm.bindFromRequest(request);
        Integer artistId = SessionController.getCurrentUserId(request);
        Integer currentUserId = SessionController.getCurrentUserId(request);
        Optional<String> artistFormString = artistProfileForm.field("artistId").value();
        if (artistFormString.isPresent()) {
            artistId = Integer.parseInt(artistFormString.get());
        }

        Artist artist = setValues(artistId, artistProfileForm);
        if (artist.getFacebookLink() != null && !artist.getFacebookLink().isEmpty()) {
            if (!artist.getFacebookLink().contains("www.facebook.com/")) {
                return supplyAsync(() -> redirect("/artists/" + id).flashing("error", "Invalid Facebook Link provided"));
            }
        }
        if (artist.getInstagramLink() != null && !artist.getInstagramLink().isEmpty()) {
            if (!artist.getInstagramLink().contains("www.instagram.com/")) {
                return supplyAsync(() -> redirect("/artists/" + id).flashing("error", "Invalid Instagram Link provided"));
            }
        }
        if (artist.getSpotifyLink() != null && !artist.getSpotifyLink().isEmpty()) {
            if (!artist.getSpotifyLink().contains(".spotify.com/")) {
                return supplyAsync(() -> redirect("/artists/" + id).flashing("error", "Invalid Spotify Link provided"));
            }
        }
        if (artist.getTwitterLink() != null && !artist.getTwitterLink().isEmpty()) {
            if (!artist.getTwitterLink().contains("twitter.com/")) {
                return supplyAsync(() -> redirect("/artists/" + id).flashing("error", "Invalid Twitter Link provided"));
            }
        }
        return artistRepository.editArtistProfile(id, artist, artistProfileForm, currentUserId).thenApplyAsync(artId -> redirect("/artists/" + artId).flashing("info", "Artist " + artist.getArtistName() + " has been updated."));
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

    /**
     * Get follower count for the artist
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Integer> getFollowerCount(int artistId) {
        return supplyAsync(() -> artistRepository.getNumFollowers(artistId));
    }
}
