package com.yzs.demo.notificationdemo.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import static android.bluetooth.le.AdvertiseSettings.ADVERTISE_MODE_LOW_POWER;
import static android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_LOW;

public class BTManagerImpl implements BTManager {

    private static final String TAG = "BTManagerImpl";
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private BTManagerImpl() {
    }

    private static class SignalHolder {
        private final static BTManagerImpl instance = new BTManagerImpl();
    }

    public static BTManager getInstance() {
        return SignalHolder.instance;
    }

    @Override
    public boolean initBT(Context context) {
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager != null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    @Override
    public String getAddress() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.getAddress();
        }
        return "";
    }

    @Override
    public void enableBT(Activity activity, int requestCode) {
        Intent eblIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(eblIntent, requestCode);
    }

    @Override
    public void startBTScan() {

    }

    @Override
    public void stopBTScan() {

    }

    @Override
    public void startBLEScan(ScanCallback scanCallback) {
        if (mBluetoothAdapter == null) return;
        //android 5.0以上接口
        BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.startScan(scanCallback);
    }

    @Override
    public void stopBLEScan(ScanCallback scanCallback) {
        if (mBluetoothAdapter == null) return;
        BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.stopScan(scanCallback);
    }

    @Override
    public void openDiscovery() {

    }

    @Override
    public void startAdvertising(AdvertiseCallback callback) {
        BluetoothLeAdvertiser bluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (bluetoothLeAdvertiser != null) {
            bluetoothLeAdvertiser.startAdvertising(createAdvertiseSettings(),
                    createAdvertiseData(),
                    createScanResponse(),
                    callback);
        }
    }

    @Override
    public void stopAdvertising(AdvertiseCallback callback) {
        BluetoothLeAdvertiser bluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (bluetoothLeAdvertiser != null) {
            bluetoothLeAdvertiser.stopAdvertising(callback);
        }
    }

    private AdvertiseSettings createAdvertiseSettings() {
        return new AdvertiseSettings.Builder()
                .setAdvertiseMode(ADVERTISE_MODE_LOW_POWER)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(ADVERTISE_TX_POWER_LOW)
                .build();
    }

    /** create AdvertiseSettings */
    public static AdvertiseSettings createAdvSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder mSettingsbuilder = new AdvertiseSettings.Builder();
        mSettingsbuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        mSettingsbuilder.setConnectable(connectable);
        mSettingsbuilder.setTimeout(timeoutMillis);
        mSettingsbuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        AdvertiseSettings mAdvertiseSettings = mSettingsbuilder.build();
        if(mAdvertiseSettings == null){
            Log.e(TAG,"mAdvertiseSettings == null");
        }
        return mAdvertiseSettings;
    }



    private AdvertiseData createAdvertiseData() {
        return new AdvertiseData.Builder()
//                .addServiceUuid()
//                .addServiceData()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(true)
//                .addManufacturerData()
                .build();
    }

    private AdvertiseData createScanResponse() {

        return new AdvertiseData.Builder().build();
    }

}
