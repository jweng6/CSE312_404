package service;

import domain.User;

import java.sql.SQLException;

public interface UserService {
     User addUser(String email, String firstname, String lastname, String password);
     boolean login(String email, String password) throws SQLException, ClassNotFoundException;
     boolean joinCourse(String email, Integer courseCode) throws SQLException, ClassNotFoundException;
}
