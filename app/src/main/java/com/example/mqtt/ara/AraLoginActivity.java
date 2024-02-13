package com.example.mqtt.ara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mqtt.R;
import com.example.mqtt.ara.dependency.AraSessionManager;
import com.example.mqtt.ara.dependency.FactoryMethods;
import com.example.mqtt.ara.model.LoggedInUser;
import com.example.mqtt.ara.model.LoginRequest;
import com.example.mqtt.dependency.Result;
import com.example.mqtt.dependency.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class AraLoginActivity extends AppCompatActivity implements View.OnClickListener {



    TextInputEditText userNameEdit,passwordEdit;

    Button submitButton;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ara_login);

        viewReferFields();

        userSessionCheck();

    }

    private void userSessionCheck() {

        AraSessionManager sessionManager=new AraSessionManager(this);
        LoggedInUser loggedInUser=sessionManager.getUserInfo();

        if(loggedInUser==null){
          //  Toast.makeText(getApplicationContext(),"No User Found",Toast.LENGTH_SHORT).show();
        }else{
            toAraHome();
        }
    }

    private void toAraHome() {
        Intent intent=new Intent(AraLoginActivity.this,AraHomeActivity.class);
        startActivity(intent);
        finish();

    }


    private void viewReferFields() {
        userNameEdit=findViewById(R.id.act_login_user_id);
        passwordEdit=findViewById(R.id.act_login_password);
        submitButton=findViewById(R.id.act_login_button);
        progressBar=findViewById(R.id.ara_act_login_pb);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.act_login_button:
                proceedLogin();
             break;

        }

    }

    private void proceedLogin() {
        String userName=userNameEdit.getText().toString().trim();
        String passWord=passwordEdit.getText().toString().trim();

        if(userName.isEmpty()){
            userNameEdit.setError("User Name is Empty");
            return;
        }
        if(passWord.isEmpty()){
            userNameEdit.setError("Password is Empty");
            return;
        }

        LoginRequest loginRequest=new LoginRequest();
        loginRequest.userName=userName;
        loginRequest.passWord=passWord;

        FactoryMethods.getUserRepository().login(loginRequest).observe(this, new Observer<Result<LoggedInUser>>() {
            @Override
            public void onChanged(Result<LoggedInUser> loggedInUserResult) {
                if(loggedInUserResult.isSuccess()){

                    ProceedToHome(loggedInUserResult.getData());

                }else{
                    Toast.makeText(getApplicationContext(),loggedInUserResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void ProceedToHome(LoggedInUser data) {

        AraSessionManager sessionManager=new AraSessionManager(this);
        sessionManager.saveUserInfo(data);
        toAraHome();
    }
}