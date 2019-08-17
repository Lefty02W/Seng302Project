package controllers;


import models.PaginationHelper;
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
import repository.UndoStackRepository;
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
    private final UndoStackRepository undoStackRepository;
    private final Form<TreasureHunt> huntForm;
    private String huntShowRoute = "/treasure/0";


    /**
     * Constructor for the treasure hunt controller class
     */
    @Inject
    public TreasureHuntController(FormFactory formFactory, MessagesApi messagesApi, ProfileRepository profileRepository,
                                  DestinationRepository destinationRepository, TreasureHuntRepository treasureHuntRepository,
                                  UndoStackRepository undoStackRepository) {
        this.messagesApi = messagesApi;
        this.profileRepository = profileRepository;
        this.destinationRepository = destinationRepository;
        this.huntForm = formFactory.form(TreasureHunt.class);
        this.treasureHuntRepository = treasureHuntRepository;
        this.undoStackRepository = undoStackRepository;
    }

    /**
     * Function to render the treasure hunts page with available hunts in the database and the users
     * own personally created treasure hunts
     *
     * @param request the users request
     * @return a redirect to the treasure hunt page
     */
    public CompletionStage<Result> show(Http.Request request, Integer offset) {
        Integer profId = SessionController.getCurrentUserId(request);
        PaginationHelper paginationHelper = new PaginationHelper(offset, offset, offset, true, true, treasureHuntRepository.getNumHunts());
        paginationHelper.alterNext(9);
        paginationHelper.alterPrevious(9);
        List<TreasureHunt> availableHunts = treasureHuntRepository.getAllActiveTreasureHunts(offset);
        List<TreasureHunt> myHunts = treasureHuntRepository.getAllUserTreasureHunts(profId);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            undoStackRepository.clearStackOnAllowed(profile.get());
            return profile.map(profile1 -> {
                return ok(treasureHunts.render(profile1, availableHunts, myHunts, destinationRepository.getPublicDestinations(), huntForm, new RoutedObject<TreasureHunt>(null, false, false), paginationHelper, request, messagesApi.preferred(request)));
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
        Form<TreasureHunt> filledForm = huntForm.bindFromRequest(request);
        TreasureHunt treasureHunt = setValues(SessionController.getCurrentUserId(request), filledForm);

        if (treasureHunt.getStartDate().after(treasureHunt.getEndDate())){
            return supplyAsync(() -> redirect("/treasure").flashing("error", "Error: Start date cannot be after end date."));
        }

        return treasureHuntRepository.insert(treasureHunt).thenApplyAsync(x -> {
            return redirect(huntShowRoute).flashing("success", "Treasure Hunt has been added.");
        });
    }

    /**
     * Called treasure hunt delete method in the treasureHuntRepository to delete the treasureHunt from the database
     * @param id int Id of the treasureHunt the user wishes to delete
     */
    public CompletionStage<Result> deleteHunt(Http.Request request, Integer id){
        return treasureHuntRepository.deleteTreasureHunt(id)
                .thenApplyAsync(x -> redirect("/treasure").flashing("success", "Hunt: " + id + " was deleted"));
    }

    /**
     * Helper function to extract into treasurehunt object
     * @param userId User id of the creator of this TreasureHunt
     * @param values form of the incoming data to be put into a treasurehunt object
     * @return TreasureHunt object with all values inside
     */
    TreasureHunt setValues(Integer userId, Form<TreasureHunt> values){
        TreasureHunt treasureHunt = values.get();
        String destinationId = null;
        String startDate = null;
        String endDate = null;

        Optional<String> endDateString = values.field("endDate").value();
        if (endDateString.isPresent()) {
            endDate = endDateString.get();
        }
        Optional<String> startDateString = values.field("startDate").value();
        if (startDateString.isPresent()) {
            startDate = startDateString.get();
        }
        Optional<String> destinationIdString = values.field("destinationId").value();
        if (destinationIdString.isPresent()) {
            destinationId = destinationIdString.get();
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
        if (treasureHunt.getStartDate().after(treasureHunt.getEndDate())){
            return supplyAsync(() -> redirect("/treasure").flashing("error", "Error: Start date cannot be after end date."));
        }

        return treasureHuntRepository.update(treasureHunt, id).thenApplyAsync(x -> {
            return redirect(huntShowRoute).flashing("success", "Treasure Hunt has been updated.");
        });

    }

    /**
     * End point that opens up the edit treasure hunt modal
     *
     * @param request the users request holding the treasure hunt form
     * @param id unique treasure hunt id
     * @return a redirect to the treasure hunt page
     */
    public CompletionStage<Result> showEditTreasureHunt(Http.Request request , Integer id, Integer offset) {
        TreasureHunt hunt = treasureHuntRepository.lookup(id);
        PaginationHelper paginationHelper = new PaginationHelper(offset, offset, offset, true, true, treasureHuntRepository.getNumHunts());
        paginationHelper.alterNext(9);
        paginationHelper.alterPrevious(9);
        huntForm.fill(hunt);
        Integer profId = SessionController.getCurrentUserId(request);
        List<TreasureHunt> availableHunts = treasureHuntRepository.getAllActiveTreasureHunts(offset);
        List<TreasureHunt> myHunts = treasureHuntRepository.getAllUserTreasureHunts(profId);
        return profileRepository.findById(profId).thenApplyAsync(profile -> {
            return profile.map(profile1 -> {
                return ok(treasureHunts.render(profile1, availableHunts, myHunts, destinationRepository.getPublicDestinations(), huntForm, new RoutedObject<TreasureHunt>(hunt, true, true), paginationHelper, request, messagesApi.preferred(request)));
            }).orElseGet(() -> redirect("/login"));
        });
    }

    /**
     * Implement the undo delete method from interface
     * @param treasureHuntID - ID of the treausre hunt to undo deletion of
     */
    public void undo(int treasureHuntID) {
        treasureHuntRepository.setSoftDelete(treasureHuntID, 0);
    }
}
