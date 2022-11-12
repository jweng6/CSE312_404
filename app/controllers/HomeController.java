package controllers;

import akka.stream.impl.JsonObjectParser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import domain.Course;
import domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.i18n.MessagesApi;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.*;
import service.CourseService;
import service.UserService;
import service.impl.CourseImpl;
import service.impl.UserImpl;
import play.api.libs.json.*;


import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;

import com.typesafe.config.Config;
import play.mvc.Http.Session;

import java.util.ArrayList;
import java.util.Optional;
import domain.Info;
import java.util.List;
import java.util.Arrays;

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
            System.out.println(data.getEmail());
            System.out.println(data.getPassword());
            System.out.println(data.getFirstname());
            System.out.println(data.getLastname());

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
        //显示登入页面，并且清空所有session
        return ok(views.html.sign_in.render(userForm,f,request, messagesApi.preferred(request))).withNewSession();

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

        //返回的是一个true和false
        boolean check = user.login(request_email,request_password);

        //登入正确：去main page，并且添加connecting的session。
        if (check ==true) {

            return redirect("/main").addingToSession(request, "connecting",request_email);
        }

        //登入失败：返回登入页面，并且添加connect_fail的session。
        return redirect("/").addingToSession(request, "connect_fail",request_email);


    }

    public Result showCreate(Http.Request request){

        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        if (connecting.isPresent() == true){
            return ok(views.html.create_course.render(courseForm,request,messagesApi.preferred(request)));
        }

        //不在线（没登入） 返回401
        return unauthorized("Oops, you are not connected");
    }

    public Result postCreate(Http.Request request) throws SQLException, ClassNotFoundException {
        final Form<Course> boundForm = courseForm.bindFromRequest(request);
        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(views.html.create_course.render(boundForm,request, messagesApi.preferred(request)));
        } else {
            Course data = boundForm.get();

            //获取session里的email，然后转换从optional<String> -> String:
            String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
            //返回的是一个course放到session中
            Course c = course.addCourse(data.getCourseName(), user.getUserByEmail(session_email));
            return redirect("/main").addingToSession(request, "connecting",session_email);
        }
    }

    public Result showJoin(Http.Request request){

        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        if (connecting.isPresent() == true){
            return ok(views.html.join_course.render(courseForm,request,messagesApi.preferred(request)));
        }

        //不在线（没登入） 返回401
        return unauthorized("Oops, you are not connected");

    }

    public Result postJoin(Http.Request request){
        final Form<Course> boundForm = courseForm.bindFromRequest(request);
        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(views.html.join_course.render(boundForm,request, messagesApi.preferred(request)));
        } else {
            Course data = boundForm.get();
            //获取session里的email，然后转换从optional<String> -> String:
            String session_email = request.session().get("connecting").map(Object::toString).orElse(null);

            System.out.println(data.getCode());
            //加入课程，返回的是一个true(成功)或false(失败)
            boolean join_course= course.joinCourse(session_email,data.getCode());
//            "alert","You are already enrolled in this course"
            if (!join_course ){
                redirect("/main").addingToSession(request, "connecting",session_email);
            }

            return redirect("/main").addingToSession(request, "connecting",session_email);
        }
    }
    public Result showMain(Http.Request request){

        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        System.out.println("session connecting:");
        System.out.println(connecting);
        System.out.println("\n");

        //获取session里的email，然后转换从optional<String> -> String:
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        if (connecting.isPresent() == true){
            List<Info> allCourse = course.showCourse(session_email);
            return ok(views.html.main_page.render(allCourse));
        }

        //不在线（没登入） 返回401
        return unauthorized("Oops, you are not connected");

    }

}