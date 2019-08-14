package controllers;

import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import repository.ProfileRepository;
import views.html.events;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class EventsController extends Controller {

    private final ProfileRepository profileRepository;
    private MessagesApi messagesApi;

    @Inject
    public EventsController(ProfileRepository profileRepository, MessagesApi messagesApi){
        this.profileRepository = profileRepository;
        this.messagesApi = messagesApi;
    }

    /**
     * Endpoint for landing page for Events
     *
     * @param request client request
     */
    @Security.Authenticated(SecureSession.class)
    public CompletionStage<Result> show(Http.Request request){
        Integer profId = SessionController.getCurrentUserId(request);

        return profileRepository.findById(profId)
                .thenApplyAsync(profileRec -> profileRec.map(profile -> ok(events.render(profile, request, messagesApi.preferred(request))))
                .orElseGet(() -> redirect("/")));
    }
}
