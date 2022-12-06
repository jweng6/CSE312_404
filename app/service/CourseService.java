package service;

import domain.Course;
import domain.User;
import java.util.List;
import domain.Info;
public interface CourseService {
    Course addCourse(String courseName, User instr);
    Boolean joinCourse(String email, int code);
    List<Info> showCourse(String email);
    Boolean isInstrutor(int code,String email);
}
