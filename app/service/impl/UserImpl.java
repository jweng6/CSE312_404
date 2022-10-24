package service.impl;

import domain.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import service.UserService;
import utility.CRUD;
import utility.Constant;

import java.sql.SQLException;

public class UserImpl implements UserService {

    CRUD crud = new CRUD();
//    int courseId, String email,String firstname, String lastname, String password
    public User addUser(String email, String firstname, String lastname, String password){
        if (StringUtils.isAnyBlank(email,firstname,lastname,password)){
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
        return user_password.equals(password);
    }

}
