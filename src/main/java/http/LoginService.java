package http;

import http.models.LoginRequest;
import http.models.LoginResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    @POST("login")
    Call<LoginResult> login(@Body LoginRequest loginRequest);
}
