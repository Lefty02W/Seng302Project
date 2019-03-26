package controllers;

import models.Profile;
import models.Trip;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.Optional;

/**
 * This class manages sessions cookies
 * @author George
 */
public class SessionController {

    /**
     * Get the currently logged in user
     * @param request
     * @return Web page showing connected user's email
     */
    public static Profile getCurrentUser(Http.Request request) {
        Optional<String> connected = request.session().getOptional("connected");
        String email;
        if (connected.isPresent()) {
            email = connected.get();
            Profile profile = Profile.find.byId(email);
            // Temp until merge with trips branch
            profile.setTrips(new ArrayList<Trip>());
            return profile;
        } else {
            return null;
        }
    }
}
