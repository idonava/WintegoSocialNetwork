package http.models;

public class LoginResult {
    public static final String SUCCESS_STATUS = "success";

    public String status;

    public boolean isStatusSuccess() {
        return status.compareTo(SUCCESS_STATUS) == 0;
    }
}
