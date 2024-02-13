package com.example.mqtt.ara.model;

import com.google.gson.annotations.SerializedName;

public class LoggedInUser {

    @SerializedName("userId")
    public int userId;

    @SerializedName("loginId")
    public String loginId;

    @SerializedName("userType")
    public String userType;

    @SerializedName("tenantId")
    public int tenantId;

    @SerializedName("designationName")
    public String designationName;

    @SerializedName("designationId")
    public int designationId;

    @SerializedName("displayName")
    public String displayName;

    @SerializedName("branchId")
    public int branchId;

    @SerializedName("branchName")
    public String branchName;

    @SerializedName("designationCode")
    public int designationCode;

    @SerializedName("imageId")
    public int imageId;

    @SerializedName("tenantImageId")
    public int tenantImageId;

    @SerializedName("companyName")
    public String companyName;

    @SerializedName("hasCustomerEnabled")
    public boolean hasCustomerEnabled;

    @SerializedName("hasProjectEnabled")
    public boolean hasProjectEnabled;

    @SerializedName("hasDepartmentEnabled")
    public boolean hasDepartmentEnabled;

    @SerializedName("hasRoleEnabled")
    public boolean hasRoleEnabled;

    @SerializedName("hasMileStoneEnabled")
    public boolean hasMileStoneEnabled;

    @SerializedName("designationLevel")
    public int designationLevel;

    @SerializedName("token")
    public String token;

    public LoggedInUser(int userId, String userType, int tenantId, String designationName, String displayName, int branchId, String branchName, String companyName, String token) {
        this.userId = userId;
        this.userType = userType;
        this.tenantId = tenantId;
        this.designationName = designationName;
        this.displayName = displayName;
        this.branchId = branchId;
        this.branchName = branchName;
        this.companyName = companyName;
        this.token = token;
    }

}
