package service;

import domain.Answer;
import domain.Question;


import java.util.List;


public interface QuestionService {

    void addQuestion(String header, String detail, String answer, int from, int grade);

    List<Question> showAllQuestion(int courseId);

    Question getQuestion(int question_id);

    Answer answerQuestion(int question_id, String email, String answer);

}
