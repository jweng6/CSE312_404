package utility;

import domain.Student_Answer;

import java.sql.*;

public class JDBC {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        CreateQuestionTable();
    }

    static final String JdbcDriver = "com.mysql.cj.jdbc.Driver";
    //    static final String Url = "jdbc:mysql://mysql:3306/db";
//    static String Url = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true";
    static final String Url = "jdbc:mysql://localhost:3306/cse312";
    static final String User = "root" ;
 //       static final String PassWord = "jia893607219";
    static final String PassWord = "0257";
//    static final String PassWord = "JayX2029";



    public static Connection getConnection() throws ClassNotFoundException, SQLException {;
        Class.forName(JdbcDriver);
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", User, PassWord);
        Statement statement = connection.createStatement();
//        statement.executeUpdate("create database if not exists db;");
        statement.executeUpdate("create database if not exists cse312;");

        statement.close();
        connection.close();
        return DriverManager.getConnection(Url, User, PassWord);
    }

    public static Connection CreateUserTable() throws SQLException, ClassNotFoundException {
        Connection connection = JDBC.getConnection();
        Statement statement = connection.createStatement();
        String sql = "create table if not exists userTable "+
                "(id INT AUTO_INCREMENT, " +
                "email VARCHAR(516), " +
                "firstname VARCHAR(516)," +
                "lastname VARCHAR(516)," +
                "password VARCHAR(516), "+
                "description TEXT, " +
                "PRIMARY KEY (id));";
        statement.executeUpdate(sql);
        statement.close();
        return connection;
    }

    public static Connection CreateCourseTable() throws SQLException, ClassNotFoundException {
        Connection connection = JDBC.getConnection();
        Statement statement = connection.createStatement();
        String sql = "create table if not exists courseTable "+
                "(id INT AUTO_INCREMENT, " +
                "instr_email VARCHAR(516), " +
                "courseName VARCHAR(516), " +
                "courseCode INT, "+
                "PRIMARY KEY (id))";
        statement.executeUpdate(sql);
        statement.close();
        return connection;
    }

    public static Connection CreateJoinCourse() throws SQLException, ClassNotFoundException {
        Connection connection = JDBC.getConnection();
        Statement statement = connection.createStatement();
        String sql = "create table if not exists joinCourse "+
                "(id INT AUTO_INCREMENT, " +
                "userid INT, " +
                "courseCode INT, "+
                "grade INT DEFAULT 0, " +
                "answer TEXT, " +
                "PRIMARY KEY (id))";
        statement.executeUpdate(sql);
        statement.close();
        return connection;
    }

    public static Connection CreateQuestionTable() throws SQLException, ClassNotFoundException {
        Connection connection = JDBC.getConnection();
        Statement statement = connection.createStatement();
        String sql = "create table if not exists questionTable "+
                "(id INT AUTO_INCREMENT, " +
                "courseId INT, " +
                "header TEXT, "+
                "detail TEXT, " +
                "answer TEXT, "+
                "choiceA TEXT, " +
                "choiceB TEXT, " +
                "choiceC TEXT, " +
                "choiceD TEXT, " +
                "grade INT, " +
                "expires BIGINT DEFAULT 0," +
                "PRIMARY KEY (id))";
        statement.executeUpdate(sql);
        statement.close();
        return connection;
    }

    public static Connection CreateStudentAnswer() throws Exception{
        Connection connection = JDBC.getConnection();
        Statement statement = connection.createStatement();
        String sql = "create table if not exists studentTable" +
                "(id INT AUTO_INCREMENT, " +
                "questionId INT," +
                "userId INT, " +
                "courseId INT, " +
                "grade INT DEFAULT 0," +
                "PRIMARY KEY (id))";
        statement.executeUpdate(sql);
        statement.close();
        return connection;
    }

}
