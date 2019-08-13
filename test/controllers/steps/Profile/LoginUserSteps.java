package controllers.steps.Profile;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LoginUserSteps {

    Map<String, String> loginForm = new HashMap<>();
    Result redirectLoginResult;
    Result loginResult;

    @Given("John is at the login page")
    public void johnIsAtTheLoginPage() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/");
        redirectLoginResult = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(200, redirectLoginResult.status());
    }


    @When("he fills in his email with {string}")
    public void heFillsInEmailWith(String string) {
        loginForm.put("email", string);
    }


    @And("he fills in his password with {string}")
    public void heFillsInHisPasswordWith(String string) {
        loginForm.put("password", string);
    }


    @And("he presses Login")
    public void hePressesLogin() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(loginForm)
                .session("connected", "1");

        loginResult = Helpers.route(TestApplication.getApplication(), request);
    }


    @Then("the profile page should be shown")
    public void theProfilePageShouldBeShown() {
        assertEquals(303, loginResult.status());
        assertEquals("/profile", loginResult.redirectLocation().get());
    }

    @Then("he is not redirected to the profile page")
    public void iAmNotRedirectedToTheProfilePage() {
        assertEquals(303, loginResult.status());
        assertEquals("/", loginResult.redirectLocation().get());
    }

    @Given("^Johnny Sins is at the login page$")
    public void johnnySinsIsAtTheLoginPage() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/");
        redirectLoginResult = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(200, redirectLoginResult.status());
    }
}