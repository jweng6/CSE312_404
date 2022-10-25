package service;

import domain.Course;

public interface CourseService {
    Course addCourse(String courseName);
    Boolean joinCourse(String email, int code);
}
