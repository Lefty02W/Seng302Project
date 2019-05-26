package roles;


import models.Destination;
import org.junit.BeforeClass;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import java.util.ArrayList;
import controllers.ProvideApplication;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;


public class RestrictAnnotationActionTest extends ProvideApplication{


    /**
     * Testing profile GET endpoint /admin.
     * Should send redirect as this user is non-admin.
     */
    @Test // Having issues with this test will sort at a later date
    public void show() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/admin")
                .session("connected", "1");

        Result result = Helpers.route(provideApplication(),request);

        assertEquals(303, result.status());
    }








}
