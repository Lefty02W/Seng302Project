package controllers;

import models.Destination;
import models.Profile;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This class manages sessions cookies
 * @author George, ajl190
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

            ArrayList<Destination> destinations = new ArrayList<>(Destination.find.query()
                    .where()
                    .eq("user_email", email)
                    .findList());

            profile.setDestinations(destinations);
            return profile;
        } else {
            return null;
        }
    }
}
