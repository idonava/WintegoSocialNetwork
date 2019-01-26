package http;

import http.models.*;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.util.concurrent.*;

public class HttpApi {
    public final String BASE_URL = "http://35.188.78.78:8888/api/";
    private final int MAXIMUM_REQUEST_ATTEMPTS = 10;
    private final Retrofit retrofitClient;
    private final LoginService loginService;
    private final UserService userService;

    private final String cookie;

    private boolean printFullLogs;
    public HttpApi(String username, String password, boolean printFullLogs) throws IOException {
        this.printFullLogs=printFullLogs;
        OkHttpClient okHttpClient = getOkHttpClient();
        retrofitClient = getRetrofitClient(okHttpClient);
        loginService = retrofitClient.create(LoginService.class);
        userService = retrofitClient.create(UserService.class);
        Call<LoginResult> loginResultCall = loginService.login(new LoginRequest(username, password));
        Response<LoginResult> response = loginResultCall.execute();
        cookie = response.headers().get("Set-Cookie");
        if (cookie == null) {
            throw new RuntimeException("Login failed");
        }
    }

    private Retrofit getRetrofitClient(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .callbackExecutor(Executors.newFixedThreadPool(4))
                .baseUrl(BASE_URL)
                .build();
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                if (isCookieDefine()) {
                    builder.addHeader("Cookie", cookie);
                }
                Request request = builder.build();
                okhttp3.Response response = null;
                int tryCount = 0;
                boolean isSuccessful = false;
                do {
                    try {
                        response = chain.proceed(request);
                        isSuccessful = response.isSuccessful();
                        if (!isSuccessful) {
                            // close the response if not successful, because no need to use it more
                            response.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        tryCount++;
                    }
                } while (!isSuccessful && (tryCount < MAXIMUM_REQUEST_ATTEMPTS));
                if (!isSuccessful && printFullLogs) {
                    System.out.println("Request is not successful: " + request);
                }
                return response;
            }
        });

        return httpClient.build();
    }

    //  Getting the user by http get request.
    public User getUser(String userId) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        if (printFullLogs) {
            System.out.println("Getting user: " + userId);
        }
        CompletableFuture<User> userCompletableFuture = new CompletableFuture<>();
        Call<String> userProfile = userService.getUserProfile(userId);
        userProfile.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                userCompletableFuture.complete(new User(response.body()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

            }
        });
        return userCompletableFuture.get(1, TimeUnit.MINUTES);
    }

    //  Getting a part of the users followers by http get request.
    private Followers getUserProfileFollowers(String userID, int skipNum) throws ExecutionException, InterruptedException, TimeoutException {
        if (printFullLogs) {
            System.out.println("Getting user followers, user:" + userID + " skipNum:" + skipNum);
        }
        CompletableFuture<Followers> followerCompletableFuture = new CompletableFuture<>();
        Call<Followers> followersCall = userService.getUserProfileFollowers(userID, skipNum);
        followersCall.enqueue(new Callback<Followers>() {
            @Override
            public void onResponse(Call<Followers> call, Response<Followers> response) {
                followerCompletableFuture.complete(response.body());
            }

            @Override
            public void onFailure(Call<Followers> call, Throwable throwable) {

            }
        });
        return followerCompletableFuture.get(1, TimeUnit.MINUTES);
    }

    //  Getting all of the user followers
    public UserFollowers getAllFollowers(String userID) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        int HUNK_SIZE = 10;
        UserFollowers userFollowers = new UserFollowers(userID);
        Followers currentFollowers;
        int skipNum = 0;
        do {
            currentFollowers = getUserProfileFollowers(userID, skipNum);
            userFollowers.addFollowes(currentFollowers.getFollowers());
            skipNum += HUNK_SIZE;
        } while (currentFollowers.isMore());
        return userFollowers;
    }

    private boolean isCookieDefine() {
        return cookie != null;
    }
}
