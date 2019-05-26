package controllers.steps.Destinations;

import controllers.ProvideApplication;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Destination;
import org.junit.Assert;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class publicDestinationSteps extends ProvideApplication {
    private Map<String, String> loginForm = new HashMap<>();
    private Map<String, String> destForm = new HashMap<>();
    private Result redirectDestination;

    @When("he fills in name with {string}")
    public void heFillsInNameWith(String string) { destForm.put("name", string); }

    @When("he fills in type with {string}")
    public void heFillsInTypeWith(String string) { destForm.put("type", string); }

    @When("he fills in country with {string}")
    public void heFillsInCountryWith(String string) { destForm.put("country", string); }

    @When("he presses save")
    public void hePressesSave() {
        System.out.println("yoyo this is the visibile = " + destForm.get("visible"));
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/destinations")
                .bodyForm(destForm)
                .session("connected", "1");
        redirectDestination = Helpers.route(provideApplication(), request);
        assertEquals(303, redirectDestination.status());
    }

    @Then("he is redirected to the create destination page and destination is not saved")
    public void heIsRedirectedToTheCreateDestinationPageAndDestinationIsNotSaved() {
        assertEquals(303, redirectDestination.status());
        assertEquals("/destinations/create", redirectDestination.redirectLocation().get());
    }

    @Given("Steve Miller has a private destination with name {string}, type {string}, and country {string}")
    public void steveMillerHasPrivateDestination(String name, String type, String country) {
        Destination destination = new Destination();
        destination.setProfileId(2);
        destination.setName(name);
        destination.setType(type);
        destination.setCountry(country);
        injectRepositories();
        destinationRepository.insert(destination);
    }

    @Given("user creates a public destination with name {string}, type {string}, and country {string}")
    public void createPublicDestination(String name, String type, String country) {
        Destination destination = new Destination();
        destination.setProfileId(1);
        destination.setName(name);
        destination.setType(type);
        destination.setCountry(country);
        destination.setVisible(1);
        injectRepositories();
        destinationRepository.insert(destination);
    }


    @Then("Steve Millers private destination doesnt exist")
    public void steveMillersProfileDoesntExist() {
        injectRepositories();
        List<Destination> destinationList = destinationRepository.getUserDestinations(2);
        for (Destination destination : destinationList) {
            List<String> destDetails = new ArrayList<>();
            destDetails.add(destination.getName());
            destDetails.add(destination.getType());
            destDetails.add(destination.getCountry());
            List<String> oldDest = new ArrayList<>();
            oldDest.add("Waiau");
            oldDest.add("town");
            oldDest.add("New Zealand");
            assertNotEquals(destDetails, oldDest);
        }
    }

    @Then("Steve Miller is following the new public destination")
    public void steveMillerFollowingNewPublicDest() {
        injectRepositories();
        Optional<ArrayList<Integer>> optionalListDests = destinationRepository.getFollowedDestinationIds(2);
        if (optionalListDests.isPresent()) {
            ArrayList<Integer> listDests = optionalListDests.get();
            assertEquals(1, listDests.size());
        } else {
            Assert.fail();
        }
    }
}
