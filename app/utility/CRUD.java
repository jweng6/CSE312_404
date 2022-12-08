package utility;

import domain.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CRUD {

    public static void main(String[] args) throws Exception {
        CRUD crud = new CRUD();
        System.out.println(crud.getExpire(1));
    }

    /* --------------------------------------- userTable -------------------------------------------*/
    public Integer addUser(User user) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateUserTable();
        Integer id = 0;
        String sql = ""+
                "INSERT INTO userTable" +
                "(email, firstname, lastname, password)"+
                "values(?,?,?,?)";
        PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        psmt.setString(1, user.getEmail());
        psmt.setString(2, user.getFirstname());
        psmt.setString(3, user.getLastname());
        psmt.setString(4, user.getPassword());
        psmt.executeUpdate();
        ResultSet rs = psmt.getGeneratedKeys();
        if (rs.next()) {
            id = rs.getInt(1);
        }
        psmt.close();
        conn.close();
        return id;
    }

    public User getUserByid(int id) throws SQLException, ClassNotFoundException {
        User user = new User();
        JDBC.getConnection();
        Connection conn = JDBC.CreateUserTable();
        String sql = "" +
                "SELECT * FROM userTable WHERE id=?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, id);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()){
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setFirstname(rs.getString("firstname"));
            user.setLastname(rs.getString("lastname"));
            user.setPassword(rs.getString("password"));
            user.setDescription(rs.getString("description"));
        }
        psmt.close();
        conn.close();
        return user;
    }

    public void updateFirstName(int id, String first_name) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateUserTable();
        String sql = "" +
                "update userTable set firstname = ? where id = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1, first_name);
        psmt.setInt(2, id);
        psmt.executeUpdate();
        psmt.close();
        conn.close();
    }

    public void updateLastName(int id, String last_name) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateUserTable();
        String sql = "" +
                "update userTable set lastname = ? where id = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1, last_name);
        psmt.setInt(2, id);
        psmt.executeUpdate();
        psmt.close();
        conn.close();
    }


    public void updateDescription(int id, String description) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateUserTable();
        String sql = "" +
                "update userTable set description = ? where id = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1, description);
        psmt.setInt(2, id);
        psmt.executeUpdate();
        psmt.close();
        conn.close();
    }

    public User getUserByEmail(String email) throws SQLException, ClassNotFoundException {
        User user = new User();
        JDBC.getConnection();
        Connection conn = JDBC.CreateUserTable();
        String sql = "" +
                "SELECT id,email,firstname,lastname,password,description FROM userTable WHERE email=?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1, email);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()){
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setFirstname(rs.getString("firstname"));
            user.setLastname(rs.getString("lastname"));
            user.setPassword(rs.getString("password"));
            user.setDescription(rs.getString("description"));
        }
        if (user.getId() == 0) {
            return null;
        }
        psmt.close();
        conn.close();
        return user;
    }


    /* --------------------------------------- courseTable -------------------------------------------*/
    public  void addCourse(Course course, User instr) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateCourseTable();
        String sql = ""+
                "INSERT INTO courseTable" +
                "(instr_email, courseName, courseCode)"+
                "values(?,?,?)";

        PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        psmt.setString(1, instr.getEmail());
        psmt.setString(2, course.getCourseName());
        psmt.setInt(3, course.getCode());
        psmt.executeUpdate();
        psmt.close();
        conn.close();
    }

    public Course getCourseByCode(int courseCode) throws SQLException, ClassNotFoundException {
        Course course = new Course();
        JDBC.getConnection();
        Connection conn = JDBC.CreateCourseTable();
        String sql = "" +
                "SELECT id,instr_email,courseName,courseCode FROM courseTable WHERE courseCode=?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, courseCode);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()){
            course.setId(rs.getInt("id"));
            course.setCourseName(rs.getString("courseName"));
            course.setEmail(rs.getString("instr_email"));
            course.setCode(rs.getInt("courseCode"));
        }
        if (course.getId() == 0) {
            return null;
        }
        psmt.close();
        conn.close();
        return course;
    }


    public ArrayList<Integer> getAllCourseCode() throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateCourseTable();
        ArrayList<Integer> ret = new ArrayList<>();
        String sql = "" +
                "SELECT courseCode FROM courseTable";
        PreparedStatement psmt = conn.prepareStatement(sql);

        ResultSet rs = psmt.executeQuery();
        while(rs.next()) {
            ret.add(rs.getInt("courseCode"));
        }
        psmt.close();
        conn.close();
        return ret;
    }

    /* --------------------------------------- joinCourse -------------------------------------------*/
    public void joinCourse(int uid, int code) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateJoinCourse();
        String sql = "" +
                "INSERT INTO joinCourse" +
                "(userid, courseCode, answer)" +
                "values(?,?,?)";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, uid);
        psmt.setInt(2, code);
        psmt.setString(3, "noAnswer");
        psmt.executeUpdate();
        psmt.close();
        conn.close();
    }


    public ArrayList<Integer> getAllCourseByID(int uid) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateJoinCourse();
        ArrayList<Integer> ret = new ArrayList<>();
        String sql = "" +
                " select courseCode from joinCourse where userid = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, uid);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()) {
            ret.add(rs.getInt("courseCode"));
        }
        psmt.close();
        conn.close();
        return ret;
    }

    public ArrayList<User> getAllUserByCourse(Integer courseId) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateJoinCourse();
        ArrayList<User> ret = new ArrayList<>();
        String sql = "" +
                "select userid from joinCourse where courseCode = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, courseId);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()) {
            User curren_user = getUserByid(rs.getInt("userid"));
            ret.add(curren_user);
        }
        psmt.close();
        conn.close();
        return ret;
    }

    public void updateGrade(int userid, int grade) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateJoinCourse();
        String sql = "" +
                "update joinCourse set grade = ? where userid = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1,grade);
        psmt.setInt(2,userid);
        psmt.executeUpdate();
        psmt.close();
        conn.close();
    }

    public int getGradeWithCourse(int uid, int courseCode) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateJoinCourse();
        String sql = "select grade from joinCourse where userid = ? and courseCode =?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1,uid);
        psmt.setInt(2,courseCode);
        ResultSet rs = psmt.executeQuery();
        int ret = 0;
        while (rs.next()){
            ret = rs.getInt("grade");
        }
        return ret;
    }

    public int returnGrade(int userid) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateJoinCourse();
        String sql = "" +
                "select grade from joinCourse where userid = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1,userid);
        ResultSet rs = psmt.executeQuery();
        int ret = 0;
        while (rs.next()){
            ret = rs.getInt("grade");
        }
        rs.close();
        psmt.close();
        conn.close();
        return ret;
    }

    public void updateAnswer(int userid, String answer) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateJoinCourse();
        String sql = "" +
                "update joinCourse set answer = ? where userid = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1, answer);
        psmt.setInt(2, userid);
        psmt.executeUpdate();
        psmt.close();
        conn.close();
    }

    public ArrayList<Student_Answer> getAnswerByCourse(Integer courseId) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateJoinCourse();
        ArrayList<Student_Answer> ret = new ArrayList<>();
        String sql = "" +
                "select userid,answer from joinCourse where courseCode = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, courseId);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()) {
            Student_Answer curr_user = new Student_Answer(rs.getInt("userid"),rs.getString("answer"));
            ret.add(curr_user);
        }
        psmt.close();
        conn.close();
        return ret;
    }


    /* --------------------------------------- Question Table -------------------------------------------*/
    public void addQuestion(Question question) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateQuestionTable();
        String sql = ""+
                "INSERT INTO questionTable" +
                "(CourseId, header, detail, answer, choiceA, choiceB, choiceC, choiceD, grade)"+
                "values(?,?,?,?,?,?,?,?,?)";
        PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        psmt.setInt(1, question.getFrom());
        psmt.setString(2, question.getHeader());
        psmt.setString(3, question.getDetail());
        psmt.setString(4, question.getAnswer());
        psmt.setString(5, question.getAnswerA());
        psmt.setString(6, question.getAnswerB());
        psmt.setString(7, question.getAnswerC());
        psmt.setString(8, question.getAnswerD());
        psmt.setInt(9, question.getGrade());
        psmt.executeUpdate();
        psmt.close();
        conn.close();
    }

