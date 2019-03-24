package controllers;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import static org.junit.Assert.*;


import play.Application;
import play.data.FormFactory;
import play.data.format.Formatters;
import play.i18n.MessagesApi;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import play.twirl.api.Content;
import repository.ProfileRepository;
import play.test.*;
import static play.test.Helpers.*;
import static play.test.Helpers.fakeRequest;


public class CreateUserControllerTest extends WithApplication {


    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }


    @Test
    public void save() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/");
        Result result = route(app, request);
    }

    //@Test
    public void show() {
    }
}