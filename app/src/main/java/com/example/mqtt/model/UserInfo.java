package com.example.mqtt.model;

import com.google.gson.annotations.SerializedName;

public class UserInfo {
    @SerializedName("userId")
    public int userInfoId;
    @SerializedName("userName")
    public String userName;
    @SerializedName("title")
    public String userTitle;
    @SerializedName("level")
    public String userInfoLevel;
    @SerializedName("master_id")
    public String userMasterId;
    @SerializedName("branch_id")
    public String branchId;
    @SerializedName("branch_latitude")
    public String branchLatitude;
    @SerializedName("branch_longitude")
    public String branchLongitude;
    @SerializedName("notification_token")
    public String userInfoNotificationToken;
    @SerializedName("face_data")
    public String faceData;
    @SerializedName("company_id")
    public String companyId;
    @SerializedName("company_name")
    public String companyName;
    @SerializedName("register_status")
    public String registerStatus;
    @SerializedName("scan_status")
    public String scanStatus;
    @SerializedName("designation_name")
    public String designation;


    public UserInfo(int userInfoId, String userInfoName, String userTitle, String userInfoLevel, String userMasterId, String branchId, String userInfoNotificationToken, String faceData, String companyId, String companyName, String registerStatus, String scanStatus, String designation) {
        this.userInfoId = userInfoId;
        this.userName = userInfoName;
        this.userTitle = userTitle;
        this.userInfoLevel = userInfoLevel;
        this.userMasterId = userMasterId;
        this.branchId = branchId;
        this.userInfoNotificationToken = userInfoNotificationToken;
        this.faceData = faceData;
        this.companyId = companyId;
        this.companyName = companyName;
        this.registerStatus = registerStatus;
        this.scanStatus = scanStatus;
        this.designation = designation;
    }
}
