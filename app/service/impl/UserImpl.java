package service.impl;

import domain.Course;
import domain.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import scala.concurrent.java8.FuturesConvertersImpl;
import service.UserService;
import utility.CRUD;
import utility.Constant;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class UserImpl implements UserService {

    CRUD crud = new CRUD();
//    int courseId, String email,String firstname, String lastname, String password
    public User addUser(String email, String firstname, String lastname, String password){
        if (!StringUtils.isAnyBlank(email,firstname,lastname,password)){
            try {
                //password编码
                String newPass = DigestUtils.md5Hex(Constant.SALT + password);
                User user = new User(email, firstname, lastname, newPass);
                Integer id = crud.addUser(user);
                User res = crud.getUserByid(id);
                //int id, int courseId, String email, String firstname, String lastname
                return new User(id,res.getCourseId(),res.getEmail(),res.getFirstname(),res.getLastname());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean login(String email, String password) throws SQLException, ClassNotFoundException {

        if (StringUtils.isAnyBlank(email, password)) {
            return false;
        }
        User user = crud.getUserByEmail(email);
        String user_password = DigestUtils.md5Hex(Constant.SALT + password);

        try{
            if (user == null) {
                return false;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return user_password.equals(user.getPassword());
    }

    @Override
    public User getUserByEmail(String email) throws SQLException, ClassNotFoundException {
        if (!StringUtils.isBlank(email)){
            try {
                return crud.getUserByEmail(email);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public ArrayList<Course> getAllCourse(String email) {
        ArrayList<Course> ret = new ArrayList<>();
        if (!StringUtils.isBlank(email)){
            try {
                ArrayList<Integer> allCourse = crud.getAllCourseByID(crud.getUserByEmail(email).getId());
                for(Integer course : allCourse) {
                    Course singleCourse = new Course();
                    singleCourse.setCourseName(crud.getCourseByCode(course).getCourseName());
                    singleCourse.setEmail(crud.getCourseByCode(course).getEmail());
                    singleCourse.setCode(crud.getCourseByCode(course).getCode());
                    ret.add(singleCourse);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public List<User> showAllStudent(int courseId) {
        try {
           return crud.getAllUserByCourse(courseId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateDescription(String email, String description) {
        try {
            User user = crud.getUserByEmail(email);
            crud.updateDescription(user.getId(),description);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User nowChat(String userEmail){
        try {
            return crud.getUserByEmail(userEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User updateName(String email, String first, String last) throws SQLException, ClassNotFoundException {
        User user = crud.getUserByEmail(email);
        try {
            if (first != null && last == null) {
                crud.updateFirstName(user.getId(), first);
            } else if (first == null && last != null) {
                crud.updateLastName(user.getId(), last);
            } else if (first != null && last != null) {
                crud.updateFirstName(user.getId(), first);
                crud.updateLastName(user.getId(), last);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }



}
