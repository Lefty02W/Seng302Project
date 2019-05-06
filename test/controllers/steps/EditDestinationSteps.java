package controllers.steps;

import controllers.ProvideApplication;
import cucumber.api.java.After;
import cucumber.api.java.AfterStep;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EditDestinationSteps extends ProvideApplication{
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> destForm = new HashMap<>();
    private Result redirectDestinationEdit;


    @Given("User is at the edit destinations page")
    public void userIsAtTheEditDestinationsPage() {
        loginForm.put("email", "john@gmail.com");
        loginForm.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(loginForm)
                .session("connected", "john@gmail.com");

        Result loginResult = Helpers.route(provideApplication(), request);

        Http.RequestBuilder requestDest = Helpers.fakeRequest()
                .method("GET")
                //todo dont hard code the trip id, create a trip and use new id
                .uri("/destinations/513/edit")
                .bodyForm(destForm)
                .session("connected", "john@gmail.com");

        Result destinationResult = Helpers.route(provideApplication(), requestDest);
    }

    @When("user changes Name to {string}")
    public void heFillsInNameWith(String string) {
        destForm.put("name", string);
    }

    @And("he changes Type to {string}")
    public void userFillsInTypeWith(String string) {
        destForm.put("type", string);
    }

    @And("user changes Country to {string}")
    public void userFillsInCountryWith(String string) {
        destForm.put("country", string);
    }

    @When("he presses the Save button")
    public void iPressTheSaveButton() {
        System.out.println("--------------HERE-----------");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations")
                .session("connected", "john@gmail.com");

        redirectDestinationEdit = Helpers.route(provideApplication(), request);
        assertEquals(303, redirectDestinationEdit.status());
    }

    @Then("we are redirected to the destination page")
    public void iAmRedirectedToMyProfilePage() {
        assertEquals(200, redirectDestinationEdit.status());
        assertEquals("/destinations", redirectDestinationEdit.redirectLocation().get());
    }

    @And("the destination is displayed with updated fields")
    public void theDestinationIsDisplayedWithUpdatedFields() {
        assertEquals(destForm.get("name"), "Hello");
        assertEquals(destForm.get("type"), "World");
        assertEquals(destForm.get("country"), "New Zealand");
    }
}


