package domain;

public class Course {
    private String courseName;
    private String email;
    private String code;

    public Course(){

    }
    public Course(String courseName) {
        this.courseName = courseName;
    }

    public Course(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }
}
