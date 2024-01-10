package com.example.mqtt.dependency;

//import static com.arasoftwares.attendance.dependency.AppConstants.BASE_URL;
//
//import com.arasoftwares.attendance.repository.UserRepository;
//import com.arasoftwares.attendance.service.WebService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import static com.example.appraisal.dependency.AppConstants.BASE_URL;

public class FactoryMethods {

    private static Retrofit retrofit;
//    private static WebService webService;
//    private static UserRepository userRepository;
//
//    public static WebService getService() {
//        if (retrofit == null) {
//            Gson gson = new GsonBuilder()
//                    .setLenient()
//                    .create();
//            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
//                    .connectTimeout(60, TimeUnit.SECONDS)
//                    .readTimeout(60, TimeUnit.SECONDS)
//                    .writeTimeout(60, TimeUnit.SECONDS)
//                    .build();
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .client(okHttpClient)
//                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .build();
//        }
//        if (webService == null)
//            webService= retrofit.create(WebService.class);
//        return webService;
//    }
//
//    public static UserRepository getUserRepository() {
//        if (userRepository == null) {
//            userRepository = new UserRepository(getService());
//        }
//        return userRepository;
//    }

}
