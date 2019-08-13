package controllers.steps.TreasureHunts;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.TreasureHunt;
import org.joda.time.DateTime;
import play.mvc.Http;
import play.test.Helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Steps for testing createHunt in TreasureHuntController.
 */
public class CreateHuntSteps {

        private Map<String, String> createForm = new HashMap<>();
        private static DateFormat dateFormatEntry = new SimpleDateFormat("YYYY-MM-dd");
        private static String TODAY_DATE_STRING = dateFormatEntry.format(DateTime.now().toDate());
        private static String TOMORROW_DATE_STRING = dateFormatEntry.format(DateTime.now().plusDays(1).toDate());
        private static int numberExistingHunts;

        @When("^I insert a riddle \"([^\"]*)\"$")
        public void iInsertARiddle(String riddle) {
                createForm.put("riddle", riddle);
        }

        @And("^I select the destination Tokyo with id \"([^\"]*)\"$")
        public void iSelectTheDestinationTokyoWithId(String id) {
            createForm.put("destinationId", id);
        }

        @And("^I enter today's date for start date$")
        public void iEnterTodaySDateForStartDate() {
            createForm.put("startDate", TODAY_DATE_STRING);
        }

        @And("^I enter tomorrow's date for end date$")
        public void iEnterTomorrowSDateForEndDate() {
            createForm.put("endDate", TOMORROW_DATE_STRING);
        }

        @And("^I enter tomorrow's date for start date$")
        public void iEnterTomorrowSDateForStartDate() {
            createForm.put("startDate", TOMORROW_DATE_STRING);
        }

        @And("^I enter today's date for end date$")
        public void iEnterTodaySDateForEndDate() {
            createForm.put("endDate", TODAY_DATE_STRING);
        }


        @When("^I click create treasure hunt$")
        public void iClickCreateTreasureHunt() {
            numberExistingHunts = TestApplication.getTreasureHuntRepository().getAllUserTreasureHunts(1).size();
            Http.RequestBuilder request = Helpers.fakeRequest()
                    .method("POST")
                    .uri("/hunts/create")
                    .bodyForm(createForm)
                    .session("connected", "1");
            Helpers.route(TestApplication.getApplication(), request);
        }

        @Then("^the treasure hunt is made$")
        public void theTreasureHuntIsMade(){

            List<TreasureHunt> allJohnHunts = TestApplication.getTreasureHuntRepository().getAllUserTreasureHunts(1);
            TreasureHunt newHunt = allJohnHunts.get(allJohnHunts.size() - 1);

            //Check a hunt was added
            assertEquals(allJohnHunts.size(), numberExistingHunts + 1);

            //Check some arbitrary details...
            assertEquals("Riddle me this...", newHunt.getRiddle());
            assertEquals(1, newHunt.getTreasureHuntDestinationId());

        }

        @Then("The treasure hunt is not made")
        public void theTreasureHuntIsNotMade() {
            List<TreasureHunt> allJohnHunts = TestApplication.getTreasureHuntRepository().getAllUserTreasureHunts(1);
            TreasureHunt newHunt = allJohnHunts.get(allJohnHunts.size() - 1);

            //Check a hunt was not added
            assertEquals(allJohnHunts.size(), numberExistingHunts);

        }
}
