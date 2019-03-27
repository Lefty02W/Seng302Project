package controllers.steps;


import com.google.common.collect.ImmutableMap;
import controllers.routes;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.en.And;
import org.junit.Assert;
import models.Profile;
import play.Application;
import play.Mode;
import play.data.Form;

import play.data.FormFactory;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import javax.inject.Inject;

import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.route;

public class CreateUserSteps {

    private Form<Profile> profileForm;
    protected Application application;
    protected Profile profile;


    @Inject FormFactory formFactory;
    @Before
    public void setUp() {
        application = new GuiceApplicationBuilder().in(Mode.TEST).build();
        profile = new Profile(null, null, null, null, null, null,null,
                null, null, null, null, null, false);
        Helpers.start(application);
        formFactory = new FormFactory(null, null, null, null);
        profileForm = formFactory.form(Profile.class);
    }


    @Given("John is at the sign up page")
    public void at_sign_up_page() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/user/create")
                .host("localhost:9000");
        Result result = route(application, request);
        Assert.assertEquals(200,  result.status());
    }

    @When("he fills in First Name with {string}")
    public void fill_first_name(String firstName) {
        profile.setFirstName(firstName);
        Assert.assertEquals(profile.getFirstName(), firstName);
    }


    @And("he fills in Middle Name with {string}")
    public void fill_middle_name(String middleName) {
        profile.setMiddleName(middleName);
        Assert.assertEquals(profile.getMiddleName(), middleName);
    }

    @And("he fills in Last Name with {string}")
    public void fill_last_name(String lastName) {
        profile.setLastName(lastName);
        Assert.assertEquals(profile.getLastName(), lastName);
    }

    @And("he fills in Email with {string}")
    public void fill_email(String email) {
        profile.setEmail(email);
        Assert.assertEquals(profile.getEmail(), email);
    }


    @And("the email he used does not exist")
    public void email_not_exist() {
        throw new cucumber.api.PendingException();
    }


    @And("he fills in Gender with {string}")
    public void fill_gender(String gender) {
        throw new cucumber.api.PendingException();
    }


    @And("he fills in Birth date with {string}")
    public void fill_birth_date(String birthDate) {
        throw new cucumber.api.PendingException();
    }


    @And("he fills in Nationalities with {string}")
    public void fill_nationality(String nationality) {
        throw new cucumber.api.PendingException();
    }

    @And("he fills in Passport with {string}")
    public void fill_passport(String passport) {
        throw new cucumber.api.PendingException();
    }

    @And("he selects {string} from Traveller Type")
    public void select_traveller_type(String type) {
        throw new cucumber.api.PendingException();
    }

    @And("he presses OK")
    public void press_ok() {
        throw new cucumber.api.PendingException();
    }

    @Then("the login form should be shown")
    public void login() {
        throw new cucumber.api.PendingException();
    }

}
