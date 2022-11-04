package service.impl;

import domain.Course;
import domain.User;
import org.apache.commons.lang3.StringUtils;
import service.CourseService;
import utility.CRUD;

import java.sql.SQLException;
import java.util.Random;

public class CourseImpl implements CourseService {

    CRUD crud = new CRUD();
    @Override
    public Course addCourse(String courseName, User inst) {
        System.out.println("MYSQL17");
        System.out.println(courseName);
        if (courseName!=null){
            //随机生成courseCode
            StringBuilder str = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 6; i++) {
                str.append(random.nextInt(10));
            }
            int code = Integer.parseInt(str.toString());
            Course course = new Course(courseName,code);
            try {
                crud.addCourse(course, inst);
                return new Course(inst.getId(),courseName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    return null;
    }

    @Override
    public Boolean joinCourse(String email, int code) {
        if (!StringUtils.isAnyBlank(email,Integer.toString(code))){
            try {
                User userByEmail = crud.getUserByEmail(email);
                Course courseByCode = crud.getCourseByCode(code);
                if (userByEmail != null && courseByCode != null){
                    crud.joinCourse(userByEmail.getId(),courseByCode.getCode());
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
