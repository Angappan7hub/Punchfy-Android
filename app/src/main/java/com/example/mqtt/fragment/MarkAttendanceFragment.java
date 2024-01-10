package com.example.mqtt.fragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_OK;
import static com.example.mqtt.dependency.AppConstants.PERMISSIONS_REQUEST_LOCATION;
import static java.text.DateFormat.getDateInstance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mqtt.BuildConfig;
import com.example.mqtt.MainActivity;
import com.example.mqtt.MqttHandler;
import com.example.mqtt.R;
import com.example.mqtt.ViewModel.MainViewModel;
import com.example.mqtt.dependency.PermissionsDelegate;
import com.example.mqtt.dependency.Result;
import com.example.mqtt.dependency.SessionManager;
import com.example.mqtt.dependency.UtilityMethods;
import com.example.mqtt.model.Branch;
import com.example.mqtt.model.SampleData;
import com.example.mqtt.model.Token;
import com.example.mqtt.model.UserInfo;
import com.google.android.gms.common.api.Status;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MarkAttendanceFragment extends Fragment {
    private static final int REQUEST_CODE = 101;
    private static final int CAMERA_PERMISSION_REQUEST_CODE=102;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private ConstraintLayout rootView;
    private TextView todayText;
    private TextView userName;
    private TextView branchName, currentAddress;
    ProgressBar progressBar;
    private MainViewModel mViewModel;
    public List<SampleData> sampleDataList=new ArrayList<>();
    private Bitmap photo;
    private String currentPhotoPath;
    private String strCurrentLocation = "";
    private Location currentLocation;
    private Button subButton, checkOutBtn;
    String distanceStatus;
    ImageButton addProfile;
    Branch branch;
    Status status;
    String type;
    TextView userLevel, versionName;
    public String address;
    String date;
  //  SwipeRefreshLayout rootLayout;
    int gps_value = 0;
    public double la= 0.0;
    public double lo=0.0;
    public String dateTime="";
    public static final int REQUEST_IMAGE_CAPTURE_COMPLAINT = 1777;
    //EmployeeProfile employeeProfile;
    private PermissionsDelegate permissionsDelegate;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    public static final int SELECT_PICTURE = 5;
    TextView txtLat;
    String lat;
    CircularImageView empImage;
    String provider;
    private boolean hasPermission = false;
    protected String latitude, longitude;
    protected boolean gps_enabled = true, connected;
    String imagePath;
    MultipartBody.Part files;
    MqttHandler mqttHandler;

    MainViewModel mainViewModel;
    NavController navController;

    private ImageCapture imageCapture;
    Dialog dialog;
    PreviewView mPreviewView;
    Button bTakePicture;
    ProgressBar cameraProgress;
    ProcessCameraProvider cameraProvider;
    public String base64;
    public String ack;
    Bitmap cameraxBitmap;
    //    public Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private static final String TAG = "AndroidCameraApi";
    private Button takePictureButton;
    private TextureView textureView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mark_attendance, container, false);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                //showAlertDialog();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
//        String serverUri = "tcp://164.52.203.123:1883";  // Replace with your MQTT broker URL
//        String clientId = "android-client";
//        mqttHandler=new MqttHandler(getActivity());
//        mqttHandler.connect(serverUri,clientId);
//        mqttHandler.subscribe("mqtt/ack");
        permissionsDelegate = new PermissionsDelegate(getActivity());
        hasPermission = permissionsDelegate.hasPermissions();
        if (hasPermission) {
            //cameraView.setVisibility(View.VISIBLE);
        } else {
            permissionsDelegate.requestPermissions();
        }
        inject();
        referViewFields(view);
        getBranch();
        getStatus();
        setListener();
        observeNecessary();
        doOnLocationPermission();
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            // ask permissions here using below code
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
          //  requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
        }
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        gps_value = 0;
        gnss();
        network();
        checkConnection();
        getEmployeeProfile();
        //getLocationPermission();
        initUI();

        setData();
        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location-related tasks
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
                // ...
            } else {
                // Permission denied, handle accordingly
                // ...
            }
        }
    }
