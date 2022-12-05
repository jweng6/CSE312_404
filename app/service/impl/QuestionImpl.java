package service.impl;

import domain.Question;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import service.QuestionService;
import utility.CRUD;

import java.util.List;

public class QuestionImpl implements QuestionService {
    CRUD crud = new CRUD();

    @Override
    public void addQuestion(String header, String detail, String answer, int from, int grade) {
        if(!StringUtils.isAnyBlank(header,detail,answer)){
            Question question = new Question(from,header,detail,answer,grade);
            //crud.addQuestion(question);
        }
    }

    @Override
    public List<Question> showAllQuestion(int courseId) {
        //crud.showAllQuestion(courseId);
        return null;
    }

    @Override
    public Question getQuestion(int question_id) {
        return null;
    }
}
