package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.Files;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import repository.*;
import utility.Country;
import views.html.artists;
import views.html.events;
import views.html.viewArtist;

import javax.inject.Inject;
import java.nio.file.Paths;
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
    private final Form<ArtistPhotoFormData> artistPhotoForm;
    private MessagesApi messagesApi;
    private final ArtistRepository artistRepository;
    private final ProfileRepository profileRepository;
    private final PassportCountryRepository passportCountryRepository;
    private final GenreRepository genreRepository;
    private final EventRepository eventRepository;
    private final Form<ArtistFormData> searchForm;
    private final DestinationRepository destinationRepository;
    private final ArtistProfilePictureRepository artistProfilePictureRepository;
    private final PersonalPhotoRepository personalPhotoRepository;
    private final PhotoRepository photoRepository;
    private final AttendEventRepository attendEventRepository;
    private final UndoStackRepository undoStackRepository;
    private final long MAX_PHOTO_SIZE = 8000000;


    @Inject
    public ArtistController(FormFactory artistProfileFormFactory, MessagesApi messagesApi,
                            ArtistRepository artistRepository, ProfileRepository profileRepository,
                            PassportCountryRepository passportCountryRepository,
                            GenreRepository genreRepository, EventRepository eventRepository,
                            DestinationRepository destinationRepository,
                            ArtistProfilePictureRepository artistProfilePictureRepository,
                            PersonalPhotoRepository personalPhotoRepository, PhotoRepository photoRepository,
                            AttendEventRepository attendEventRepository, UndoStackRepository undoStackRepository){

        this.artistForm = artistProfileFormFactory.form(Artist.class);
        this.messagesApi = messagesApi;
        this.artistRepository = artistRepository;
        this.profileRepository = profileRepository;
        this.passportCountryRepository = passportCountryRepository;
        this.genreRepository = genreRepository;
        this.searchForm = artistProfileFormFactory.form(ArtistFormData.class);
        this.eventRepository = eventRepository;
        this.destinationRepository = destinationRepository;
        this.artistProfilePictureRepository = artistProfilePictureRepository;
        this.personalPhotoRepository = personalPhotoRepository;
        this.photoRepository = photoRepository;
        this.artistPhotoForm = artistProfileFormFactory.form(ArtistPhotoFormData.class);
        this.attendEventRepository = attendEventRepository;
        this.undoStackRepository = undoStackRepository;
    }



    /**
     * Endpoint method to search for only a genre used by the hash tags
     * @param request Http Request
     * @param genreId Id of the genre to search
     * @return CompletionStage that redirects to the artists page displaying only artists with the given genre
     */
    public CompletionStage<Result> searchGenre(Http.Request request, Integer genreId) {
        Integer profId = SessionController.getCurrentUserId(request);
        EventFormData eventFormSent = new EventFormData();
        String genreName = genreRepository.getGenre(genreId);
        ArtistFormData formData = new ArtistFormData();
        List<Artist> artistsList = artistRepository.searchArtist("", genreName, "", 0, 0, profId);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            if(profile.isPresent()) {
                    formData.setGenre(genreName);
                    return ok(artists.render(searchForm, profile.get(), genreRepository.getAllGenres(), profileRepository.getAllEbeans(), Country.getInstance().getAllCountries(), artistsList, artistRepository.getFollowedArtists(profId), artistRepository.getAllUserArtists(profId), formData, request, messagesApi.preferred(request)));
            } else {
            return redirect("/artists");
            }
        });
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
                .thenApplyAsync(profileRec -> profileRec.map(profile -> {
                    undoStackRepository.clearStackOnAllowed(profileRec.get());

                    return ok(artists.render(searchForm, profile,
                        genreRepository.getAllGenres(), profileRepository.getAllEbeans(),
                        Country.getInstance().getAllCountries(),  artistRepository.getPagedArtists(0),
                        artistRepository.getFollowedArtists(profId), artistRepository.getAllUserArtists(profId), null,
                        request, messagesApi.preferred(request)));
                    }).orElseGet(() -> redirect("/profile")));

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
                            return redirect("/artists");
                        }

                        searchForm.fill(formData);
                        return ok(artists.render(searchForm, profile.get(), genreRepository.getAllGenres(), profileRepository.getAllEbeans(), Country.getInstance().getAllCountries(), artistRepository.searchArtist(formData.name, formData.genre, formData.country, followed, created, profId), artistRepository.getFollowedArtists(profId), artistRepository.getAllUserArtists(profId), formData, request, messagesApi.preferred(request)));
                    } else {
                        return redirect("/artists");
                    }
        });
    }


    /**
     * Method to determine if an artist has a profile picture linked to it
     * - returns the profile photo if it exists
     * - returns null if there is none (this is handled on the frontend)
     * @param artistId id of the artist
     * @return an optional photo object of the artist picture or null
     */
    private Photo getCurrentArtistProfilePhoto(Integer artistId) {
        ArtistProfilePhoto artistPictureLink = artistProfilePictureRepository.lookup(artistId);
        if (artistPictureLink != null) {
            Optional<Photo> optionalPhoto = photoRepository.getImage(artistPictureLink.getPhotoId());
            return optionalPhoto.orElse(null);
        } else { return null; }
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

        Photo artistPicture = getCurrentArtistProfilePhoto(artistId);

        if (artist == null) {
            return profileRepository.findById (profId).thenApplyAsync(profile -> redirect("/artists"));
        }
        return profileRepository.findById(profId)
                .thenApplyAsync(profileRec -> profileRec.map(profile ->
                {
                    List<Artist> userArtists = artistRepository.getAllUserArtists(profId);
                    undoStackRepository.clearStackOnAllowed(profileRec.get());
                    if (userArtists.contains(artist)) {
                        return ok(viewArtist.render(profile, artist, new ArrayList<Events>(),
                                Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), 0,
                                new PaginationHelper(), profileRepository.getAllEbeans(), destinationRepository.getAllFollowedOrOwnedDestinations(profId),
                                artistRepository.getAllUserArtists(profId), new RoutedObject<Events>(null, false, false), null, artistPicture, artistRepository.getFollowedArtists(profId), request, messagesApi.preferred(request)));
                    } else {
                        return ok(viewArtist.render(profile, artist, new ArrayList<Events>(),
                                new ArrayList<String>(), new ArrayList<MusicGenre>(), 0,
                                new PaginationHelper(), new ArrayList<Profile>(), new ArrayList<Destination>(),
                                new ArrayList<Artist>(), new RoutedObject<Events>(null, false, false), null, artistPicture, artistRepository.getFollowedArtists(profId), request, messagesApi.preferred(request)));
                    }

                })
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
        Photo artistPicture = getCurrentArtistProfilePhoto(id);
        if (artist == null) {
            return supplyAsync(() -> redirect("/artists"));
        }
        PaginationHelper paginationHelper = new PaginationHelper(offset, offset, offset, 1, true, true, eventRepository.getNumArtistEvents(id));
        paginationHelper.alterNext(8);
        paginationHelper.alterPrevious(8);
        paginationHelper.checkButtonsEnabled();
        return profileRepository.findById(profId)
                .thenApplyAsync(profileOpt -> profileOpt.map(profile ->
                {
                    List<Artist> userArtists = artistRepository.getAllUserArtists(profId);
                    if (userArtists.contains(artist)) {
                        return ok(viewArtist.render(profile, artist, eventRepository.getArtistEventsPage(id, offset), Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), 1,
                                paginationHelper, profileRepository.getAllEbeans(), destinationRepository.getAllFollowedOrOwnedDestinations(profId),
                                artistRepository.getAllUserArtists(profId), new RoutedObject<Events>(null, false, false),
                                null, artistPicture, artistRepository.getFollowedArtists(profId), request, messagesApi.preferred(request)));
                    } else {
                        return ok(viewArtist.render(profile, artist, eventRepository.getArtistEventsPage(id, offset), new ArrayList<String>(),
                                new ArrayList<MusicGenre>(), 1,
                                paginationHelper, new ArrayList<Profile>(), new ArrayList<Destination>(),
                                new ArrayList<Artist>(), new RoutedObject<Events>(null, false, false),
                                null, artistPicture, artistRepository.getFollowedArtists(profId), request, messagesApi.preferred(request)));
                    }
                })
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
        Photo artistPicture = getCurrentArtistProfilePhoto(id);
        if (artist == null) {
            return supplyAsync(() -> redirect("/artists"));
        }
        return profileRepository.findById(profId)
                .thenApplyAsync(profileOpt -> profileOpt.map(profile ->
                {
                    List<Artist> userArtists = artistRepository.getAllUserArtists(profId);
                    if (userArtists.contains(artist)) {
                        return ok(viewArtist.render(profile, artist, new ArrayList<Events>(),
                                Country.getInstance().getAllCountries(), genreRepository.getAllGenres(), 2,
                                new PaginationHelper(), profileRepository.getAllEbeans(), destinationRepository.getAllFollowedOrOwnedDestinations(profId),
                                artistRepository.getAllUserArtists(profId), new RoutedObject<Events>(null, false, false),
                                null, artistPicture, artistRepository.getFollowedArtists(profId),request, messagesApi.preferred(request)));
                    } else {
                        return ok(viewArtist.render(profile, artist, new ArrayList<Events>(),
                                new ArrayList<String>(), new ArrayList<MusicGenre>(), 2,
                                new PaginationHelper(), new ArrayList<Profile>(), new ArrayList<Destination>(),
                                new ArrayList<Artist>(), new RoutedObject<Events>(null, false, false),
                                null, artistPicture, artistRepository.getFollowedArtists(profId), request, messagesApi.preferred(request)));
                    }
                })
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
                .thenApplyAsync(x -> redirect("/artists").flashing("info", "Unfollowed artist: " + artistRepository.getArtistById(artistId).getArtistName()));
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
                .thenApplyAsync(x -> redirect("/artists").flashing("info", "Followed artist: " + artistRepository.getArtistById(artistId).getArtistName()));
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
     * Method to return the follower count of an artist
     * @param artistId Id of the artist to find follower count
     * @return CompletionStage of the artist Id
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Integer> getFollowerCount(int artistId) {
        return supplyAsync(() -> artistRepository.getNumFollowers(artistId));
    }


    /**
     * Endpoint method to withdraw from an event with the given eventID from the artist page
     * @param request http request
     * @param eventId event id
     * @param artistId artist id
     * @return redirects back to event page
     */
    public Result leaveEvent(Http.Request request, Integer artistId, Integer eventId) {
        attendEventRepository.delete(attendEventRepository.getAttendEventId(eventId, SessionController.getCurrentUserId(request)));
        return redirect("/events/details/"+eventId).flashing("info", "No longer going to event");
    }


    /**
     * Endpoint method to attend an event with the given eventID from the artist page
     * @param request http request
     * @param eventId event id
     * @param artistId artist Id
     * @return redirects back to event page
     */
    public Result attendEvent(Http.Request request, Integer artistId, Integer eventId) {

        attendEventRepository.insert(new AttendEvent(eventId, SessionController.getCurrentUserId(request)));
        return redirect("/events/details/"+eventId).flashing("info", "No longer going to event");
    }

    /**
     * Endpoint method for an artist admin to rmeove the artist profile photo
     *
     * @param request request to remove photo
     * @param id id of artist to remove photo for
     * @return Redirect back to the artists detailed page
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> removePhoto(Http.Request request, Integer id) {
        return artistProfilePictureRepository.removeArtistProfilePicture(id).thenApplyAsync(artist -> redirect("/artists/" + artist));
    }


    /**
     * Endpoint to upload a profile photo for an artist
     * The method will extract the photo from the request,
     * save it to the user's photos, use the created personal photo id,
     * create an instance of ArtistProfilePhoto and pass that to the repo to save
     *
     * @param request - The HTTP Request for uploading a profile photo
     * @param id - ID of the artist to set profile photo for
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> uploadProfilePhoto(Http.Request request, Integer id) {
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> picture = body.getFile("image");

        if(artistProfilePictureRepository.lookup(id) != null) {
            artistProfilePictureRepository.removeArtistProfilePicture(id);
        }

        String fileName = picture.getFilename();
        String contentType = picture.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/gif")) {
            return supplyAsync(() -> redirect("/artists/"+id).flashing("error", "Invalid file type!"));
        }
        long fileSize = picture.getFileSize();
        if (fileSize >= MAX_PHOTO_SIZE) {
            return supplyAsync(() -> redirect("artists/"+ id).flashing("error",
                    "File size must not exceed 8MB!"));
        }

        Files.TemporaryFile tempFile = picture.getRef();
        String filepath = System.getProperty("user.dir") + "/photos/personalPhotos/" + fileName;
        tempFile.copyTo(Paths.get(filepath), true);
        Photo photo = new Photo("photos/personalPhotos/" + fileName, contentType, 0, fileName);
        photoRepository.insert(photo).thenApplyAsync(photoId ->
                artistProfilePictureRepository.addArtistProfilePicture(new ArtistProfilePhoto(id, photoId)));

        return supplyAsync(() -> redirect("/artists/"+ id));
    }
}
