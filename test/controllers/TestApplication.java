package controllers;

import play.Application;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.Helpers;
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

    public static void stopApplication() {
        Helpers.stop(application);
        application = null;
    }

    private static void checkApplication() {
        if (application == null) {
            application = new GuiceApplicationBuilder().in(Mode.TEST).build();
        }
    }

    public static TreasureHuntRepository getTreasureHuntRepository() {
        checkApplication();
        return application.injector().instanceOf(TreasureHuntRepository.class);
    }

    public static UndoStackRepository getUndoStackRepository() {
        checkApplication();
        return application.injector().instanceOf(UndoStackRepository.class);
    }

    public static ProfilePassportCountryRepository getProfilePassportCountryRepository() {
        checkApplication();
        return application.injector().instanceOf(ProfilePassportCountryRepository.class);
    }

    public static PhotoRepository getPhotoRepository() {
        checkApplication();
        return application.injector().instanceOf(PhotoRepository.class);
    }

    public static DestinationRepository getDestinationRepository() {
        checkApplication();
        return application.injector().instanceOf(DestinationRepository.class);
    }

    public static ProfileRepository getProfileRepository() {
        checkApplication();
        return application.injector().instanceOf(ProfileRepository.class);
    }

    public static TripRepository getTripRepository() {
        checkApplication();
        return application.injector().instanceOf(TripRepository.class);
    }

    public static RolesRepository getRolesRepository() {
        checkApplication();
        return application.injector().instanceOf(RolesRepository.class);
    }

    public static PassportCountryRepository getPassportCountryRepository() {
        checkApplication();
        return application.injector().instanceOf(PassportCountryRepository.class);
    }

    public static ArtistRepository getArtistRepository() {
        checkApplication();
        return application.injector().instanceOf(ArtistRepository.class);
    }

    public static GenreRepository getGenreRepository() {
        checkApplication();
        return application.injector().instanceOf(GenreRepository.class);
    }

}
