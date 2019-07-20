package controllers;


import models.Destination;
import models.RoutedObject;
import models.TreasureHunt;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import repository.DestinationRepository;
import repository.ProfileRepository;
import repository.TreasureHuntRepository;
import views.html.treasureHunts;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class TreasureHuntController {

    private MessagesApi messagesApi;
    private final ProfileRepository profileRepository;
    private final DestinationRepository destinationRepository;
    private final TreasureHuntRepository treasureHuntRepository;
    private final Form<TreasureHunt> huntForm;
    private String huntShowRoute = "/treasure";

    /**
     * Constructor for the treasure hunt controller class
     *
     * @param messagesApi
     */
    @Inject
    public TreasureHuntController(FormFactory formFactory, MessagesApi messagesApi, ProfileRepository profileRepository, DestinationRepository destinationRepository, TreasureHuntRepository treasureHuntRepository) {
        this.messagesApi = messagesApi;
        this.profileRepository = profileRepository;
        this.destinationRepository = destinationRepository;
        this.huntForm = formFactory.form(TreasureHunt.class);
        this.treasureHuntRepository = treasureHuntRepository;
    }


    public CompletionStage<Result> show(Http.Request request) {
        Integer profId = SessionController.getCurrentUserId(request);
        List<TreasureHunt> availableHunts = treasureHuntRepository.getAllActiveTreasureHunts();
        List<TreasureHunt> myHunts = treasureHuntRepository.getAllUserTreasureHunts(profId);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            return profile.map(profile1 -> {
                return ok(treasureHunts.render(profile1, availableHunts, myHunts, destinationRepository.getPublicDestinations(), huntForm, new RoutedObject<TreasureHunt>(null, false, false), request, messagesApi.preferred(request)));
            }).orElseGet(() -> redirect("/login"));
        });
    }

    /**
     * Endpoint method to handle a users request to create a new treasure hunt
     *
     * @apiNote /hunts/create
     * @param request the get end datesers request holding the treasure hunt form
     * @return CompletionStage redirecting back to the treasure hunts page
     */
    public CompletionStage<Result> createHunt(Http.Request request) {
        return supplyAsync(() -> {
            Form<TreasureHunt> filledForm = huntForm.bindFromRequest(request);
            TreasureHunt treasureHunt = setValues(SessionController.getCurrentUserId(request), filledForm);
            treasureHuntRepository.insert(treasureHunt);
            return redirect(huntShowRoute);
        });
    }



    /**
     * Called treasure hunt delete method in the treasureHuntRepository to delete the treasureHunt from the database
     * @param id int Id of the treasureHunt the user wishes to delete
     */
    public CompletionStage<Result> deleteHunt(Http.Request request, Integer id){
        return treasureHuntRepository.deleteTreasureHunt(id)
                .thenApplyAsync(x -> redirect("/treasure").flashing("success", "Hunt: " + id + "was deleted"));
    }

    /**
     * Helper function to extract into treasurehunt object
     * @param treasureHunt TreasureHunt object to be made up
     * @param values form of the incoming data to be put into a treasurehunt object
     * @return TreasureHunt object with all values inside
     */
    public TreasureHunt setValues(Integer userId, Form<TreasureHunt> values){
        TreasureHunt treasureHunt = values.get();
        String destinationId = null;
        String startDate = null;
        String endDate = null;
        if (values.field("endDate").value().isPresent()) {
            endDate = values.field("endDate").value().get();
        }
        if (values.field("startDate").value().isPresent()) {
            startDate = values.field("startDate").value().get();
        }
        if (values.field("destinationId").value().isPresent()) {
            destinationId = values.field("destinationId").value().get();
        }

        treasureHunt.setDestinationIdString(destinationId);
        treasureHunt.setStartDateString(startDate);
        treasureHunt.setEndDateString(endDate);
        treasureHunt.setTreasureHuntProfileId(userId);
        return treasureHunt;
    }
    /**
     * Endpoint method to handle a users request to edit a previously made treasure hunt
     * @apiNote /hunts/:id/edit
     * @param request the users request holding the treasure hunt form
     * @param id Id of the treasure hunt to be edited
     * @return CompletionStage redirecting back to the treasure hunts page
     */
    public CompletionStage<Result> editTreasureHunt(Http.Request request, Integer id) {
        Form<TreasureHunt> treasureHuntForm = huntForm.bindFromRequest(request);
        TreasureHunt treasureHunt = setValues(SessionController.getCurrentUserId(request), treasureHuntForm);
        return supplyAsync(() -> {
            treasureHuntRepository.update(treasureHunt, id);
            return redirect(huntShowRoute).flashing("success", "Treasure Hunt has been updated.");
        });
    }

    public CompletionStage<Result> showEditTreasureHunt(Http.Request request , Integer id) {
        TreasureHunt hunt = treasureHuntRepository.lookup(id);
        huntForm.fill(hunt);
        Integer profId = SessionController.getCurrentUserId(request);
        List<TreasureHunt> availableHunts = treasureHuntRepository.getAllActiveTreasureHunts();
        List<TreasureHunt> myHunts = treasureHuntRepository.getAllUserTreasureHunts(profId);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            return profile.map(profile1 -> {
                return ok(treasureHunts.render(profile1, availableHunts, myHunts, destinationRepository.getPublicDestinations(), huntForm, new RoutedObject<TreasureHunt>(hunt, true, true), request, messagesApi.preferred(request)));
            }).orElseGet(() -> redirect("/login"));
        });
    }
}
