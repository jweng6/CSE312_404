package service.impl;

import domain.Question;
import service.QuestionService;

import java.util.List;

public class QuestionImpl implements QuestionService {

    @Override
    public Question addQuestion(String header, String detail, String answer, int from, int grade) {
        return null;
    }

    @Override
    public List<Question> showAllQuestion(Integer courseId) {
        return null;
    }

    @Override
    public Question getQuestion(int question_id) {
        return null;
    }
}
