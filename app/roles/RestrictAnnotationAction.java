package roles;


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

        if (configuration.value().equals("admin")) {

            System.out.println("Calling annotation action for "+ req);
            return supplyAsync(() -> redirect("/login").flashing("success", "Visibility updated."));
        }

        return delegate.call(req);
    }
}