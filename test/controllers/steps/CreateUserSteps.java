package controllers.steps;


import com.google.common.collect.ImmutableMap;
import controllers.routes;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.en.And;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import models.Profile;
import play.Application;
import play.Mode;
import play.data.Form;

import play.data.FormFactory;
import play.data.format.Formats;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import javax.inject.Inject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static play.test.Helpers.*;

public class CreateUserSteps {

    private Form<Profile> profileForm;
    protected Application application;
    protected Profile profile;
    private Optional redirectedPage;

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
        Profile duplicate = Profile.find.query()
                .where()
                .eq("email", profile.getEmail())
                .findOne();
        Assert.assertNull(duplicate);
    }


    @And("he fills in Gender with {string}")
    public void fill_gender(String gender) {
        profile.setGender(gender);
        Assert.assertEquals(profile.getGender(), gender);
    }


    @And("he fills in Birth date with {string}")
    public void fill_birth_date(String birthDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(birthDate);
            profile.setBirthDate(date);
            Assert.assertEquals(profile.getBirthDate(), date);
        } catch (ParseException e) {
            throw new AssertionFailedError();
        }
        Assert.assertNotNull(date);
    }


    @And("he fills in Nationalities with {string}")
    public void fill_nationality(String nationality) {
        profile.setNationalities(nationality);
        Assert.assertEquals(profile.getNationalities(), nationality);
    }

    @And("he fills in Passport with {string}")
    public void fill_passport(String passport) {
        profile.setPassports(passport);
        Assert.assertEquals(profile.getPassports(), passport);
    }

    @And("he selects {string} from Traveller Type")
    public void select_traveller_type(String type) {
        profile.setTravellerTypes(type);
        Assert.assertEquals(profile.getTravellerTypes(), type);
    }

    @And("he presses OK")
    public void press_ok() {
        Date now = new Date();
        profile.setTimeCreated(now);
        Assert.assertEquals(profile.getTimeCreated(), now);
        profileForm.fill(profile);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/user/create")
                .bodyForm(profileForm.rawData())
                .host("localhost:9000");
        Result result = route(application, request);
        redirectedPage = result.redirectLocation();
        // Expect a redirect!
        Assert.assertEquals(303,  result.status());
    }

    @Then("the login form should be shown")
    public void login() {
        Assert.assertEquals("/", redirectedPage.get());
    }

}
