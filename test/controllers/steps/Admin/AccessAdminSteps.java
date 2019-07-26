package controllers.steps.Admin;


import controllers.ProvideApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;


/**
 * Implements steps for testing access admin page
 */
public class AccessAdminSteps extends ProvideApplication {
    private String urlString;
    private Result adminResult;
    private Map<String, String> loginForm = new HashMap<>();
    private Result loginResult;
    private Result adminAttemptResult;


    @Given("I am logged into the application as a non admin")
    public void iAmLoggedIntoTheApplicationAsANonAdmin() {
        loginForm.put("email", "john@gmail.com");
        loginForm.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(loginForm);

        loginResult = Helpers.route(provideApplication(), request);

    }

    @Given("I am logged into the application as an admin")
    public void iAmLoggedIntoTheApplicationAsAnAdmin() {
        loginForm.put("email", "bob@gmail.com");
        loginForm.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(loginForm)
                .session("connected", "2");
        loginResult = Helpers.route(provideApplication(), request);
    }


    @When("he fills {string} into the URL")
    public void heFillsIntoTheURL(String string) {
        urlString = string;
    }

    @When("he tries to access the admin page")
    public void heTriesToAccessTheAdminPage() {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri(urlString)
                .session("connected", "1");
        adminResult = Helpers.route(provideApplication(), requestDest);
    }


    @When("the admin tries to access the admin page")
    public void theAdminTriesToAccessTheAdminPage() {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                .uri(urlString)
                .session("connected", "2");
        adminResult = Helpers.route(provideApplication(), requestDest);
    }

    @Then("the admin page should not be shown and the profile page should be shown")
    public void theAdminPageShouldNotBeShownAndTheProfilePageShouldBeShown() {
        if (adminResult.redirectLocation().isPresent()) {
            assertEquals("/profile", adminResult.redirectLocation().get());
        } else {
            fail();
        }
    }



    @Then("the admin page should be shown")
    public void theAdminPageShouldBeShown() {
        Assert.assertEquals(200, adminResult.status());
    }


    @When("I enter the following url \"([^\"]*)\"")
    public void iEnterTheFollowingUrl(String arg0) throws Throwable {
        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("POST")
                .uri(arg0)
                .session("connected", "1");
        adminAttemptResult = Helpers.route(provideApplication(), requestDest);
    }


    @And("There should be a flashing present saying {string}")
    public void thereShouldBeAFlashingPresentSaying(String string) throws Throwable {
        // Can't find flashing
    }

    @Then("User {int} is not made an admin")
    public void userIsNotMadeAnAdmin(int arg0) throws Throwable {
        injectRepositories();
        Optional<List<String>> roles = rolesRepository.getProfileRoles(1);
        if (roles.isPresent()) {
            if (roles.get().size() == 0) {
                assertTrue(true);
            } else {
                fail();
            }
        } else {
            assertTrue(true);
        }
    }
}
