package com.yzs.demo.notificationdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yzs.demo.notificationdemo.utils.ScreenUtils;

public class ViewDragDemoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drag_demo);
        findViewById(R.id.textview1).setOnClickListener(this);
        findViewById(R.id.textview2).setOnClickListener(this);
        findViewById(R.id.textview3).setOnClickListener(this);

        ScreenUtils.setStatusBarTranslucent(this);
        ScreenUtils.hideActionbar(this);
    }


    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            String text = (String) ((TextView) v).getText();
            Toast.makeText(this, "text:" + text, Toast.LENGTH_SHORT).show();
        }
    }
}
