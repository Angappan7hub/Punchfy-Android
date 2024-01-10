package com.example.mqtt.dependency;

import android.app.Application;
import android.widget.Toast;

public class AraApplication extends Application {



    public void onCreate() {

        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });


    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        //AppLog.updateLogToServer(e, "AraApplication");

        Toast.makeText(this, "Something Went Wrong!, Contact Support", Toast.LENGTH_LONG)
                .show();

        System.exit(1); // kill off the crashed app
    }

    @Override
    public void onTerminate() {

        super.onTerminate();

    }
}