//    public void setTimer(int question_id, long expires) throws Exception{
//        JDBC.getConnection();
//        Connection conn = JDBC.CreateQuestionTable();
//        String sql = ""+
//                "UPDATE questionTable SET expires = ? where id = ?";
//        PreparedStatement psmt = conn.prepareStatement(sql);
//        psmt.setLong(1,expires);
//        psmt.setInt(2,question_id);
//        psmt.executeUpdate();
//        psmt.close();
//        conn.close();
//    }

    public ArrayList<Question> getAllQuestionByHeader(Integer courseId) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateQuestionTable();
        ArrayList<Question> ret = new ArrayList<>();
        LocalDateTime dateTime = LocalDateTime.now();
        ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();
        long nowTime = dateTime.toEpochSecond(zoneOffset);
        String sql = "" +
                "SELECT id,header,detail,answer,grade,expires FROM questionTable WHERE courseId = ? AND expires < ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, courseId);
        psmt.setLong(2,nowTime);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()) {
            Question curr_ques = new Question();
            curr_ques.setId(rs.getInt("id"));
            curr_ques.setHeader(rs.getString("header"));
            curr_ques.setDetail(rs.getString("detail"));
            curr_ques.setAnswer(rs.getString("answer"));
            curr_ques.setGrade(rs.getInt("grade"));
            curr_ques.setExpires(rs.getLong("expires"));
            ret.add(curr_ques);
        }
        psmt.close();
        conn.close();
        return ret;
    }

    public boolean getAllExpireCheckByQid(Integer qid,Integer courseId) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Boolean ret = false;
        Connection conn = JDBC.CreateQuestionTable();
        LocalDateTime dateTime = LocalDateTime.now();
        ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();
        long nowTime = dateTime.toEpochSecond(zoneOffset);
        String sql = "" +
                "SELECT id FROM questionTable WHERE courseId = ? AND expires < ? AND id = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, courseId);
        psmt.setLong(2,nowTime);
        psmt.setInt(3, qid);
        ResultSet rs = psmt.executeQuery();
        if(rs.next()) {
            ret = true;
        }
        psmt.close();
        conn.close();
        return ret;
    }
    public ArrayList<Question> getAllQuestionByHeaderIns(Integer courseId) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateQuestionTable();
        ArrayList<Question> ret = new ArrayList<>();
        String sql = "" +
                "SELECT id,header,detail,answer,grade,expires FROM questionTable WHERE courseId = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, courseId);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()) {
            Question curr_ques = new Question();
            curr_ques.setId(rs.getInt("id"));
            curr_ques.setHeader(rs.getString("header"));
            curr_ques.setDetail(rs.getString("detail"));
            curr_ques.setAnswer(rs.getString("answer"));
            curr_ques.setGrade(rs.getInt("grade"));
            curr_ques.setExpires(rs.getLong("expires"));
            ret.add(curr_ques);
        }
        psmt.close();
        conn.close();
        return ret;
    }

    public Question getQuestion(Integer questionId) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateQuestionTable();
        Question ret = new Question();
        String sql = "" +
                "SELECT id,courseId,header,detail,answer,choiceA,choiceB,choiceC,choiceD,grade FROM questionTable WHERE id = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, questionId);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()) {
            ret.setFrom(rs.getInt("courseId"));
            ret.setHeader(rs.getString("header"));
            ret.setDetail(rs.getString("detail"));
            ret.setAnswer(rs.getString("answer"));
            ret.setAnswerA(rs.getString("choiceA"));
            ret.setAnswerB(rs.getString("choiceB"));
            ret.setAnswerC(rs.getString("choiceC"));
            ret.setAnswerD(rs.getString("choiceD"));
            ret.setGrade(rs.getInt("grade"));
            ret.setId(rs.getInt("id"));
        }
        psmt.close();
        conn.close();
        return ret;
    }

    public int getAllQuestionGradeByCourse(int courseId) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateQuestionTable();
        String sql = "select grade from questionTable where courseId = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1,courseId);
        ResultSet rs = psmt.executeQuery();
        int total = 0;
        while (rs.next()){
            total += rs.getInt("grade");
        }
        rs.close();
        psmt.close();
        conn.close();
        return total;
    }

    public void updateExpire(int questionId, Long expire) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateQuestionTable();
        String sql = "update questionTable set expires = ? where id = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setLong(1, expire);
        psmt.setInt(2, questionId);
        psmt.executeUpdate();
        psmt.close();
        conn.close();
    }

    public long getExpire(int qid) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateQuestionTable();
        String sql = "select expires from questionTable where id = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1,qid);
        ResultSet rs = psmt.executeQuery();
        long re = 0;
        while (rs.next()){
            re= rs.getLong("expires");
        }
        return re;
    }

    /* --------------------------------------- Student Table -------------------------------------------*/
    public void insertStudentAnswer(int question, int userid, int courseId, int grade)throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateStudentAnswer();
        String sql = ""+
                "INSERT INTO studentTable" +
                "(questionId,userId,courseId,grade)"+
                "values(?,?,?,?)";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1,question);
        psmt.setInt(2,userid);
        psmt.setInt(3,courseId);
        psmt.setInt(4,grade);
        psmt.executeUpdate();
        psmt.close();
        conn.close();
    }

    public List<Answer> showAllStudentAnswer(int userId, int courseId) throws Exception{
        JDBC.getConnection();
        Connection connection = JDBC.CreateStudentAnswer();
        String sql = "" +
                "select questionId, userId, courseId, grade from studentTable where userId = ? and courseId = ?";
        PreparedStatement psmt = connection.prepareStatement(sql);
        psmt.setInt(1,userId);
        psmt.setInt(2,courseId);
        ResultSet rs = psmt.executeQuery();
        List<Answer> ret = new ArrayList<>();
        while (rs.next()){
            Answer answer = new Answer(rs.getInt("questionId"),
                    rs.getInt("courseId"),
                    rs.getInt("grade"));
            ret.add(answer);
        }
        return ret;
    }

    /* --------------------------------------- Main page -------------------------------------------*/


}




