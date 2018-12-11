package com.yzs.demo.notificationdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yzs.demo.notificationdemo.utils.InterpolatorUtils;
import com.yzs.demo.notificationdemo.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class ViewDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ViewDemoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_demo);
        findViewById(R.id.btn_add_shortcut).setOnClickListener(this);
        findViewById(R.id.btn_remove_shortcut).setOnClickListener(this);
        findViewById(R.id.btn_auto_start_change).setOnClickListener(this);
        findViewById(R.id.btn_dialog).setOnClickListener(this);
        findViewById(R.id.btn_toast).setOnClickListener(this);
        findViewById(R.id.btn_anm_test).setOnClickListener(this);
        findViewById(R.id.btn_enter_full).setOnClickListener(this);
        findViewById(R.id.btn_exit_full).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_shortcut) {
            addOneShortCut();
        } else if (v.getId() == R.id.btn_remove_shortcut) {
            removeOneShortCut();
        } else if (v.getId() == R.id.btn_auto_start_change) {
            changeShortCut();
        } else if (v.getId() == R.id.btn_dialog) {
            showDialog();
        } else if (v.getId() == R.id.btn_toast) {
            Toast.makeText(this, "This is a Toast!!!", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.btn_anm_test) {
            startAnimatorTest();
        } else if (v.getId() == R.id.btn_enter_full) {
            ScreenUtils.enterFullScreen(this);
        } else if (v.getId() == R.id.btn_exit_full) {
            ScreenUtils.exitFullScreen(this);
        }
    }

    public static void hideNavKey(Context context) {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = ((Activity) context).getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = ((Activity) context).getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private ValueAnimator valueAnimator;
    private int count;
    private void startAnimatorTest() {
        if(valueAnimator != null && valueAnimator.isRunning())
            return;
        valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                count = 0;
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                count++;
                int value = (int) animation.getAnimatedValue();
                Log.i(TAG, "animate is update.value:" + value + ",count:" + count);
            }
        });
        valueAnimator.setInterpolator(InterpolatorUtils.pathInterpolator(InterpolatorUtils.ControlPanelViewPoints));
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    private void showDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Dialog test")
                .setMessage("this is a message!!!")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(true)
                .create();
        alertDialog.show();
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
//        ComponentName componentName = new ComponentName("", "");
        ShortcutInfo info = new ShortcutInfo.Builder(this, shortCutId)
                .setIcon(Icon.createWithResource(this, R.drawable.ic_card_user_default))
                .setShortLabel("新添加的ShortCut")
                .setLongLabel("add a new shortCut.long")
//                .setExtras()
//                .setActivity()
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
