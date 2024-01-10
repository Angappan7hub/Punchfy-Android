package com.example.mqtt.model;

import com.google.gson.annotations.SerializedName;

public class Branch {

    @SerializedName("branch_id")
    public int branchId;

    @SerializedName("branch_code")
    public String branchCode;

    @SerializedName("branch_name")
    public String branchNme;

    @SerializedName("branch_location")
    public String branchLocation;

    @SerializedName("branch_latitude")
    public String branchLatitude;

    @SerializedName("branch_longitude")
    public String branchLongitude;

    @SerializedName("branch_address")
    public String branchAddress;

    @SerializedName("branch_contact_no")
    public String branchContactNo;

    @SerializedName("branch_company_id")
    public String branchCompanyId;


}
