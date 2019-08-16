package controllers.steps.Trips;

import controllers.TestApplication;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class EditTripsSteps {

    private String userEmail;


    @Given("^I am logged into the application as user \"([^\"]*)\" with password \"([^\"]*)\"$")
    public void iAmLoggedIntoTheApplicationAsUserWithPassword(String arg0, String arg1) throws Throwable {
        Map<String, String> loginForm = new HashMap<>();
        loginForm.put("email", arg0);
        loginForm.put("password", arg1);
        userEmail = arg0;

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(loginForm)
                .session("connected", "1");

        Result loginResult = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(303, loginResult.status());
    }

    @And("^I select my trip named \"([^\"]*)\" to edit$")
    public void iSelectMyTripNamedToEdit(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I remove all but one destinations from the trip$")
    public void iRemoveAllButOneDestinationsFromTheTrip() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I try to save the trip$")
    public void iTryToSaveTheTrip() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I am redirected back to the edit trip page$")
    public void iAmRedirectedBackToTheEditTripPage() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^A error message is shown saying \"([^\"]*)\"$")
    public void aErrorMessageIsShownSaying(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I select the first destination to edit$")
    public void iSelectTheFirstDestinationToEdit() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I move the first destination to be last$")
    public void iMoveTheFirstDestinationToBeLast() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^Select the first destination to edit$")
    public void selectTheFirstDestinationToEdit() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I change it to be the same as the second destination$")
    public void iChangeItToBeTheSameAsTheSecondDestination() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I select the last destination to add$")
    public void iSelectTheLastDestinationToAdd() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I try to add the destination$")
    public void iTryToAddTheDestination() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I select a destination$")
    public void iSelectADestination() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I enter \"([^\"]*)\" into the arrival time$")
    public void iEnterIntoTheArrivalTime(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I enter \"([^\"]*)\" into the departure time$")
    public void iEnterIntoTheDepartureTime(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I change its order to (\\d+)$")
    public void iChangeItsOrderTo(int arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I try to save the trip edit$")
    public void iTryToSaveTheTripEdit() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I am redirected to the \"([^\"]*)\" endpoint$")
    public void iAmRedirectedToTheEndpoint(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I change the destination to one that isn't the second destination$")
    public void iChangeTheDestinationToOneThatIsnTTheSecondDestination() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I select a destination that isn't the last destination$")
    public void iSelectADestinationThatIsnTTheLastDestination() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I delete the last destination$")
    public void iDeleteTheLastDestination() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I change the arrival time to \"([^\"]*)\"$")
    public void iChangeTheArrivalTimeTo(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I change the departure time to \"([^\"]*)\"$")
    public void iChangeTheDepartureTimeTo(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I change the name to \"([^\"]*)\"$")
    public void iChangeTheNameTo(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
