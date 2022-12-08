package domain;

public class Answer {
    private int question_id;
    private int course_id;
    private int return_grade;

    public Answer() {
    }

    public Answer(int question_id, int course_id, int return_grade) {
        this.question_id = question_id;
        this.course_id = course_id;
        this.return_grade = return_grade;
    }


    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public int getReturn_grade() {
        return return_grade;
    }

    public void setReturn_grade(int return_grade) {
        this.return_grade = return_grade;
    }
}
