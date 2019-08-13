package roles;

import controllers.TestApplication;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.GET;


public class RestrictAnnotationActionTest {


    /**
     * Testing profile GET endpoint /admin.
     * Should send redirect as this user is non-admin.
     * This tests the annotation as the whole admin controller is restricted by the custom annotation.
     */
    @Test
    public void attemptToShowAdminPage() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "john@gmail.com");
        formData.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(TestApplication.getApplication(), request);

        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/admin")
                .session("connected", "1");

        result = Helpers.route(TestApplication.getApplication(),request);
        assertEquals(303, result.status());
    }

    /**
     * Testing profile GET endpoint /admin.
     * Should send OK response as this user is admin.
     * This tests the annotation as the whole admin controller is restricted by the custom annotation.
     */
    @Test
    public void showAdminPage() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "bob@gmail.com");
        formData.put("password", "password");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("POST")
                .uri("/login")
                .bodyForm(formData);

        Result result = Helpers.route(TestApplication.getApplication(), request);

        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/admin")
                .session("connected", "4");

        result = Helpers.route(TestApplication.getApplication(),request);

        assertEquals(200, result.status());
    }

}
