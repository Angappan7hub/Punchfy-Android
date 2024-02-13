package com.example.mqtt.ara.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("username")
    public String userName;

    @SerializedName("password")
    public String passWord;
}
