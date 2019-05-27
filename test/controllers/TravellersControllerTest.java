package controllers;

import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;

public class TravellersControllerTest extends ProvideApplication {

    /**
     * Testing the GET /travellers endpoint
     */
    @Test
    public void showTravellersEndPoint() {
        loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/travellers")
                .session("connected", "1");

        Result result = Helpers.route(provideApplication(), request);

        assertEquals(OK, result.status());
    }
}