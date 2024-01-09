package com.example.mqtt.ViewModel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mqtt.dependency.Screen;
import com.example.mqtt.model.AckModel;
import com.example.mqtt.model.SampleData;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends BaseViewModel {

    private final MutableLiveData<String> locationLiveData = new MutableLiveData<>();

    private final MutableLiveData<Location> cachedLocationLiveData = new MutableLiveData<>();

//    public AttendanceState attendanceState;
//    public AttendanceList attendanceList;

    public LiveData<String> location() {
        return locationLiveData;
    }

    public void setCachedLocation(String location) {
        locationLiveData.setValue(location);
    }

    private final MutableLiveData<Screen> navigate = new MutableLiveData<>();

    public LiveData<Screen> navigator() {
        return navigate;
    }

    public void navigateTo(Screen screen) {
        navigate.setValue(screen);
    }


    public void setCachedLocation(Location mLastLocation) {
        cachedLocationLiveData.setValue(mLastLocation);
    }

    public LiveData<Location> getCachedLocation() {

        return cachedLocationLiveData;
    }

    public double lat;
    public double lon;
    public Location location;
    public String base64;
    public double a=6.78900;
    public AckModel ackModel;
    public SampleData sampleData;
}
