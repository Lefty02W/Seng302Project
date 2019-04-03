package controllers.steps;


import cucumber.api.java.After;
import cucumber.api.java.AfterStep;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.en.And;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import play.Application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import play.test.WithBrowser;
import static play.test.Helpers.*;


/**
 * Implements steps for testing CreateUser
 */
public class CreateUserSteps extends WithBrowser {

    protected Application application;
    private WebDriver driver;
    private WebElement element;
    private WebDriverWait wait;

    private final int port = 50000; // Port to use, must not conflict
    private final String loginPage = "http://localhost:" + port + "/";

    @Before
    /**
     * Create an application instance, empty profile, profile form
     * and run the application.
     */
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        application = fakeApplication();         // Create a fake application instance
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        driver = new ChromeDriver(options);      // Use Chrome
        testServer(port, application).start();  //Run the application
        driver.manage().window().maximize();
    }


    @AfterStep
    // Simply to allow visual following of selenium's execution
    public void pause() throws InterruptedException {
        Thread.sleep(500); // 0.5 second delay
    }


    @After
    public void teardown() {
        driver.close();
        driver.quit();
    }


    @Given("John is at the sign up page")
    public void at_sign_up_page() throws InterruptedException {
        driver.get(loginPage);
        element = driver.findElement(By.id("createUserButton"));
        element.click();
        element = driver.findElement(By.id("createProfileModal"));

        Assert.assertNotNull(element);
    }


    @When("he fills in First Name with {string}")
    public void fill_first_name(String firstName) {
        element = driver.findElement(By.id("firstName"));
        element.click();
        element.sendKeys(firstName);
        Assert.assertEquals(firstName, element.getAttribute("value"));
    }


    @And("he fills in Middle Name with {string}")
    public void fill_middle_name(String middleName) { ;
        element = driver.findElement(By.id("middleName"));
        element.sendKeys(middleName);

        Assert.assertEquals(middleName, element.getAttribute("value"));
    }


    @And("he fills in Last Name with {string}")
    public void fill_last_name(String lastName) {
        element = driver.findElement(By.id("lastName"));
        element.sendKeys(lastName);
        Assert.assertEquals(lastName, element.getAttribute("value"));
    }


    @And("he fills in Email with {string}")
    public void fill_email(String email) {
        element = driver.findElement(By.className("createEmail"));
        element.sendKeys(email);
        Assert.assertEquals(email, element.getAttribute("value"));
    }


    @And("he fills in Password with {string}")
    public void fill_password(String password) {
        element = driver.findElement(By.className("createPassword"));
        element.sendKeys(password);
        Assert.assertEquals(password, element.getAttribute("value"));
    }


    @And("he fills in Gender with {string}")
    public void fill_gender(String gender) {
        Select genderSelect = new Select(driver.findElement(By.id("gender")));
        genderSelect.selectByVisibleText(gender);

        Assert.assertEquals(gender, genderSelect.getFirstSelectedOption().getAttribute("value"));
    }


    @And("he fills in Birth date with {string}")
    public void fill_birth_date(String birthDate) throws ParseException {
        element = driver.findElement(By.id("birthDate"));
        element.sendKeys(birthDate);

        Assert.assertNotNull(element.getAttribute("value"));
    }


    @And("he fills in Nationalities with {string}")
    public void fill_nationality(String nationality) {
        element = driver.findElement(By.id("nationalities"));
        element.sendKeys(nationality);

        Assert.assertEquals(nationality, element.getAttribute("value"));
    }


    @And("he fills in Passport with {string}")
    public void fill_passport(String passport) {
        element = driver.findElement(By.id("passports"));
        element.sendKeys(passport);

        Assert.assertEquals(passport, element.getAttribute("value"));
    }


    @And("he selects {string} from Traveller Type")
    public void select_traveller_type(String type) {
        Select travellerTypes = new Select(driver.findElement(By.id("typeDropdown")));
        travellerTypes.selectByValue(type);

        Assert.assertEquals(type, travellerTypes.getFirstSelectedOption().getAttribute("value"));
    }


    @And("he presses OK")
    public void press_ok() {
        element = driver.findElement(By.id("createButton"));
        element.click();

        Assert.assertEquals(driver.getCurrentUrl(), loginPage);
    }


    @Then("the login page should be shown")
    public void login() {
        // Ensure sign up modal is no longer on page
        Assert.assertEquals(0, driver.findElements(By.id("createUserModal")).size());
    }
}