import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "test/features",
        plugin = {"pretty", "html:target/site/cucumber -pretty", "json:target/cucumber.json"},
        glue = "controllers.steps",
        snippets = SnippetType.CAMELCASE)

public class GenericTestRunner {

    /**
     * This method populates the database with test data that is used during acceptance tests
     */
    @BeforeClass
    public static void setUpTestData() {

    }
}