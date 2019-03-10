package controllers;
import play.i18n.MessagesApi;

import play.data.FormFactory;

import com.fasterxml.jackson.core.JsonParser;
import io.ebean.annotation.NotNull;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.*;
import models.Destination;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class DestinationsController extends Controller {

    private MessagesApi messagesApi;
    private final ArrayList<Destination> destinationsList;
    private final Form<Destination> form;

    @Inject
    public DestinationsController(FormFactory formFactory, MessagesApi messagesApi) {
        this.form = formFactory.form(Destination.class);
        this.messagesApi = messagesApi;
        this.destinationsList = new ArrayList<>();
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("seng302_destination.json"));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonData);
            Destination destination;
            for (int i = 0; i < rootNode.size() / 100; i++) {
                destination = Destination.fromJson(rootNode.get(i));
                destination.setId(i);
                this.destinationsList.add(destination);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Result show(Http.Request request) {
        return ok(destinations.render(destinationsList, request, messagesApi.preferred(request)));
    }

    public Result showCreate(Http.Request request) {
        Destination dest = new Destination();
        dest.setLatitude(0.0);
        dest.setLongitude(0.0);
        Form<Destination> destinationForm = form.fill(dest);
        return ok(createDestinations.render(destinationForm, request, messagesApi.preferred(request)));
    }

    public Result edit(Http.Request request, Integer id){
        Destination destination = destinationsList.get(id);
        Form<Destination> destinationForm = form.fill(destination);
        return ok(edit.render(id, destinationForm, request, messagesApi.preferred(request)));
    }

    public Result update(Http.Request request, Integer id){
        Form<Destination> destinationForm = form.bindFromRequest(request);
        Destination dest = destinationForm.get();
        destinationsList.set(id, dest);
        return redirect(routes.DestinationsController.show());
    }

    public Result save(Http.Request request){
        Form<Destination> destinationForm = form.bindFromRequest(request);
        Destination dest = destinationForm.get();

        dest.setId(destinationsList.size()+1);
        destinationsList.add(0, dest);
        return redirect(routes.DestinationsController.show());
    }

    public Result delete(Integer id){
        //System.out.println(id);
        //for (int i = id; i < destinationsList.size(); i++) {
         //       destinationsList.get(i).setId(i-1);
        //}
        //destinationsList.remove(id*0);
        return redirect(routes.DestinationsController.show());
    }

}
