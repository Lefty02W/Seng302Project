package controllers;

import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import repository.ProfileRepository;
import views.html.admin;

import javax.inject.Inject;

import static play.mvc.Results.ok;

public class AdminController {

    private MessagesApi messagesApi;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public AdminController(HttpExecutionContext httpExecutionContext, MessagesApi messagesApi){
        this.httpExecutionContext = httpExecutionContext;
        this.messagesApi = messagesApi;
    }


    public Result show(Http.Request request) {
        return ok(admin.render(request, messagesApi.preferred(request)));
    }
}
