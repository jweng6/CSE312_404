package domain;

public class User {
    private int id;
    private String email;
    private String password;
    private String v_code;

    public User() {

    }

    public User(int id, String email, String password, String v_code) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.v_code = v_code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getV_code() {
        return v_code;
    }

    public void setV_code(String v_code) {
        this.v_code = v_code;
    }
}
