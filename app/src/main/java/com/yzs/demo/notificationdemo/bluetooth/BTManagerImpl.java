package com.yzs.demo.notificationdemo.bluetooth;

import android.app.Activity;

public class BTManagerImpl implements BTManager {

    private BTManagerImpl() {
    }

    private static class SignalHolder{
        private final static BTManagerImpl instance = new BTManagerImpl();
    }

    public static BTManager getInstance() {
        return SignalHolder.instance;
    }

    @Override
    public boolean initBT(Activity activity, int code) {
        return false;
    }

    @Override
    public void startBTScan() {

    }

    @Override
    public void stopBTScan() {

    }

    @Override
    public void startBLEScan() {

    }

    @Override
    public void stopBLEScan() {

    }

    @Override
    public void openDiscovery() {

    }

    @Override
    public void sendAdvister() {

    }
}
