package controllers;

import domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.i18n.MessagesApi;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import scala.Int;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import utility.CRUD;


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
    MessagesApi messagesApi;
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Inject
    public HomeController(FormFactory formFactory, MessagesApi messagesApi) {
        this.userForm = formFactory.form(User.class);
        this.courseForm = formFactory.form(Course.class);
        this.questionFrom = formFactory.form(Question.class);
        this.messagesApi = messagesApi;
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result showRegister(Http.Request request) {
        return ok(views.html.sign_up.render(userForm, request, messagesApi.preferred(request)));
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
            user.addUser(data.getEmail(), data.getFirstname(), data.getLastname(), data.getPassword());
            return redirect("/").withNewSession();
        }
    }

    public Result showLogin(Http.Request request) {
        Optional<String> connect_fail = request.session().get("connect_fail");
        String f = "false";
        String t = "true";

        //如果登入失败：返回alert,并且清空所有session
        if (connect_fail.isPresent() == true) {

            return ok(views.html.sign_in.render(userForm, t, request, messagesApi.preferred(request))).withNewSession();
        }
        //显示登入页面，并且清空所有session
        return ok(views.html.sign_in.render(userForm, f, request, messagesApi.preferred(request))).withNewSession();

    }

    public Result login(Http.Request request) throws SQLException, ClassNotFoundException {
        final Form<User> loginForm = userForm.bindFromRequest(request);
        String request_email = loginForm.get().getEmail();
        String request_password = loginForm.get().getPassword();

        if (loginForm.hasErrors()) {
            String f = "false";
            logger.error("errors = {}", loginForm.errors());
            return badRequest(views.html.sign_in.render(loginForm, f, request, messagesApi.preferred(request)));
        }

        //返回的是一个true和false
        boolean check = user.login(request_email, request_password);

        //登入正确：去main page，并且添加connecting的session。
        if (check == true) {

            return redirect("/main").addingToSession(request, "connecting", request_email);
        }

        //登入失败：返回登入页面，并且添加connect_fail的session。
        return redirect("/").addingToSession(request, "connect_fail", request_email);


    }

    public Result showCreate(Http.Request request) {

        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        if (connecting.isPresent() == true) {
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
            Course c = course.addCourse(data.getCourseName(), user.getUserByEmail(session_email));
            return redirect("/main").addingToSession(request, "connecting", session_email);
        }
    }

    public Result showJoin(Http.Request request) {

        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        if (connecting.isPresent() == true) {
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

            System.out.println(data.getCode());
            //加入课程，返回的是一个true(成功)或false(失败)
            boolean join_course = course.joinCourse(session_email, data.getCode());
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

        if (connecting.isPresent() == true) {
            List<Info> allCourse = course.showCourse(session_email);
            return ok(views.html.main_page.render(allCourse));
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

        //获取session里的email，然后转换从optional<String> -> String:
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        if (connecting.isPresent() == true) {
            Course courseInfo = course.course_info(Integer.parseInt(code));
            courseInfo.setCourseName(courseInfo.getCourseName().toUpperCase());
            Boolean isInstrutor = course.isInstrutor(Integer.parseInt(code), session_email);
            String s = "none";
            List<Question> listq = question.showAllQuestionIns(Integer.parseInt(code));
            Question currq = new Question();
            if (isInstrutor) {
//              这里面是：course_ins.render(courseInfo,listq,show,q, email, questionFrom,request,messageApi.preferred(request),currq))
//                    show：0 = 不显示东西 ; 1 = 选择问题后，准备输入时间，然后发布 ; 2 = add question ; 3 = roster
                return ok(views.html.course_ins.render(courseInfo, listq, s, currq, session_email, questionFrom, request, messagesApi.preferred(request))).addingToSession(request, "code", code);
            } else { //if is student
                List<Question> listqed = question.showAllQuestion(Integer.parseInt(code));
                return ok(views.html.course_student.render(courseInfo, listqed, s, currq, session_email, questionFrom, request, messagesApi.preferred(request))).addingToSession(request, "code", code);
            }
        }
        //不在线（没登入） 返回401
        return unauthorized("Oops, you are not connected");
    }

    public Result showCourse_with_status(String code, String status, Http.Request request) {
        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        System.out.println("session connecting:");
        System.out.println(connecting);
        System.out.println("\n");

        //获取session里的email，然后转换从optional<String> -> String:
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        if (connecting.isPresent()) {
            Boolean isInstrutor = course.isInstrutor(Integer.parseInt(code), session_email);
            Course courseInfo = course.course_info(Integer.parseInt(code));
            courseInfo.setCourseName(courseInfo.getCourseName().toUpperCase());
            List<Question> listq = question.showAllQuestionIns(Integer.parseInt(code));
            Question currq = new Question();
            if (isInstrutor) {
                System.out.println("1111");
//              这里面是：course_ins.render(courseInfo,listq,show,questionFrom,request,messageApi.preferred(request),currq))
//                    show：'none' = 不显示东西 ; 'show_question' = 选择问题后，准备输入时间，然后发布 ; 'add_question' = add question ; "roster" = roster
                return ok(views.html.course_ins.render(courseInfo, listq, status, currq, session_email, questionFrom, request, messagesApi.preferred(request)));
            } else { //if is student

                List<Question> listqed = question.showAllQuestion(Integer.parseInt(code));
                return ok(views.html.course_student.render(courseInfo, listqed, status, currq, session_email, questionFrom, request, messagesApi.preferred(request))).addingToSession(request, "code", code);
            }
        }
        //不在线（没登入） 返回401
        return unauthorized("Oops, you are not connected");
    }


    public Result post_course_with_status(String code, String status, Http.Request request) throws SQLException, ClassNotFoundException {
        //确定用户是在线的
        Optional<String> connecting = request.session().get("connecting");
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        if (connecting.isPresent()) {
            Boolean isInstrutor = course.isInstrutor(Integer.parseInt(code), session_email);
            if (isInstrutor) {
                Course courseInfo = course.course_info(Integer.parseInt(code));
                courseInfo.setCourseName(courseInfo.getCourseName().toUpperCase());
//              这里面是：course_ins.render(courseInfo,listq,show,questionFrom,request,messageApi.preferred(request)))
//                    show：'none' = 不显示东西 ; 'show_question' = 选择问题后，准备输入时间，然后发布 ; 'add_question' = add question ; "roster" = roster
                List<Question> listq = question.showAllQuestionIns(Integer.parseInt(code));
                Question currq = new Question();
                if (status.equals("add_question")) {
                    final Form<Question> addQuestionForm = questionFrom.bindFromRequest(request);
                    String request_header = addQuestionForm.get().getHeader();
                    Integer request_from = addQuestionForm.get().getFrom();
                    String request_details = addQuestionForm.get().getDetail();
                    String request_answer = addQuestionForm.get().getAnswer();
                    Integer request_grade = addQuestionForm.get().getGrade();

                    String request_A = addQuestionForm.get().getAnswerA();
                    String request_B = addQuestionForm.get().getAnswerB();
                    String request_C = addQuestionForm.get().getAnswerC();
                    String request_D = addQuestionForm.get().getAnswerD();

                    String request_answerA = addQuestionForm.get().getHeader();

                    question.addQuestion(request_header, request_details, request_answer, request_from, request_grade, request_A, request_B, request_C, request_D);
                    return redirect("/course/" + code).addingToSession(request, "connecting", session_email);
                }
            }
        }
        //不在线（没登入） 返回401
        return unauthorized("Oops, you are not connected");
    }


    public Result show_question(String code, String questionId, Http.Request request) {
        Optional<String> connecting = request.session().get("connecting");
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        
        if (connecting.isPresent() == true) {
            Boolean isInstrutor = course.isInstrutor(Integer.parseInt(code), session_email);
            Course courseInfo = course.course_info(Integer.parseInt(code));
            courseInfo.setCourseName(courseInfo.getCourseName().toUpperCase());
            Question currq = question.getQuestion(Integer.parseInt(questionId));
            List<Question> listq = question.showAllQuestionIns(Integer.parseInt(code));
            if (isInstrutor) {
                return ok(views.html.course_ins.render(courseInfo, listq, "show_question", currq, session_email, questionFrom, request, messagesApi.preferred(request)));
            } else {
                String qid = request.path().split("/")[4];
                boolean answerd_question = question.getAllExpireCheckByQid(Integer.parseInt(qid), Integer.parseInt(code));
                if (answerd_question) {
                    List<Question> listqed = question.showAllQuestion(Integer.parseInt(code));
                    return ok(views.html.course_student.render(courseInfo, listqed, "show_question", currq, session_email, questionFrom, request, messagesApi.preferred(request))).addingToSession(request, "code", code);
                }
                return unauthorized("Oops, you are not connected");
            }
        }
        //不在线（没登入） 返回401
        return unauthorized("Oops, you are not connected");
    }

    public Result showinstructorgradebook(Integer code, Http.Request request) {
        Optional<String> connecting = request.session().get("connecting");
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        List<User> users = course.instrSeeGrade(code);
        List<Info> allCourse = course.showCourse(session_email);
        System.out.println(users);
        String coursename="";
        int x=0;
        while(x!=allCourse.size()){
            if(Integer.parseInt(allCourse.get(x).getCode())==code){
                coursename=allCourse.get(x).getCourseName();
            }
            x=x+1;
        }
        if (connecting.isPresent() == true) {
            return ok(views.html.instructorgradebook.render(users,coursename,session_email));
        }
        return unauthorized("Oops, you are not connected");
    }

    public Result showstudentgradebook(Integer code, Http.Request request) {
        Optional<String> connecting = request.session().get("connecting");
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        try {
            User current  = user.getUserByEmail(session_email);
            Course thisCourse = course.course_info(code);
            List<Answer> li = course.showAllStudentAnswer(current.getId(), thisCourse.getId());
            List<Question> allquestions = new ArrayList<>();
            List<String> expires = new ArrayList<>();
            int earnGrade = 0;
            int totalGrade = 0;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            for (int i = 0; i< li.size(); i++){
                Question question = this.question.getQuestion(li.get(i).getQuestion_id());
                earnGrade += li.get(i).getReturn_grade();
                totalGrade += question.getGrade();
                question.setGrade(li.get(i).getReturn_grade());
                LocalDateTime triggerTime =
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(question.getExpires()),
                                TimeZone.getDefault().toZoneId());
                expires.add(triggerTime.format(formatter));
                allquestions.add(question);
            }
            if (connecting.isPresent() == true) {
                //earnGrade 是这个学生在这节课获得的分
                //totalGrade 是这节课的中分
                //expires list类型 是这节课问题的提交日期 格式 yyyy/MM/dd
                
                return ok(views.html.studentgradebook.render(code, allquestions, earnGrade, totalGrade,thisCourse.getCourseName(),expires));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unauthorized("Oops, you are not connected");
    }

    public Result showGradebook(Http.Request request) {
        Optional<String> connecting = request.session().get("connecting");
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        List<Info> allCourse = course.showCourse(session_email);
        try {
            User current = user.getUserByEmail(session_email);
            List<Integer> allCourseId = course.getAllCourse(current.getId());
            int zz = 0;
            List<Integer> allgrade = new ArrayList<>();
            List<Integer> total = new ArrayList<>();
            while (zz != allCourse.size()) {
                String code = allCourse.get(zz).getCode();
                if (allCourse.get(zz).getCode() == code) {
                    int temp = 0;
                    List<Question> questions = question.showAllQuestion(allCourseId.get(zz));
                    int thisGrade = course.showGrade(current.getId(), allCourseId.get(zz));
                    for (int i = 0; i < questions.size(); i++){
                        temp += questions.get(i).getGrade();
                    }
                    total.add(temp);
                    allgrade.add(thisGrade);
                }
                zz = zz + 1;
            }
            // total 是一个list ｜｜ 是每一个course的总成绩
            if (connecting.isPresent() == true) {
                return ok(views.html.gradebook.render(allCourse, allgrade));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unauthorized("Oops, you are not connected");
    }

    public Result AllCourseYouSee(Http.Request request){
        Optional<String> connecting = request.session().get("connecting");
        List<Course> allcourse = course.AllCourse();
        //allcourse里面有 教授邮箱，课的名字，这节课的code前端可以不用显示
        if (connecting.isPresent() == true) {
//            return ok(views.html.gradebook.render());
            //自己写return回去
        }
        return unauthorized("Oops, you are not connected");
    }
//    public Result showGradebook(Http.Request request){
//        Optional<String> connecting = request.session().get("connecting");
//        if (connecting.isPresent() == true){
//            return ok(views.html.gradebook.render());
//        }
//        return unauthorized("Oops, you are not connected");
//    }
//    public Result showinstructorgradebook(Http.Request request){
//        Optional<String> connecting = request.session().get("connecting");
//        if (connecting.isPresent() == true){
//            return ok(views.html.instructorgradebook.render());
//        }
//        return unauthorized("Oops, you are not connected");
//    }
//    public Result showstudentgradebook(Http.Request request){
//        Optional<String> connecting = request.session().get("connecting");
//        if (connecting.isPresent() == true){
//            return ok(views.html.studentgradebook.render());
//        }
//        return unauthorized("Oops, you are not connected");
//    }


}
