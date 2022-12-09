package service;

import domain.Answer;
import domain.Question;


import java.util.List;


public interface QuestionService {

    Integer addQuestion(String header, String detail, String answer, int from, int grade,
                     String answerA, String answerB, String answerC, String answerD );

    List<Question> showAllQuestion(int courseId);
    List<Question> showAllQuestionIns(int courseId);

    Question getQuestion(int question_id);

    void answerQuestion(int question_id, String email, String answer);

    void grading(int qid);

    int getQuestionGradeByID(int qid,int uid);

    void expires(int qid, long time);

    long getExpire(int qid);

    boolean getAllExpireCheckByQid(int qid, int courseId);

    void assignQuestion(Question q);
}
