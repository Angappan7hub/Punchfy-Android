package com.example.mqtt.ara.model;

import com.google.gson.annotations.SerializedName;

public class PostEmpLog {
    @SerializedName("latitude")
    public String latitude;
    @SerializedName("longitude")
    public String longitude;
    @SerializedName("branchId")
    public long branchId;
    @SerializedName("attendanceType")
    public int attendanceType;
    @SerializedName("time")
    public String time;
    @SerializedName("imageUrlId")
    public int imageUrlId;
    @SerializedName("address")
    public String address;
}
