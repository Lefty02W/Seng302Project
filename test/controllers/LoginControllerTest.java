package controllers;

import org.junit.Assert;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import static org.junit.Assert.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

public class LoginControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
    return new GuiceApplicationBuilder().build();
  }

     @Test
     public void save() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/login");
        Result result = route(app, request);
        System.out.println(result);
        //Assert.assertEquals(200, result.status());
    }

    @Test
    public void login() {}
}
