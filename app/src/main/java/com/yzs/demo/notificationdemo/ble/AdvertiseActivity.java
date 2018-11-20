package com.yzs.demo.notificationdemo.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yzs.demo.notificationdemo.R;
import com.yzs.demo.notificationdemo.ble.advertis.Advertise;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AdvertiseActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Advertise mAdvertise;

    private SwitchCompat mAdvertiseSwitch;
    private RadioGroup mRadioGroup;

    @SuppressLint("LogNotTimber")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise);
        mAdvertiseSwitch = findViewById(R.id.switch_advertise);
        mAdvertiseSwitch.setOnCheckedChangeListener(this);
        initSettings();
        TextView textView = findViewById(R.id.textview);
        String address = getBtAddressByReflection();
        Log.i("yzs", "address:" + address);
        textView.setText("address:" + address);
    }

    private void initSettings() {
        enableBluetooth();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            startAdvertise();
        } else {
            stopAdvertise();
        }
    }

    public RadioGroup getRadioGroup() {
        if (mRadioGroup == null) {
            mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        }

        return mRadioGroup;
    }

    private String getCheckedButtonText() {
        TextView textView = (TextView) findViewById(getRadioGroup().getCheckedRadioButtonId());
        return textView.getText().toString();
    }

    private void startAdvertise() {
        if (mAdvertise == null && getBluetoothAdapter() != null && getBluetoothAdapter().isEnabled()) {
            mAdvertise = new Advertise(getCheckedButtonText());
            mAdvertise.startAdvertise(this);

            Toast.makeText(this, "START", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "START FAILED", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAdvertise() {
        if (mAdvertise != null && getBluetoothAdapter() != null && getBluetoothAdapter().isEnabled()) {
            mAdvertise.stopAdvertise();
            mAdvertise = null;

            Toast.makeText(this, "STOP", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "STOP FAILED", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Bluetoothの有効化
     */
    private void enableBluetooth() {
        if (getBluetoothAdapter() == null) {
            return;
        }

        if (!getBluetoothAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0x101);
        }
    }

    private BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }


    public static String getBtAddressByReflection() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Field field = null;
        try {
            field = BluetoothAdapter.class.getDeclaredField("mService");
            field.setAccessible(true);
            Object bluetoothManagerService = field.get(bluetoothAdapter);
            if (bluetoothManagerService == null) {
                return null;
            }
            Method method = bluetoothManagerService.getClass().getMethod("getAddress");
            if (method != null) {
                Object obj = method.invoke(bluetoothManagerService);
                if (obj != null) {
                    return obj.toString();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
