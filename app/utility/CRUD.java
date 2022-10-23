package utility;

import domain.User;
import java.sql.*;


public class CRUD {

    public static void main(String[] args) throws Exception {
        //int courseId, @Email String email, @Required String firstname, String lastname, String password
        User a = new User("hasa@gmail.com", "asd", "aslkdj", "asldjasldj");
        System.out.println(addUser(a));
    }

    public static Integer addUser(User user) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateUserTable();
        Integer id = 0;
        String sql = ""+
                "INSERT INTO userTable" +
                "(email, firstname, lastname, password,courseId)"+
                "values(?,?,?,?,?)";
        PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        psmt.setString(1, user.getEmail());
        psmt.setString(2, user.getFirstname());
        psmt.setString(3, user.getLastname());
        psmt.setString(4, user.getPassword());
        psmt.setInt(5,0);
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

}
