package com.example.nearbylocation;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    public static final String urlFoursquare = "https://api.foursquare.com/";
    private static Retrofit retrofitFoursquare = null;

    public static Retrofit getRetrofitInstance() {
        if (retrofitFoursquare == null) {
            try {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(30, TimeUnit.SECONDS);
                builder.readTimeout(30, TimeUnit.SECONDS);
                builder.writeTimeout(30, TimeUnit.SECONDS);
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(interceptor);

                retrofitFoursquare = new Retrofit.Builder()
                        .baseUrl(urlFoursquare)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(builder.build())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retrofitFoursquare;
    }
}
