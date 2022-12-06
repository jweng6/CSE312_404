package domain;

public class Answer {
    private int question_id;
    private String student_email;
    private Boolean check;
    private int return_grade;

    public Answer() {
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getStudent_email() {
        return student_email;
    }

    public void setStudent_email(String student_email) {
        this.student_email = student_email;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public int getReturn_grade() {
        return return_grade;
    }

    public void setReturn_grade(int return_grade) {
        this.return_grade = return_grade;
    }

}
