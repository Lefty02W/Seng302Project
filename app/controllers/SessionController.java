package controllers;

import models.Profile;
import play.mvc.Http;

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
            return Profile.find.byId(email);
        } else {
            return null;
        }
    }
}
