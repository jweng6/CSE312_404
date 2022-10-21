package controllers;

import domain.User;
import play.mvc.*;
import service.UserService;
import service.impl.UserImpl;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    UserImpl user = new UserImpl();
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(views.html.register.render());
    }

    public Result process(){
        return ok(views.html.index.render());
    }

    public Result register(String email, String password){
        user.addUser(email,password);//传id
        User user = new User();
        return ok(views.html.index.render());
    }

    public Result signIn(){
        return null;
    }

}
