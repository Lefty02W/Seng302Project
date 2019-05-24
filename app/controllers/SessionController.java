package controllers;


import play.mvc.Http;

import java.util.Optional;

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
            return Integer.parseInt(stringId);
        } else {
            return null;
        }
    }

}
