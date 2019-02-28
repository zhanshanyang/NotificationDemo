package com.yzs.providerdemo2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.chehejia.m01.cable.BrainCableImpl;
import com.chehejia.m01.cable.IBrainCable;
import com.chehejia.m01.cable.Surface;
import com.chehejia.m01.cable.SurfaceState;
import com.chehejia.m01.cable.SurfaceStateObserver;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleBrainCable();

        findViewById(R.id.btn_360).setOnClickListener(this);
    }

    private IBrainCable brainCable = BrainCableImpl.getInstance();
    private SurfaceStateObserver surfaceStateObserver = new SurfaceStateObserver() {
        @Override
        public void onchange(String type, boolean state) {
            Log.i(TAG, "onchange: type:" + type + ",state:" + state);
        }
    };

    private void handleBrainCable() {
        brainCable.init(this);
        brainCable.registerObserver(surfaceStateObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        brainCable.unregisterObserver(surfaceStateObserver);
        brainCable.destroy();
    }

    @Override
    public void onClick(View v) {
        SurfaceState state = brainCable.getState();
        Log.i(TAG, "onClick1: state:" + state.toString());
        brainCable.setState(Surface.Discontinue.TYPE_360, !state.isState360());
        SurfaceState state2 = brainCable.getState();
        Log.i(TAG, "onClick2: state2:" + state2.toString());
    }
}
