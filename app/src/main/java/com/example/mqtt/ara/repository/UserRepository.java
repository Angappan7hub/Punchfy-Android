package com.example.mqtt.ara.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.mqtt.ara.model.Branch;
import com.example.mqtt.ara.model.EmpLog;
import com.example.mqtt.ara.model.LoggedInUser;
import com.example.mqtt.ara.model.LoginRequest;
import com.example.mqtt.ara.model.PostEmpLog;
import com.example.mqtt.ara.service.WebService;
import com.example.mqtt.dependency.FactoryMethods;
import com.example.mqtt.dependency.Result;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    public static int position = -1;

    public static int screen = -1;

    public static int appraisal = -1;

    public static int empId = -1;

    public static LoggedInUser currentUser;

    private WebService webService;

    public UserRepository() {
    }

    public UserRepository(WebService webService) {
        this.webService = webService;
    }

    public static UserRepository getInstance() {
        return com.example.mqtt.ara.dependency.FactoryMethods.getUserRepository();
    }
//    public LiveData<Result<LoggedInUser>> validateUser(String loginId, String password) {
//        final MutableLiveData<Result<LoggedInUser>> liveData = new MutableLiveData<>();
//        LoginModel loginModel = new LoginModel();
//        loginModel.userId = loginId;
//        loginModel.password = password;
//        UtilityMethods.getWebService().login(loginModel)
//                .enqueue(new Callback<LoggedInUser>() {
//                    @Override
//                    public void onResponse(Call<LoggedInUser> call, Response<LoggedInUser> response) {
//                        Result<LoggedInUser> userResult;
//                        if (response.isSuccessful()) {
//                            LoggedInUser loggedInUser = response.body();
//
//                            userResult = new SuccessResult<LoggedInUser>(loggedInUser);
//                        } else {
//                            try {
//                                userResult =
//                                        new StringErrorResult<>(response.errorBody().string());
//                            } catch (Exception e) {
//                                userResult = new StringErrorResult<>(e.getMessage());
//                            }
//                        }
//                        liveData.setValue(userResult);
//                    }
//
//                    @Override
//                    public void onFailure(Call<LoggedInUser> call, Throwable t) {
//                        liveData.setValue(new StringErrorResult<LoggedInUser>(t.getMessage()));
//                    }
//                });
//        return liveData;
//    }


    public LiveData<Result<LoggedInUser>> login(LoginRequest loginRequest) {
        final MutableLiveData<Result<LoggedInUser>> liveData = new MutableLiveData<>();

        webService.login(loginRequest)
                .enqueue(new Callback<LoggedInUser>() {
                    @Override
                    public void onResponse(Call<LoggedInUser> call, Response<LoggedInUser> response) {
                        Result result = new Result<LoggedInUser>();
                        if (response.isSuccessful()) {
                            LoggedInUser user = response.body();
                            currentUser = user;
                            // saveSession(context);
                            liveData.setValue(new Result<LoggedInUser>(currentUser));
                        } else {
                            try {
                                result.setErrorMessage(response.errorBody().string());
                                liveData.setValue(result);
                            } catch (Exception e) {
                                result.setErrorMessage(e.getMessage());
                                liveData.setValue(result);
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<LoggedInUser> call, Throwable t) {
                        Result result = new Result<LoggedInUser>();
                        result.setErrorMessage(t.getMessage());
                        liveData.setValue(result);
                    }
                });
        return liveData;
    }

    public LiveData<Result<List<EmpLog>>> getEmpLogs(String fromDate,String toDate) {
        final MutableLiveData<Result<List<EmpLog>>> liveData = new MutableLiveData<>();

        webService.getEmpLogs(fromDate,toDate)
                .enqueue(new Callback<List<EmpLog>>() {
                    @Override
                    public void onResponse(Call<List<EmpLog>> call, Response<List<EmpLog>> response) {
                        Result result = new Result<List<EmpLog>>();
                        if (response.isSuccessful()) {
                            List<EmpLog> user = response.body();

                            // saveSession(context);
                            liveData.setValue(new Result<List<EmpLog>>(user));
                        } else {
                            try {
                                result.setErrorMessage(response.errorBody().string());
                                liveData.setValue(result);
                            } catch (Exception e) {
                                result.setErrorMessage(e.getMessage());
                                liveData.setValue(result);
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<List<EmpLog>> call, Throwable t) {
                        Result result = new Result<List<EmpLog>>();
                        result.setErrorMessage(t.getMessage());
                        liveData.setValue(result);
                    }
                });
        return liveData;
    }



     public LiveData<Result<Branch>> getBranch() {
        final MutableLiveData<Result<Branch>> liveData = new MutableLiveData<>();

        webService.getBranch()
                .enqueue(new Callback<Branch>() {
                    @Override
                    public void onResponse(Call<Branch> call, Response<Branch> response) {
                        Result result = new Result<Branch>();
                        if (response.isSuccessful()) {
                            Branch user = response.body();

                            // saveSession(context);
                            liveData.setValue(new Result<Branch>(user));
                        } else {
                            try {
                                result.setErrorMessage(response.errorBody().string());
                                liveData.setValue(result);
                            } catch (Exception e) {
                                result.setErrorMessage(e.getMessage());
                                liveData.setValue(result);
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<Branch> call, Throwable t) {
                        Result result = new Result<Branch>();
                        result.setErrorMessage(t.getMessage());
                        liveData.setValue(result);
                    }
                });
        return liveData;
    }

    public LiveData<Result<EmpLog>> postEmpLog(PostEmpLog postEmpLog,long branchId) {
        final MutableLiveData<Result<EmpLog>> liveData = new MutableLiveData<>();

        webService.postEmpLog(postEmpLog, branchId)
                .enqueue(new Callback<EmpLog>() {
                    @Override
                    public void onResponse(Call<EmpLog> call, Response<EmpLog> response) {
                        Result result = new Result<EmpLog>();
                        if (response.isSuccessful()) {
                            EmpLog user = response.body();

                            // saveSession(context);
                            liveData.setValue(new Result<EmpLog>(user));
                        } else {
                            try {
                                result.setErrorMessage(response.errorBody().string());
                                liveData.setValue(result);
                            } catch (Exception e) {
                                result.setErrorMessage(e.getMessage());
                                liveData.setValue(result);
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<EmpLog> call, Throwable t) {
                        Result result = new Result<EmpLog>();
                        result.setErrorMessage(t.getMessage());
                        liveData.setValue(result);
                    }
                });
        return liveData;
    }


}
