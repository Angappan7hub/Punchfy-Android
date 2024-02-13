package com.example.mqtt.ara.dependency;




import static com.example.mqtt.ara.dependency.AraAppConstants.COMPANY_NAME;
import static com.example.mqtt.ara.dependency.AraAppConstants.DESIGNATION;
import static com.example.mqtt.ara.dependency.AraAppConstants.TENANT_ID;
import static com.example.mqtt.ara.dependency.AraAppConstants.TOKEN;
import static com.example.mqtt.ara.dependency.AraAppConstants.USER_BRANCH_ID;
import static com.example.mqtt.ara.dependency.AraAppConstants.USER_BRANCH_NAME;
import static com.example.mqtt.ara.dependency.AraAppConstants.USER_ID;
import static com.example.mqtt.ara.dependency.AraAppConstants.USER_NAME;
import static com.example.mqtt.ara.dependency.AraAppConstants.USER_TITLE;
import static com.example.mqtt.ara.dependency.AraAppConstants.USER_TYPE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mqtt.ara.model.LoggedInUser;
import com.example.mqtt.model.Branch;
import com.example.mqtt.model.SampleData;
import com.example.mqtt.model.Token;
import com.example.mqtt.model.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class AraSessionManager {
    private LoggedInUser userInfo;

    private SharedPreferences preferences;

    public AraSessionManager(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(context.getPackageName(),
                Context.MODE_PRIVATE);
    }

    public LoggedInUser getUserInfo() {

        if (userInfo != null) {
            return userInfo;
        }

        int userId = preferences.getInt(USER_ID, -1);
        if (userId == -1)
            return null;

        String userType= preferences.getString(USER_TYPE, "");
        int tenantId= preferences.getInt(TENANT_ID, -1);
        String designation = preferences.getString(DESIGNATION, "");
        String userName = preferences.getString(USER_NAME, "");
        String userTitle = preferences.getString(USER_TITLE, "");
        int branchId = preferences.getInt(USER_BRANCH_ID, -1);
        String branchName=preferences.getString(USER_BRANCH_NAME,"");
        String companyName=preferences.getString(COMPANY_NAME,"");
        String token = preferences.getString(TOKEN, "");

        userInfo = new LoggedInUser(userId, userType, tenantId, designation, userTitle, branchId, branchName, companyName, token);
        return userInfo;
    }

    public void saveUserInfo(LoggedInUser userInfo) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(USER_ID, userInfo.userId);
        editor.putString(USER_NAME, userInfo.displayName);
        editor.putInt(TENANT_ID,userInfo.tenantId);
        editor.putString(USER_TYPE, userInfo.userType);
        editor.putInt(USER_BRANCH_ID, userInfo.branchId);
        editor.putString(DESIGNATION, userInfo.designationName);
        editor.putString(COMPANY_NAME, userInfo.companyName);
        editor.putString(USER_TITLE, userInfo.displayName);
        editor.putString(USER_BRANCH_NAME, userInfo.branchName);
        editor.putString(TOKEN, userInfo.token);
        editor.apply();
        editor.commit();
        this.userInfo = userInfo;
    }




    public void clearSession() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }




}
