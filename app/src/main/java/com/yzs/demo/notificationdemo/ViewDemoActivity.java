package com.yzs.demo.notificationdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.yzs.demo.notificationdemo.utils.InterpolatorUtils;
import com.yzs.demo.notificationdemo.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;

public class ViewDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ViewDemoActivity";

    private static final String BROADCAST_ACTION = "com.chehejia.demo.broadcast.action";

    private RadioGroup radioGroupStyles;

    private ShortcutManager shortcutManager;
    private String shortCutId = "viewdemo_id";
    private List<String> shortcutsIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shortcutManager = getSystemService(ShortcutManager.class);
        setContentView(R.layout.activity_view_demo);
        findViewById(R.id.btn_add_shortcut).setOnClickListener(this);
        findViewById(R.id.btn_remove_shortcut).setOnClickListener(this);
        findViewById(R.id.btn_auto_start_change).setOnClickListener(this);
        findViewById(R.id.btn_dialog).setOnClickListener(this);
        findViewById(R.id.btn_toast).setOnClickListener(this);
        findViewById(R.id.btn_anm_test).setOnClickListener(this);
        findViewById(R.id.btn_enter_full).setOnClickListener(this);
        findViewById(R.id.btn_exit_full).setOnClickListener(this);
        findViewById(R.id.edit_text).setOnClickListener(this);
        radioGroupStyles = findViewById(R.id.rg_styles);

        initLottieViews();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void initLottieViews() {

        LottieAnimationView wifiViewAnim = findViewById(R.id.wifi_view);
        wifiViewAnim.setOnClickListener(v -> wifiViewAnim.playAnimation());
//        wifiViewAnim.setProgress(0f);

        LottieAnimationView playViewAnim = findViewById(R.id.play_view);
        playViewAnim.setOnClickListener(v -> playViewAnim.playAnimation());
//        playViewAnim.setProgress(0.5f);

        LottieAnimationView pauseViewAnim = findViewById(R.id.pause_view);
        pauseViewAnim.setOnClickListener(v -> pauseViewAnim.playAnimation());
//        pauseViewAnim.setProgress(1f);



        SeekBar seekBar = findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float pro = progress / 100f;
                wifiViewAnim.setProgress(pro);
                playViewAnim.setProgress(pro);
                pauseViewAnim.setProgress(pro);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        LottieAnimationView demoAnim = findViewById(R.id.demo_view);
//        demoAnim.setImageAssetsFolder("volume/");
        demoAnim.setOnClickListener(view -> {
            demoAnim.playAnimation();
        });
        //LottieComposition#setImagesFolder or LottieDrawable#setImagesFolder
//        demoAnim.setImageAssetsFolder();
//        demoAnim.
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
        } else if (v.getId() == R.id.edit_text) {
            Log.i(TAG, "editText is Click.");
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

    public static final String SHORTCUT_VOICE_KEYWORD = "shortcut_voice_keyword";
    private void addOneShortCut() {
        if (shortcutManager != null) {
            int maxShortcutCountPerActivity = shortcutManager.getMaxShortcutCountPerActivity();
            int sum = shortcutManager.getManifestShortcuts().size() +
                    shortcutManager.getDynamicShortcuts().size();
            if (sum >= maxShortcutCountPerActivity) {
                return;
            }
        }
        Intent intent = new Intent(this, ViewDemoActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        shortCutId = shortCutId + SystemClock.uptimeMillis();

        PersistableBundle pBundle = new PersistableBundle();
        pBundle.putString(SHORTCUT_VOICE_KEYWORD, SHORTCUT_MAP_KEYWORD);
        ShortcutInfo.Builder builder = new ShortcutInfo.Builder(this, shortCutId)
                .setShortLabel("回家Demo")
                .setExtras(pBundle)
                .setIntent(intent);
        int style = getStyle();
        if (style == STYLE_TEXT_DESC) {
            builder.setLongLabel("35分钟");
        } else if (style == STYLE_TEXT_PIC) {
            builder.setIcon(Icon.createWithResource(this, R.drawable.ic_card_user_default));
        }
        if (shortcutManager != null) {
            shortcutsIds.add(shortCutId);
            shortcutManager.addDynamicShortcuts(Collections.singletonList(builder.build()));
        }
    }


    public static final String SHORTCUT_MAP_KEYWORD = "SHORTCUT_OPEN_MAP";
    public static final String SHORTCUT_HOME_KEYWORD = "SHORTCUT_DESKTOP";


    private static final int STYLE_NORMAL = 1;
    private static final int STYLE_TEXT_DESC = 2;
    private static final int STYLE_TEXT_PIC = 3;
    private int getStyle() {
        int checkedRadioButtonId = radioGroupStyles.getCheckedRadioButtonId();
        int style = 0;
        if (checkedRadioButtonId == R.id.rb_normal) {
            style = STYLE_NORMAL;
        } else if (checkedRadioButtonId == R.id.rb_text_desc) {
            style = STYLE_TEXT_DESC;
        } else if (checkedRadioButtonId == R.id.rb_text_pic) {
            style = STYLE_TEXT_PIC;
        }
        return style;
    }

    private void removeOneShortCut() {
        Log.i("ViewDemo", "removeOneShortCut is run.");
        if (shortcutManager != null) {
            for (ShortcutInfo shortcutInfo : shortcutManager.getDynamicShortcuts()) {
                shortcutsIds.add(shortcutInfo.getId());
            }
            shortcutManager.removeDynamicShortcuts(shortcutsIds);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: broadcast receive a intent.");
        }
    };
}
