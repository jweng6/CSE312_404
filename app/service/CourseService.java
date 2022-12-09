package service;

import domain.Answer;
import domain.Course;
import domain.User;
import java.util.List;
import domain.Info;
public interface CourseService {
    Course addCourse(String courseName, User instr);
    Boolean joinCourse(String email, int code);
    List<Info> showCourse(String email);
    Boolean isInstrutor(int code,String email);
    Course course_info(int code);
    List<Integer> getAllCourse(int uid);
    int showGrade(int uid, int course);

    List<Answer>  showAllStudentAnswer(int uid, int cid);
    List<User> instrSeeGrade(int code);
    List<Course> AllCourse();

}
