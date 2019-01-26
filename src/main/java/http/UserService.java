package http;

import http.models.Followers;
import http.models.LoginRequest;
import http.models.LoginResult;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserService {
    @GET("user/{userID}")
    Call<String> getUserProfile(@Path("userID") String userId);

    @GET("user/{userID}/followers")
    Call<Followers> getUserProfileFollowers(@Path("userID") String userId, @Query("skip") int skip);
}
