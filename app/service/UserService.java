package service;

import domain.Course;
import domain.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public interface UserService {
     User addUser(String email, String firstname, String lastname, String password);
     boolean login(String email, String password) throws SQLException, ClassNotFoundException;
     User getUserByEmail(String email) throws SQLException, ClassNotFoundException;
     ArrayList<Course> getAllCourse(String email);
}
