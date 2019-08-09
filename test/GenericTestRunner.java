import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
import play.Application;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import repository.*;

@RunWith(Cucumber.class)
@CucumberOptions(features = "test/features",
        plugin = {"pretty", "html:target/site/cucumber -pretty", "json:target/cucumber.json"},
        glue = "controllers.steps",
        snippets = SnippetType.CAMELCASE)

public class GenericTestRunner extends WithApplication {

    protected ProfileRepository profileRepository;
    protected DestinationRepository destinationRepository;
    protected TripRepository tripRepository;
    protected RolesRepository rolesRepository;
    protected PhotoRepository photoRepository;
    protected TreasureHuntRepository treasureHuntRepository;
    protected ProfilePassportCountryRepository profilePassportCountryRepository;
    protected PassportCountryRepository passportCountryRepository;
    protected UndoStackRepository undoStackRepository;

    @Override
    public Application provideApplication() {
        return new GuiceApplicationBuilder().in(Mode.TEST).build();
    }

    protected void injectRepositories() {
        app = provideApplication();
        treasureHuntRepository = app.injector().instanceOf(TreasureHuntRepository.class);
        rolesRepository = app.injector().instanceOf(RolesRepository.class);
        tripRepository = app.injector().instanceOf(TripRepository.class);
        profileRepository = app.injector().instanceOf(ProfileRepository.class);
        destinationRepository = app.injector().instanceOf(DestinationRepository.class);
        photoRepository = app.injector().instanceOf(PhotoRepository.class);
        profilePassportCountryRepository = app.injector().instanceOf(ProfilePassportCountryRepository.class);
        passportCountryRepository = app.injector().instanceOf(PassportCountryRepository.class);
        undoStackRepository = app.injector().instanceOf(UndoStackRepository.class);
    }
}