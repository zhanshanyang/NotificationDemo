package com.yzs.demo.notificationdemo.utils;

import android.app.Activity;
import android.view.WindowManager;

public class ScreenUtils {

    /**
     * 退出全屏
     * @param activity
     */
    public static void exitFullScreen(Activity activity) {
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 进入全屏
     * @param activity
     */
    public static void enterFullScreen(Activity activity) {
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
