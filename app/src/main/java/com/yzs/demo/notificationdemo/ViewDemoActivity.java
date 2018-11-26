package com.yzs.demo.notificationdemo;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_shortcut) {
            addOneShortCut();
        } else if (v.getId() == R.id.btn_remove_shortcut) {
            removeONeShortCut();
        }

    }

    private String shortCutId = "viewdemo_id";

    private void addOneShortCut() {
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

    private void removeONeShortCut() {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        if (shortcutManager != null) {
            List<String> ids = new ArrayList<>();
            ids.add(shortCutId);
            shortcutManager.removeDynamicShortcuts(ids);
        }
    }
}
