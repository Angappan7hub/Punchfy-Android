package com.example.mqtt.dependency;




import static com.example.mqtt.dependency.AppConstants.BRANCH_ID;
import static com.example.mqtt.dependency.AppConstants.COMPANY_ID;
import static com.example.mqtt.dependency.AppConstants.COMPANY_NAME;
import static com.example.mqtt.dependency.AppConstants.DESIGNATION;
import static com.example.mqtt.dependency.AppConstants.FACE_DATA;
import static com.example.mqtt.dependency.AppConstants.LEVEL;
import static com.example.mqtt.dependency.AppConstants.MASTER_ID;
import static com.example.mqtt.dependency.AppConstants.NOTIFICATION_TOKEN;
import static com.example.mqtt.dependency.AppConstants.REGISTER_STATUS;
import static com.example.mqtt.dependency.AppConstants.SCAN_STATUS;
import static com.example.mqtt.dependency.AppConstants.TITLE;
import static com.example.mqtt.dependency.AppConstants.TOKEN;
import static com.example.mqtt.dependency.AppConstants.USER_ID;
import static com.example.mqtt.dependency.AppConstants.USER_NAME;

import android.content.Context;
import android.content.SharedPreferences;


import com.example.mqtt.model.Branch;
import com.example.mqtt.model.SampleData;
import com.example.mqtt.model.Token;
import com.example.mqtt.model.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class SessionManager {
    private UserInfo userInfo;
    public Token currentLoggedInUser;
    private SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(context.getPackageName(),
                Context.MODE_PRIVATE);
    }





    public UserInfo getUserInfo() {

        if (userInfo != null) {
            return userInfo;
        }

        int userId = preferences.getInt(USER_ID, -1);
        if (userId == -1)
            return null;

        String userName = preferences.getString(USER_NAME, "");
        String title = preferences.getString(TITLE, "");
        String level = preferences.getString(LEVEL, "");
        String masterId = preferences.getString(MASTER_ID, "");
        String branchId = preferences.getString(BRANCH_ID, "");
        String notificationToken = preferences.getString(NOTIFICATION_TOKEN, "");
        String faceData = preferences.getString(FACE_DATA, "");
        String registerStatus = preferences.getString(REGISTER_STATUS, "");
        String scanStatus = preferences.getString(SCAN_STATUS, "");
        String companyId = preferences.getString(COMPANY_ID, "");
        String companyName = preferences.getString(COMPANY_NAME, "");
        String designation = preferences.getString(DESIGNATION, "");
        userInfo = new UserInfo(userId, userName, title, level, masterId, branchId, notificationToken, faceData, companyId,companyName, registerStatus, scanStatus,designation);
        return userInfo;
    }

    public void saveUserInfo(UserInfo userInfo) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(USER_ID, userInfo.userInfoId);
        editor.putString(USER_NAME, userInfo.userName);
        editor.putString(TITLE, userInfo.userTitle);
        editor.putString(LEVEL, userInfo.userInfoLevel);
        editor.putString(MASTER_ID, userInfo.userMasterId);
        editor.putString(BRANCH_ID, userInfo.branchId);
        editor.putString(NOTIFICATION_TOKEN, userInfo.userInfoNotificationToken);
        editor.putString(FACE_DATA, userInfo.faceData);
        editor.putString(COMPANY_ID, userInfo.companyId);
        editor.putString(COMPANY_NAME, userInfo.companyName);
        editor.putString(REGISTER_STATUS, userInfo.registerStatus);
        editor.putString(SCAN_STATUS, userInfo.scanStatus);
        editor.putString(DESIGNATION, userInfo.designation);
        editor.apply();
        editor.commit();
        this.userInfo = userInfo;
    }

    public void saveLoggedInUser(Token currentLoggedInUser) {

        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt(USER_ID, currentLoggedInUser.userId);
//        editor.putString(USER_NAME, currentLoggedInUser.title);
//        editor.putString(ROLE, currentLoggedInUser.role);
        editor.putString(TOKEN, currentLoggedInUser.token);
        this.currentLoggedInUser = currentLoggedInUser;
        editor.apply();
        editor.commit();


    }
    public boolean isLoggedIn() {
        return preferences.contains(USER_ID);
    }
    public Token getLoggedInUser() {
        if (currentLoggedInUser != null) {
            return currentLoggedInUser;
        }
//         // String token=preferences.getString(TOKEN,"");
//        if(token=="")
//            return null;
//        int userId = preferences.getInt(USER_ID, -1);
//        if (userId == -1)
//            return null;
//        String role = preferences.getString(ROLE, "");
//        String title=preferences.getString(USER_NAME,"");
        String token = preferences.getString(TOKEN, "");
        currentLoggedInUser = new Token(token);
        return currentLoggedInUser;
    }

//    public void saveLoggedInUser(Token currentLoggedInUser) {
//
//        SharedPreferences.Editor editor = preferences.edit();
////        editor.putInt(USER_ID, currentLoggedInUser.userId);
////        editor.putString(USER_NAME, currentLoggedInUser.title);
////        editor.putString(ROLE, currentLoggedInUser.role);
//        editor.putString(TOKEN, currentLoggedInUser.token);
//        this.currentLoggedInUser = currentLoggedInUser;
//        editor.apply();
//        editor.commit();
//
//
//    }
    public void clearSession() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
    public void saveSampleData(List<SampleData> data) {

        SharedPreferences.Editor editor = preferences.edit();
        String list = new Gson().toJson(data);
        editor.putString("data", list);
        editor.apply();
       // editor.commit();
        return;
    }

    public List<SampleData> getData(){

        String data=preferences.getString("data","");
        Gson gson = new Gson();
        Type type = new TypeToken<List<SampleData>>(){}.getType();
        List<SampleData> sampleDataList = gson.fromJson(data, type);

        return sampleDataList;
     //   return data;
    }

    public void clearSingleSampleData(SampleData sampleData) {
        List<SampleData> currentList = getData();

        // Check if the list contains the sampleDataToRemove
        if (currentList!=null) {
            for(int i=0;i<currentList.size();i++){
                if(currentList.get(i).ack.compareTo(sampleData.ack)==0) {
                    currentList.remove(i);
                    saveSampleData(currentList);
                }
                saveSampleData(currentList);

            }
           // currentList.remove(sampleData);

            // Save the modified list back to SharedPreferences
            saveSampleData(currentList);
        }
    }
    public void saveBranch(Branch branch2) {

        SharedPreferences.Editor editor = preferences.edit();

      Branch faceDataList=branch2;
                String list = new Gson().toJson(faceDataList);
                editor.putString("branch", list);
                editor.apply();
                editor.commit();
                return;
    }

    public Branch getBranch() {
        Branch faceData1 = null;
        String oldData = preferences.getString("branch", "");
        if (!oldData.isEmpty()) {
            faceData1 = new Gson().fromJson(oldData, new TypeToken<Branch>() {
            }.getType());
        }
        return faceData1;
    }

    public void saveButtonStatus(String date) {

        SharedPreferences.Editor editor = preferences.edit();

        String faceDataList=date;
        String list = new Gson().toJson(faceDataList);
        editor.putString("date", list);
        editor.apply();
        editor.commit();
        return;
    }

    public String getButtonStatus() {
        String date = null;
        String oldData = preferences.getString("date", "");
        if (!oldData.isEmpty()) {
            date = new Gson().fromJson(oldData, new TypeToken<String>() {
            }.getType());
        }
        return date;
    }

}
