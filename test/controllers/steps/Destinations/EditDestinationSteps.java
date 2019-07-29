package controllers.steps.Destinations;

import controllers.ProvideApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Destination;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EditDestinationSteps extends ProvideApplication{
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> destForm = new HashMap<>();
    private Map<String, String> travellerTypeDestForm = new HashMap<>();
    private Result redirectDestinationEdit;

    @Given("User is at the edit destinations page for destination {string}")
    public void userIsAtTheEditDestinationForDestination(String id) {


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

    @Given("^I am on the \"([^\"]*)\" page$")
    public void iAmOnThePage(String arg0) throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri(arg0)
                .session("connected", "1");
        Helpers.route(provideApplication(), request);
    }

    @When("^I press the edit button on destination \"([^\"]*)\"$")
    public void iPressTheEditButtonOnDestination(String arg0) throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/destinations/" + arg0 + "/edit/show")
                .session("connected", "1");
        Helpers.route(provideApplication(), request);
        injectRepositories();
        destinationRepository.lookup(2);
        Destination destination = destinationRepository.lookup(1);
        travellerTypeDestForm.put("name", destination.getName());
        travellerTypeDestForm.put("type", destination.getType());
        travellerTypeDestForm.put("country", destination.getCountry());
        travellerTypeDestForm.put("district", destination.getDistrict());
        travellerTypeDestForm.put("travellerTypesStringDestEdit", destination.getTravellerTypesString());
        travellerTypeDestForm.put("visible", Integer.toString(destination.getVisible()));
        travellerTypeDestForm.put("latitude", Double.toString(destination.getLatitude()));
        travellerTypeDestForm.put("longitude", Double.toString(destination.getLongitude()));

    }

    @And("^I select \"([^\"]*)\" and \"([^\"]*)\" from the traveller type dropdown$")
    public void iSelectAndFromTheTravellerTypeDropdown(String arg0, String arg1) throws Throwable {
        travellerTypeDestForm.put("travellerTypesStringDestEdit", arg0 + "," + arg1);
    }

    @And("^I press the Save button to save the destination$")
    public void iPressTheSaveButtonToSaveTheDestination() throws Throwable {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations/1")
                .bodyForm(travellerTypeDestForm)
                .session("connected", "1");
        redirectDestinationEdit = Helpers.route(provideApplication(), request);
    }

    @Then("^I am redirected to the destinations page$")
    public void iAmRedirectedToTheDestinationsPage() throws Throwable {
        if (redirectDestinationEdit.redirectLocation().isPresent()) {
            assertEquals("/destinations/show/false", redirectDestinationEdit.redirectLocation().get());
        } else {
            fail();
        }
    }

    @And("^destination (\\d+) now has traveller types; \"([^\"]*)\" and \"([^\"]*)\"$")
    public void destinationNowHasTravellerTypesAnd(int arg0, String arg1, String arg2) throws Throwable {
        injectRepositories();
        Destination destination = destinationRepository.lookup(arg0);
        assertTrue(destination.getTravellerTypesList().contains(arg1));
        assertTrue(destination.getTravellerTypesList().contains(arg2));
    }
}


