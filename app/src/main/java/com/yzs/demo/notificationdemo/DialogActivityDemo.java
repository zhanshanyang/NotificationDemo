package com.yzs.demo.notificationdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class DialogActivityDemo extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
        setContentView(R.layout.activity_dialog_demo);

        EditText editText = findViewById(R.id.edit_text);

        Button btnOk = findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(view -> {
            finish();
        });

    }
}
