package utility;

import domain.Course;
import domain.Question;
import domain.User;
import java.sql.*;
import java.util.ArrayList;

public class CRUD {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        joinCourse(1, 1235);
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
            user.setPassword(rs.getString("password"));
        }
        psmt.close();
        conn.close();
        return user;
    }

    public User getUserByEmail(String email) throws SQLException, ClassNotFoundException {
        User user = new User();
        JDBC.getConnection();
        Connection conn = JDBC.CreateUserTable();
        String sql = "" +
                "SELECT id,email,firstname,lastname,password FROM userTable WHERE email=?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1, email);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()){
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setFirstname(rs.getString("firstname"));
            user.setLastname(rs.getString("lastname"));
            user.setPassword(rs.getString("password"));
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
    public static void joinCourse(int uid, int code) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateJoinCourse();
        String sql = "" +
                "INSERT INTO joinCourse" +
                "(userid, courseCode)" +
                "values(?,?)";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, uid);
        psmt.setInt(2, code);
        psmt.executeUpdate();
        psmt.close();
        conn.close();
    }

    public ArrayList<Integer> getAllCourse(int uid) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateJoinCourse();
        ArrayList<Integer> ret = new ArrayList<>();
        String sql = "" +
                "SELECT courseCode FROM joinCourse WHERE userid = ?";
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



    /* --------------------------------------- Question Table -------------------------------------------*/

    public static void addQuestion(Question question) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateQuestionTable();
        String sql = ""+
                "INSERT INTO questionTable" +
                "(CourseId, header, detail, answer, grade)"+
                "values(?,?,?,?,?)";
        PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        psmt.setInt(1, question.getFrom());
        psmt.setString(2, question.getHeader());
        psmt.setString(3, question.getDetail());
        psmt.setString(4, question.getAnswer());
        psmt.setInt(5, question.getGrade());
        psmt.executeUpdate();
        psmt.close();
        conn.close();
    }

    public ArrayList<Question> getAllQuestionByHeader(Integer courseId) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateQuestionTable();
        ArrayList<Question> ret = new ArrayList<>();
        String sql = "" +
                "SELECT id,header,detail,answer,grade FROM questionTable WHERE courseId = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, courseId);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()) {
            Question curr_ques = new Question();
            curr_ques.setHeader(rs.getString("header"));
            curr_ques.setDetail(rs.getString("detail"));
            curr_ques.setAnswer(rs.getString("answer"));
            curr_ques.setGrade(rs.getInt("grade"));
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
                "SELECT header,detail,answer,grade FROM questionTable WHERE id = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, questionId);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()) {
            ret.setHeader(rs.getString("header"));
            ret.setDetail(rs.getString("detail"));
            ret.setAnswer(rs.getString("answer"));
            ret.setGrade(rs.getInt("grade"));
        }
        psmt.close();
        conn.close();
        return ret;
    }


    /* --------------------------------------- Main page -------------------------------------------*/


}




