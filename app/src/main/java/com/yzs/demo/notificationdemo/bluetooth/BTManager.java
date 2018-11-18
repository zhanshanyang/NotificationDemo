package com.yzs.demo.notificationdemo.bluetooth;

import android.app.Activity;
import android.bluetooth.le.ScanCallback;
import android.content.Context;

public interface BTManager {

    /**
     * init manager and adapter.
     *
     * check bluetooth is enable?
     *
     * @param context this
     * @return true:finish init.   false:need open bluetooth devices.
     */
    boolean initBT(Context context);

    void enableBT(Activity activity, int requestCode);

    /**
     * 开始经典蓝牙扫描
     */
    void startBTScan();

    /**
     * 停止经典蓝牙扫描
     */
    void stopBTScan();

    /**
     * 开始ble的扫描
     */
    void startBLEScan(ScanCallback scanCallback);

    /**
     * 结束ble的扫描
     */
    void stopBLEScan(ScanCallback scanCallback);

    /**
     * 打开发现
     */
    void openDiscovery();

    /**
     * 发送广播
     */
    void sendAdvertise();

}
