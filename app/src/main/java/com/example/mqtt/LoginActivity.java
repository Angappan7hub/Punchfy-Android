package com.example.mqtt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginActivity extends AppCompatActivity {
public Button loginBtn;
    private EditText userId;
    private EditText password;
    private Button loginButton;
    private ConstraintLayout rootLayout;
    private ProgressBar progressBar;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        referviewFields();
       // inject();
        //initViewFields();
        setListener();
        setListener();
    }

    private void setListener() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }


    private void referviewFields() {
        loginBtn=findViewById(R.id.fragment_login_button);
    }
}