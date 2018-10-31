package com.yzs.demo.notificationdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class BTDemo1Activity extends AppCompatActivity {

    private static final String TAG = "BTDemo1Activity";

    private static final int REQUEST_ENABLE_BT = 10;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    private String[] permissions = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_demo1);

        checkPermission();
        initBt();
    }

    private void checkPermission() {
//        ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION
//        for ()

    }

    private void initBt() {
        //get manager
        mBluetoothManager = getBtManager();
        if (mBluetoothManager != null) {
            //get bluetooth adapter
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            //judge bluetooth is enable?
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent eblIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(eblIntent, REQUEST_ENABLE_BT);
                return;
            }
            startScanDevices();
        }
    }

    private void startScanDevices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //2. Android 5.0以上，扫描的结果在mScanCallback中进行处理
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mBluetoothLeScanner.startScan(mScanCallback);
//            mBluetoothLeScanner.startScan();
            Log.i(TAG, "startScanDevices is run.");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //1. Android 4.3以上，Android 5.0以下
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    private BluetoothGatt mBluetoothGatt;
    //new api
    ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.i(TAG, "ScanResult:" + result.toString());

            BluetoothDevice bluetoothDevice = result.getDevice();
            bluetoothDevice.getAddress();
            mBluetoothGatt = bluetoothDevice.connectGatt(BTDemo1Activity.this, true, bluetoothGattCallback);

//            bluetoothGatt.discoverServices();

            //RSSI的值作为对远程蓝牙设备的报告; 0代表没有蓝牙设备;
            result.getRssi();
            //远程设备提供的配对号(公告)
            result.getScanRecord();

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i(TAG, "results's size:" + results.size());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i(TAG, "errorCode:" + errorCode);
        }
    };

    private void writeBTCharacteristic(BluetoothGattCharacteristic characteristic) {
        mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        characteristic.setValue("Write temp String.");
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        //连接状态改变的回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 连接成功后启动服务发现
                Log.e("AAAAAAAA", "启动服务发现:" + mBluetoothGatt.discoverServices());
            }
        }

        //发现服务的回调
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "成功发现服务");
            }else{
                Log.e(TAG, "服务发现失败，错误码为:" + status);
            }
        }

        //写操作的回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "写入成功" + characteristic.getValue());
            }
        }

        //读操作的回调
//        gatt.readCharacteristic(characteristic)
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "读取成功" + characteristic.getValue());
            }
        }

        //数据返回的回调（此处接收BLE设备返回数据）
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

        }
    };



    //old api
    BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.i(TAG, "LeScanCallback device's name:" + device.getName() + ",device's address:" + device.getAddress());
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            initBt();
        }
    }

    private BluetoothManager getBtManager() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        }
        return mBluetoothManager;
    }
}
