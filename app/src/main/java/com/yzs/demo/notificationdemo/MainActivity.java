package com.yzs.demo.notificationdemo;

import android.app.ActivityOptions;
import android.app.ListActivity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yzs.demo.notificationdemo.ble.AdvertiseActivity;
import com.yzs.demo.notificationdemo.ble.BleCentralDemoActivity;
import com.yzs.demo.notificationdemo.bluetooth.BTDemo1Activity;
import com.yzs.demo.notificationdemo.notifications.NotificationsReceiveActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] data = new String[]{"Notification 样式", "View Demo样式", "LottieAnimViews Demo",
                "RecyclerViewDemo", "RequestPermission Demo", "Bluetooth Demo",
                "ContentProviderDemo", "NotificationAppDemo", "Ble Central Demo",
                "Ble Peripherals Demo", "Controller Panel Demo", "FullScreenActivity",
                "ViewDragDemoActivity", "Pip Demo", "Button Demo"
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, data);

        ListView listView = this.getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        setListAdapter(arrayAdapter);

        getICCID();

    }

    private void getICCID() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.e(TAG, "1  ICCID is " + telephonyManager.getSimSerialNumber());

        SubscriptionInfo info = SubscriptionManager.from(this).getActiveSubscriptionInfoForSimSlotIndex(-1);
        if (info != null)
        {
            Log.e(TAG, "2  getICCID: " + info.getIccId());
        }

        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method method = tm.getClass().getDeclaredMethod("getSubscriberInfo");
            try {
                method.setAccessible(true);
                Object obj = method.invoke(tm);
                Method method2 = obj.getClass().getDeclaredMethod("getPhone",int.class);
                method2.setAccessible(true);
                Object obj2 = method2.invoke(obj,0);
                Method method3 = obj2.getClass().getMethod("getFullIccSerialNumber");
                String iccid2 = (String) method3.invoke(obj2);

                Log.e(TAG, "3  getICCID: " + iccid2);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }



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

    public static final int LAUNCHER_PASSENGER_DISPLAY_ID = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startPassengerLauncher() {
        ActivityOptions options = ActivityOptions.makeBasic();
        options.setLaunchDisplayId(LAUNCHER_PASSENGER_DISPLAY_ID);
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.yzs.demo.notificationtools");
        if (null != intent) {
            startActivity(intent, options.toBundle());
        } else {
            Log.e(TAG, "passenger launcher intent is null");
        }
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
