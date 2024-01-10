//package com.example.mqtt.dependency;
//
//import android.os.AsyncTask;
//import android.os.Build;
//import android.util.Log;
//
//import com.arasoftwares.attendance.BuildConfig;
//import com.google.gson.Gson;
//import com.google.gson.annotations.SerializedName;
//
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.io.Writer;
//import java.util.Properties;
//
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//
//
//
//public class AppLog {
//    @SerializedName("user_id")
//    private String userId;
//
//    @SerializedName("message")
//    private String message;
//
//    @SerializedName("title")
//    private String title;
//
//    @SerializedName("json_data")
//    private Properties[] properties;
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public Properties[] getProperties() {
//        if (properties == null) {
//            properties = new Properties[1];
//            properties[0] = new Properties();
//        }
//        return properties;
//    }
//
//    public void setProperties(Properties[] properties) {
//        this.properties = properties;
//    }
//
//    public static AppLog fromException(Throwable exception, String className) {
//
//        AppLog appLog = new AppLog();
//        appLog.setTitle(exception.getMessage());
//        appLog.setMessage(getStackTrace(exception));
//        appLog.setUserId("-1");
//        appLog.addBasicProperties(className);
//        return appLog;
//    }
//
//    private static AppLog fromString(String message, String className) {
//        AppLog appLog = new AppLog();
//        appLog.setTitle("Info");
//        appLog.setMessage(message);
//        appLog.setUserId("-1");
//        appLog.addBasicProperties(className);
//        return appLog;
//    }
//
//    private void addBasicProperties(String className) {
//        if (properties == null) {
//            properties = new Properties[1];
//            properties[0] = new Properties();
//        }
//
//        properties[0].put("class_name", className);
//        properties[0].put("mobile_brand", Build.BRAND);
//        properties[0].put("mobile_model", Build.MODEL);
//        properties[0].put("version_sdk_int", Build.VERSION.SDK_INT);
//        properties[0].put("version_code_name", Build.VERSION.CODENAME);
//        properties[0].put("app_version", BuildConfig.VERSION_NAME);
//        properties[0].put("app_id", BuildConfig.APPLICATION_ID);
//        properties[0].put("status", "Error");
//    }
//
//    private String toJson() {
//        Gson gson = new Gson();
//        return gson.toJson(this);
//    }
//
//    public static void updateLogToServer(String message, final String className) {
//
//
//        try {
//            AppLog appLog = fromString(message, className);
//            UtilityMethods.getWebService().sendAppLog(appLog)
//                    .enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            Log.i("AppLog", "Logged error of " + className);
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            Log.i("AppLog", "Error on Logging " + t.getMessage());
//                        }
//                    });
//        } catch (Exception ex) {
//            Log.e("AppLog", ex.getLocalizedMessage());
//            Log.e("AppLog", message);
//        }
//    }
//
//    public static void updateLogToServer(Throwable exception, final String className) {
//
//
//        try {
//            AppLog appLog = fromException(exception, className);
//            UtilityMethods.getWebService().sendAppLog(appLog)
//                    .enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            Log.i("AppLog", "Logged error of " + className);
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            Log.i("AppLog", "Error on Logging " + t.getMessage());
//                        }
//                    });
//        } catch (Exception ex) {
//            Log.e("AppLog", ex.getLocalizedMessage());
//            Log.e("AppLog", exception.getLocalizedMessage());
//        }
//    }
//
//    public static String getStackTrace(Throwable throwable) {
//        Writer result = new StringWriter();
//        PrintWriter printWriter = new PrintWriter(result);
//        throwable.printStackTrace(printWriter);
//        return result.toString();
//    }
//
//    class LogToServer extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            return null;
//        }
//    }
//}