//    private void showAlertDialog() {
//
//        Snackbar mySnackBar = Snackbar.make(rootLayout, "Are you sure you want leave?",
//                Snackbar.LENGTH_LONG);
//        mySnackBar.setAction(YES_STRING, new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onClick(View view) {
//                ActivityCompat.finishAffinity(getActivity());
//                getActivity().finish();
//                getActivity().finishAndRemoveTask();
//                System.exit(0);
//            }
//        });
//        mySnackBar.show();
//    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Home");
    }

    private void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        if (connected) {
            connected = true;
            progressBar.setVisibility(View.VISIBLE);
            getBranch();
            getStatus();
        } else {
            connected = false;
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
           // rootLayout.setRefreshing(false);
        }

        if (!gps_enabled) {
            Toast.makeText(getContext(), "Please On Your location", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            //rootLayout.setRefreshing(false);
        }
    }


    private void getStatus() {
        Token tok = mViewModel.getCurrentUser(this.getContext());
        UserInfo userInfo = mViewModel.getUserInfo(getContext());
//        UserFactoryMethods.getUseDataRepository(tok.token).getAttendanceStatus().observe(getViewLifecycleOwner(), new Observer<Result<Status>>() {
//            @Override
//            public void onChanged(Result<Status> userInfoResult) {
//
//                if (userInfoResult.isSuccess()) {
//                    //    progressBar.setVisibility(View.GONE);
//                    status = userInfoResult.getData();
//                    if (status.status.compareTo("") == 0) {
//                        type = "in";
//                        subButton.setVisibility(View.VISIBLE);
//                        checkOutBtn.setVisibility(View.GONE);
//                    } else if (status.status.compareTo("in") == 0) {
//                        type = "out";
//                        subButton.setVisibility(View.GONE);
//                        checkOutBtn.setVisibility(View.VISIBLE);
//                    } else if (status.status.compareTo("out") == 0) {
//                        type = "in";
//                        subButton.setVisibility(View.VISIBLE);
//                        checkOutBtn.setVisibility(View.GONE);
//                    }
//                }
//            }
//        });
    }

    private void setData() {
        if(mainViewModel.sampleData!=null){
            addProfile.setVisibility(View.GONE);
            if(mainViewModel.sampleData.base64!=null) {
                baseToBitmap(mainViewModel.sampleData.base64);
            }
            currentAddress.setText(mainViewModel.sampleData.address);
            base64=mainViewModel.sampleData.base64;
            ack=mainViewModel.sampleData.ack;
            la=mainViewModel.sampleData.latitude;
            lo=mainViewModel.sampleData.longitude;
            dateTime=mainViewModel.sampleData.datetime;
        }
    }
  private void baseToBitmap(String base64){
      String base64String =base64;

      // Convert Base64 to byte array
      byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);

      // Convert byte array to Bitmap
      Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
      empImage.setImageBitmap(bitmap);
  }
    private void getBranch() {

        Token tok = mViewModel.getCurrentUser(this.getContext());
        UserInfo userInfo = mViewModel.getUserInfo(getContext());
//        UserFactoryMethods.getUseDataRepository(tok.token).getBranchDetails(userInfo.branchId).observe(getViewLifecycleOwner(), new Observer<Result<Branch>>() {
//            @Override
//            public void onChanged(Result<Branch> userInfoResult) {
//                if (userInfoResult.isSuccess()) {
//                    //    progressBar.setVisibility(View.GONE);
//
//                    branch = userInfoResult.getData();
//                    saveSession(branch);
//                    String b = branch.branchCode;
//                    branchName.setText(branch.branchNme);
//                }
//            }
//        });
    }

    private void saveSession(Branch branch) {
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.saveBranch(branch);
    }

    private String formatDate(Date date) {
        return getDateInstance().format(date);
    }

    private String formatTime(Calendar calendar) {
        DateFormat dateFormat = new SimpleDateFormat("h:mm a");
        return dateFormat.format(calendar.getTime());
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return formatTime(calendar);
    }
    private void inject() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        navController = NavHostFragment.findNavController(this);
    }

    private void initUI() {

        todayText.setText(formatDate(Calendar.getInstance().getTime()));
        String strCurrentTime = getCurrentTime();
//        AttendanceState state = mViewModel.attendanceState;
//        if (mViewModel == null) {
//            mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
//        }
//
//        if (state == null) {
//            state = new AttendanceState();
//            mViewModel.attendanceState = state;
//            state.attendance = CHECK_OUT;
//        }
//        if (state.attendance == CHECK_IN) {
//            //Current Status is Check IN,so need to update UI for Check Out
////            inText.setText(state.time);
//            //outText.setText(strCurrentTime);
//
//        } else {
//            //Current Status is check out , so need to update UI for Check In
//            // inText.setText(strCurrentTime);
//            // outText.setText(EMPTY_TIME);
//
//        }
    }



    private void observeNecessary() {
        mViewModel.location().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String strLocation) {
                strCurrentLocation = strLocation;
                if(mainViewModel.sampleData!=null){
                    currentAddress.setText(mainViewModel.sampleData.address);
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    if (gps_enabled && connected) {
                        currentAddress.setText(strCurrentLocation);
                    } else {
                        currentAddress.setText("");
                    }
                }

             //   rootLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        });
        mViewModel.getCachedLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {

                if(mainViewModel.sampleData!=null){
                    currentAddress.setText(mainViewModel.sampleData.address);
                   progressBar.setVisibility(View.GONE);
                }
                else {
                    currentLocation = location;
                    updateAddressUI(currentLocation);
                }
            }
        });
    }

    private void updateAddressUI(Location location) {
        Result<String> result = fetchAddress(location);
        if (result.isSuccess()) {
            address = result.getData();
            strCurrentLocation = address;
            currentLocation = location;
            if (gps_enabled && connected) {
                currentAddress.setText(address);
            } else {
                currentAddress.setText("");
            }
            UtilityMethods.gps_value = 1 + UtilityMethods.gps_value;
            progressBar.setVisibility(View.GONE);
            //rootLayout.setRefreshing(false);
//            Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
            //   mViewModel.setCachedLocation(result.getData());
        } else {

        }
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
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

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


    private void setListener() {
//
//        rootLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                checkConnection();
//                observeNecessary();
//
//            }
//        });
//        takePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dispatchTakePictureIntent();observeNecessary
//            }
//        });
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String serverUri = "tcp://164.52.203.123:1883";  // Replace with your MQTT broker URL
//                String clientId = "android-client";
//                mqttHandler=new MqttHandler();
//                mqttHandler.connect(serverUri,clientId);
             //   mqttHandler.subscribe("mqtt/test");

                SampleData data=new SampleData();
                if(mainViewModel.sampleData==null) {
                    base64=mViewModel.base64;
                    data.address=currentAddress.getText().toString();
                    String uuid = UUID.randomUUID().toString();
                    ack=uuid;
                    if (mViewModel.location != null) {
                        la = mViewModel.location.getLatitude();
                        lo = mViewModel.location.getLongitude();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String currentDateAndTime = sdf.format(new Date());
                    dateTime = currentDateAndTime;
                }

                data.datetime= dateTime;
                data.latitude=la;
                data.longitude=lo;
                data.base64=base64;
                data.address=currentAddress.getText().toString();
                data.ack=ack;
                SampleData data1=data;
                SessionManager sessionManager = new SessionManager(getContext());
                List<SampleData> jsonData= sessionManager.getData();
                if(jsonData!=null && jsonData.size()!=0)
                {
                    sampleDataList=jsonData;

                    if(mainViewModel.sampleData!=null){
                        sampleDataList=jsonData;
                        //sampleDataList.add(data);
                        for(int i=0;i<jsonData.size();i++){
                            if(mainViewModel.sampleData.ack.compareTo(jsonData.get(i).ack)==0){
                                sampleDataList=jsonData;
                                //return;
                            }
//                            else{
//                               sampleDataList.add(data);
//                            }
                        }

                    }

             else{
                 sampleDataList.add(data);
             }
                }
                else {
                    sampleDataList.add(data);
                }

                sessionManager.saveSampleData(sampleDataList);
                SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

                Gson gson1=new Gson();
                String jsonData1=gson1.toJson(jsonData);
                // Retrieve the stored string data
            //    String jsonData = preferences.getString("data", "");
              // SharedPreferences preferences = getContext().getSharedPreferences(context.getPackageName(),
                //        Context.MODE_PRIVATE);
                //String jsonData = preferences.getString("data", "");
                Log.i("json",jsonData1);
                Gson gson=new Gson();
                String json=gson.toJson(data);
                System.out.print("JsonData "+jsonData1);
                MqttMessage message = new MqttMessage(json.getBytes());
                message.setQos(1);
                String serverUri = "tcp://164.52.203.123:1883";  // Replace with your MQTT broker URL
        String clientId = "android-client";
        mqttHandler=new MqttHandler();
        mqttHandler.connect(serverUri,clientId);
        mqttHandler.publish("mqtt/test", String.valueOf(message));
//                if(mViewModel.ackModel!=null){
//                    String a=mViewModel.ackModel.response;
//                        if (jsonData != null) {
//                            for (int i = 0; i <= jsonData.size(); i++) {
//                                if (jsonData.get(i).ack.compareTo(a) == 0) {
//                                    sessionManager.clearSingleSampleData(jsonData.get(i));
//                                }
//                            }
//                        }
//                }
                Toast.makeText(getContext(), "Checked", Toast.LENGTH_SHORT).show();
                //           '
                //
                //           [
                //           \
                //           \navController.navigate(R.id.missingLogsFragment);

              //  alertDialog(type);

            }
        });
        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageCameraX();
             //   selectImageDialog();

            }
        });

        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog(type);
            }
        });

        empImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (employeeProfile.profilePath != null) {
                  //  if (!employeeProfile.profilePath.isEmpty()) {
                        showImage();
                    //}
                //}
            }
        });

    }

    public void showImage() {

//        final Dialog mDialog = new Dialog(getContext(), R.style.AppBaseTheme);
//        mDialog.setContentView(R.layout.image_load_screen);
//        mDialog.setCancelable(true);
//        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        TouchImageView bmImage = (TouchImageView) mDialog.findViewById(R.id.image_view);
//        Picasso.get().
//                load(Uri.parse(employeeProfile.profilePath)).into(bmImage);
//        ImageButton cancel = (ImageButton) mDialog.findViewById(R.id.dialog_cancel);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mDialog.dismiss();
//            }
//        });
//        mDialog.show();
    }

    private void alertDialog(String type) {
        String types = type;

        if (types.compareTo("in") == 0) {
            types = "IN";
        } else {
            types = "OUT";
        }
        UtilityMethods.gps_value = -1;
        new AlertDialog.Builder(getContext())
                .setTitle("Mark " + types + " Attendance")
                .setMessage("Are you sure to mark " + types + " attendance.?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        submit(type);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void referViewFields(View view) {
        currentAddress = view.findViewById(R.id.frag_mark_attendance_user_address);
        progressBar = view.findViewById(R.id.frag_mark_attendance_progress_bar);
        versionName = view.findViewById(R.id.frag_mark_attendance_version_name);
        versionName.setText("V : " + BuildConfig.VERSION_NAME);
        todayText = view.findViewById(R.id.emp_current_date);
        userName = view.findViewById(R.id.emp_name);
        branchName = view.findViewById(R.id.frag_mark_attendance_branch_name);
        userLevel = view.findViewById(R.id.frag_mark_attendance_user_level);
        mViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        subButton = view.findViewById(R.id.frag_mark_attendance_submit_btn);
        addProfile = view.findViewById(R.id.frag_user_profile_edit_icon_btn);
        checkOutBtn = view.findViewById(R.id.frag_mark_attendance_check_out_submit_btn);
        checkOutBtn.setVisibility(View.GONE);
        empImage = view.findViewById(R.id.mark_att_image);
     //   rootLayout = view.findViewById(R.id.root_layout);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1002);
    }


    /*
    Take photo code goes here.
     */
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    private void selectImageCameraX() {
        if (checkPermission()) {
            try {
                PackageManager pm = getActivity().getPackageManager();
                //   requestPermission();
                int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getActivity().getPackageName());
                if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                    final CharSequence[] options = {"Take Photo","Cancel"};
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity().peekAvailableContext());
                    alertDialogBuilder.setTitle("Select Option");
                    alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take Photo")) {
                                dialog.dismiss();
                                if (hasPerm == 0) {
                                    // startCamera();
                                    openCamerax();
                                }

                                //  startCamera();

//                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                startActivityForResult(intent, SELECT_FROM_CAMERA);
                            }
//                            else if (options[item].equals("Choose From Gallery")) {
//                                dialog.dismiss();
//                                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                startActivityForResult(pickPhoto, SELECT_FROM_GALLERY);
//                            }
                            else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                        }
                    });
                    alertDialogBuilder.show();
                } else
                    //requestPermission();
                    Toast.makeText(getActivity(), "Camera Permission error", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Camera Permission error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            //main logic or main code

            // . write your main code to execute, It will execute if the permission is already given.

        } else {
            requestPermission();
        }
    }

    private void selectImageDialog() {

        try {
            PackageManager pm = getContext().getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getContext().getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo",
                        //"Choose From Gallery",
                        "Cancel"};

                android.app.AlertDialog.Builder alertDialogBuilder =
                        new android.app.AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("Select Option");
                alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();

//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(intent, 101);
                           // dispatchTakePictureIntent();
                            openCamerax();

                        }
