package controllers;

import play.Application;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import repository.*;

/**
 * Utility class used to run tests
 *  - provides an instance of the application in test mode to use
 *  - provides an instance of each of the repositories to use
 */
public class TestApplication {

    private static Application application = null;
    private static ProfileRepository profileRepository;
    private static DestinationRepository destinationRepository;
    private static TripRepository tripRepository;
    private static RolesRepository rolesRepository;
    private static PhotoRepository photoRepository;
    private static TreasureHuntRepository treasureHuntRepository;
    private static ProfilePassportCountryRepository profilePassportCountryRepository;
    private static PassportCountryRepository passportCountryRepository;
    private static UndoStackRepository undoStackRepository;
    private static ArtistRepository artistRepository;
    private static GenreRepository genreRepository;
    private static EventRepository eventRepository;
    private static AttendEventRepository attendEventRepository;

    /**
     * Static method to get an instance of the test application
     * If the application is null a new one is started
     *
     * @return the test Application
     */
    public static Application getApplication() {
        if (application == null) {
            application = new GuiceApplicationBuilder().in(Mode.TEST).build();
        }
        return application;
    }

    /**
     * Method to check if the application is running before creating a repository class instance
     */
    private static void checkApplication() {
        if (application == null) {
            application = new GuiceApplicationBuilder().in(Mode.TEST).build();
        }
    }

    /**
     * Creates an instance of the TreasureHuntRepository
     * @return TreasureHuntRepository instance
     */
    public static TreasureHuntRepository getTreasureHuntRepository() {
        checkApplication();
        if (treasureHuntRepository == null) {
            treasureHuntRepository = application.injector().instanceOf(TreasureHuntRepository.class);
        }
        return treasureHuntRepository;
    }

    /**
     * Creates an instance of the UndoStackRepository
     * @return UndoStackRepository instance
     */
    public static UndoStackRepository getUndoStackRepository() {
        checkApplication();
        if (undoStackRepository == null) {
            undoStackRepository = application.injector().instanceOf(UndoStackRepository.class);
        }
        return undoStackRepository;
    }

    /**
     * Creates an instance of the ProfilePassportCountryRepository
     * @return ProfilePassportCountryRepository instance
     */
    public static ProfilePassportCountryRepository getProfilePassportCountryRepository() {
        checkApplication();
        if (profilePassportCountryRepository == null) {
            profilePassportCountryRepository = application.injector().instanceOf(ProfilePassportCountryRepository.class);
        }
        return profilePassportCountryRepository;
    }

    /**
     * Creates an instance of the PhotoRepository
     * @return PhotoRepository instance
     */
    public static PhotoRepository getPhotoRepository() {
        checkApplication();
        if (photoRepository == null) {
            photoRepository = application.injector().instanceOf(PhotoRepository.class);
        }
        return photoRepository;
    }

    /**
     * Creates an instance of the DestinationRepository
     * @return DestinationRepository instance
     */
    public static DestinationRepository getDestinationRepository() {
        checkApplication();
        if (destinationRepository == null) {
            destinationRepository = application.injector().instanceOf(DestinationRepository.class);
        }
        return destinationRepository;
    }

    /**
     * Creates an instance of the ProfileRepository
     * @return ProfileRepository instance
     */
    public static ProfileRepository getProfileRepository() {
        checkApplication();
        if (profileRepository == null) {
            profileRepository = application.injector().instanceOf(ProfileRepository.class);
        }
        return profileRepository;
    }

    /**ther successful Engineering
     * Creates an instance of the TripRepository
     * @return TripRepository instance
     */
    public static TripRepository getTripRepository() {
        checkApplication();
        if (tripRepository == null) {
            tripRepository = application.injector().instanceOf(TripRepository.class);
        }
        return tripRepository;
    }

    /**
     * Creates an instance of the RolesRepository
     * @return RolesRepository instance
     */
    public static RolesRepository getRolesRepository() {
        checkApplication();
        if (rolesRepository == null) {
            rolesRepository = application.injector().instanceOf(RolesRepository.class);
        }
        return rolesRepository;
    }

    /**
     * Creates an instance of the PassportCountryRepository
     * @return PassportCountryRepository instance
     */
    public static PassportCountryRepository getPassportCountryRepository() {
        checkApplication();
        if (passportCountryRepository == null) {
            passportCountryRepository = application.injector().instanceOf(PassportCountryRepository.class);
        }
        return passportCountryRepository;
    }

    /**
     * Creates an instance of the ArtistRepository
     * @return ArtistRepository instance
     */
    public static ArtistRepository getArtistRepository() {
        checkApplication();
        if (artistRepository == null) {
            artistRepository = application.injector().instanceOf(ArtistRepository.class);
        }
        return artistRepository;
    }

    /**
     * Creates an instance of the GenreRepository
     * @return GenreRepository instance
     */
    public static GenreRepository getGenreRepository() {
        checkApplication();
        if (genreRepository == null) {
            genreRepository = application.injector().instanceOf(GenreRepository.class);
        }
        return genreRepository;
    }

    /**
     * Creates an instance of the EventRepository
     * @return EventRepository instance
     */
    public static EventRepository getEventRepository() {
        checkApplication();
        if (eventRepository == null) {
            eventRepository = application.injector().instanceOf(EventRepository.class);
        }
        return eventRepository;
    }

    /**
     * Creates an instance of the AttendEventRepository
     * @return AttendEventRepository instance
     */
    public static AttendEventRepository getAttendEventRepository() {
        checkApplication();
        if (attendEventRepository == null) {
            attendEventRepository = application.injector().instanceOf(AttendEventRepository.class);
        }
        return attendEventRepository;
    }

    }
