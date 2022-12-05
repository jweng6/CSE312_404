package service.impl;

import domain.Question;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import service.QuestionService;
import utility.CRUD;

import java.sql.SQLException;
import java.util.List;

public class QuestionImpl implements QuestionService {
    CRUD crud = new CRUD();

    @Override
    public void addQuestion(String header, String detail, String answer, int from, int grade) {
        if(!StringUtils.isAnyBlank(header,detail,answer)){
            try {
                Question question = new Question(from,header,detail,answer,grade);
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
}
