package service;

import domain.User;

public interface UserService {
     User addUser(String email, String firstname, String lastname, String password);
}
