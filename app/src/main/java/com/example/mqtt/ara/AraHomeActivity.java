package com.example.mqtt.ara;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.mqtt.dependency.AppConstants.PERMISSIONS_REQUEST_LOCATION;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mqtt.R;
import com.example.mqtt.adapter.HistoryAdapter;
import com.example.mqtt.adapter.PunchHistoryAdapter;
import com.example.mqtt.ara.dependency.AraSessionManager;
import com.example.mqtt.ara.dependency.FactoryMethods;
import com.example.mqtt.ara.model.Branch;
import com.example.mqtt.ara.model.EmpLog;
import com.example.mqtt.ara.model.PostEmpLog;
import com.example.mqtt.dependency.ItemChoiceListener;
import com.example.mqtt.dependency.Result;
import com.example.mqtt.dependency.UtilityMethods;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;

public class AraHomeActivity extends AppCompatActivity implements ItemChoiceListener {


    LocationRequest locationRequest;

    private LocationCallback locationCallback;

    private FusedLocationProviderClient fusedLocationClient;

    boolean requestingLocationUpdates = true;

    String address;

    String strCurrentLocation;

    Location currentLocation;

    TextView locationText,dateText,fromDateText,toDateText;
    RecyclerView recyclerView;

    CardView punchCard, wfHomePunchCard;

    private static final String TAG = "AraHomeActivity";

    PunchHistoryAdapter punchHistoryAdapter;

    double latPt;
    double lonPt;
    double braLat;
    double braLong;

    boolean workType;

    private Handler handler;
    private Runnable updateTimeRunnable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        viewReferFields();

