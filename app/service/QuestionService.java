package service;

import domain.Question;


import java.util.List;


public interface QuestionService {

    Question addQuestion(String header, String detail, String answer, int from, int grade);

    List<Question> showAllQuestion(Integer courseId);

    Question getQuestion(int question_id);


}
