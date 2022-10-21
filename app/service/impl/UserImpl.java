package service.impl;

import domain.User;
import utility.CRUD;
import utility.Constant;

public class UserImpl {

    CRUD crud = new CRUD();
    public Integer addUser(String email, String password){
        try {
            //password编码
            String newPass = Constant.SALT + password;
            User user = new User(email, newPass);
            crud.addUser(user);//id
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
