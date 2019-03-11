package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Profile;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is the controller for the travellers.scala.html file, it provides the route to the
 * travellers page
 */
public class TravellersController extends Controller {


    /**
     * This method shows the travellers page on the screen
     * @return
     */
    public Result show() {
        List<Profile> profiles = Profile.find.all();
        return ok(travellers.render(profiles);
    }
}
