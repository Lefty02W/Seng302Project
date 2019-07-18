package controllers;

import models.Photo;
import play.data.FormFactory;
import play.mvc.Http;
import play.i18n.MessagesApi;
import views.html.treasureHunts;

import javax.inject.Inject;
import play.mvc.Result;

import repository.ProfileRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class TreasureHuntController {

    private MessagesApi messagesApi;
    private final ProfileRepository profileRepository;

    /**
     * Constructor for the treasure hunt controller class
     *
     * @param messagesApi
     */
    @Inject
    public TreasureHuntController( MessagesApi messagesApi, ProfileRepository profileRepository) {
        this.messagesApi = messagesApi;
        this.profileRepository = profileRepository;
    }


    public CompletionStage<Result> show(Http.Request request) {
        Integer profId = SessionController.getCurrentUserId(request);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            if (profile.isPresent()) {
                return ok(treasureHunts.render(profile.get(),request, messagesApi.preferred(request)));
            } else {
                return redirect("/login");
            }
        });
    }
}
