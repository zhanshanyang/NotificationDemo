package com.yzs.demo.notificationdemo;

import android.app.ListActivity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yzs.demo.notificationdemo.ble.AdvertiseActivity;
import com.yzs.demo.notificationdemo.ble.BleCentralDemoActivity;
import com.yzs.demo.notificationdemo.bluetooth.BTDemo1Activity;
import com.yzs.demo.notificationdemo.notifications.NotificationsReceiveActivity;

public class MainActivity extends ListActivity {

    private static final int NULL_DEMO_ITEM = 0;
    private static final int NOTIFICATION_DEMO_ITEM = NULL_DEMO_ITEM ;
    private static final int VIEW_DEMO_ITEM = NULL_DEMO_ITEM + 1;
    private static final int PIP_DEMO_ITEM = NULL_DEMO_ITEM + 2;
    private static final int RECYCLER_VIEW_ITEM = NULL_DEMO_ITEM + 3;
    private static final int REQUEST_PERMISSION_ITEM = NULL_DEMO_ITEM + 4;
    private static final int BLUETOOTH_ITEM = NULL_DEMO_ITEM + 5;
    private static final int CONTENTPROVIDER_DEMO_ITEM = NULL_DEMO_ITEM + 6;
    private static final int NOTIFICATION_APP_DEMO_ITEM = NULL_DEMO_ITEM + 7;
    private static final int BLE_CENTRAL_DEMO_ITEM = NULL_DEMO_ITEM + 8;
    private static final int BLE_PERIPHERALS_DEMO_ITEM = NULL_DEMO_ITEM + 9;
    private static final int CONTROLLER_PANEL_DEMO_ITEM = NULL_DEMO_ITEM + 10;

    private static final int none = 1;
    private static final int floating = 1 << 1;
    private static final int headsup = 1 << 2;
    private static final int all = 1<<3;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] data = new String[]{"Notification 样式", "View Demo样式", "Pip Demo",
                "RecyclerViewDemo", "RequestPermission Demo", "Bluetooth Demo",
                "ContentProviderDemo", "NotificationAppDemo", "Ble Central Demo",
                "Ble Peripherals Demo", "Controller Panel Demo"
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, data);

        ListView listView = this.getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        setListAdapter(arrayAdapter);
    }

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
            default:
                break;
        }
        startActivity(intent);
    }

    private void testMessenger() {
        HandlerThread handlerThread = new HandlerThread("test thread");
        Handler handler = new Handler(getMainLooper());
        Handler handler1 = new Handler(handlerThread.getLooper());
        Messenger messenger = new Messenger(handler);
        messenger.getBinder();
        Service service = new Service() {
            @Nullable
            @Override
            public IBinder onBind(Intent intent) {
                return messenger.getBinder();
            }
        };

        Messenger messenger1 = new Messenger(messenger.getBinder());
    }

       

}
