package controllers;

import domain.Course;
import domain.Question;
import domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.i18n.MessagesApi;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import service.CourseService;
import service.QuestionService;
import service.UserService;
import service.impl.CourseImpl;
import service.impl.QuestionImpl;
import service.impl.UserImpl;


import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.sql.SQLException;

import java.util.*;
import java.util.List;

import domain.Info;
import utility.CRUD;
import utility.Constant;

import utility.Constant;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
@Singleton
public class HomeController extends Controller {

    UserService user = new UserImpl();
    CourseService course = new CourseImpl();
    QuestionService question = new QuestionImpl();

    Form<User> userForm;
    Form<Question> questionFrom;
    Form<Course> courseForm;
    Form<Question> editForm;

    MessagesApi messagesApi;
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Inject
    public HomeController(FormFactory formFactory, MessagesApi messagesApi) {
        this.userForm = formFactory.form(User.class);
        this.courseForm = formFactory.form(Course.class);
        this.questionFrom = formFactory.form(Question.class);
        this.editForm = formFactory.form(Question.class);
        this.messagesApi = messagesApi;
    }

    /**S
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result showRegister(Http.Request request) {
        String alert = "";
        return ok(views.html.sign_up.render(alert,userForm, request, messagesApi.preferred(request)));
    }

    public Result notExistPage(String url,Http.Request request) {
        return Results.status(404, "Page not found");
    }


    public Result register(Http.Request request) throws SQLException, ClassNotFoundException {
        final Form<User> boundForm = userForm.bindFromRequest(request);
        if (boundForm.hasErrors()) {
            return redirect("/").withNewSession();
        } else {
            User data = boundForm.get();
//            String email, String firstname, String lastname, String password
            //返回的是一个user
            User isUserExist = user.getUserByEmail(Constant.injection(data.getEmail()));
            System.out.println(isUserExist);
            if(isUserExist== null){
                user.addUser(Constant.injection(data.getEmail()), Constant.injection(data.getFirstname()), Constant.injection(data.getLastname()), Constant.injection(data.getPassword()));
                return redirect("/").withNewSession();
            }
            String alert = "Email already Exist";
            return ok(views.html.sign_up.render(alert,userForm, request, messagesApi.preferred(request)));
        }
    }

    public Result showLogin(Http.Request request) {
        Optional<String> connect_fail = request.session().get("connect_fail");
        String f = "false";
        String t = "true";
        //如果登入失败：返回alert,并且清空所有session
        if (connect_fail.isPresent()) {
            return ok(views.html.sign_in.render(userForm, t, request, messagesApi.preferred(request))).withNewSession();
        }
        //显示登入页面，并且清空所有session
        return ok(views.html.sign_in.render(userForm, f, request, messagesApi.preferred(request))).withNewSession();

    }

    public Result login(Http.Request request) throws SQLException, ClassNotFoundException {
        final Form<User> loginForm = userForm.bindFromRequest(request);
        String request_email = Constant.injection(loginForm.get().getEmail());
        String request_password = Constant.injection(loginForm.get().getPassword());

        if (loginForm.hasErrors()) {
            String f = "false";
            logger.error("errors = {}", loginForm.errors());
            return badRequest(views.html.sign_in.render(loginForm, f, request, messagesApi.preferred(request)));
        }
        //返回的是一个true和false
        boolean check = user.login(request_email, request_password);
        //登入正确：去main page，并且添加connecting的session。
        if (check) {
            return redirect("/main").addingToSession(request, "connecting", request_email);
        }
        //登入失败：返回登入页面，并且添加connect_fail的session。
        return redirect("/").addingToSession(request, "connect_fail", request_email);


    }

    public Result showCreate(Http.Request request) {
        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        if (connecting.isPresent()) {
            return ok(views.html.create_course.render(courseForm, request, messagesApi.preferred(request)));
        }
        //不在线（没登入） 返回401
        return unauthorized("Oops, you are not connected");
    }

    public Result postCreate(Http.Request request) throws SQLException, ClassNotFoundException {
        final Form<Course> boundForm = courseForm.bindFromRequest(request);
        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(views.html.create_course.render(boundForm, request, messagesApi.preferred(request)));
        } else {
            Course data = boundForm.get();
            //获取session里的email，然后转换从optional<String> -> String:
            String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
            //返回的是一个course放到session中
            Course c = course.addCourse(Constant.injection(data.getCourseName()), user.getUserByEmail(session_email));
            return redirect("/main").addingToSession(request, "connecting", session_email);
        }
    }

    public Result showJoin(Http.Request request) {

        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        if (connecting.isPresent()) {
            return ok(views.html.join_course.render(courseForm, request, messagesApi.preferred(request)));
        }

        //不在线（没登入） 返回401
        return unauthorized("Oops, you are not connected");

    }

    public Result postJoin(Http.Request request) {
        final Form<Course> boundForm = courseForm.bindFromRequest(request);
        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(views.html.join_course.render(boundForm, request, messagesApi.preferred(request)));
        } else {
            Course data = boundForm.get();
            //获取session里的email，然后转换从optional<String> -> String:
            String session_email = request.session().get("connecting").map(Object::toString).orElse(null);

            //加入课程，返回的是一个true(成功)或false(失败)
            int code = Integer.parseInt(Constant.injection(data.getCode().toString()));
            boolean join_course = course.joinCourse(session_email, code);
//            "alert","You are already enrolled in this course"
            if (!join_course) {
                redirect("/main").addingToSession(request, "connecting", session_email);
            }

            return redirect("/main").addingToSession(request, "connecting", session_email);
        }
    }

    public Result showMain(Http.Request request) {
        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        System.out.println("session connecting:");
        System.out.println(connecting);
        System.out.println("\n");
        //获取session里的email，然后转换从optional<String> -> String:
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);

        if (connecting.isPresent()) {
            List<Info> allCourse = course.showCourse(session_email);
            return ok(views.html.main_page.render(allCourse,session_email));
        }

        //不在线（没登入） 返回401
        return unauthorized("Oops, you are not connected");
    }

    public Result showCourse(String code, Http.Request request) {
        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        System.out.println("session connecting:");
        System.out.println(connecting);
        System.out.println("\n");

        String code_safe = Constant.injection(code);

        //获取session里的email，然后转换从optional<String> -> String:
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        if (connecting.isPresent()) {
            List<Info> allCourse = course.showCourse(session_email);
            Iterator<Info> it =  allCourse.iterator();
            boolean hasCourse = false;
            // 查看这个session的用户有没有这个course： 如果没有就返回403
            while (it.hasNext()){
                if(it.next().getCode().equals(code)){
                    hasCourse = true;
                }
            }
            if (hasCourse){
                Course courseInfo = course.course_info(Integer.parseInt(code_safe));
                courseInfo.setCourseName(courseInfo.getCourseName().toUpperCase());
                Boolean isInstrutor = course.isInstrutor(Integer.parseInt(code_safe), session_email);
                String s = "none";
                List<Question> listq = question.showAllQuestionIns(Integer.parseInt(code_safe));
                Question currq = new Question();
                currq.setId(-1);
                if (isInstrutor) {
//              这里面是：course_ins.render(courseInfo,listq,show,q, email, questionFrom,request,messageApi.preferred(request),currq))
//                    show：0 = 不显示东西 ; 1 = 选择问题后，准备输入时间，然后发布 ; 2 = add question ; 3 = roster
                    List<User> roster = user.showAllStudent(Integer.parseInt(code_safe));
                    for (User value : roster) {
                        int uid = value.getId();
                        value.setGrade(course.showGrade(uid, Integer.parseInt(code)));
                    }

                    return ok(views.html.course_ins.render(courseInfo, listq, s, currq, session_email, roster, questionFrom, request, messagesApi.preferred(request))).addingToSession(request, "code", code);
                } else { //if is student
                    List<Question> listqed = question.showAllQuestion(Integer.parseInt(code_safe));
                    return ok(views.html.course_student.render(courseInfo, listq, s, currq, session_email, questionFrom, request, messagesApi.preferred(request))).addingToSession(request, "code", code);
                }
            }
            else {
                return Results.status(403, "Page not found");
            }
        }
        //不在线（没登入） 返回401
        return unauthorized("unauthorized, Please login your account");
    }

    public Result showCourse_with_status(String code, String status, Http.Request request) {
        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        System.out.println("session connecting:");
        System.out.println(connecting);
        System.out.println("\n");

        String code_safe = Constant.injection(code);
        String status_safe = Constant.injection(status);

        //获取session里的email，然后转换从optional<String> -> String:
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        if (connecting.isPresent()) {
            List<Info> allCourse = course.showCourse(session_email);
            Iterator<Info> it =  allCourse.iterator();
            boolean hasCourse = false;
            // 查看这个session的用户有没有这个course： 如果没有就返回403
            while (it.hasNext()){
                if(it.next().getCode().equals(code)){
                    hasCourse = true;
                }
            }
            if (hasCourse){
                Boolean isInstrutor = course.isInstrutor(Integer.parseInt(code_safe), session_email);
                Course courseInfo = course.course_info(Integer.parseInt(code_safe));
                courseInfo.setCourseName(courseInfo.getCourseName().toUpperCase());
                List<Question> listq = question.showAllQuestionIns(Integer.parseInt(code_safe));
                Question currq = new Question();
                currq.setId(-1);
                if (isInstrutor) {
                    System.out.println("1111");
//              这里面是：course_ins.render(courseInfo,listq,show,questionFrom,request,messageApi.preferred(request),currq))
//                    show：'none' = 不显示东西 ; 'show_question' = 选择问题后，准备输入时间，然后发布 ; 'add_question' = add question ; "roster" = roster

                    List<User> roster = user.showAllStudent(Integer.parseInt(code_safe));

                    for (User value : roster) {
                        int uid = value.getId();
                        value.setGrade(course.showGrade(uid, Integer.parseInt(code)));
                    }
                    return ok(views.html.course_ins.render(courseInfo, listq, status_safe, currq, session_email, roster, questionFrom, request, messagesApi.preferred(request)));
                } else { //if is student
                    List<Question> listqed = question.showAllQuestion(Integer.parseInt(code));
                    return ok(views.html.course_student.render(courseInfo, listq, status_safe, currq, session_email, questionFrom, request, messagesApi.preferred(request)));
                }
            }

            else {
                return Results.status(403, "Page not found");
            }
        }
        //不在线（没登入） 返回401
        return unauthorized("unauthorized, Please login your account");
    }


    public Result post_course_with_status(String code, String status, Http.Request request) throws SQLException, ClassNotFoundException {
        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        String code_safe = Constant.injection(code);
        String status_safe = Constant.injection(status);
        if (connecting.isPresent()) {
            List<Info> allCourse = course.showCourse(session_email);
            Iterator<Info> it =  allCourse.iterator();
            boolean hasCourse = false;
            // 查看这个session的用户有没有这个course： 如果没有就返回403
            while (it.hasNext()){
                if(it.next().getCode().equals(code)){
                    hasCourse = true;
                }
            }
            if (hasCourse){
                Boolean isInstrutor = course.isInstrutor(Integer.parseInt(code_safe), session_email);
                if (isInstrutor) {
                    Course courseInfo = course.course_info(Integer.parseInt(code_safe));
                    courseInfo.setCourseName(courseInfo.getCourseName().toUpperCase());
//              这里面是：course_ins.render(courseInfo,listq,show,questionFrom,request,messageApi.preferred(request)))
//                    show：'none' = 不显示东西 ; 'show_question' = 选择问题后，准备输入时间，然后发布 ; 'add_question' = add question ; "roster" = roster
                    List<Question> listq = question.showAllQuestionIns(Integer.parseInt(code_safe));
                    Question currq = new Question();
                    currq.setId(-1);
                    if (status.equals("add_question")) {
                        final Form<Question> addQuestionForm = questionFrom.bindFromRequest(request);

                        String request_header = Constant.injection(addQuestionForm.get().getHeader());
                        Integer request_from = Integer.parseInt(Constant.injection(Integer.toString(addQuestionForm.get().getFrom())));
                        String request_details = Constant.injection(addQuestionForm.get().getDetail());
                        String request_answer = Constant.injection(addQuestionForm.get().getAnswer());
                        Integer request_grade = Integer.parseInt(Constant.injection(Integer.toString(addQuestionForm.get().getGrade())));

                        String request_A = Constant.injection(addQuestionForm.get().getAnswerA());
                        String request_B = Constant.injection(addQuestionForm.get().getAnswerB());
                        String request_C = Constant.injection(addQuestionForm.get().getAnswerC());
                        String request_D = Constant.injection(addQuestionForm.get().getAnswerD());

                        question.addQuestion(request_header, request_details, request_answer, request_from, request_grade, request_A, request_B, request_C, request_D);
                        return redirect("/course/" + code_safe).addingToSession(request, "connecting", session_email);
                    }
                }
            }
            return Results.status(403, "Page not found");

        }
        //不在线（没登入） 返回401
        return unauthorized("unauthorized, Please login your account");
    }

    public Result show_question(String code, String questionId, Http.Request request) {
        Optional<String> connecting = request.session().get("connecting");
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        String code_safe = Constant.injection(code);
        String questionId_safe = Constant.injection(questionId);
        if (connecting.isPresent()) {
            List<Info> allCourse = course.showCourse(session_email);
            Iterator<Info> it =  allCourse.iterator();
            boolean hasCourse = false;
            // 查看这个session的用户有没有这个course： 如果没有就返回403
            while (it.hasNext()){
                if(it.next().getCode().equals(code)){
                    hasCourse = true;
                }
            }
            if (hasCourse){
                Boolean isInstrutor = course.isInstrutor(Integer.parseInt(code_safe), session_email);
                Course courseInfo = course.course_info(Integer.parseInt(code_safe));
                courseInfo.setCourseName(courseInfo.getCourseName().toUpperCase());
                Question currq = question.getQuestion(Integer.parseInt(questionId_safe));

                List<Question> listq = question.showAllQuestionIns(Integer.parseInt(code_safe));
                if (isInstrutor) {
                    List<User> roster = user.showAllStudent(Integer.parseInt(code_safe));
                    for (User value : roster) {
                        int uid = value.getId();
                        value.setGrade(course.showGrade(uid, Integer.parseInt(code)));
                    }
                    return ok(views.html.course_ins.render(courseInfo, listq, "show_question", currq, session_email, roster, questionFrom, request, messagesApi.preferred(request)));
                } else {
                    boolean answerd_question = question.getAllExpireCheckByQid(Integer.parseInt(questionId_safe), Integer.parseInt(code_safe));
                    if (answerd_question) {
                        List<Question> listqed = question.showAllQuestion(Integer.parseInt(code_safe));
                        return ok(views.html.course_student.render(courseInfo, listq, "show_question", currq, session_email, questionFrom, request, messagesApi.preferred(request)));
                    }

                    return ok(views.html.course_student.render(courseInfo, listq, "show_onlyQ", currq, session_email, questionFrom, request, messagesApi.preferred(request)));
                }
            }
            return Results.status(404, "Page not found");
        }
        //不在线（没登入） 返回401
        return unauthorized("unauthorized, Please login your account");
    }


    public Result showProfile(String email,Http.Request request) throws SQLException, ClassNotFoundException {
        Optional<String> connecting = request.session().get("connecting");
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        if (connecting.isPresent()){
            assert session_email != null;
            User userInfo = user.getUserByEmail(session_email);
            // show_profile = has edit button
            // show_otheruser = no edit button
            // edit_profile = editing with form


            System.out.println(session_email.equals(Constant.injection(email)));
            if(session_email.equals(Constant.injection(email))){ //是本人登入自己的主页
                String show="show_profile";
                return ok(views.html.user_profile.render(userInfo,show,editForm,request,messagesApi.preferred(request)));
            }
            else {//陌生人查看主页
                User isUserExist = user.getUserByEmail(Constant.injection(email));
                if(isUserExist== null){
                    return Results.status(404, "Page not found");
                }
                String show="show_otheruser";
                User cur_userInfo = user.getUserByEmail(Constant.injection(email));
                return ok(views.html.user_profile.render(cur_userInfo,show,editForm,request,messagesApi.preferred(request)));
            }
        }
        //不在线（没登入） 返回401
        return unauthorized("unauthorized, Please login your account");
    }

    public Result posteditprofile(String email,Http.Request request) throws SQLException, ClassNotFoundException {
        Optional<String> connecting = request.session().get("connecting");
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);

        String email_safe = Constant.injection(email);
        if (connecting.isPresent()){
            assert session_email != null;
            // user can edit their own profile:
            if(session_email.equals(email_safe)){
                final Form<Question> editf = editForm.bindFromRequest(request);
                String first = Constant.injection(editf.get().getHeader());
                String last = Constant.injection(editf.get().getAnswer());
                String desc = Constant.injection(editf.get().getDetail());
                user.updateDescription(email_safe, desc);
                User a = user.updateName(session_email, first, last);
                return redirect("/profile/"+email_safe);
            }
            return Results.status(404, "Page not found");
        }
        //不在线（没登入） 返回401
        return unauthorized("unauthorized, Please login your account");
    }


}



