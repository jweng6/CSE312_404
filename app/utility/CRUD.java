package utility;

import domain.User;
import java.sql.*;


public class CRUD {

    public static void main(String[] args) throws Exception {
        //int courseId, @Email String email, @Required String firstname, String lastname, String password
        User a = new User("hasa@gmail.com", "asd", "aslkdj", "asldjasldj");

    }

    public Integer addUser(User user) throws Exception{
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

}
