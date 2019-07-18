package controllers;


import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import repository.DestinationRepository;
import repository.ProfileRepository;
import views.html.treasureHunts;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class TreasureHuntController {

    private MessagesApi messagesApi;
    private final ProfileRepository profileRepository;
    private final DestinationRepository destinationRepository;

    /**
     * Constructor for the treasure hunt controller class
     *
     * @param messagesApi
     */
    @Inject
    public TreasureHuntController( MessagesApi messagesApi, ProfileRepository profileRepository, DestinationRepository destinationRepository) {
        this.messagesApi = messagesApi;
        this.profileRepository = profileRepository;
        this.destinationRepository = destinationRepository;
    }


    public CompletionStage<Result> show(Http.Request request) {
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            return profile.map(profile1 -> {
                return ok(treasureHunts.render(profile1, destinationRepository.getPublicDestinations(), request, messagesApi.preferred(request)));
            }).orElseGet(() -> redirect("/login"));
        });
    }
}
