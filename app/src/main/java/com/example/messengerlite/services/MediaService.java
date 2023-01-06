package com.example.messengerlite.services;

import com.example.messengerlite.Tools;
import com.example.messengerlite.entities.WebLinkEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MediaService
{
    int SAVE = 0;
    int DELETE = 1;

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.MINUTES)
            .build();

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();

    MediaService service = new Retrofit.Builder()
            .baseUrl(Tools.DOMAIN)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)).build().create(MediaService.class);

    @GET("/media/getLinks/{id}")
    Call<List<WebLinkEntity>> getLinksUsingOwnerId(@Path("id") int id);

    @POST("media/editLink/{action}")
    Call<Integer> editLink(@Path("action") int action, @Body WebLinkEntity link);
}
