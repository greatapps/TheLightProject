package com.app.rightbulb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rightbulb.Prefs.Prefs;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.ActivatorModelEnum;

public class DeviceConnectionActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String ssid,Token,passowrd;
    TextView ssidView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_connection);
        Intent intent = getIntent();
        Long homeId= intent.getLongExtra("HomeID",0);
        ssidView = findViewById(R.id.ssid);
        TuyaHomeSdk.getActivatorInstance().getActivatorToken(homeId, new ITuyaActivatorGetToken() {
            @Override
            public void onSuccess(String token) {
                Toast.makeText(DeviceConnectionActivity.this, ""+token, Toast.LENGTH_SHORT).show();
                Log.d("Token", "onSuccess: "+token);
                Prefs.setToken(DeviceConnectionActivity.this,token);
                Token = token;
//                startActivity(new Intent(DeviceConnectionActivity.this,DashboardActivity.class));
                finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMsg) {
                Log.d("Token", "onFailure: "+errorCode+"  "+errorMsg);
            }
        });
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;

        wifiInfo = wifiManager.getConnectionInfo();
        if (((WifiInfo) wifiInfo).getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID();
            ssidView.setText(ssid);
        }

    }

    public void connectDevice(View view) {
        ActivatorBuilder builder = new ActivatorBuilder().setSsid(ssid).setPassword(passowrd).setContext(this).setActivatorModel(ActivatorModelEnum.TY_EZ)
                .setTimeOut(100)
                .setToken(Token)
                .setListener(new ITuyaSmartActivatorListener() {
                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        Toast.makeText(DeviceConnectionActivity.this, ""+errorMsg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onActiveSuccess(DeviceBean devResp) {
                        Toast.makeText(DeviceConnectionActivity.this, ""+devResp.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStep(String step, Object data) {
                        Toast.makeText(DeviceConnectionActivity.this, step, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void skip(View view) {
    }
}
