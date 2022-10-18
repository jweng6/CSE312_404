package utility;

import java.sql.*;

public class JDBC {
    static final String JdbcDriver = "com.mysql.cj.jdbc.Driver";
//    static final String Url = "jdbc:mysql://mysql:3306/db";
    static final String Url = "jdbc:mysql://localhost:3306/312";
    static final String User = "root" ;
    static final String PassWord = "jia893607219";

    public static Connection getConnection() throws ClassNotFoundException, SQLException {;
        Class.forName(JdbcDriver);
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", User, PassWord);
        Statement statement = connection.createStatement();
//        statement.executeUpdate("create database if not exists db;");
        statement.executeUpdate("create database if not exists 312;");
        statement.close();
        connection.close();
        return DriverManager.getConnection(Url, User, PassWord);
    }

    public static void CreateTable() throws SQLException, ClassNotFoundException {
        Connection connection = JDBC.getConnection();
        Statement statement = connection.createStatement();
        String sql = "create table if not exists userTable "+
                    "(id INT AUTO_INCREMENT, " +
                    "email VARCHAR(516), " +
                    "username VARCHAR(516), "+
                    "PRIMARY KEY (id));";
        statement.executeUpdate(sql);
        statement.close();
        connection.close();
    }



}