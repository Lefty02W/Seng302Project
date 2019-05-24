package roles;


import controllers.SessionController;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import repository.RolesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * The business logic ("action") for each role is defined here.
 * This is essentially a middleware class
 **/
public class RestrictAnnotationAction extends Action<RestrictAnnotation> {

    private RolesRepository rolesRepository;


    /**
     * When the method using the RestrictAnnotation tag is called, this will be run.
     * The user's role can be acquired here and then the restrict role checked against this.
     *
     * @param req The http request of the "tagged" method
     * @return Either a redirect call if not authorized, or the requested call if they are.
     */
    public CompletionStage<Result> call(Http.Request req) {

        //TODO Get roles of the user here.
        Integer profileId = SessionController.getCurrentUserId(req);
        List<String> profileRoles = new ArrayList<>();

        /* TODO replace check to ensure user role matches configuration.value()
            This will require the database structure set up
         */

        //TODO change this to check if user's roles contains configuration.value.
        // If so we are authorized, and the request should be called.
        if (profileRoles.contains(configuration.value())) {
            System.out.println("Calling annotation action for "+ req);
            return delegate.call(req);  // Allow the request proceed
        }

        // Otherwise we are not authorized, and the user should be redirected.
        return supplyAsync(() -> redirect("/profile").flashing("invalid",
                "You do not have permission to do this"));
    }
}