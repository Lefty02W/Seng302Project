package controllers.steps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.SessionId;
import play.Application;
import play.test.Helpers;
import play.test.TestServer;

import static play.test.Helpers.fakeApplication;

public class BaseStep {
    protected static Application application;
    public static WebDriver driver;
    public static WebElement element;
    public static TestServer testServer;

    public static int port = 0; // Port to use, must not conflict

    public static String loginPage;
    public static String profilePage;

    public static void setUp() {
        if (driver == null || ((ChromeDriver) driver).getSessionId() == null) {
            WebDriverManager.chromedriver().setup();

            application = fakeApplication();         // Create a fake application instance
            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless");
            driver = new ChromeDriver(options);      // Use Chrome

            testServer = Helpers.testServer(port, application);
            testServer.start();  //Run the application
            port = testServer.getRunningHttpPort().getAsInt();

            loginPage = "http://localhost:" + port + "/";
            profilePage = "http://localhost:" + port + "/profile";
            driver.manage().window().maximize();
        }
    }


    public static void tearDown() {
        SessionId session = ((ChromeDriver) driver).getSessionId();
        if (session != null) {
            driver.close();
            driver.quit();
            testServer.application().stop();
            testServer.stop();
        }
    }
}
