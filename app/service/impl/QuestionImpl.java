package service.impl;

import domain.Answer;
import domain.Question;
import domain.User;
import org.apache.commons.lang3.StringUtils;
import service.QuestionService;
import utility.CRUD;

import java.sql.SQLException;
import java.util.List;

public class QuestionImpl implements QuestionService {
    CRUD crud = new CRUD();

    @Override
    public void addQuestion(String header, String detail, String answer, int from, int grade,
                            String answerA, String answerB, String answerC, String answerD) {
        if(!StringUtils.isAnyBlank(header,detail,answer)){
            try {
                Question question = new Question(from,header,detail,answer,grade);
                question.setAnswerA(answerA);
                question.setAnswerB(answerB);
                question.setAnswerC(answerC);
                question.setAnswerD(answerD);
                crud.addQuestion(question);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Question> showAllQuestion(int courseId) {
        try {
           return crud.getAllQuestionByHeader(courseId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Question getQuestion(int question_id) {
        try {
            return crud.getQuestion(question_id);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Answer answerQuestion(int question_id, String email, String answer) {
        Answer ret = new Answer();
        try {
            Question question = crud.getQuestion(question_id);
            User user = crud.getUserByEmail(email);
            ret.setQuestion_id(question_id);
            ret.setStudent_email(email);
            int current = crud.returnGrade(user.getId());
            if (answer.equals(question.getAnswer())){
                int nowGrade = current + question.getGrade();
                crud.updateGrade(user.getId(),nowGrade);
                ret.setCheck(true);
                ret.setReturn_grade(nowGrade);
            }else {
                ret.setCheck(false);
                ret.setReturn_grade(current);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void main(String[] args) {
        QuestionImpl q = new QuestionImpl();
        Answer answer = q.answerQuestion(1, "chuanlon@buffalo.edu", "budui");
        System.out.println(answer.getReturn_grade());
    }
//    @Override
//    public void setTimer(int question_id, int min) {
//        LocalDateTime localDateTime = LocalDateTime.now();
//        LocalDateTime now = localDateTime.plusMinutes(min);
//        try {
//            crud.setTimer(question_id,now.toEpochSecond(zoneOffset));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
