package controllers;

import controllers.routes;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.Mode;
import play.api.Environment;
import play.api.inject.guice.GuiceApplicationBuilder;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.io.File;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;

public class CreateUserControllerTest extends ProvideApplication{

    private Application app;

    @Before
    public void setUp() {
        app = super.provideApplication();
    }

    @Test
    public void save() {

    }

    @Test
    public void show() {
        Http.RequestBuilder request = Helpers.fakeRequest()
             .method(GET)
                .uri("/user/create");

        Result result = Helpers.route(provideApplication(), request);
        //System.out.println(Helpers.contentAsString(result));

        assertEquals(OK, result.status());
    }
}