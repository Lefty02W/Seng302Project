package controllers;

import org.junit.Test;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;

public class DestinationsControllerTest extends ProvideApplication{

    @Test
    public void showDestinationCreateEndPoint() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/destinations/create");

        Call call = controllers.routes.DestinationsController.showCreate();
        Result result = Helpers.route(provideApplication(), Helpers.fakeRequest(call));

        assertEquals(OK, result.status());
    }

    @Test
    public void showDestinationsEndPoint() {
        //TODO need to mock sessions to test this
    }
}