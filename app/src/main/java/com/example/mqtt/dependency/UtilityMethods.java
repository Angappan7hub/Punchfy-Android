package com.example.mqtt.dependency;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

//import com.arasoftwares.attendance.service.WebService;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UtilityMethods {
    private static Retrofit retrofit;
//    private static WebService webService;
    public static int gps_value = 0;
    private static final String TAG = "UtilityMethods";





    static Retrofit getRetrofit() {
        if (retrofit == null) {
            JsonDeserializer<Date> deserializer = new JsonDeserializer<Date>() {
                @Override
                public Date deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context) throws JsonParseException {
                    String strDate = json.getAsString();
                    if (strDate.isEmpty())
                        return new Date();
                    String[] strDates = strDate.split("/");
                    int date = Integer.parseInt(strDates[0]);
                    int month = Integer.parseInt(strDates[1]);
                    int year = Integer.parseInt(strDates[2]);
                    return new Date(year, month, date);

                }
            };
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, deserializer)
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

//    public static WebService getWebService() {
//        if (webService == null) {
//            webService = getRetrofit().create(WebService.class);
//        }
//
//        return webService;
//    }




    public static Snackbar showSnackBar(View rootView, String message) {
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        snackbar.show();
        return snackbar;
    }

    public static Snackbar showSnackBar(Activity activity, String message) {
        View rootView = activity.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        snackbar.show();
        return snackbar;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static int toInt(String toString) {
        if (toString == null || toString.isEmpty())
            return 0;
        return Integer.parseInt(toString);
    }

    public static String fromDate(Date date) {
        int day = date.getDate();
        int month = date.getMonth();
        int year = date.getYear();
        return String.format("%d/%d/%d", day, month, year);
    }

    public static String toRupees(double billAmount) {
        return String.format("₹ %5.2f", billAmount);

    }

//    public static String toJson(LoggedInUser loggedInUser) {
//        Gson gson = new Gson();
//        return gson.toJson(loggedInUser);
//    }

    public static String toBase64(String billImage) {
        File file = new File(billImage);
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(billImage);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);

            byte[] data = outputStream.toByteArray();
            return Base64.encodeToString(data, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    public static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (location == null) {
            return false;
        }
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    public static String toString(int startingKm) {
        return String.format("%d", startingKm);
    }
}
