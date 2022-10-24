package controllers;

import domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.i18n.MessagesApi;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import service.UserService;
import service.impl.UserImpl;


import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
@Singleton
public class HomeController extends Controller {

    UserService user = new UserImpl();
    Form<User> userForm;
    MessagesApi messagesApi;
    private final Logger logger = LoggerFactory.getLogger(getClass()) ;
    @Inject
    public HomeController(FormFactory formFactory, MessagesApi messagesApi) {
        this.userForm = formFactory.form(User.class);
        this.messagesApi = messagesApi;
    }
        /**
         * An action that renders an HTML page with a welcome message.
         * The configuration in the <code>routes</code> file means that
         * this method will be called when the application receives a
         * <code>GET</code> request with a path of <code>/</code>.
         */

    public Result showRegister(Http.Request request){
        return ok(views.html.register.render(userForm,request,messagesApi.preferred(request)));
    }

    public Result register(Http.Request request) {
        final Form<User> boundForm = userForm.bindFromRequest(request);

        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(views.html.register.render(boundForm,request, messagesApi.preferred(request)));
        } else {
            User data = boundForm.get();
            System.out.println(data.getEmail());
            System.out.println(data.getPassword());
            System.out.println(data.getFirstname());
            System.out.println(data.getLastname());
//            String email, String firstname, String lastname, String password
            user.addUser(data.getEmail(),data.getFirstname(),data.getLastname(),data.getPassword());
            return ok(views.html.register.render(userForm, request, messagesApi.preferred(request)));
        }
    }


    public Result showLogin(Http.Request request){
        return ok(views.html.login.render(userForm, request, messagesApi.preferred(request)));
    }

    public Result login(Http.Request request) throws SQLException, ClassNotFoundException {
        final Form<User> loginForm = userForm.bindFromRequest(request);
        String request_email = loginForm.get().getEmail();
        String request_password = loginForm.get().getPassword();
        if (loginForm.hasErrors()) {
            logger.error("errors = {}", loginForm.errors());
            return badRequest(views.html.login.render(loginForm,request, messagesApi.preferred(request)));
        }
        else {
            user.login(request_email,request_password);
            return ok(views.html.login.render(userForm, request, messagesApi.preferred(request)));
        }
    }

}
