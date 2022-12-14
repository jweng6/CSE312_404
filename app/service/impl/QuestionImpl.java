package service.impl;

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
    public Integer addQuestion(String header, String detail, String answer, int from, int grade,
                            String answerA, String answerB, String answerC, String answerD) {
        if(!StringUtils.isAnyBlank(header,detail,answer)){
            try {
                Question question = new Question(from,header,detail,answer,grade);
                question.setAnswerA(answerA);
                question.setAnswerB(answerB);
                question.setAnswerC(answerC);
                question.setAnswerD(answerD);
                int qid = crud.addQuestion(question);
                System.out.println(qid);
                ArrayList<User> allStudent = crud.getAllUserByCourse(from);
                for (int i = 0; i < allStudent.size(); i++){
                    crud.insertStudentAnswer(qid,allStudent.get(i).getId(),from,0);
                }
                return qid;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }
        return -1;
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
    public boolean getAllExpireCheckByQid(int qid, int courseId) {
        try {
            return crud.getAllExpireCheckByQid(qid,courseId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void assignQuestion(Question question) {
        try {
            ArrayList<User> allStudent = crud.getAllUserByCourse(question.getFrom());
            for (int i = 0; i < allStudent.size(); i++){
                crud.updateCurrentGrade(question.getId(),allStudent.get(i).getId(),0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getQuestionGradeByID(int qid,int uid){
        try {
            return crud.getStudentGradebyId(qid, uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
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
    public void answerQuestion(int courseId, String email, String answer) {
        try {
            User user = crud.getUserByEmail(email);
            crud.updateAnswer(user.getId(),answer,courseId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void grading(int qid){
        try {
            Question question = crud.getQuestion(qid);
            ArrayList<Student_Answer> answers = crud.getAnswerByCourse(question.getFrom());
            for (int i = 0; i < answers.size();i++){
                Student_Answer student = answers.get(i);
                int current = crud.returnGrade(student.getId());
                //
                if (question.getAnswer().equalsIgnoreCase(student.getAnswer())){
                    int newGrade = current + question.getGrade();

                    crud.updateCurrentGrade(qid,student.getId(),question.getGrade());
                    crud.updateGrade(student.getId(), newGrade);
                }else {
                    crud.updateCurrentGrade(qid, student.getId(),0);
                }
                crud.clearAnswer(student.getId(), question.getFrom());
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

}