//                        else if (options[item].equals("Choose From Gallery")) {
//                            selectImage();
//                        }
                        else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                alertDialogBuilder.show();
            } else
               // ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                //permissionsDelegate.requestPermissions();
            Toast.makeText(getContext(), "Camera Permission denied", Toast.LENGTH_SHORT).show();
            if (permissionsDelegate.hasPermissions() && !hasPermission) {
                hasPermission = true;
//            frontFotoapparat.start();
            } else {
                permissionsDelegate.requestPermissions();
            }

        } catch (Exception e) {
           // permissionsDelegate.requestPermissions();
            Toast.makeText(getContext(), "Camera Permission denied", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            if (permissionsDelegate.hasPermissions() && !hasPermission) {
                hasPermission = true;
//            frontFotoapparat.start();
            } else {
                permissionsDelegate.requestPermissions();
            }
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            //  Create the File where the photo should go
         /*  File photoFile = null;
         photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
               Uri photoURI = FileProvider.getUriForFile(getContext(),
                       "com.arasoftwares.tripcontrol.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);*/
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_COMPLAINT);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE_COMPLAINT && resultCode == RESULT_OK) {

            if (data != null) {
                updateImageUI(data.getExtras(), data);
                // setPic();
            } else {
                Toast.makeText(getContext(), "Please ReTake image", Toast.LENGTH_LONG).show();
            }
        }


        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            Bitmap bitmap = fromuri(selectedImageUri);
            try {
                createFile(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            complaintImagePath = UtilityMethods.getBase64String(bitmap);

            // Picasso.get().load(selectedImageUri).into(empImage);

        }
    }

    public Bitmap fromuri(Uri uri) {
        InputStream imageStream = null;
        try {
            imageStream = getContext().getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
        return yourSelectedImage;
    }

    private void openCamerax() {
        dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.camera_preview_dialog);
        dialog.setTitle("Bill Image");
        mPreviewView = (PreviewView) dialog.findViewById(R.id.camera);
        bTakePicture = (Button) dialog.findViewById(R.id.bCapture);
        cameraProgress = (ProgressBar) dialog.findViewById(R.id.fragment_camera_pb);
        cameraProgress.setVisibility(View.GONE);
        dialog.show();
        startCamera();

        // set the custom dialog components - text, image and button
//        ImageView image = (ImageView) dialog.findViewById(R.id.my_image);

        // Picasso.get().load(img).resize(500,500).into(image);

    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(getActivity()));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        cameraProvider.unbindAll();
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

