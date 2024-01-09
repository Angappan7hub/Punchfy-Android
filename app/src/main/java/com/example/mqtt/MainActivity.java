package com.example.mqtt;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import static com.example.mqtt.dependency.AppConstants.PERMISSIONS_REQUEST_LOCATION;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mqtt.ViewModel.MainViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.FormatFlagsConversionMismatchException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    NavController navController;
    private MainViewModel mViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    boolean requestingLocationUpdates = true;
    LocationRequest locationRequest;
    private boolean hasPermission = false;
    private Toolbar toolbar;
    private static final String TAG = "MainActivity";
    MqttHandler mqttHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        toolbar = findViewById(R.id.activity_main_toolbar);

        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.draw_layout);
        ActionBarDrawerToggle tog = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(tog);

        tog.syncState();
        // getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        NavigationView navigationView = findViewById(R.id.navigation_views);
        navigationView.setNavigationItemSelectedListener(this);

       // navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        navController = navHostFragment.getNavController();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationValue();

        next();

        LocationCall();

        Log.v(TAG, "------------------");
       // setContentView(R.layout.activity_main);
//        String serverUri = "tcp://164.52.203.123:1883";  // Replace with your MQTT broker URL
//        String clientId = "android-client";
//        mqttHandler = new MqttHandler();
//     //   mqttHandler = new MqttHandler();
//        mqttHandler.connect(serverUri, clientId);
//        mqttHandler.subscribe("mqtt/ack");
        // Log.i(mqtt/face/heartbeat);
        Data data = new Data();
        data.customId = "E00003";
        data.personId = "3";
        data.recordID = "003";
        data.verifyStatus = "1";
        data.personType = "0";
        data.similarity1 = "91.400002";
        data.similarity2 = "0.000000";
        data.sendintime = "1";
        data.direction = "entr";
        data.otype = "1";
        data.persionName = "Sms";
        data.facesluiceId = "1849929";
        data.facesluiceName = "Face1";
        data.idCard = "E002";
        data.telnum = "";
        data.left = "263";
        data.top = "481";
        data.right = "626";
        data.bottom = "831";
        data.time = "2023-12-22 09:25";
        data.pushType = "0";
        data.opendoorWay = "0";
        data.cardNum2 = "1";
        data.rFIDCard = "0";
        data.szQrCodeData = "";
        data.isNoMask = "0";
        data.dwFileIndex = "0";
        data.dwFilePos = "32047104";
        data.pic = "http://192.168.1.7:8081/files/download/arasoftwares\\Record\\2023\\11\\16\\face1849929_1700115439.jpg";

        Gson gson = new Gson();
        String json = gson.toJson(data);
//        MqttMessage message = new MqttMessage();
//        message.setPayload("{
//                "customId" : "E002",
//                " \"personId\" : \"2\",\n" +
//                " \"RecordID\" : \"13\",\n" +
//                " \"VerifyStatus\" : \"1\",\n" +
//                " \"PersonType\" : \"0\",\n" +
//                " \"similarity1\" : \"91.400002\",\n" +
//                " \"similarity2\" : \"0.000000\",\n" +
//                " \"Sendintime\" : NumberInt(1),\n" +
//                " \"direction\" : \"entr\",\n" +
//                " \"otype\" : \"1\",\n" +
//                " \"persionName\" : \"Sms\",\n" +
//                " \"facesluiceId\" : \"1849929\",\n" +
//                " \"facesluiceName\" : \"Face1\",\n" +
//                " \"idCard\" : \"E002\",\n" +
//                " \"telnum\" : \" \",\n" +
//                " \"left\" : \"263\",\n" +
//                " \"top\" : \"481\",\n" +
//                " \"right\" : \"626\",\n" +
//                " \"bottom\" : \"831\",\n" +
//                " \"time\" : \"2023-11-16 11:51:18\",\n" +
//                " \"PushType\" : \"0\",\n" +
//                " \"OpendoorWay\" : \"0\",\n" +
//                " \"cardNum2\" : \"1\",\n" +
//                " \"RFIDCard\" : \"0\",\n" +
//                " \"szQrCodeData\" : \"\",\n" +
//                " \"isNoMask\" : \"0\",\n" +
//                " \"dwFileIndex\" : \"0\",\n" +
//                " \"dwFilePos\" : \"32047104\",\n" +
//                " \"pic\" : \"http://192.168.1.7:8081/files/download/arasoftwares\\\\Record\\\\2023\\\\11\\\\16\\\\face1849929_1700115439.jpg\"\n" +
//                " }".getBytes());
//        mqttHandler.publish("mqtt/test", json);


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void LocationValue() {

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                    }
                    Boolean coarseLocationGranted = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        coarseLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    }
                    if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                turnLocationOn();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                turnLocationOn();
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );


        //  setFragment();
        locationPermissionRequest.launch(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        doOnLocationPermission();
    }

    private void doOnLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
            }
        } else {

        }
    }

    void turnLocationOn() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setNeedBle(true);
        Task<LocationSettingsResponse> task =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        MainActivity.this,
                                        111);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });

    }

    private void next() {
        locationRequest = new com.google.android.gms.location.LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(1000)
                .build();
    }

    private void LocationCall() {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                   mViewModel.setCachedLocation(location);
                    //Toast.makeText(getApplicationContext(), (int) location.getLongitude(), Toast.LENGTH_SHORT).show();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    mViewModel.lat=latitude;
                    mViewModel.lon=longitude;
                    mViewModel.location=location;
                   // String a=latitude.t

                      // Toast.makeText(getApplicationContext(),location.toString(),Toast.LENGTH_SHORT).show();
                    // Update UI with location data
                    // ...
                }
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 111:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            mViewModel.sampleData=null;
            navController.navigate(R.id.markAttendanceFragment);
        } else if (id == R.id.nav_log) {
            navController.navigate(R.id.missingLogsFragment);
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}