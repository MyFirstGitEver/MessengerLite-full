package com.example.messengerlite.services;

import com.example.messengerlite.commontools.Tools;
import com.example.messengerlite.dtos.UserNameDTO;
import com.example.messengerlite.entities.UserEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService
{
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.MINUTES)
            .build();

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();

    UserService service = new Retrofit.Builder()
            .baseUrl(Tools.DOMAIN)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)).build().create(UserService.class);

    @GET("/user/avatar/{userName}")
    Call<UserNameDTO> getAvatar(@Path("userName") String userName);

    @GET("/user/login/{userName}/{password}")
    Call<UserEntity> getUserInfo(@Path("userName") String userName, @Path("password") String password);

    @GET("/user/checkUserName/{userName}")
    Call<Boolean> checkUserName(@Path("userName") String userName);

    @POST("/user/register")
    Call<Boolean> register(@Body UserEntity user);

    @PUT("/user/updateInfo")
    Call<Boolean> updateInfo(@Body UserEntity user);
}