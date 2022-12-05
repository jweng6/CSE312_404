package domain;


public class Question {
    private String header;
    private String detail;
    private String answer;
    private int from;
    private int grade;
    public Question() {

    }

    public Question(Integer from, String title, String detail, String answer, Integer grade){
        this.from = from;
        this.header = title;
        this.detail = detail;
        this.answer = answer;
        this.grade = grade;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}
