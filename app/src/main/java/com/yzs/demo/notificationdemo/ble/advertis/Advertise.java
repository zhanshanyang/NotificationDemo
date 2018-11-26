package com.yzs.demo.notificationdemo.ble.advertis;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.Arrays;
import java.util.UUID;

public class Advertise extends AdvertiseCallback {

    //UUID
    public static final String SERVICE_UUID = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_UUID = "00002a29-0000-1000-8000-00805f9b34fb";

    //アドバタイズの設定
    private static final boolean CONNECTABLE = true;
    private static final int TIMEOUT = 0;

    //BLE
    private BluetoothLeAdvertiser bleAdvertiser;
    private BluetoothGattServer bluetoothGattServer;

    private static final String TAG = "AdvertiseClass";

    private String advertiseKey;

    public Advertise(String advertiseKey) {
        this.advertiseKey = advertiseKey;
    }

    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

        //セントラル（クライアント）からReadRequestが来ると呼ばれる
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId,
                                                int offset, BluetoothGattCharacteristic characteristic) {

            //クライアントにキー名を送信
            setCharacteristicValue(characteristic, offset, advertiseKey.getBytes());
            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                    characteristic.getValue());
        }

        //セントラル（クライアント）からWriteRequestが来ると呼ばれる
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                                 BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded,
                                                 int offset, byte[] value) {
            //セントラルにnullを返信する
            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
        }

        /**
         * 文字列を分割する
         *
         * @param original
         * @param offset
         * @return
         */
        private byte[] extractAry(byte[] original, int offset) {
            return Arrays.copyOfRange(original, offset, original.length);
        }

        /**
         * Characteristicに値をセットする
         *
         * @param characteristic
         * @param offset
         * @param values
         */
        private void setCharacteristicValue(BluetoothGattCharacteristic characteristic,
                                            int offset, byte[] values) {
            characteristic.setValue(extractAry(values, offset));
        }
    };

    //アドバタイズを開始
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertise(Context context) {

        //BLE各種を取得
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        bleAdvertiser = getAdvertiser(adapter);

        //BluetoothGattServer関連の初期化
        bluetoothGattServer = getGattServer(context, manager);

        //UUIDを設定
        setUuid();

        //アドバタイズを開始
        bleAdvertiser.startAdvertising(makeAdvertiseSetting(), makeAdvertiseData(), this);
    }

    //アドバタイズを停止
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopAdvertise() {

        //サーバーを閉じる
        if (bluetoothGattServer != null) {
            bluetoothGattServer.clearServices();
            bluetoothGattServer.close();
            bluetoothGattServer = null;
        }

        //アドバタイズを停止
        if (bleAdvertiser != null) {
            bleAdvertiser.stopAdvertising(this);
            bleAdvertiser = null;
        }
    }

    //Advertiserを取得
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BluetoothLeAdvertiser getAdvertiser(BluetoothAdapter adapter) {
        if (bleAdvertiser == null) {
            bleAdvertiser = adapter.getBluetoothLeAdvertiser();
        }

        return bleAdvertiser;
    }

    //GattServerを取得
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothGattServer getGattServer(Context context, BluetoothManager manager) {
        if (bluetoothGattServer == null) {
            bluetoothGattServer = manager.openGattServer(context, mGattServerCallback);
        }

        return bluetoothGattServer;
    }

    //UUIDを設定
    private void setUuid() {
        //serviceUUIDを設定
        BluetoothGattService service = new BluetoothGattService(
                UUID.fromString(SERVICE_UUID),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        //characteristicUUIDを設定
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(CHAR_UUID),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);

        //characteristicUUIDをserviceUUIDにのせる
        service.addCharacteristic(characteristic);


        //serviceUUIDをサーバーにのせる
        bluetoothGattServer.addService(service);
    }

    //アドバタイズを設定
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private AdvertiseSettings makeAdvertiseSetting() {

        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();

        //アドバタイズモード
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        //アドバタイズパワー
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW);
        //ペリフェラルへの接続を許可する
        builder.setConnectable(CONNECTABLE);
        //調査中。。
        builder.setTimeout(TIMEOUT);

        return builder.build();
    }

    //アドバタイズデータを作成
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private AdvertiseData makeAdvertiseData() {
//
//        AdvertiseData.Builder builder = new AdvertiseData.Builder();
//        builder.addServiceUuid(new ParcelUuid(UUID.fromString(SERVICE_UUID)));
//        builder.addServiceData(ParcelUuid.fromString());


        AdvertiseData.Builder mDataBuilder = new AdvertiseData.Builder();
        // mDataBuilder.addServiceUuid(ParcelUuid.fromString(HEART_RATE_SERVICE));
        mDataBuilder.setIncludeDeviceName(true);
        //添加的数据
        mDataBuilder.addServiceData(ParcelUuid.fromString(SERVICE_UUID), "eeeeeeeeee".getBytes());
        AdvertiseData advertiseData = mDataBuilder.build();
        if (advertiseData == null) {
            Log.e(TAG, "mAdvertiseSettings == null");
        }


        return advertiseData;
    }
}
