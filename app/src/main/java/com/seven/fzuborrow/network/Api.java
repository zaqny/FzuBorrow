package com.seven.fzuborrow.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class Api {

    public static final String baseURL = "http://49.235.150.59:8080/rusha/";

    private Api() {
    }

    private static ApiInterface api;

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20L, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build();

    private static Retrofit retrofit = new Retrofit.Builder()
            .client(client)
            .baseUrl(baseURL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    public static ApiInterface get() {
        if (api == null) {
            api = retrofit.create(ApiInterface.class);
        }
        return api;
    }
}
