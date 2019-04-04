//package controllers.steps;
//
//import controllers.SessionController;
//import cucumber.api.java.After;
//import cucumber.api.java.AfterStep;
//import cucumber.api.java.Before;
//import cucumber.api.java.en.And;
//import cucumber.api.java.en.Given;
//import cucumber.api.java.en.Then;
//import cucumber.api.java.en.When;
//import io.github.bonigarcia.wdm.WebDriverManager;
//import models.Profile;
//import org.junit.Assert;
//import org.openqa.selenium.By;
//import org.openqa.selenium.Cookie;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.support.ui.Select;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import play.Application;
//import play.mvc.Http;
//import play.test.Helpers;
//import play.test.TestServer;
//import play.test.WithBrowser;
//
//import java.util.Set;
//
//import static play.test.Helpers.fakeApplication;
//
//public class LoginUserSteps extends WithBrowser {
//
//    protected Application application;
//    private WebDriver driver;
//    private WebElement element;
//    private TestServer testServer;
//
//    private final int port = 50000; // Port to use, must not conflict
//    private final String loginPage = "http://localhost:" + port + "/";
//    private final String profilePage = "http://localhost:" + port + "/profile";
//
//    @Before
//    /**
//     * Create an application instance, empty profile, profile form
//     * and run the application.
//     */
//    public void setUp() {
//        WebDriverManager.chromedriver().setup();
//        application = fakeApplication();         // Create a fake application instance
//        ChromeOptions options = new ChromeOptions();
//        //options.addArguments("headless");
//        driver = new ChromeDriver();      // Use Chrome
//        testServer = Helpers.testServer(port, application);
//        testServer.start();  //Run the application
//        driver.manage().window().maximize();
//    }
//
//
//    @AfterStep
//    // Simply to allow visual following of selenium's execution
//    public void pause() throws InterruptedException {
//        Thread.sleep(1000); // 0.5 second delay
//    }
//
//
//    @After
//    public void teardown() {
//        driver.close();
//        driver.quit();
//        testServer.application().stop();
//        testServer.stop();
//    }
//
//
//    @Given("John is at the login page")
//    public void at_login_page() {
//        driver.get(loginPage);
//    }
//
//
//    @When("he fills in his email with {string}")
//    public void fill_email(String email) {
//        element = driver.findElement(By.className("loginEmail"));
//        element.sendKeys(email);
//        Assert.assertEquals(email, element.getAttribute("value"));
//    }
//
//
//    @And("he types {string} for his password")
//    public void fill_password(String password) {
//        element = driver.findElement(By.id("password"));
//        element.sendKeys(password);
//        System.out.print("NOT DUPLICATE");
//
//        Assert.assertEquals(password, element.getAttribute("value"));
//    }
//
//
//    @And("he presses Login")
//    public void press_ok() {
//        element = driver.findElement(By.id("loginButton"));
//        element.click();
//
//        Assert.assertEquals(driver.getCurrentUrl(), profilePage);
//    }
//
//
//    @Then("the profile page should be shown")
//    public void login() {
//        String title = driver.getTitle();
//        // Ensure sign up modal is no longer on page
//        System.out.print(driver.findElements(By.id("pageTitle")));
//        Assert.assertEquals(title, driver.getCurrentUrl());
//    }
//
//    @And("Johns session is loaded in")
//    public void check_session(){
//        Set<Cookie> cookies = driver.manage().getCookies();
//        System.out.println(cookies.contains("connected"));
//
//
//    }
//}