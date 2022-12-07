package service.impl;

import domain.Answer;
import domain.Question;
import domain.Student_Answer;
import domain.User;
import org.apache.commons.lang3.StringUtils;
import service.QuestionService;
import utility.CRUD;

import java.sql.SQLException;
import java.util.*;

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
    public List<Question> showAllQuestionIns(int courseId) {
        try {
            return crud.getAllQuestionByHeaderIns(courseId);
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
    public void answerQuestion(int question_id, String email, String answer) {
        try {
            User user = crud.getUserByEmail(email);
            crud.updateAnswer(user.getId(),answer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void grading(int qid){
        try {
            Question question = crud.getQuestion(qid);
            ArrayList<Student_Answer> answers = crud.getAnswerByCourse(question.getFrom());
            for (int i = 0; i < answers.size();i++){
                Student_Answer student = answers.get(i);
                int current = crud.returnGrade(student.getId());
                if (question.getAnswer().equals(student.getAnswer())){
                    int newGrade = current + question.getGrade();
                    crud.updateGrade(student.getId(), newGrade);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void expires(int qid, long expire) {
        try {
            crud.updateExpire(qid, expire);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getExpire(int qid) {
        try {
           return crud.getExpire(qid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        QuestionImpl q = new QuestionImpl();
        q.answerQuestion(1, "chuanlon@buffalo.edu", "budui");

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
