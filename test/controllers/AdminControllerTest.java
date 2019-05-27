package controllers;

import models.Destination;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.List;

public class AdminControllerTest extends ProvideApplication {

    private Integer profileId;

    @Before
    public void setUp() throws Exception {
        profileId = loginUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin")
                .session("connected", profileId.toString());;
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void deleteDestination() {
        injectRepositories();
        List<Destination> destinationList = Destination.find.all();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/admin/destinations/"+destinationList.get(0).getDestinationId()+"/delete")
                .session("connected", profileId.toString());
        Result result = Helpers.route(provideApplication(), request);


        Assert.assertEquals(303, result.status());
    }
}