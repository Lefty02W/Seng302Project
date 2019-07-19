package roles;

import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import controllers.ProvideApplication;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.GET;


public class RestrictAnnotationActionTest extends ProvideApplication{


    /**
     * Testing profile GET endpoint /admin.
     * Should send redirect as this user is non-admin.
     * This tests the annotation as the whole admin controller is restricted by the custom annotation.
     */
    @Test
    public void attemptToShowAdminPage() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/admin")
                .session("connected", "1");

        Result result = Helpers.route(provideApplication(),request);
        System.out.println(result.body());
        assertEquals(303, result.status());
    }

    /**
     * Testing profile GET endpoint /admin.
     * Should send OK response as this user is admin.
     * This tests the annotation as the whole admin controller is restricted by the custom annotation.
     */
    @Test
    public void showAdminPage() {

        adminLogin();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/admin")
                .session("connected", "4");

        Result result = Helpers.route(provideApplication(),request);

        assertEquals(200, result.status());
    }

}