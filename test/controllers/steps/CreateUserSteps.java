package controllers.steps;


import controllers.ProvideApplication;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import play.mvc.Http;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * Implements steps for testing CreateUser
 */
public class CreateUserSteps extends ProvideApplication {

    private Map<String, String> createForm = new HashMap<>();

    @When("he enters the First Name {string}")
    public void enter_first_name(String firstName) {
        createForm.put("firstName", firstName);
    }

    @When("he enters the Middle Name {string}")
    public void enter_middle_name(String middleName) {
        createForm.put("middleName", middleName);
    }

    @When("he enters the Last Name {string}")
    public void enter_last_name(String lastName) {
        createForm.put("lastName", lastName);
    }

    @When("he enters the Email {string}")
    public void enter_email(String email) {
        createForm.put("email", email);
    }

    @When("he enters the Password {string}")
    public void enter_password(String password) {
        createForm.put("password", password);
    }

    @When("he enters the Gender {string}")
    public void enter_gender(String gender) {
        createForm.put("gender", gender);
    }

    @When("he enters the Birth date {string}")
    public void enter_DOB(String DOB) {
        createForm.put("birthDate", DOB);
    }

    @When("he enters the Nationalities {string}")
    public void enter_nationalities(String nat) {
        createForm.put("nationalitiesForm", nat);
    }

    @When("he enters the Passport {string}")
    public void enter_passports(String passport) {
        createForm.put("passportsForm", passport);
    }

    @When("he chooses {string} in Traveller Type")
    public void enter_traveller_type(String travellerType) {
        createForm.put("travellerTypesForm", travellerType);
    }


    @When("he submits")
    public void he_submits() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/user/create")
                .bodyForm(createForm)
                .session("connected", "1");
    }

    @Then("his account should be saved")
    public void saved_account() {
        injectRepositories();
        profileRepository.lookupEmail("john.gherkin.doe@travelea.com").thenApplyAsync(profileOpt -> {
            profileOpt.ifPresent(profile -> {
                assertEquals("John", profile.getFirstName());
                assertEquals("Gherkin", profile.getMiddleName());
                assertEquals("Doe", profile.getLastName());
                assertEquals("john.gherkin.doe@travelea.com", profile.getEmail());
                assertEquals("password", profile.getPassword());
                assertEquals("Male", profile.getGender());
                assertEquals("01/04/2019", profile.getBirthString());
                assertEquals("New Zealand, China", profile.getNationalityString());
                assertEquals("New Zealand, China", profile.getPassportsString());
                assertEquals("Holidaymaker, Thrillseeker", profile.getTravellerTypesString());
            });
            return "done";
        });
    }
}
