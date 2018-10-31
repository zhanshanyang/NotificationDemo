package com.yzs.demo.notificationdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.yzs.demo.notificationdemo.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class RequestPermissionActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 2;


    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE
    };

    private List<String> tmpPermissions = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permission);
    }


    public void btnClick(View view) {
        if (view.getId() == R.id.btn1) {
            requestSinglePermission();
        } else if (view.getId() == R.id.btn2) {
            requestSomePermission();
        } else if (view.getId() == R.id.btn3) {
            openCamera();
        } else if (view.getId() == R.id.btn4) {
            openPhone();
        }
    }

    @SuppressLint("MissingPermission")
    private void openPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + "10086");
        intent.setData(data);
        startActivity(intent);
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, 1);
    }

    private void requestSomePermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            tmpPermissions.clear();
            for (String permission : permissions) {
                if (!PermissionUtils.hasPermission(this, permission)) {
                    tmpPermissions.add(permission);
                }
            }
            if (tmpPermissions.size() > 0) {
                String[] tmps = tmpPermissions.toArray(new String[tmpPermissions.size()]);
                PermissionUtils.requestPermission(this, tmps, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
            } else {
                showToast("已经全部授权");
            }
        }

    }

    private void requestSinglePermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (!PermissionUtils.hasPermission(this, permissions[2])) {
                PermissionUtils.requestPermission(this, new String[]{permissions[2]}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
            } else {
                showToast("已经授权");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("权限以申请");
            } else {
                showToast("权限没有得到申请");
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_CALL_CAMERA) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                    if (showRequestPermission) {
                        showToast("权限未申请");
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
