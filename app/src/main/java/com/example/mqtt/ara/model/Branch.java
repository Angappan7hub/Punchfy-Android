package com.example.mqtt.ara.model;

import com.google.gson.annotations.SerializedName;

public class Branch {
    @SerializedName("id")
    public int id;
    @SerializedName("branchName")
    public String branchName;
    @SerializedName("branchCode")
    public String branchCode;
    @SerializedName("contactPersonName")
    public String conPerName;
    @SerializedName("contactNo")
    public String contactNo;
    @SerializedName("alternatecontactNo")
    public String alterConNo;
    @SerializedName("email")
    public String email;
    @SerializedName("address")
    public String address;
    @SerializedName("city")
    public String city;
    @SerializedName("state")
    public String state;
    @SerializedName("country")
    public String country;
    @SerializedName("latitude")
    public double lat;
    @SerializedName("longitude")
    public double lon;

}