//        final ImageCapture imageCapture = builder
//                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
//                .build();
        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());


        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        //dialog.show();

        bTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraProgress.setVisibility(View.VISIBLE);
//                Intent intent = new Intent();
//                startActivityForResult(intent, SELECT_FROM_CAMERA);
                bTakePicture.setVisibility(View.GONE);

                capturePhoto();
            }
        });
        // startCamera();


    }
    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(getActivity());
    }

    private void capturePhoto() {

        Toast.makeText(getContext(), "Please wait inprocess..", Toast.LENGTH_SHORT).show();
        long timeStamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getContext().getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                        Uri uri = outputFileResults.getSavedUri();
                        Uri uri1 = uri;
                        Log.v("uri", String.valueOf(uri1));

                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(getContext()
                                    .getContentResolver().openInputStream(uri));
                            Bitmap photo = bitmap;
                            String base64=toBase64(photo);
                            mViewModel.base64=base64;
                            Log.i("BASETAG",base64);
                            System.out.println("Base64"+base64);
                            try {
                                createFile(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                            //billImageView.setVisibility(View.VISIBLE);
                            empImage.setImageBitmap(bitmap);
                            //dialogBitmap = bitmap;
                            dialog.dismiss();
//                            cameraxBitmap=bitmap;
                            // showBitmap(cameraxBitmap);
                            cameraProvider.unbindAll();

//                            Bitmap bitmap1=bitmap;
//                            Bitmap bitmap2=bitmap1;

                        } catch (FileNotFoundException e) {
                            bTakePicture.setVisibility(View.VISIBLE);
                            cameraProgress.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                        //billImageView.setImageBitmap(bitmap);
                        //   Picasso.get().load("https://picsum.photos/536/354").into(billImageView);
                        // Toast.makeText(getContext(),"Saving...",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {

                        cameraProgress.setVisibility(View.GONE);
                        bTakePicture.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateImageUI(Bundle extras, Intent data) {
        if (extras == null) {
            Toast.makeText(getActivity(), "Please Retake Photo", Toast.LENGTH_SHORT).show();
        }
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        Bitmap photo = imageBitmap;
        String base64=toBase64(photo);
        mViewModel.base64=base64;
        Log.i("BASETAG",base64);
        System.out.println("Base64"+base64);
        empImage.setImageBitmap(photo);
        // complaintImagePath = UtilityMethods.getBase64String(photo);
        try {
            createFile(photo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toBase64(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);

        byte[] data = outputStream.toByteArray();
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    private File createFile(Bitmap photo) throws IOException {
        // Create an image file namea001
        String imageFileName = "Photo_" + Calendar.getInstance().get(Calendar.MINUTE);
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir   /* directory */
        );
        try {
            FileOutputStream out = new FileOutputStream(image);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // Save a file: path for use with ACTION_VIEW intents
            imagePath = image.getAbsolutePath();
            uploadProfileImage();
            return image;
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void uploadProfileImage() {
        Token tok = mViewModel.getCurrentUser(this.getContext());
        progressBar.setVisibility(View.VISIBLE);
        if (imagePath != null) {
            File file = new File(imagePath);
            files = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        }
//        UserFactoryMethods.getUseDataRepository(tok.token).uploadProfileImage(files).observe(getViewLifecycleOwner(),
//                new Observer<Result<String>>() {
//                    @Override
//                    public void onChanged(Result<String> responseDataResult) {
//                        if (responseDataResult.isSuccess()) {
//
//                            getEmployeeProfile();
//                        } else {
//                            progressBar.setVisibility(View.GONE);
//                            Toast.makeText(getContext(), responseDataResult.getErrorMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
    }

    private void getEmployeeProfile() {

//        Token tok = mViewModel.getCurrentUser(this.getContext());
//        UserFactoryMethods.getUseDataRepository(tok.token).getEmpProfile().observe(getViewLifecycleOwner(), new Observer<Result<EmployeeProfile>>() {
//            @Override
//            public void onChanged(Result<EmployeeProfile> userInfoResult) {
//                if (userInfoResult.isSuccess()) {
//
//                    employeeProfile = userInfoResult.getData();
//                    if (employeeProfile.profilePath != null) {
//                        if (!employeeProfile.profilePath.isEmpty()) {
//                            Picasso.get().load(Uri.parse(employeeProfile.profilePath)).fit().into(empImage);
//                        }
//                    }
//
//                    ((MainActivity) getActivity()).getProfile();
//
//                    progressBar.setVisibility(View.GONE);
//                } else {
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });
    }

    private void submit(String status) {


//        Session session = new Session(getContext());
//        LatLng latLngA = session.getLocation();
        if (!connected) {
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            return;
        }
        if (branch == null) {
            Toast.makeText(getContext(), "Branch Location is Not Available", Toast.LENGTH_SHORT).show();
            return;
        }
        if (branch.branchLatitude.isEmpty() || branch.branchLongitude.isEmpty()) {
            Toast.makeText(getContext(), "Branch Location is Not Available", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (currentLocation==null) {
//            Toast.makeText(getContext(), "Please Turn on your location", Toast.LENGTH_LONG).show();
//            return;
//        }

//        LatLng latLngA = new LatLng(Double.parseDouble(branch.branchLatitude), Double.parseDouble(branch.branchLongitude));
//        LatLng latLngB = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//        Location locationA = new Location("point A");
//        locationA.setLatitude(latLngA.latitude);
//        locationA.setLongitude(latLngA.longitude);
//        Location locationB = new Location("point B");
//        locationB.setLatitude(latLngB.latitude);
//        locationB.setLongitude(latLngB.longitude);
//        double distanceMeter = locationA.distanceTo(locationB);
//        int meter = (int) distanceMeter;
//        double distance = Double.valueOf(branch.attendanceDis);
//        int dis = (int) distance;
//
//        if (meter > dis) {
//            distanceStatus = "2";
//            Toast.makeText(getContext(), "Location Miss Match", Toast.LENGTH_LONG).show();
//            // return;
//        } else {
//            distanceStatus = "1";
////            Toast.makeText(getContext(), "Attendance Marked Successfully", Toast.LENGTH_LONG).show();
////            mViewModel.navigateTo(Screen.HOME);
//        }

        if (!valid())
            return;
        //final Snackbar progressBar = UtilityMethods.showProgressSnackBar(rootView, "Submitting Attendance ....");
//        submitButton.setEnabled(false);
        String uniqueId = UUID.randomUUID().toString();
      //  Attendance attendance = new Attendance();

        Token tok = mViewModel.getCurrentUser(this.getContext());
        UserInfo userInfo = mViewModel.getUserInfo(getContext());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        //     attendance.employeeId = Integer.parseInt(userInfo.userMasterId);
//        attendance.date = currentDateandTime;
//        //    attendance.companyId = userInfo.companyId;
//        //    attendance.branchId = userInfo.branchId;
//        if (currentLocation != null) {
//            attendance.lat = String.valueOf(currentLocation.getLatitude());
//            attendance.lang = String.valueOf(currentLocation.getLongitude());
//            attendance.location = strCurrentLocation;
//        } else {
//            attendance.lat = "";
//            attendance.lang = "";
//            attendance.location = "";
//        }
//        if (attendance.location.isEmpty()) {
//            Toast.makeText(getContext(), "Address Not Available Please Wait..", Toast.LENGTH_SHORT).show();
//            return;
//        }
        String add = currentAddress.getText().toString();
        if (add.compareTo("") == 0) {
            Toast.makeText(getContext(), "Location is off Please turn on your location", Toast.LENGTH_SHORT).show();
            return;
        }
//        attendance.guid = uniqueId;
//        attendance.checkStatus = status;
        // attendance.addedBy = userInfo.userInfoId;
        //  attendance.time = currentTime;

        progressBar.setVisibility(View.VISIBLE);
        subButton.setEnabled(false);
        checkOutBtn.setEnabled(false);
//        UserFactoryMethods.getUseDataRepository(tok.token).postAttendance(attendance).observe(this, new Observer<Result<ResponseData>>() {
//            @Override
//            public void onChanged(Result<ResponseData> userResult) {
//                String types = type;
//
//                if (types.compareTo("in") == 0) {
//                    types = "IN";
//                } else {
//                    types = "OUT";
//                }
//
//                if (userResult.isSuccess()) {
//                    // saveSession();
//                    //setDialog(attendance);
////                    if (status.compareTo("Check In") == 0) {
////                        subButton.setVisibility(View.GONE);
////                        checkOutBtn.setVisibility(View.VISIBLE);
////                    } else {
////                        checkOutBtn.setVisibility(View.GONE);
////                        subButton.setVisibility(View.VISIBLE);
////                    }
//
//                    getStatus();
//                    subButton.setEnabled(true);
//                    checkOutBtn.setEnabled(true);
//                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(getContext(), types + " Attendance Marked Successfully", Toast.LENGTH_SHORT).show();
//                    //Toast.makeText(getApplicationContext(), "Face added Successfully", Toast.LENGTH_LONG).show();
//                    //  AppLog.updateLogToServer("Logged", "LoggedIn", "LoginActivity", userResult.getData());
//                } else {
////                            String text2 = checkButton.getText().toString();
////                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("btnStatus", text2).apply();
////                            Session session = new Session(getApplicationContext());
////                            session.saveOfflineWeightList(currentDate(), attendance);
////                            setDialog(attendance);
//                    progressBar.setVisibility(View.GONE);
//                    subButton.setEnabled(true);
//                    checkOutBtn.setEnabled(true);
//                    String d = "hh";
//                    Toast.makeText(getContext(), userResult.getErrorMessage(), Toast.LENGTH_SHORT).show();                                  // UtilityMethods.showToast(getApplicationContext(), userResult.getErrorMessage());
//                    // progressBar.setVisibility(View.GONE);
//                }
//            }
//        });
    }


    private boolean valid() {
        if (currentLocation == null) {
            Toast.makeText(getActivity(), "Location is not valid.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (strCurrentLocation == null || strCurrentLocation.isEmpty()) {
            Toast.makeText(getActivity(), "Location required to mark attendance.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void doOnLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
            }
        } else {

        }
    }


    @SuppressLint("MissingPermission")
    private void gnss() {
        GnssStatus.Callback gnssStatusListener = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            gnssStatusListener = new GnssStatus.Callback() {
                @Override
                public void onStarted() {
                    //   Log.d(APP_NAME,"");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            gps_1();
                            // Stuff that updates the UI
                        }
                    });
                }

                @Override
                public void onStopped() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gps_2();
                            // Stuff that updates the UI
                        }
                    });
                    // Toast.makeText(getContext(), "Location is off. Please turn on your location", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSatelliteStatusChanged(GnssStatus status) {
                    // Log.d(APP_NAME,"GPS started"+status.toString());
                }
            };
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationManager.registerGnssStatusCallback(gnssStatusListener);
        }
    }


    private void gps_1() {

        gps_enabled = true;

        if (UtilityMethods.gps_value > 0) {
            Toast.makeText(getContext(), "Location Connected", Toast.LENGTH_SHORT).show();
        }

        if (connected) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getContext(), "Internet Disconnected", Toast.LENGTH_SHORT).show();
        }
    }

    private void gps_2() {

        gps_enabled = false;
        progressBar.setVisibility(View.GONE);
        currentAddress.setText("");

        if (UtilityMethods.gps_value <= 0) {

        } else {

            Toast.makeText(getContext(), "Location Disconnected", Toast.LENGTH_SHORT).show();
        }
        // currentAddress.setText("Location is off. Please turn on your location");
    }

    private void network() {

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                connected = true;
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        gd2();
                        // Stuff that updates the UI

                    }
                });

            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        gd();
                        // Stuff that updates the UI

                    }
                });

            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
                final boolean unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
            }
        };

        ConnectivityManager connectivityManager =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            connectivityManager = (ConnectivityManager) getActivity().getSystemService(ConnectivityManager.class);
        }
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }

    private void gd2() {
        getStatus();
        getBranch();
        Toast.makeText(getContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
        if (gps_enabled) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getContext(), "Location is off. Please turn on your location", Toast.LENGTH_SHORT).show();
        }
    }

    private void gd() {
        progressBar.setVisibility(View.GONE);
        connected = false;
        Toast.makeText(getContext(), "Internet Disconnected", Toast.LENGTH_SHORT).show();
        currentAddress.setText("");
    }


}