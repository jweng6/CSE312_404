package domain;
import play.data.validation.Constraints.*;
import scala.Int;

import java.util.HashMap;


public class User {
    private int id;
    private int courseId;
    private String firstname;
    private String lastname;
    @Email
    private String email;
    @Required
    private String password;

    private String description;

    private int grade;

    public User() {

    }

    public User(int id, int courseId, String email, String firstname, String lastname){
        this.id = id;
        this.courseId = courseId;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public User(int id, int courseId, String email, String firstname, String lastname, String password) {
        this.id = id;
        this.courseId = courseId;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
    }

    public User(String email,String firstname, String lastname, String password) {
        this.courseId = courseId;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
    }


    public int getCourseId() {
        return courseId;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
