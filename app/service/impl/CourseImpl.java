package service.impl;

import domain.Course;
import domain.User;
import domain.Info;
import org.apache.commons.lang3.StringUtils;
import service.CourseService;
import utility.CRUD;

import java.sql.SQLException;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class CourseImpl implements CourseService {

    CRUD crud = new CRUD();

    @Override
    public Course addCourse(String courseName, User inst) {
        if (courseName!=null){
            try {
                ArrayList<Integer> allCode = crud.getAllCourseCode();
                //随机生成courseCode, 并且create table数据库里不存在
                StringBuilder str = new StringBuilder();
                boolean check = false;
                while (!check){
                    StringBuilder str2 = new StringBuilder();
                    Random random = new Random();
                    for (int i = 0; i < 6; i++) {
                        str2.append(random.nextInt(10));
                    }
                    if (!allCode.contains(str2.toString())){
                        str = str2;
                        check = true;
                    }
                }

                int code = Integer.parseInt(str.toString());
                Course course = new Course(courseName,code);
                try {
                    crud.addCourse(course, inst);
                    joinCourse(inst.getEmail(), code);
                    return new Course(inst.getId(),courseName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                ArrayList<Integer> userAllCourse = crud.getAllCourseByID(userByEmail.getId());
                if (userByEmail != null && courseByCode != null && !userAllCourse.contains(code)){
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

    @Override
    public List<Info> showCourse(String email){
        // return : [[course_name,instructor,joincode,check],[],[]...]
        // check 0/1 : student = 0 ; instrctor = 1;
        try {
            User userByEmail = crud.getUserByEmail(email);
            ArrayList<Integer> userAllCourse = crud.getAllCourseByID(userByEmail.getId());

            List<Info> allCouse = new ArrayList<Info>();

            for (int i = 0; i < userAllCourse.size(); i++) {
                Info courseInfo = new Info();
                Course courseByCode = crud.getCourseByCode(userAllCourse.get(i));

                String courseName = courseByCode.getCourseName().toString();
                String courseEmail = courseByCode.getEmail().toString();
                String courseCode = courseByCode.getCode().toString() ;
                // student = 0 ; instrctor = 1;
                String courseCheck ="0";
                if(courseEmail.equals(userByEmail.getEmail()))  {
                    courseCheck = "1";
                }
                courseInfo.setCheck(courseCheck);
                courseInfo.setCourseName(courseName);
                courseInfo.setEmail(courseEmail);
                courseInfo.setCode(courseCode);
//                System.out.println(courseInfo.getCourseName());
//                System.out.println(courseInfo.getEmail());
//                System.out.println(courseInfo.getCode());
//                System.out.println(courseInfo.getCheck() );

                allCouse.add(courseInfo);
            }
            return allCouse;
        }
        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Boolean isInstrutor(int code,String email){
        boolean ret = false;

        try {

            Course course = crud.getCourseByCode(code);
            if (course.getEmail().equals(email)){
                ret = true;
            }
            return ret;
        }
        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public Course course_info(int code){

        try {
            return crud.getCourseByCode(code);
        }
        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Course();
    }

}
