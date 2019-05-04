package controllers.steps;

import controllers.ProfileController;
import controllers.SessionController;
import controllers.routes;
import cucumber.api.java.After;
import cucumber.api.java.AfterStep;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import models.Profile;
import org.junit.Assert;
import org.openqa.selenium.By;
import repository.ProfileRepository;

public class viewProfileSteps extends BaseStep {

    @Before
    public void setup() {
        setUp();
    }


    @After
    public void teardown() {
        tearDown();
    }


    @AfterStep
    // Simply to allow visual following of selenium's execution
    public void pause() throws InterruptedException {
        Thread.sleep(500); // 0.5 second delay
    }


    @Given("John has logged in with email {string} and password {string}")
    public void john_has_logged_in_with_email_and_password(String email, String password) {
        driver.get(loginPage);
        element = driver.findElement(By.id("email"));
        element.sendKeys(email);

        element = driver.findElement(By.id("password"));
        element.sendKeys(password);

        element = driver.findElement(By.id("loginButton"));
        element.click();
    }


    @Then("he should see a greeting {string}")
    public void he_should_see_a_greeting(String greeting) {
        element = driver.findElement(By.id("greeting"));

        Assert.assertEquals(greeting, element.getAttribute("innerText"));
    }


    @Given("he has no profile photo")
    public void he_has_no_profile_photo() {
        throw new cucumber.api.PendingException();
    }

    @Then("he should see a default profile photo")
    public void he_should_see_a_default_profile_photo() {
        element = driver.findElement(By.id("profileImage"));
        Assert.assertTrue(element.getAttribute("currentSrc").endsWith("defaultPic.jpg"));
    }

}
