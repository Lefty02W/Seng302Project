package controllers;

import play.Application;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import repository.*;

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
        return application.injector().instanceOf(TreasureHuntRepository.class);
    }

    /**
     * Creates an instance of the UndoStackRepository
     * @return UndoStackRepository instance
     */
    public static UndoStackRepository getUndoStackRepository() {
        checkApplication();
        return application.injector().instanceOf(UndoStackRepository.class);
    }

    /**
     * Creates an instance of the ProfilePassportCountryRepository
     * @return ProfilePassportCountryRepository instance
     */
    public static ProfilePassportCountryRepository getProfilePassportCountryRepository() {
        checkApplication();
        return application.injector().instanceOf(ProfilePassportCountryRepository.class);
    }

    /**
     * Creates an instance of the PhotoRepository
     * @return PhotoRepository instance
     */
    public static PhotoRepository getPhotoRepository() {
        checkApplication();
        return application.injector().instanceOf(PhotoRepository.class);
    }

    /**
     * Creates an instance of the DestinationRepository
     * @return DestinationRepository instance
     */
    public static DestinationRepository getDestinationRepository() {
        checkApplication();
        return application.injector().instanceOf(DestinationRepository.class);
    }

    /**
     * Creates an instance of the ProfileRepository
     * @return ProfileRepository instance
     */
    public static ProfileRepository getProfileRepository() {
        checkApplication();
        return application.injector().instanceOf(ProfileRepository.class);
    }

    /**
     * Creates an instance of the TripRepository
     * @return TripRepository instance
     */
    public static TripRepository getTripRepository() {
        checkApplication();
        return application.injector().instanceOf(TripRepository.class);
    }

    /**
     * Creates an instance of the RolesRepository
     * @return RolesRepository instance
     */
    public static RolesRepository getRolesRepository() {
        checkApplication();
        return application.injector().instanceOf(RolesRepository.class);
    }

    /**
     * Creates an instance of the PassportCountryRepository
     * @return PassportCountryRepository instance
     */
    public static PassportCountryRepository getPassportCountryRepository() {
        checkApplication();
        return application.injector().instanceOf(PassportCountryRepository.class);
    }

    /**
     * Creates an instance of the ArtistRepository
     * @return ArtistRepository instance
     */
    public static ArtistRepository getArtistRepository() {
        checkApplication();
        return application.injector().instanceOf(ArtistRepository.class);
    }

    /**
     * Creates an instance of the GenreRepository
     * @return GenreRepository instance
     */
    public static GenreRepository getGenreRepository() {
        checkApplication();
        return application.injector().instanceOf(GenreRepository.class);
    }

}
