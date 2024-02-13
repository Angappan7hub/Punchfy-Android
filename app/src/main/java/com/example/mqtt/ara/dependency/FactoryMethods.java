package com.example.mqtt.ara.dependency;




import static com.example.mqtt.ara.dependency.AraAppConstants.BASE_URL;



import com.example.mqtt.ara.repository.UserRepository;
import com.example.mqtt.ara.service.WebService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import static com.example.appraisal.dependency.AppConstants.BASE_URL;

public class FactoryMethods {

    private static Retrofit retrofit;
    private static WebService webService;
    private static UserRepository userRepository;




    public static WebService getService() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        if (webService == null)
            webService= retrofit.create(WebService.class);
        return webService;
    }

    public static WebService getService(final String token) {


        JsonDeserializer<Date> deserializer = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
                String strDate = json.getAsString();
                if (strDate.isEmpty())
                    return new Date();
                String[] strDates = strDate.split("/");
                int date = Integer.parseInt(strDates[0]);
                int month = Integer.parseInt(strDates[1]);
                int year = Integer.parseInt(strDates[2]);
                return new Date(year, month, date);

            }
        };
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, deserializer)
                .create();
//            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//            loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
//        TokenInterceptor tokenInterceptor=new TokenInterceptor();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization","Bearer "+token)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
//                    .addInterceptor(loggingInterceptor)
                .build();
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(tokenInterceptor)
//            .build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        webService= retrofit.create(WebService.class);
        return webService;
    }

    public static UserRepository getUserRepository() {
            userRepository = new UserRepository(getService());
        return userRepository;
    }


    public  static UserRepository getUserRepository(String token){
        userRepository = new UserRepository(getService(token));
        return userRepository;
    }




}
