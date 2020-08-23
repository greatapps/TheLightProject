package com.app.rightbulb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rightbulb.Family.FamilySpHelper;
import com.app.rightbulb.Family.event.EventCurrentHomeChange;
import com.app.rightbulb.Maps.LocationTrack;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.api.ITuyaHomeChangeListener;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.bean.GroupBean;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeCreation_Activity extends AppCompatActivity {

    TextView userID;
    private static volatile HomeCreation_Activity instance;
    private HomeBean currentHomeBean;
    LocationManager locationManager;
    LatLng location;
    EditText homeName;
    private FamilySpHelper mFamilySpHelper;
    String cityName;
    Double lat,longitude;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    List<String> rooms = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_home_creation_);

        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            isgpsEnabled(this);
        }
        LocationTrack locationTrack= new LocationTrack(this);
        lat=locationTrack.getLatitude();
        longitude=locationTrack.getLongitude();
        TuyaHomeSdk.getHomeManagerInstance();
        userID=findViewById(R.id.userID);
        homeName = findViewById(R.id.homeName);
        rooms.add("BedRoom");
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                lat=location.getLatitude();
//                longitude=location.getLongitude();
//            }
//        });
        Intent intent= getIntent();
        String uid =intent.getStringExtra("userID");
//        userID.setText(uid);
        getCityName();
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> homeBeans) {
//                Toast.makeText(HomeCreation_Activity.this, ""+homeBeans, Toast.LENGTH_SHORT).show();
                Log.d("Home_Created", "onSuccess: "+homeBeans);
//                getCurrentHome();
            }

            @Override
            public void onError(String errorCode, String error) {

            }
        });


    }
    public void setCurrentHome(HomeBean homeBean) {
        if (null == homeBean) {
            return;
        }
        boolean isChange = false;

        if (null == currentHomeBean) {
            Log.i("HomeBean", "setCurrentHome  currentHome is null so push current home change event");
            isChange = true;
        } else {
            long currentHomeId = currentHomeBean.getHomeId();
            long targetHomeId = homeBean.getHomeId();
            Log.i("HomeBean", "setCurrentHome: currentHomeId=" + currentHomeId + " targetHomeId=" + targetHomeId);
            if (currentHomeId != targetHomeId) {
                isChange = true;
            }
        }
        //Update memory and sp
        currentHomeBean = homeBean;
        //mFamilySpHelper.putCurrentHome(currentHomeBean);
        if (isChange) {
            EventBus.getDefault().post(new EventCurrentHomeChange(currentHomeBean));
        }
    }
    public void getCityName()
    {
        Geocoder geocoder =new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(46.8565177,-71.4817746,1);
            cityName =addresses.get(0).getLocality();
            Toast.makeText(this, ""+cityName, Toast.LENGTH_SHORT).show();
            Log.d("Location", "onCreate: "+cityName);
//            userID.setText(cityName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void checkFields(View view)
    {
        if(homeName.getText().toString().isEmpty())
        {
            homeName.requestFocus();
            homeName.setError("field Can not be empty");
        }
        else{
            createHome();
        }
    }
    public void createHome()
    {
        String home = homeName.getText().toString().trim();
        TuyaHomeSdk.getHomeManagerInstance().createHome(home, 46.8565177, -71.4817746, cityName, rooms, new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
//                setCurrentHome(homeBean);
//                callback.onSuccess(homeBean);
                Toast.makeText(HomeCreation_Activity.this, "Home Created"+bean.getHomeStatus(), Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(HomeCreation_Activity.this,DeviceConnectionActivity.class);
                intent.putExtra("HomeID",bean.getHomeId());
                startActivity(intent);
                finish();
                TuyaHomeSdk.newHomeInstance(bean.getHomeId()).getHomeDetail(new ITuyaHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean bean) {
                        Log.d("Homedetails", "onSuccess: "+bean.getGroupList());
                        List<String> devID = new ArrayList<>();
                        TuyaHomeSdk.newHomeInstance(bean.getHomeId()).createGroup("", "Family", devID, new ITuyaResultCallback<Long>() {
                            @Override
                            public void onSuccess(Long result) {
                                Log.d("Group", "onSuccess Group: "+result.longValue());
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {

                            }
                        });

                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {

                    }
                });
                TuyaHomeSdk.getHomeManagerInstance().registerTuyaHomeChangeListener(new ITuyaHomeChangeListener() {
                    @Override
                    public void onHomeAdded(long homeId) {
                        Log.d("HomeAdded", "onHomeAdded: ");
                    }

                    @Override
                    public void onHomeInvite(long homeId, String homeName) {

                    }

                    @Override
                    public void onHomeRemoved(long homeId) {

                    }

                    @Override
                    public void onHomeInfoChanged(long homeId) {

                    }

                    @Override
                    public void onSharedDeviceList(List<DeviceBean> sharedDeviceList) {

                    }

                    @Override
                    public void onSharedGroupList(List<GroupBean> sharedGroupList) {

                    }

                    @Override
                    public void onServerConnectSuccess() {
                        Log.d("Server", "onServerConnectSuccess: ");
                    }
                });

            }

            @Override
            public void onError(String errorCode, String errorMsg) {

            }
        });
    }
    public HomeBean getCurrentHome() {
        if (null == currentHomeBean) {
            setCurrentHome(mFamilySpHelper.getCurrentHome());
        }
        return currentHomeBean;
    }
    public void isgpsEnabled(final Context context)
    {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(context)
                    .setTitle("Location Disabled")  // GPS not found
                    .setMessage("Enable Location") // Want to enable?
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        }
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

    }


}
