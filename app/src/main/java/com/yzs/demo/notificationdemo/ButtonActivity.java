package com.yzs.demo.notificationdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

public class ButtonActivity extends AppCompatActivity {
    private static final String SHOW_STATUSBAR = "action.show.statusbar";
    private static final String HIDE_STATUSBAR = "action.hide.statusbar";
    private static final String SHOW_SUB_STATUSBAR = "action.show.sub.statusbar";
    private static final String HIDE_SUB_STATUSBAR = "action.hide.sub.statusbar";
    private static final String TAG_SOURCE_APP = "SourceAppTAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);

    }

    public void buttonClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.btn1:
                intent.setAction(SHOW_STATUSBAR);
                intent.putExtra(TAG_SOURCE_APP, "Button1");
                break;
            case R.id.btn2:
                intent.setAction(HIDE_STATUSBAR);
                intent.putExtra(TAG_SOURCE_APP, "Button2");
                break;
            case R.id.btn3:
                intent.setAction(SHOW_SUB_STATUSBAR);
                intent.putExtra(TAG_SOURCE_APP, "Button3");
                break;
            case R.id.btn4:
                intent.setAction(HIDE_SUB_STATUSBAR);
                intent.putExtra(TAG_SOURCE_APP, "Button4");
                break;
        }
        if (!TextUtils.isEmpty(intent.getAction())) {
            sendBroadcast(intent);
        }
    }
}
