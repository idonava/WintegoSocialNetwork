package http.models;

public class LoginRequest {
    final String username;
    final String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
