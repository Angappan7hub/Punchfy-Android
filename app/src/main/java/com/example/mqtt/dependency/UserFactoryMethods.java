//package com.example.mqtt.dependency;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonDeserializationContext;
//import com.google.gson.JsonDeserializer;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonParseException;
//
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.Date;
//
//import okhttp3.Interceptor;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//
//public class UserFactoryMethods {
//
//    private static Retrofit retrofit;
//    private static WebService webService;
//    private static UserRepository userRepository;
//
//    public static WebService getService() {
//        if (retrofit == null) {
//
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(AppConstants.BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }
//        if (webService == null)
//            webService= retrofit.create(WebService.class);
//        return webService;
//    }
//
//
//
//    public static WebService getService(final String token) {
//
//        JsonDeserializer<Date> deserializer = new JsonDeserializer<Date>() {
//            @Override
//            public Date deserialize(JsonElement json, Type typeOfT,
//                                    JsonDeserializationContext context) throws JsonParseException {
//                String strDate = json.getAsString();
//                if (strDate.isEmpty())
//                    return new Date();
//                String[] strDates = strDate.split("/");
//                int date = Integer.parseInt(strDates[0]);
//                int month = Integer.parseInt(strDates[1]);
//                int year = Integer.parseInt(strDates[2]);
//                return new Date(year, month, date);
//
//            }
//        };
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(Date.class, deserializer)
//                .create();
////            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
////            loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
////        TokenInterceptor tokenInterceptor=new TokenInterceptor();
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request originalRequest = chain.request();
//                        Request newRequest = originalRequest.newBuilder()
//                                .header("TOKEN",token)
//                                .build();
//                        return chain.proceed(newRequest);
//                    }
//                })
////                    .addInterceptor(loggingInterceptor)
//                .build();
////        OkHttpClient client = new OkHttpClient.Builder()
////                .addInterceptor(tokenInterceptor)
////            .build();
//        retrofit = new Retrofit.Builder()
//                .client(okHttpClient)
//                .baseUrl(AppConstants.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        webService= retrofit.create(WebService.class);
//        return webService;
//    }
//
//
//    public static UserRepository getUseDataRepository(String token) {
//            userRepository = new UserRepository(getService(token));
//        return userRepository;
//    }
//
//}
