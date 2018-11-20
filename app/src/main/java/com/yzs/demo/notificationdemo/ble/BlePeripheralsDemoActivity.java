package com.yzs.demo.notificationdemo.ble;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yzs.demo.notificationdemo.R;
import com.yzs.demo.notificationdemo.bluetooth.BTManager;
import com.yzs.demo.notificationdemo.bluetooth.BTManagerImpl;


public class BlePeripheralsDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BlePeripheralsDemo";

    private static final int REQUEST_BT_ENABLE_CODE = 1;
    private TextView mTvTips;
    private Button mBtnStartAdvertise;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_peripherals_demo);
        initViews();
    }

    private void initViews() {
        mTvTips = findViewById(R.id.tv_tips);
        mBtnStartAdvertise = findViewById(R.id.btn_start_advertise);
        mProgress = findViewById(R.id.progress);
        mBtnStartAdvertise.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!getBtManager().initBT(this)) {
            getBtManager().enableBT(this, REQUEST_BT_ENABLE_CODE);
            return;
        }
        Log.i(TAG, "button onclick is run.add:" + getBtManager().getAddress());
        getBtManager().startAdvertising(mAdvertiseCallback);
    }

    AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "AdvertiseCallback's onStartSuccess is run.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.i(TAG, "AdvertiseCallback's onStartFailure is run.");
        }
    };

    private BTManager getBtManager() {
        return BTManagerImpl.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BT_ENABLE_CODE && resultCode == RESULT_OK) {
            getBtManager().initBT(this);
        }
    }
}
