package controllers.steps;

import controllers.routes;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;


public class ProfileImageSteps extends BaseStep {


    @Given("he has clicked Change Profile Picture button")
    public void he_has_clicked_Change_Profile_Picture_button() {
        element = driver.findElement(By.id("changeProfilePictureButton"));
        element.click();
    }


    @Given("John clicks Upload new photo")
    public void john_clicks_Upload_new_photo() {
        element = driver.findElement(By.id("uploadPhotoButton"));
        element.click();
    }


    @Given("he clicks the Crop this myself button without selecting a photo")
    public void he_clicks_the_Crop_this_myself_button_without_selecting_a_photo() {
        element = driver.findElement(By.id("selfCropped"));
        element.click();
    }


    @Then("an error message should be shown telling John to select a photo")
    public void an_error_message_should_be_shown_telling_John_to_select_a_photo() {
        element = driver.findElement(By.id("invalidAlert"));
        Assert.assertEquals("No image selected.", element.getAttribute("innerText"));
    }


    @Given("he clicks the Crop this myself button with a photo selected")
    public void he_clicks_the_Crop_this_myself_button_with_a_photo_selected() {
        element = driver.findElement(By.id("image"));
        element.sendKeys( System.getProperty("user.dir") + "/public/images/defaultPic.jpg");
        element = driver.findElement(By.id("selfCropped"));
        element.click();
    }


    @When("he sets the width and height field as {int} and accepts")
    public void he_sets_the_width_and_height_field_as_and_accepts(Integer size) {
        element = driver.findElement(By.id("widthHeight"));
        element.clear();
        element.sendKeys(size.toString());
        element = driver.findElement(By.id("acceptCrop"));
        element.click();
    }


    @Then("the size of the image should be {int} x {int}")
    public void the_size_of_the_image_should_be_x(Integer width, Integer height) {
        element = driver.findElement(By.id("profileImagePreview"));
        Integer imageWidth = element.getSize().getWidth();
        Integer imageHeight = element.getSize().getHeight();
        String expected = width.toString() + "x " + height.toString();
        String actual = imageWidth.toString() + "x " + imageHeight.toString();

        Assert.assertEquals(expected, actual);
    }
}
