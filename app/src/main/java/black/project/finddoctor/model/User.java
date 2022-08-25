package black.project.finddoctor.model;

public class User {
    String userName, email, mobile;

    public User(String userName, String email, String mobile) {
        this.userName = userName;
        this.email = email;
        this.mobile = mobile;
    }

    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
