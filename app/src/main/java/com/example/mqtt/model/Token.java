package com.example.mqtt.model;

import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("key")
    public String token;

    public Token(String token) {
        this.token = token;
    }
}
