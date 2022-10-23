package service;

import domain.User;

public interface UserService {
     User addUser(String email, String password);
}
