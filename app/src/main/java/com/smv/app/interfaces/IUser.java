package com.smv.app.interfaces;
import com.smv.app.models.User;

import java.util.List;


import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface IUser {
     @GET("all")
    Call<List<User>> listUsers();
    @POST("add")
    Call<User> createUser(@Body User user);
}
