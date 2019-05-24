package controllers;

import models.Destination;
import models.Profile;
import play.Application;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import repository.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProvideApplication extends WithApplication {

    protected DestinationRepository destinationRepository;
    protected PhotoRepository photoRepository;
    protected ProfileRepository profileRepository;
    protected TripDestinationsRepository tripDestinationsRepository;
    protected TripRepository tripRepository;
    protected NationalityRepository nationalityRepository;
    protected PassportCountryRepository passportRepository;


    @Override
    public Application provideApplication() {
        return new GuiceApplicationBuilder().in(Mode.TEST).build();
    }


    Integer loginUser() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "john@gmail.com");
        formData.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(provideApplication(), request);

        for (Profile profile : Profile.find.all()) {
            if (profile.getEmail().equals("john@gmail.com")) {
                return profile.getProfileId();
            }
        }
        return 0;
    }


    protected void injectRepositories() {
        app = provideApplication();
        profileRepository = app.injector().instanceOf(ProfileRepository.class);
        destinationRepository = app.injector().instanceOf(DestinationRepository.class);
        photoRepository = app.injector().instanceOf(PhotoRepository.class);
        tripDestinationsRepository = app.injector().instanceOf(TripDestinationsRepository.class);
        tripRepository = app.injector().instanceOf(TripRepository.class);
        nationalityRepository = app.injector().instanceOf(NationalityRepository.class);
        passportRepository = app.injector().instanceOf(PassportCountryRepository.class);
    }

    protected ArrayList<Destination> getUserDest(int id) {
        return destinationRepository.getUserDestinations(id);
    }


}
