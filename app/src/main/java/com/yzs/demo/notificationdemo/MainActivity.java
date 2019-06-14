package com.yzs.demo.notificationdemo;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yzs.demo.notificationdemo.ble.AdvertiseActivity;
import com.yzs.demo.notificationdemo.ble.BleCentralDemoActivity;
import com.yzs.demo.notificationdemo.bluetooth.BTDemo1Activity;
import com.yzs.demo.notificationdemo.notifications.NotificationsReceiveActivity;
import com.yzs.demo.notificationdemo.service.ForegroundServiceDemo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    private static final String TAG = "MainActivity";

    private static final int NULL_DEMO_ITEM = 0;
    private static final int NOTIFICATION_DEMO_ITEM = NULL_DEMO_ITEM;
    private static final int VIEW_DEMO_ITEM = NULL_DEMO_ITEM + 1;
    private static final int LOTTIEANIMVIEWS_ITEM = NULL_DEMO_ITEM + 2;
    private static final int RECYCLER_VIEW_ITEM = NULL_DEMO_ITEM + 3;
    private static final int REQUEST_PERMISSION_ITEM = NULL_DEMO_ITEM + 4;
    private static final int BLUETOOTH_ITEM = NULL_DEMO_ITEM + 5;
    private static final int CONTENTPROVIDER_DEMO_ITEM = NULL_DEMO_ITEM + 6;
    private static final int NOTIFICATION_APP_DEMO_ITEM = NULL_DEMO_ITEM + 7;
    private static final int BLE_CENTRAL_DEMO_ITEM = NULL_DEMO_ITEM + 8;
    private static final int BLE_PERIPHERALS_DEMO_ITEM = NULL_DEMO_ITEM + 9;
    private static final int CONTROLLER_PANEL_DEMO_ITEM = NULL_DEMO_ITEM + 10;
    private static final int FULL_SCREEN_DEMO_ITEM = NULL_DEMO_ITEM + 11;
    private static final int VIEW_DRAG_DEMO_ITEM = NULL_DEMO_ITEM + 12;
    private static final int PIP_DEMO_ITEM = NULL_DEMO_ITEM + 13;
    private static final int BUTTON_DEMO_ITEM = NULL_DEMO_ITEM + 14;

    private static final int none = 1;
    private static final int floating = 1 << 1;
    private static final int headsup = 1 << 2;
    private static final int all = 1 << 3;
    private String[] data = new String[]{"Notification 样式", "View Demo样式", "LottieAnimViews Demo",
            "RecyclerViewDemo", "RequestPermission Demo", "Bluetooth Demo",
            "ContentProviderDemo", "NotificationAppDemo", "Ble Central Demo",
            "Ble Peripherals Demo", "Controller Panel Demo", "FullScreenActivity",
            "ViewDragDemoActivity", "Pip Demo", "Button Demo"
    };

    private ShortcutManager mShortcutManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate is run.");
        mShortcutManager = getSystemService(ShortcutManager.class);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, data);

        ListView listView = this.getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        setListAdapter(arrayAdapter);

        register();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundServiceDemo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createShortcut1();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForegroundServiceDemo() {
        Intent intent = new Intent(this, ForegroundServiceDemo.class);
        startForegroundService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent();
        switch (position) {
            case NOTIFICATION_DEMO_ITEM:
                intent.setClass(this, NotificationsActivity.class);
                break;
            case VIEW_DEMO_ITEM:
                intent.setClass(this, ViewDemoActivity.class);
                break;
            case LOTTIEANIMVIEWS_ITEM:
                intent.setClass(this, LottieAnimViewDemoActivity.class);
                break;
            case PIP_DEMO_ITEM:
                intent.setClass(this, PipDemoActivity.class);
                break;
            case RECYCLER_VIEW_ITEM:
                intent.setClass(this, RecyclerActivity.class);
                break;
            case REQUEST_PERMISSION_ITEM:
                intent.setClass(this, RequestPermissionActivity.class);
                break;
            case BLUETOOTH_ITEM:
                intent.setClass(this, BTDemo1Activity.class);
                break;
            case CONTENTPROVIDER_DEMO_ITEM:
                intent.setClass(this, ContentProviderDemoActivity.class);
                break;
            case NOTIFICATION_APP_DEMO_ITEM:
                intent.setClass(this, NotificationsReceiveActivity.class);
                break;
            case BLE_CENTRAL_DEMO_ITEM:
                intent.setClass(this, BleCentralDemoActivity.class);
                break;
            case BLE_PERIPHERALS_DEMO_ITEM:
                intent.setClass(this, AdvertiseActivity.class);
                break;
            case CONTROLLER_PANEL_DEMO_ITEM:
                intent.setClass(this, ControllerPanelActivity.class);
                break;
            case FULL_SCREEN_DEMO_ITEM:
                intent.setClass(this, FullScreenDemoActiivty.class);
                break;
            case VIEW_DRAG_DEMO_ITEM:
                intent.setClass(this, ViewDragDemoActivity.class);
                break;
            case BUTTON_DEMO_ITEM:
                intent.setClass(this, ButtonActivity.class);
                break;
            default:
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy is run.");
        unregister();
        mShortcutManager.removeAllDynamicShortcuts();
    }

    private static final String ACTION_LIST1 = "com.yzs.demo.shortcut.action1";
    private static final String ACTION_LIST2 = "com.yzs.demo.shortcut.action2";
    private String shortcutId0 = "shortcutId0";
    private String shortcutId1 = "shortcutId1";
    private String shortcutId2 = "shortcutId2";
    private String shortcutId3 = "shortcutId3";

    private void createShortcut1() {
        mShortcutManager.removeAllDynamicShortcuts();
        Intent intent = new Intent(ACTION_LIST1);
        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(this, shortcutId0)
                .setShortLabel("回家Demo")
                .setIntent(intent)
                .build();
        ShortcutInfo shortcutInfo1 = new ShortcutInfo.Builder(this, shortcutId1)
                .setShortLabel("公司Demo")
                .setIntent(intent)
                .build();
        List<ShortcutInfo> shortcutInfos = new ArrayList<>();
        shortcutInfos.add(shortcutInfo);
        shortcutInfos.add(shortcutInfo1);
        mShortcutManager.addDynamicShortcuts(shortcutInfos);
    }

    private void createShortcut2() {
        mShortcutManager.removeAllDynamicShortcuts();
        Intent intent = new Intent(ACTION_LIST2);
        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(this, shortcutId2)
                .setShortLabel("导航Demo")
                .setIntent(intent)
                .build();
        ShortcutInfo shortcutInfo1 = new ShortcutInfo.Builder(this, shortcutId3)
                .setShortLabel("结束Demo")
                .setIntent(intent)
                .build();
        List<ShortcutInfo> shortcutInfos = new ArrayList<>();
        shortcutInfos.add(shortcutInfo);
        shortcutInfos.add(shortcutInfo1);
        mShortcutManager.addDynamicShortcuts(shortcutInfos);
    }

    private void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_LIST1);
        intentFilter.addAction(ACTION_LIST2);
        registerReceiver(shortcutReceiver, intentFilter);
    }

    private void unregister() {
        unregisterReceiver(shortcutReceiver);
    }

    private BroadcastReceiver shortcutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive: action:" + action);
            if (ACTION_LIST1.equals(action)) {
                createShortcut2();
            } else if (ACTION_LIST2.equals(action)) {
                createShortcut1();
            }
        }
    };

}
