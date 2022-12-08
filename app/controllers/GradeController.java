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
import service.CourseService;
import service.QuestionService;
import service.UserService;
import service.impl.CourseImpl;
import service.impl.QuestionImpl;
import service.impl.UserImpl;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

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
            System.out.println(li.get(0).getReturn_grade());
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
            return ok(views.html.allthecourse.render(allcourse));
            //自己写return回去
        }
        return unauthorized("Oops, you are not connected");
    }
}
