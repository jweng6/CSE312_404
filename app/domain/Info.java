package domain;
import play.data.validation.Constraints.*;

public class Info {
    private String check;
    private String courseName;
    private String email;
    private String code;

    public Info() {
    }
    public Info(String check, String courseName){
        this.check = check;
        this.courseName = courseName;
    }


    public void setCheck(String check) { this.check = check; }

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

    public String getCode(){ return code; }

    public String getCheck(){ return check; }

}
