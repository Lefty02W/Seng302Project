package controllers.steps;


import controllers.ProvideApplication;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * This class provides the steps need to test the EditProfile feature
 */
public class EditProfileSteps extends ProvideApplication {

    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> editForm = new HashMap<>();

    private Result redirectResultEdit;
    private Result loginResult;


    // Scenario: I can perform an editDestinations of my profile - start
    @Given("I am logged into the application")
    public void iAmLoggedIntoTheApplication() {
        loginForm.put("email", "john@gmail.com");
        loginForm.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(loginForm);

        loginResult = Helpers.route(provideApplication(), request);
        assertEquals("/profile", loginResult.redirectLocation().get());
    }

    @Given("I am on the edit profile page")
    public void iAmOnTheEditProfilePage() {
        // Mocking auto fill operation of users current data
        editForm.put("firstName", "John");
        editForm.put("lastName", "James");
        editForm.put("email", "john@gmail.com");
        editForm.put("password", "password");
        editForm.put("birthDate", "1970-01-13");
        editForm.put("passportsForm", "NZ");
        editForm.put("gender", "Male");
        editForm.put("nationalitiesForm", "password");
        editForm.put("travellerTypesForm", "Backpacker,Gap Year");
        if (loginResult.redirectLocation().isPresent()) {
            assertEquals("/profile", loginResult.redirectLocation().get());
        } else {
            fail();
        }
    }

    @When("I change my first name to {string}")
    public void iChangeMyFirstNameTo(String string) {
        editForm.put("firstName", string);
    }

    @When("I change my traveller types to {string}")
    public void iChangeMyTravellerTypesTo(String string) {
        editForm.put("travellerTypesForm", string);
    }

    @When("I change my middle name to {string}")
    public void iChangeMyMiddleNameTo(String string) {
        editForm.put("middleName", string);
    }

    @When("I press the Save button")
    public void iPressTheSaveButton() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/profile")
                .bodyForm(editForm)
                .session("connected", "1");

        redirectResultEdit = Helpers.route(provideApplication(), request);
    }

    @Then("I am redirected to my profile page")
    public void iAmRedirectedToMyProfilePage() {
        assertEquals(303, redirectResultEdit.status());
        if (redirectResultEdit.redirectLocation().isPresent()) {
            assertEquals("/profile", redirectResultEdit.redirectLocation().get());
        } else {
            fail();
        }
    }

    @Then("My new profile data is saved")
    public void myNewProfileDataIsSaved() {
        injectRepositories();
        profileRepository.lookup(1).thenApplyAsync(profileOpt -> {
            if (profileOpt.isPresent()) {
                assertEquals("Jenny", profileOpt.get().getFirstName());
                assertEquals("Backpacker, Thrillseeker", profileOpt.get().getTravellerTypesString());
                assertEquals("Max", profileOpt.get().getMiddleName());
            }
            return "done";
        });
    }
    // Scenario: I can perform an editDestinations of my profile - end

    // Scenario: I cannot save my profile with no traveller types - end
    // Includes steps from above

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @When("I try to save the edit")
    public void iTryToSaveTheEdit() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/profile")
                .bodyForm(editForm)
                .session("connected", "1");

        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage("travellerTypesForm\":[\"This field is required\"]");
        redirectResultEdit = Helpers.route(provideApplication(), request);
    }

    @Then("I am not redirected to the profile page")
    public void iAmNotRedirectedToTheProfilePage() {
        assertNull(redirectResultEdit);
    }

    @Then("my edit is not saved")
    public void myEditIsNotSaved() {
        injectRepositories();
        profileRepository.lookup(1).thenApplyAsync(profileOpt -> {
            profileOpt.ifPresent(profile -> assertEquals("Backpacker, Thrillseeker", profile.getTravellerTypesString()));
            return "done";
        });
    }
}
