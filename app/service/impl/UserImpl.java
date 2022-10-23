package service.impl;

import domain.User;
import org.apache.commons.codec.digest.DigestUtils;
import service.UserService;
import utility.CRUD;
import utility.Constant;

public class UserImpl implements UserService {

    CRUD crud = new CRUD();
    public User addUser(String email, String password){
        try {
            //password编码
            String newPass = DigestUtils.md5Hex(Constant.SALT + password);
            User user = new User(email, newPass);
            Integer id = crud.addUser(user);
            return new User(id,user.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
