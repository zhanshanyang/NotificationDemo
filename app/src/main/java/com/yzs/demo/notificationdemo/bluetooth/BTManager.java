package com.yzs.demo.notificationdemo.bluetooth;

import android.app.Activity;

public interface BTManager {

    /**
     * init manager and adapter.
     *
     * check bluetooth is enable?
     *
     * @param activity this
     * @param code request's code
     * @return true:finish init.   false:need open bluetooth devices.
     */
    boolean initBT(Activity activity, int code);

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
    void startBLEScan();

    /**
     * 结束ble的扫描
     */
    void stopBLEScan();

    /**
     * 打开发现
     */
    void openDiscovery();

    /**
     * 发送广播
     */
    void sendAdvister();

}
