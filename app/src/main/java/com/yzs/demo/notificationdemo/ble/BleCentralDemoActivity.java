package com.yzs.demo.notificationdemo.ble;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yzs.demo.notificationdemo.R;
import com.yzs.demo.notificationdemo.ble.adapter.StringArrayAdapter;
import com.yzs.demo.notificationdemo.bluetooth.BTManager;
import com.yzs.demo.notificationdemo.bluetooth.BTManagerImpl;
import com.yzs.demo.notificationdemo.utils.PermissionUtils;

import java.util.HashMap;
import java.util.Map;

public class BleCentralDemoActivity extends AppCompatActivity {

    private static final String TAG = "BleCentralDemoActivity";
    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int REQUEST_PERMISSIONS_CODE = 0;
    private static final int REQUEST_BT_ENABLE_CODE = 1;

    private static final long SCAN_BLE_DURATION = 10000;
    private static final int MSG_START_SCAN_WHAT = 0;
    private static final int MSG_STOP_SCAN_WHAT = 1;

    private TextView mTvTips;
    private Button mBtnStartScan;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private StringArrayAdapter mArrayAdapter;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_SCAN_WHAT:
                    isScanning = true;
                    handleStartBleScan();
                    break;
                case MSG_STOP_SCAN_WHAT:
                    isScanning = false;
                    handleStopBleScan();
                    break;
                default:
                    break;
            }

            return true;
        }
    });

    private void handleStartBleScan() {
        Log.i(TAG, "handleStartBleScan is run.");
        mBtnStartScan.setText(R.string.stop_ble_scan_str);
        mProgressBar.setVisibility(View.VISIBLE);
        getBtManager().startBLEScan(mScanCallback);
    }

    private void handleStopBleScan() {
        Log.i(TAG, "handleStopBleScan is run.");
        mBtnStartScan.setText(R.string.start_ble_scan_str);
        mProgressBar.setVisibility(View.INVISIBLE);
        getBtManager().stopBLEScan(mScanCallback);
    }

    private Map<String, BluetoothDevice> mBeaconDevicesMap = new HashMap<>();

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(TAG, "ScanCallBack onScanResult is run.type:" + callbackType + ",result:" + result.toString());
            BluetoothDevice bluetoothDevice = result.getDevice();
            if (bluetoothDevice != null) {
                mBeaconDevicesMap.put(bluetoothDevice.getAddress(), bluetoothDevice);
                mArrayAdapter.updateData(mBeaconDevicesMap);

//                result.getScanRecord();
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.i(TAG, "ScanCallBack onScanFailed is run.errorCode:" + errorCode);
        }
    };


    private BTManager getBtManager() {
        return BTManagerImpl.getInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_central_demo);
        initView();
        checkPermissions();
    }

    private void checkPermissions() {
        boolean flag = PermissionUtils.requestPermission(this, permissions, REQUEST_PERMISSIONS_CODE);
        if (flag) {
            if (!getBtManager().initBT(this)) {
                getBtManager().enableBT(this, REQUEST_BT_ENABLE_CODE);
            }
        }
    }

    private void initView() {
        mTvTips = findViewById(R.id.tv_tips);
        mBtnStartScan = findViewById(R.id.btn_start_scan);
        mBtnStartScan.setOnClickListener(v -> {
            startBleScan();
        });
        mProgressBar = findViewById(R.id.progress);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mArrayAdapter = new StringArrayAdapter(this);
        mRecyclerView.setAdapter(mArrayAdapter);
    }

    private boolean isScanning = false;

    private void startBleScan() {
        if (!isScanning) {
            mHandler.sendEmptyMessage(MSG_START_SCAN_WHAT);
            mHandler.sendEmptyMessageDelayed(MSG_STOP_SCAN_WHAT, SCAN_BLE_DURATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                    if (showRequestPermission) {
                        Log.i(TAG,"request permission result. 权限未申请");
                    }
                }
            }
            Log.i(TAG,"request permission result. ");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BT_ENABLE_CODE && resultCode == RESULT_OK) {
            getBtManager().initBT(this);
        }
    }
}
