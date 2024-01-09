package com.example.mqtt.dependency;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.mqtt.model.Branch;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(context)
                .getLastLocation().addOnSuccessListener(location -> {
                    //perform your update here with last known location.
                    if (location != null) {
                        updateToServer(context, location);
                    } else {
                        Log.i("Location", "null Received");
                    }
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.i("Location", "faiuled");
                });
    }

    private void updateToServer(Context context, Location location) {
        SessionManager sessionManager = new SessionManager(context);
       Branch branch = sessionManager.getBranch();
        LatLng latLngA = new LatLng(Double.parseDouble(branch.branchLatitude), Double.parseDouble(branch.branchLongitude));
        LatLng latLngB = new LatLng(location.getLatitude(), location.getLongitude());
        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(latLngB.latitude);
        locationB.setLongitude(latLngB.longitude);
        double distanceMeter = locationA.distanceTo(locationB);
        int meter = (int) distanceMeter;
//        double areaDistance = Double.valueOf(branch.areaDis);
//        int areaDis = (int) areaDistance;
       // notification(context);
//        if (meter > areaDis) {
//
//            // Toast.makeText(getContext(), "Location Miss Match", Toast.LENGTH_LONG).show();
//            // return;
//        }

    }

//    private void notification(Context context) {
//
//        SessionManager sessionManager = new SessionManager(context);
//        Token tok = sessionManager.getLoggedInUser();
//        UserInfo userInfo = sessionManager.getUserInfo();
//        String title = "Employee Crossed Work Distance Limit";
//        String message = userInfo.userName + " crossed maximum  work distance limit.";
//
//        String url = "http://dev.arasoftwares.in/hathway/api.php?action=notification";
//        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                System.out.println(response);
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //Toast.makeText(MainActivity.this,"Service Unavailable",Toast.LENGTH_SHORT).show();
//                error.printStackTrace();
//            }
//        }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/json; charset=UTF-8");
//                params.put("Token", tok.token);
//                return params;
//            }
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> map = new HashMap<>();
//                map.put("title", title);
//                map.put("message", message);
//                return map;
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.add(request);
//
//    }
}