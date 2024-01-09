package com.example.mqtt.ViewModel;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mqtt.dependency.SessionManager;
import com.example.mqtt.model.Token;
import com.example.mqtt.model.UserInfo;


public class BaseViewModel extends ViewModel {

    private SessionManager sessionManager;

    private SessionManager getSessionManager(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }
        return sessionManager;
    }
    public void setCurrentUser(Context context, Token loggedInUser) {
        getSessionManager(context).saveLoggedInUser(loggedInUser);
    }
    public UserInfo getUserInfo(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }
        return sessionManager.getUserInfo();
    }

    public boolean isLoggedIn(Context context) {
        return getSessionManager(context).isLoggedIn();
    }

    public void setUserInfo(Context context, UserInfo userInfo) {
        getSessionManager(context).saveUserInfo(userInfo);
    }

    public Token getCurrentUser(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }
        return sessionManager.getLoggedInUser();
    }

    private MutableLiveData<Location> locationLiveData = new MutableLiveData<>();


    public void setLocation(Location location) {
        locationLiveData.postValue(location);
    }
    public void clearSession(Context context) {
        getSessionManager(context).clearSession();
    }


    }
