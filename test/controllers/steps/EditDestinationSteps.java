package controllers.steps;

import controllers.ProvideApplication;
import cucumber.api.java.After;
import cucumber.api.java.AfterStep;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Destination;
import models.Profile;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EditDestinationSteps extends ProvideApplication{
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> destForm = new HashMap<>();
    private Result redirectDestinationEdit;

    @Given("User is at the edit destinations page for destination {string}")
    public void userIsAtTheEditDestinationForDestination(String id) {

        //TODO: Change the url for the edit page for a specific destination to not have tokens as the param.
        //TODO: Need to test the edit destinations GET page as the tokens are all auto generated and not testable.
        //TODO: Uncomment the relevant step once refactor is done

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/" + id + "/edit")
                .bodyForm(loginForm)
                .session("connected", "1");

        Result editPageResult = Helpers.route(provideApplication(), request);
        if (editPageResult.redirectLocation().isPresent()) {
            assertEquals("/destinations/" + id + "/edit", editPageResult.redirectLocation().get());
        } else {
            fail();
        }
    }

    @Given("the user has a destination with id {string}")
    public void theUserHasADestinationWithId(String id) {
        Destination destination = Destination.find.byId(id);
        if (destination == null) {
            fail();
        }
        assertEquals(1, destination.getProfileId());
    }

    @When("user changes Name to {string}")
    public void heFillsInNameWith(String string) {
        destForm.put("name", string);
    }

    @And("user changes Type to {string}")
    public void userChangesTypeTo(String string) {
        destForm.put("type", string);
    }

    @And("user changes Country to {string}")
    public void userFillsInCountryWith(String string) {
        destForm.put("country", string);
    }

    @When("user presses the Save button")
    public void iPressTheSaveButton() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations/512")
                .bodyForm(destForm)
                .session("connected", "1");

        redirectDestinationEdit = Helpers.route(provideApplication(), request);
        assertEquals(303, redirectDestinationEdit.status());
    }

    @Then("user is redirected to the destination page")
    public void iAmRedirectedToMyProfilePage() {
        assertEquals(303, redirectDestinationEdit.status());
        if (redirectDestinationEdit.redirectLocation().isPresent()) {
            assertEquals("/destinations/show/false", redirectDestinationEdit.redirectLocation().get());
        } else {
            fail();
        }
    }

    @Then("the destination is displayed with the updated fields")
    public void theDestinationIsDisplayedWithTheUpdatedFields() {
        Destination destination = Destination.find.byId("512");
        if (destination == null) {
            fail();
        }
        assertEquals("Hello", destination.getName());
        assertEquals("World", destination.getType());
        assertEquals("New Zealand", destination.getCountry());
    }
}


