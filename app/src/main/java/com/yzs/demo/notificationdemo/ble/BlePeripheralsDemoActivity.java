package com.yzs.demo.notificationdemo.ble;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yzs.demo.notificationdemo.R;
import com.yzs.demo.notificationdemo.bluetooth.BTManager;
import com.yzs.demo.notificationdemo.bluetooth.BTManagerImpl;


public class BlePeripheralsDemoActivity extends AppCompatActivity implements View.OnClickListener {

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
//        RecyclerView recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//
//        recyclerView.setAdapter();
        mBtnStartAdvertise.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!getBtManager().initBT(this)) {
            getBtManager().enableBT(this, REQUEST_BT_ENABLE_CODE);
        }
        getBtManager().sendAdvertise();
    }

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