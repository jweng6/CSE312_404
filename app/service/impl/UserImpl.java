package service.impl;

import domain.Course;
import domain.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import service.UserService;
import utility.CRUD;
import utility.Constant;

import java.sql.SQLException;
import java.util.ArrayList;
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
                ArrayList<Integer> allCourse = crud.getAllCourse(crud.getUserByEmail(email).getId());
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

    //没写完
    public boolean joinCourse(String email, Integer courseCode) throws SQLException, ClassNotFoundException {
        User user = crud.getUserByEmail(email);
        if (StringUtils.isAnyBlank(email, String.valueOf(courseCode))) {
            return false;
        }
        try {
            if (user == null) {
                return false;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
