package domain;


public class Question {
    private String header;
    private String detail;
    private String answer;
    private int from;
    private int id;
    private int grade;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
//    private int expires;
//    private String dueDay;

    public Question() {

    }

    public Question(int from, String header, String detail, String answer, int grade){
        this.from = from;
        this.header = header;
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

    public int getGrade() {
        return grade;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }



//    public int getExpires() {
//        return expires;
//    }
//
//    public void setExpires(int expires) {
//        this.expires = expires;
//    }

//    public String getDueDay() {
//        return dueDay;
//    }
//
//    public void setDueDay(String dueDay) {
//        this.dueDay = dueDay;
//    }


}
