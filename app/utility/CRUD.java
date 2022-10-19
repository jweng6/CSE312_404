package utility;

import domain.User;
import java.sql.*;


public class CRUD {

    public void addUser(User user) throws Exception{
        JDBC.getConnection();
        Connection conn = JDBC.CreateUserTable();
        String sql = ""+
                "INSERT INTO user_table" +
                "(email,password)"+
                "values(?,?)";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1, user.getEmail());
        psmt.setString(2, user.getPassword());
        psmt.execute();
        psmt.close();
        conn.close();
    }

    public void updatePassword(User user, String password) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateUserTable();
        String sql = "" +
                "update userTable set password ='" + password+"'" +" where id=" + user.getId();
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.executeUpdate(sql);
        psmt.close();
        conn.close();
    }

    public void updateVCode(User user, String code) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateUserTable();
        String sql = "" +
                "update userTable set v_code ='" + code +"'" +" where id=" + user.getId();
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.executeUpdate(sql);
        psmt.close();
        conn.close();
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
            user.setV_code(rs.getString("v_code"));
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
                "SELECT * FROM userTable WHERE email=?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1, email);
        ResultSet rs = psmt.executeQuery();
        while(rs.next()){
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setV_code(rs.getString("v_code"));
        }
        psmt.close();
        conn.close();
        return user;
    }

    public boolean UserEmailExist(String email) throws SQLException, ClassNotFoundException {
        JDBC.getConnection();
        Connection conn = JDBC.CreateUserTable();
        String sql = ""+"SELECT * FROM userTable WHERE email=?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        ResultSet rs = null;
        try{
            psmt.setString(1, email);
            rs = psmt.executeQuery();
            boolean temp = rs.next();
            psmt.close();
            rs.close();
            return temp;
        }catch(SQLException e){
            psmt.close();
            rs.close();
            return false;
        }
    }
}