        handler = new Handler();
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                // Repeat the update every second (1000 milliseconds)
                handler.postDelayed(this, 1000);
            }
        };
        Date currentDate = new Date();

        // Format the date and time using SimpleDateFormat
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDateTime = dateFormat.format(currentDate);
        dateText.setText(formattedDateTime);

        LocationValue();

        next();

        LocationCall();

        getBranch();

        setLogs();

        setListener();

    }


    private void updateDateTime() {
        // Get the current date and time
        Date currentDate = new Date();

        // Format the date and time using SimpleDateFormat
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDateTime = dateFormat.format(currentDate);

        // Set the formatted date and time to the TextView
        dateText.setText(formattedDateTime);
    }




    private void getBranch() {
        AraSessionManager araSessionManager=new AraSessionManager(this);
        String token=araSessionManager.getUserInfo().token;


        FactoryMethods.getUserRepository(token).getBranch().observe(this, new Observer<Result<Branch>>() {
            @Override
            public void onChanged(Result<Branch> branchResult) {
                if(branchResult.isSuccess()){
                    Branch branch=branchResult.getData();
                    braLat=branch.lat;
                    braLong=branch.lon;
                }
            }
        });

    }

    private void setListener() {

        punchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workType=false;
                proceedPunch();
            }
        });

        wfHomePunchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workType=true;
                proceedWFHomePunch();
            }
        });


        fromDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDate();
            }
        });


    }

    private void changeDate() {


    }

    private void proceedWFHomePunch() {
       postLog(latPt,lonPt);

    }

    private void proceedPunch() {
        double a=latPt;
        double b=lonPt;
        double c=a;
        double d=b;
        LatLng latLngA = new LatLng(braLat, braLong);
                    LatLng latLngB = new LatLng(latPt, lonPt);
                    Location locationA = new Location("point A");
                    locationA.setLatitude(latLngA.latitude);
                    locationA.setLongitude(latLngA.longitude);
                    Location locationB = new Location("point B");
                    locationB.setLatitude(latLngB.latitude);
                    locationB.setLongitude(latLngB.longitude);
                    double distanceMeter = locationA.distanceTo(locationB);
                    int meter = (int) distanceMeter;

                    if (meter > 100) {
                        Toast.makeText(getApplicationContext(), "Location Miss Match, You are not in Branch Location", Toast.LENGTH_LONG).show();
                        doOnLocationPermission();
                        return;
                    }
                    else{
                        postLog(latPt,lonPt);
                    }


    }

    private void postLog(double latPt, double lonPt) {
        AraSessionManager araSessionManager=new AraSessionManager(this);
        String token=araSessionManager.getUserInfo().token;
        long branchId=araSessionManager.getUserInfo().branchId;

        PostEmpLog postEmpLog=new PostEmpLog();
        postEmpLog.branchId=branchId;
        if(workType) {
            postEmpLog.attendanceType = 1;
        }
        else {
            postEmpLog.attendanceType = 0;
        }
        postEmpLog.longitude= String.valueOf(lonPt);
        postEmpLog.latitude= String.valueOf(latPt);
        postEmpLog.address=locationText.getText().toString();
        postEmpLog.imageUrlId=0;
        Date currentDate = new Date();

        // Define the desired date and time format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        // Format the current date and time using the formatter
        String formattedDateTime = dateFormat.format(currentDate);
        postEmpLog.time=formattedDateTime;

        FactoryMethods.getUserRepository(token).postEmpLog(postEmpLog,branchId).observe(this, new Observer<Result<EmpLog>>() {
            @Override
            public void onChanged(Result<EmpLog> empLogResult) {
                if(empLogResult.isSuccess()){
                    Toast.makeText(AraHomeActivity.this, "Employee Log Posted Successfully", Toast.LENGTH_SHORT).show();
                    setLogs();
                }
            }
        });


    }

    private void setLogs() {
        AraSessionManager araSessionManager=new AraSessionManager(this);
        String token=araSessionManager.getUserInfo().token;

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        // Format the date to the desired format (yyyy-MM-dd)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(date);
        FactoryMethods.getUserRepository(token).getEmpLogs(formattedDate,formattedDate).observe(this, new Observer<Result<List<EmpLog>>>() {
            @Override
            public void onChanged(Result<List<EmpLog>> listResult) {
                if(listResult.isSuccess()){
                   List<EmpLog> empLogs=listResult.getData();
                   updateRecyclerView(empLogs);
                }else{
                    Toast.makeText(getApplicationContext(),listResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateRecyclerView(List<EmpLog> empLogs) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        punchHistoryAdapter = new PunchHistoryAdapter(this, empLogs, this);
        recyclerView.setAdapter(punchHistoryAdapter);
    }

    private void viewReferFields() {
        locationText=findViewById(R.id.act_ara_home_loc_text);
        dateText=findViewById(R.id.act_ara_home_date_text);
        fromDateText=findViewById(R.id.act_ara_home_from_date);
        toDateText=findViewById(R.id.act_ara_home_to_date);
        recyclerView=findViewById(R.id.act_ara_home_punch_history_list);
        punchCard=findViewById(R.id.act_ara_home_punch_card);
        wfHomePunchCard=findViewById(R.id.act_ara_home_wfh_punch_card);

        Date currentDate = new Date();

        // Define the desired date and time format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Format the current date and time using the formatter
        String formattedDateTime = dateFormat.format(currentDate);
        dateText.setText(formattedDateTime);

//        LocalDateTime now = null;
//        DateTimeFormatter formatter = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            now = LocalDateTime.now();
//            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            String formattedDateTime = now.format(formatter);
//            dateText.setText(formattedDateTime);
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(updateTimeRunnable);
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
                                        AraHomeActivity.this,
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

//                    Toast.makeText(getApplicationContext(), "Long:"+String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), "Lat:"+String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    latPt=latitude;
                    lonPt=longitude;
                    updateAddressUI(location);

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
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        handler.removeCallbacks(updateTimeRunnable);
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
    private void updateAddressUI(Location location) {
        Result<String> result = fetchAddress(location);
        if (result.isSuccess()) {
            address = result.getData();
            strCurrentLocation = address;
            currentLocation = location;
                locationText.setText(address);

            } else {
            locationText.setText("");

            }
            UtilityMethods.gps_value = 1 + UtilityMethods.gps_value;
           // progressBar.setVisibility(View.GONE);
            //rootLayout.setRefreshing(false);
//            Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
            //   mViewModel.setCachedLocation(result.getData());

    }

    private Result<String> fetchAddress(final Location location) {

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;
        String errorMessage = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            Log.e(TAG, errorMessage);
        } else {
            Address address = addresses.get(0);
            StringBuffer addressStringBuffer = new StringBuffer();

            // Fetch the address lines using {@code getAddressLine},
            // join them, and send them to the thread. The {@link android.location.address}
            // class provides other options for fetching address details that you may prefer
            // to use. Here are some examples:
            // getLocality() ("Mountain View", for example)
            // getAdminArea() ("CA", for example)
            // getPostalCode() ("94043", for example)
            // getCountryCode() ("US", for example)
            // getCountryName() ("United States", for example)
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressStringBuffer.append(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            return new Result.Success<String>(addressStringBuffer.toString());
        }
        return new Result.StringError(errorMessage);
        // return new Result.StringError(errorMessage);
    }


    @Override
    public void onItemChoosed(Object selectedItem, int position) {

    }
}