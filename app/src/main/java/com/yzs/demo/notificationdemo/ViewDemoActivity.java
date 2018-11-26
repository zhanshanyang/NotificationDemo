package com.yzs.demo.notificationdemo;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ViewDemoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.btn_add_shortcut).setOnClickListener(this);
        findViewById(R.id.btn_remove_shortcut).setOnClickListener(this);
        findViewById(R.id.btn_auto_start_change).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_shortcut) {
            addOneShortCut();
        } else if (v.getId() == R.id.btn_remove_shortcut) {
            removeOneShortCut();
        } else if (v.getId() == R.id.btn_auto_start_change) {
            changeShortCut();
        }

    }

    private static final int ADD_WHAT = 0;
    private static final int REMOVE_WHAT = 1;
    private static final int DELAY_DURATION = 2000;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == ADD_WHAT) {
                addOneShortCut();
                handler.sendEmptyMessageDelayed(REMOVE_WHAT, DELAY_DURATION);
            } else if (msg.what == REMOVE_WHAT) {
                removeOneShortCut();
                handler.sendEmptyMessageDelayed(ADD_WHAT, DELAY_DURATION);
            }
            return true;
        }
    });

    private boolean isChanging = false;
    private void changeShortCut() {
        isChanging = !isChanging;
        if (isChanging) {
            handler.sendEmptyMessageDelayed(ADD_WHAT, DELAY_DURATION);
        } else {
            handler.removeMessages(ADD_WHAT);
            handler.removeMessages(REMOVE_WHAT);
        }
    }

    private String shortCutId = "viewdemo_id";

    private void addOneShortCut() {
        Log.i("ViewDemo", "addOneShortCut is run.");
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        Intent intent = new Intent(this, ViewDemoActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        ShortcutInfo info = new ShortcutInfo.Builder(this, shortCutId)
                .setIcon(Icon.createWithResource(this, R.drawable.ic_card_user_default))
                .setShortLabel("新添加的ShortCut")
                .setLongLabel("add a new shortCut.long")
//                .setExtras()
                .setIntent(intent)
                .build();
        List<ShortcutInfo> infos = new ArrayList<>();
        infos.add(info);
        if (shortcutManager != null)
            shortcutManager.addDynamicShortcuts(infos);

    }

    private void removeOneShortCut() {
        Log.i("ViewDemo", "removeOneShortCut is run.");
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        if (shortcutManager != null) {
            List<String> ids = new ArrayList<>();
            ids.add(shortCutId);
            shortcutManager.removeDynamicShortcuts(ids);
        }
    }
}
