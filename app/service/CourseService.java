package service;

import domain.Course;
import domain.User;

public interface CourseService {
    Course addCourse(String courseName, User instr);
    Boolean joinCourse(String email, int code);
}
