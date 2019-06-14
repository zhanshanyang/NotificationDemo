package com.yzs.demo.notificationdemo;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ButtonActivity extends AppCompatActivity {
    private static final String SHOW_STATUSBAR = "action.show.statusbar";
    private static final String HIDE_STATUSBAR = "action.hide.statusbar";
    private static final String SHOW_SUB_STATUSBAR = "action.show.sub.statusbar";
    private static final String HIDE_SUB_STATUSBAR = "action.hide.sub.statusbar";
    private static final String TAG_SOURCE_APP = "SourceAppTAG";

    private ShortcutManager mShortcutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShortcutManager = getSystemService(ShortcutManager.class);
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
            case R.id.btn_add:
                createShortcut();
                break;
            case R.id.btn_remove:
                mShortcutManager.removeAllDynamicShortcuts();
                break;
        }
        if (!TextUtils.isEmpty(intent.getAction())) {
            sendBroadcast(intent);
        }
    }


    private static final String ACTION_LIST1 = "com.yzs.demo.shortcut.action1";
    private void createShortcut() {
        Intent intent = new Intent(ACTION_LIST1);
        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(this, String.valueOf(SystemClock.currentThreadTimeMillis()))
                .setShortLabel("新增" + SystemClock.uptimeMillis())
                .setIntent(intent)
                .build();
        List<ShortcutInfo> shortcutInfos = new ArrayList<>();
        shortcutInfos.add(shortcutInfo);
        mShortcutManager.addDynamicShortcuts(shortcutInfos);
    }

}
