package service.impl;

import domain.Course;
import org.apache.commons.lang3.StringUtils;
import service.CourseService;
import utility.CRUD;

import java.util.Random;

public class CourseImpl implements CourseService {

    CRUD crud = new CRUD();
    @Override
    public Course addCourse(String courseName) {
        if (!StringUtils.isBlank(courseName)){
            //随机生成courseCode
            StringBuilder str = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 6; i++) {
                str.append(random.nextInt(10));
            }
            int code = Integer.parseInt(str.toString());
            Course course = new Course(courseName,code);
            try {
                Integer id = crud.addCourse(course);
                return new Course(id,courseName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    return null;
    }

}
