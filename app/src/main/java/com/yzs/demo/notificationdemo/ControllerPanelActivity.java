package com.yzs.demo.notificationdemo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.yzs.demo.notificationdemo.fragment.LeftMenuFragment;
import com.yzs.demo.notificationdemo.widget.TopDrawerLayout;

public class ControllerPanelActivity extends AppCompatActivity {

    private LeftMenuFragment mMenuFragment;
    private TopDrawerLayout mTopDrawerLayout;
    private TextView mContentTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        setContentView(R.layout.activity_controller_panel);
        setContentView(R.layout.activity_controller_panel_2);

        mTopDrawerLayout = (TopDrawerLayout) findViewById(R.id.id_drawerlayout);
        mContentTv = (TextView) findViewById(R.id.id_content_tv);

        FragmentManager fm = getSupportFragmentManager();
        mMenuFragment = (LeftMenuFragment) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment = new LeftMenuFragment()).commit();
        }

        mMenuFragment.setOnMenuItemSelectedListener(new LeftMenuFragment.OnMenuItemSelectedListener() {
            @Override
            public void menuItemSelected(String title) {
                mTopDrawerLayout.closeDrawer();
                mContentTv.setText(title);
            }
        });
    }
}
