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

    protected ProfileRepository profileRepository;
    protected DestinationRepository destinationRepository;
    protected RolesRepository rolesRepository;

    @Override
    public Application provideApplication() {
        return new GuiceApplicationBuilder().in(Mode.TEST).build();
    }


    protected Integer loginUser() {
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
        app.injector().instanceOf(PhotoRepository.class);
        app.injector().instanceOf(TripDestinationsRepository.class);
        app.injector().instanceOf(TripRepository.class);
        app.injector().instanceOf(NationalityRepository.class);
        app.injector().instanceOf(PassportCountryRepository.class);

        rolesRepository = app.injector().instanceOf(RolesRepository.class);
        profileRepository = app.injector().instanceOf(ProfileRepository.class);
        destinationRepository = app.injector().instanceOf(DestinationRepository.class);
    }

    protected ArrayList<Destination> getUserDest(int id) {
        return destinationRepository.getUserDestinations(id);
    }


}
