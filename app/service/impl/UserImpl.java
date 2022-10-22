package service.impl;

import domain.User;
import org.apache.commons.codec.digest.DigestUtils;
import service.UserService;
import utility.CRUD;
import utility.Constant;

public class UserImpl implements UserService {

    CRUD crud = new CRUD();
    public Integer addUser(String email, String password){
        try {
            //password编码
            String newPass = DigestUtils.md5Hex(Constant.SALT + password);
            User user = new User(email, newPass);
            return crud.addUser(user);//id
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
