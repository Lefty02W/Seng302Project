package controllers;

import com.google.common.collect.TreeMultimap;
import io.ebean.Expr;
import models.Destination;
import models.Profile;
import models.Trip;
import models.TripDestination;
import play.mvc.Http;
import repository.ProfileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

/**
 * This class manages sessions cookies
 * @author George, ajl190, jma361
 */
public class SessionController {

    /**
     * Get the Id of the currently logged in user
     * @param request
     * @return Web page showing connected user's email
     */
    static Integer getCurrentUserId(Http.Request request) {
        Optional<String> connected = request.session().getOptional("connected");
        String stringId;
        if (connected.isPresent()) {
            stringId = connected.get();
            int id = Integer.parseInt(stringId);
            return id;
        } else {
            return null;
        }
    }

}
