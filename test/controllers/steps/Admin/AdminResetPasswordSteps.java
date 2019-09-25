package controllers.steps.Admin;

import controllers.TestApplication;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Profile;
import org.mindrot.jbcrypt.BCrypt;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import repository.ProfileRepository;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AdminResetPasswordSteps {
    private Map<String, String> updatePasswordForm = new HashMap<>();
    private ProfileRepository profileRepository = TestApplication.getProfileRepository();

    @When("admin selects change password for user with id \"([^\"]*)\"$")
    public void adminSelectsChangePasswordForUserWithId(String userId) {
        updatePasswordForm.put("userId", userId);
    }

    @And("the admin chooses new password to be \"([^\"]*)\"$")
    public void theAdminChoosesNewPasswordToBe(String newPassword) {
        updatePasswordForm.put("password", newPassword);
    }

    @And("the admin clicks save$")
    public void theAdminClicksSave() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/admin/profile/changePassword")
                .bodyForm(updatePasswordForm)
                .session("connected", "2");
        Result redirectAdmin = Helpers.route(TestApplication.getApplication(), request);

        assertEquals(303, redirectAdmin.status());
    }

    @Then("user (\\d+) password will now equal \"([^\"]*)\"$")
    public void thatUsersPasswordWillNowEqual(Integer userId, String newPassword) {
        Profile profile = profileRepository.getProfileByProfileId(userId);
        assertNotEquals(profile.getPassword(), newPassword);
        String passwordHash = BCrypt.hashpw(newPassword, "$2a$12$nODuNzk9U7Hrq6DgspSp4.");

        assertEquals(profile.getPassword(), passwordHash);
    }

}
