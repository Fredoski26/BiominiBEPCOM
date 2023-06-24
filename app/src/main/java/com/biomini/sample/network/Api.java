package com.example.bepcom.network;

import com.example.bepcom.constant.Constant;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Api {

    public static Retrofit retrofitMyNodeV1 = null;

    //public static String token = "";
   public static String token = Constant.token;


    public static com.example.bepcom.network.ApiInterface CreateNodeApi() {
        String tokens = Constant.token;
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " +tokens)
                    //.addHeader("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        }).build();

        Retrofit retrofit = new Retrofit.Builder().client(client)
                .baseUrl(Constant.URLMyV3)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(com.example.bepcom.network.ApiInterface.class);


        /*   OkHttpClient okHttpClient = Build(ShowProgress).build();
        if (retrofitMyNodeV1 == null){
            retrofitMyNodeV1 = new Retrofit.Builder()
                    .baseUrl(Constant.URLMyV3)
                    .addConverterFactory(
                            GsonConverterFactory
                                    .create())
                    .client(okHttpClient).build();
        }
        return retrofitMyNodeV1.create(ApiInterface.class);*/
    }



}
