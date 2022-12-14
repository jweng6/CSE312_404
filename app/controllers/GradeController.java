package controllers;

import domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.i18n.MessagesApi;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import service.CourseService;
import service.QuestionService;
import service.UserService;
import service.impl.CourseImpl;
import service.impl.QuestionImpl;
import service.impl.UserImpl;
import utility.Constant;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GradeController extends Controller {
    UserService user = new UserImpl();
    CourseService course = new CourseImpl();
    QuestionService question = new QuestionImpl();
    Form<User> userForm;
    Form<Question> questionFrom;
    Form<Course> courseForm;
    MessagesApi messagesApi;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    public GradeController(FormFactory formFactory, MessagesApi messagesApi) {
        this.userForm = formFactory.form(User.class);
        this.courseForm = formFactory.form(Course.class);
        this.questionFrom = formFactory.form(Question.class);
        this.messagesApi = messagesApi;
    }

    public Result showinstructorgradebook(String code, Http.Request request) {
        Optional<String> connecting = request.session().get("connecting");
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        if (session_email==null) {
            return unauthorized("Oops, you are not connected");
        }
        String code_safe = Constant.injection(code);
        List<User> users = course.instrSeeGrade_ws(Integer.parseInt(code));
        List<Info> allCourse = course.showCourse(session_email);
        String coursename="";
        int x=0;
        while(x!=allCourse.size()){
            if(Integer.parseInt(allCourse.get(x).getCode())==Integer.parseInt(code_safe)){
                coursename=allCourse.get(x).getCourseName();
            }
            x=x+1;
        }
        if (connecting.isPresent()) {
            List<Info> showallCourse = course.showCourse(session_email);
            Iterator<Info> it =  showallCourse.iterator();
            boolean hasCourse = false;
            // ????????????session????????????????????????course??? ?????????????????????403
            while (it.hasNext()){
                if(it.next().getCode().equals(code_safe)){
                    hasCourse = true;
                }
            }
            if (hasCourse) {
                Boolean isInstrutor = course.isInstrutor(Integer.parseInt(code_safe), session_email);
                if(isInstrutor){
                    return ok(views.html.instructorgradebook.render(users, coursename, session_email));
                }

            }
            return Results.status(404, "Page not found");
        }
        return unauthorized("Oops, you are not connected");
    }

    public Result showstudentgradebook(String code, Http.Request request) {
        Optional<String> connecting = request.session().get("connecting");
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        if (session_email==null) {
            return unauthorized("Oops, you are not connected");
        }
        try {
            User current  = user.getUserByEmail(session_email);
            String code_safe = Constant.injection(code);
            Course thisCourse = course.course_info(Integer.parseInt(code_safe));
            System.out.println(code);
            List<Answer> li = course.showAllStudentAnswer(current.getId(), Integer.parseInt(code_safe));
            List<Question> allquestions = new ArrayList<>();
            List<String> expires = new ArrayList<>();
            int earnGrade = course.showStudentTotalGrade(current.getId(),Integer.parseInt(code_safe)).getGrade();

            int totalGrade = 0;
//            LocalDateTime now = LocalDateTime.now().toEpochSecond();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            for (int i = 0; i< li.size(); i++){
                Question q = question.getQuestion(li.get(i).getQuestion_id());

                totalGrade += q.getGrade();
                q.setGrade(li.get(i).getReturn_grade());
                LocalDateTime triggerTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(q.getExpires()), TimeZone
                        .getDefault().toZoneId());
                expires.add(triggerTime.format(formatter));
                allquestions.add(q);
            }
            if (connecting.isPresent()) {
                List<Info> showallCourse = course.showCourse(session_email);
                Iterator<Info> it =  showallCourse.iterator();
                boolean hasCourse = false;
                // ????????????session????????????????????????course??? ?????????????????????403
                while (it.hasNext()){
                    if(it.next().getCode().equals(code_safe)){
                        hasCourse = true;
                    }
                }
                if (hasCourse) {
                    //earnGrade ???????????????????????????????????????
                    //totalGrade ?????????????????????
                    //expires list?????? ????????????????????????????????? ?????? yyyy/MM/dd
                    return ok(views.html.studentgradebook.render(Integer.parseInt(code_safe), allquestions, earnGrade, totalGrade,thisCourse.getCourseName(),expires));
                }
                return Results.status(404, "Page not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unauthorized("Oops, you are not connected");
    }

    public Result showGradebook(Http.Request request) {
        Optional<String> connecting = request.session().get("connecting");
        String session_email = request.session().get("connecting").map(Object::toString).orElse(null);
        if (session_email==null) {
            return unauthorized("Oops, you are not connected");
        }
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
                    int thisGrade = course.showStudentTotalGrade(current.getId(),Integer.parseInt(code)).getGrade();
                    for (int i = 0; i < questions.size(); i++){
                        temp += questions.get(i).getGrade();
                    }
                    total.add(temp);
                    allgrade.add(thisGrade);
                }
                zz = zz + 1;
            }
            // total ?????????list ?????? ????????????course????????????
            if (connecting.isPresent()) {
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
        //allcourse????????? ??????????????????????????????????????????code????????????????????????
        if (connecting.isPresent()) {
            return ok(views.html.allthecourse.render(allcourse));
            //?????????return??????
        }
        return unauthorized("Oops, you are not connected");
    }
}
