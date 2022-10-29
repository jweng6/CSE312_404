package controllers;

import domain.Course;
import domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.i18n.MessagesApi;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import service.CourseService;
import service.UserService;
import service.impl.CourseImpl;
import service.impl.UserImpl;


import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;

import com.typesafe.config.Config;
import play.mvc.Http.Session;
import java.util.Optional;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
@Singleton
public class HomeController extends Controller {

    UserService user = new UserImpl();
    CourseService course = new CourseImpl();
    Form<User> userForm;
    Form<Course> courseForm;
    MessagesApi messagesApi;
    private final Logger logger = LoggerFactory.getLogger(getClass()) ;


    @Inject
    public HomeController(FormFactory formFactory, MessagesApi messagesApi) {
        this.userForm = formFactory.form(User.class);
        this.courseForm = formFactory.form(Course.class);
        this.messagesApi = messagesApi;
    }
        /**
         * An action that renders an HTML page with a welcome message.
         * The configuration in the <code>routes</code> file means that
         * this method will be called when the application receives a
         * <code>GET</code> request with a path of <code>/</code>.
         */

    public Result showRegister(Http.Request request){
        return ok(views.html.sign_up.render(userForm,request,messagesApi.preferred(request)));
    }

    public Result register(Http.Request request) {
        final Form<User> boundForm = userForm.bindFromRequest(request);
        if (boundForm.hasErrors()) {
            return redirect("/").withNewSession();
        } else {
            User data = boundForm.get();
//            System.out.println(data.getEmail());
//            System.out.println(data.getPassword());
//            System.out.println(data.getFirstname());
//            System.out.println(data.getLastname());
//            String email, String firstname, String lastname, String password
            //返回的是一个user
            user.addUser(data.getEmail(),data.getFirstname(),data.getLastname(),data.getPassword());
            return redirect("/").withNewSession();
        }
    }

    public Result showLogin(Http.Request request){
        Optional<String> connect_fail = request.session().get("connect_fail");
        String f = "false";
        String t = "true";

        //如果登入失败：返回alert,并且清空所有session
        if (connect_fail.isPresent() == true){
            return ok(views.html.sign_in.render(userForm,t,request, messagesApi.preferred(request))).withNewSession();
        }
        else{ //显示登入页面，并且清空所有session
            return ok(views.html.sign_in.render(userForm,f,request, messagesApi.preferred(request))).withNewSession();
        }
    }

    public Result login(Http.Request request) throws SQLException, ClassNotFoundException {
        final Form<User> loginForm = userForm.bindFromRequest(request);
        String request_email = loginForm.get().getEmail();
        String request_password = loginForm.get().getPassword();

        if (loginForm.hasErrors()) {
            String f = "false";
            logger.error("errors = {}", loginForm.errors());
            return badRequest(views.html.sign_in.render(loginForm,f, request, messagesApi.preferred(request)));
        }
        else {
            //返回的是一个true和false
            boolean check = user.login(request_email,request_password);
            //登入正确：去main page，并且添加connecting的session。
            if (check ==true) {
                return redirect("/main").addingToSession(request, "connecting",request_email);
            }
            //登入失败：返回登入页面，并且添加connect_fail的session。
            return redirect("/").addingToSession(request, "connect_fail",request_email);


        }
    }

    public Result showCreate(Http.Request request){
        return ok(views.html.create_course.render(courseForm,request,messagesApi.preferred(request)));
    }

    public Result postCreate(Http.Request request) throws SQLException, ClassNotFoundException {
        final Form<Course> boundForm = courseForm.bindFromRequest(request);
        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(views.html.create_course.render(boundForm,request, messagesApi.preferred(request)));
        } else {
            Course data = boundForm.get();
//            System.out.println(data.getCourseName());
            //返回的是一个course放到session中
            course.addCourse(data.getCourseName(), user.getUserByEmail(request.session().get("connecting").toString()));
            return ok(views.html.main_page.render());
        }
    }

    public Result showJoin(Http.Request request){
        return ok(views.html.join_course.render(courseForm,request,messagesApi.preferred(request)));
    }

    public Result postJoin(Http.Request request){
        final Form<Course> boundForm = courseForm.bindFromRequest(request);
        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(views.html.join_course.render(boundForm,request, messagesApi.preferred(request)));
        } else {
            Course data = boundForm.get();
//            System.out.println(data.getEmail());
//            System.out.println(data.getCode());
            //返回的是一个true或false
            course.joinCourse(data.getEmail(),data.getCode());
            return ok(views.html.main_page.render());
        }
    }
    public Result showMain(Http.Request request){

        return ok(views.html.main_page.render());
    }


}
