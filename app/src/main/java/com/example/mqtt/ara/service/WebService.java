package com.example.mqtt.ara.service;


import com.example.mqtt.ara.model.Branch;
import com.example.mqtt.ara.model.EmpLog;
import com.example.mqtt.ara.model.LoggedInUser;
import com.example.mqtt.ara.model.LoginRequest;
import com.example.mqtt.ara.model.PostEmpLog;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebService {

    @POST("authenticate/TENANT_EMPLOYEE")
    Call<LoggedInUser> login(@Body LoginRequest loginRequest);

    @GET("empLogList")
    Call<List<EmpLog>> getEmpLogs(@Query("startDate")String startDate,
                                  @Query("endDate")String toDate);

    @GET("branches/currentuser")
    Call<Branch> getBranch();

    @POST("empLog/{branchId}")
    Call<EmpLog> postEmpLog(@Body PostEmpLog postEmpLog, @Path("branchId") long branchId);


}
