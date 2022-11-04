package domain;
import play.data.validation.Constraints.*;

public class Course {
    private int id;
    private String courseName;
    private String email;
    private Integer code;

    public Course() {
    }
    public Course(int id, String courseName){
        this.id = id;
        this.courseName = courseName;
    }
    public Course(String courseName, Integer code) {
        this.courseName = courseName;
        this.code = code;
    }

    public void setId(int id) { this.id = id; }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getEmail() {
        return email;
    }

    public Integer getCode() { return code; }

    public int getId() { return id; }

}
