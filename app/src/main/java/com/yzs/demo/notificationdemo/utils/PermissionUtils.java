package com.yzs.demo.notificationdemo.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtils {

    /**
     * check permission.
     * @param activity activity
     * @param permission permission
     * @return true:has     false:not
     */
    public static boolean hasPermission(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity, permission);
    }

    /**
     * request permission.
     * @param activity
     * @param permissions
     * @param requestCode
     */
    public static void requestPermission(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

}
