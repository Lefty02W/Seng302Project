package controllers;

import models.Profile;
import models.Trip;
import org.junit.Before;
import play.Application;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import repository.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProvideApplication extends WithApplication {

    protected DestinationRepository destinationRepository;
    protected ImageRepository imageRepository;
    protected ProfileRepository profileRepository;
    protected TripDestinationsRepository tripDestinationsRepository;
    protected TripRepository tripRepository;

    private static boolean setUpComplete = false;


    @Override
    public Application provideApplication() {
        return new GuiceApplicationBuilder().in(Mode.TEST).build();
    }


    void loginUser() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "admin");
        formData.put("password", "admin123");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(provideApplication(), request);
    }

    @Before
    public void setUpDb() {
        if (!setUpComplete) {
            profileRepository = app.injector().instanceOf(ProfileRepository.class);
            destinationRepository = app.injector().instanceOf(DestinationRepository.class);
            imageRepository = app.injector().instanceOf(ImageRepository.class);
            tripDestinationsRepository = app.injector().instanceOf(TripDestinationsRepository.class);
            tripRepository = app.injector().instanceOf(TripRepository.class);

            //TODO: Add more insert data here when other repositories are inserted

            profileRepository.insert(new Profile("John", "James", "john@gmial.com",
                    "password", new Date(), "NZ", "Male", new Date(), "NZ",
                    "Backpacker,Gap Year", new ArrayList<Trip>(), false));
            profileRepository.insert(new Profile("Jenny", "Smith", "jenny@gmail.com",
                    "password", new Date(), "NZ", "Female", new Date(), "NZ",
                    "Thrillseeker", new ArrayList<Trip>(), false));
        }
    }

}
