package com.example.messengerlite.services;

import com.example.messengerlite.commontools.Tools;
import com.example.messengerlite.dtos.ChatBoxDTO;
import com.example.messengerlite.dtos.MessageDTO;
import com.example.messengerlite.dtos.PictureDTO;
import com.example.messengerlite.dtos.PictureMessageDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MessageService
{
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.MINUTES)
            .build();

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();

    MessageService service = new Retrofit.Builder()
            .baseUrl(Tools.DOMAIN)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)).build().create(MessageService.class);

    @GET("/getLastMessages/{myUser}")
    Call<List<ChatBoxDTO>> getLastMessages(@Path("myUser") int myUserId);

    @GET("getConversations/{myUser}/{theOther}/{pageNumber}")
    Call<List<PictureMessageDTO>> getConversations(
            @Path("myUser") int myUser,
            @Path("theOther") int theOther,
            @Path("pageNumber") int pageNumber);

    @GET("getAllPictures/{user}/{other}")
    Call<List<PictureDTO>> getAllPictures(
            @Path("user") int myUser,
            @Path("other") int other);
}