package com.example.smarthealthcare.network;

import com.example.smarthealthcare.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.List;

public interface ApiInterface {
    // User endpoints
    @GET("users")
    Call<List<User>> getAllUsers();

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") Long id);

    @GET("users/user/{userId}")
    Call<User> getUserByUserId(@Path("userId") String userId);

    @POST("users")
    Call<User> createUser(@Body User user);

    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") Long id, @Body User user);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") Long id);

    // Auth endpoints
    @POST("auth/register")
    Call<User> registerUser(@Body User user);

    @POST("auth/login")
    Call<User> loginUser(@Body UserLoginRequest loginRequest);

    // Inner class for login request
    class UserLoginRequest {
        private String email;
        private String password;

        public UserLoginRequest() {
        }

        public UserLoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
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
    }
}