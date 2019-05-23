package roles;


import controllers.SessionController;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * The business logic ("action") for each role is defined here.
 **/
public class RestrictAnnotationAction extends Action<RestrictAnnotation> {

    /**
     * When the method using the RestrictAnnotation tag is called, this will be run.
     * The user's role can be acquired here and then the restrict role checked against this.
     *
     * @param req The http request of the "tagged" method
     * @return Either a redirect call if not authorized, or the requested call if they are.
     */

    public CompletionStage<Result> call(Http.Request req) {
        boolean isAdmin = SessionController.getCurrentUser(req).isAdmin();

        /* TODO replace check to ensure user role matches configuration.value()
            This will require the database structure set up
         */
        if (configuration.value().equals("admin") && isAdmin) {
            System.out.println("Calling annotation action for "+ req);
            return delegate.call(req);  // Allow the request proceed
        }

        return supplyAsync(() -> redirect("/profile").flashing("invalid", "You must be an admin"));
    }
}