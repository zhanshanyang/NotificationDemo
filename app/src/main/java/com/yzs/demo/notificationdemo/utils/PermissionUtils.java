package com.yzs.demo.notificationdemo.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
     *
     * @param activity
     * @param permissions
     * @param requestCode
     * @return true:has all permission.
     */
    public static boolean requestPermission(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            List<String> tmpPermissions = new ArrayList<>();
            for (String permission : permissions) {
                if (!PermissionUtils.hasPermission(activity, permission)) {
                    tmpPermissions.add(permission);
                }
            }
            Log.i("requestPermission", "申请权限. size:" + tmpPermissions.size());
            if (tmpPermissions.size() > 0) {
                String[] tmp = tmpPermissions.toArray(new String[tmpPermissions.size()]);
                ActivityCompat.requestPermissions(activity, tmp, requestCode);
                return false;
            }
        }
        Log.i("requestPermission", "已经全部授权");
        return true;
    }


}
