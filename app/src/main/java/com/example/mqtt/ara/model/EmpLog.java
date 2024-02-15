package com.example.mqtt.ara.model;

import com.google.gson.annotations.SerializedName;

public class EmpLog {
    @SerializedName("id")
    public int id;
    @SerializedName("tenantId")
    public int tenantId;
    @SerializedName("userinfo")
    public int userInfo;
    @SerializedName("latitude")
    public String latitude;
    @SerializedName("longitude")
    public String longitude;
    @SerializedName("employeeId")
    public int employeeId;
    @SerializedName("employeeName")
    public String employeeName;
    @SerializedName("imageUrlId")
    public int imageUrlId;
    @SerializedName("managerId")
    public int managerId;
    @SerializedName("branchId")
    public int branchId;
    @SerializedName("branchName")
    public String branchName;
    @SerializedName("attendanceType")
    public int attendanceType;
    @SerializedName("time")
    public String time;

    @SerializedName("address")
    public String address;
}
