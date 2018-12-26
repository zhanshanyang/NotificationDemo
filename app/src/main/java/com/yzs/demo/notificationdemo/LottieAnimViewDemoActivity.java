package com.yzs.demo.notificationdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;

import com.airbnb.lottie.LottieAnimationView;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.airbnb.lottie.LottieDrawable.REVERSE;

public class LottieAnimViewDemoActivity extends AppCompatActivity {

    private static final String TAG = "LottieAnimViewDemoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie_anim_demos);

        initLottieViews();
        initLottieViews2();
        initLottieViews3();
    }

    private void initLottieViews3() {
        LottieAnimationView santaViewAnim = findViewById(R.id.santa_view);
        AtomicBoolean flag = new AtomicBoolean(false);
        santaViewAnim.setOnClickListener(v -> {
            if (flag.get()) {
                santaViewAnim.setMinProgress(0.4f);
                santaViewAnim.setMaxProgress(1f);
            } else {
                santaViewAnim.setMinProgress(0f);
                santaViewAnim.setMaxProgress(0.4f);
            }
            santaViewAnim.playAnimation();
            flag.set(!flag.get());
        });
        santaViewAnim.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "santaViewAnim end pro:" + santaViewAnim.getProgress());
            }
        });
        santaViewAnim.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.i(TAG, "santaViewAnim update value:" + animation.getAnimatedValue());
            }
        });
    }

    private void initLottieViews2() {
        LottieAnimationView wifiViewAnim2 = findViewById(R.id.wifi_view_2);
        wifiViewAnim2.setProgress(1f);
        wifiViewAnim2.setOnClickListener(v -> {
            if (wifiViewAnim2.isAnimating()) return;
            Log.i(TAG, "initLottieViews2: pro:" + wifiViewAnim2.getProgress() + ",speed:" + wifiViewAnim2.getSpeed());

            if (wifiViewAnim2.getProgress() == 0f) {
                wifiViewAnim2.playAnimation();
            } else {
                wifiViewAnim2.reverseAnimationSpeed();
                wifiViewAnim2.playAnimation();
            }
        });
        wifiViewAnim2.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.i(TAG, "wifiViewAnim2 end progress:" + wifiViewAnim2.getProgress());
            }
        });
        wifiViewAnim2.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.i(TAG, "wifiViewAnim2 update value:" + animation.getAnimatedValue());
            }
        });

        LottieAnimationView playViewAnim2 = findViewById(R.id.play_view_2);
        AtomicBoolean pause2Play = new AtomicBoolean(true);
        playViewAnim2.setOnClickListener(v -> {
//            if (playViewAnim2.isAnimating())
//                return;
            if (pause2Play.get()) {
                playViewAnim2.setMinProgress(0f);
                playViewAnim2.setMaxProgress(0.5f);
            } else {
                playViewAnim2.setMinProgress(0.5f);
                playViewAnim2.setMaxProgress(1f);
            }
            playViewAnim2.playAnimation();
            pause2Play.set(!pause2Play.get());
        });
        playViewAnim2.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e(TAG, "playViewAnim2 end progress:" + playViewAnim2.getProgress());
            }
        });
        playViewAnim2.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.i(TAG, "playViewAnim2 update value:" + animation.getAnimatedValue());
            }
        });


        LottieAnimationView expandViewAnim2 = findViewById(R.id.expand_view_2);
        expandViewAnim2.setOnClickListener(v -> {
            if (expandViewAnim2.isAnimating())
                return;
            if (expandViewAnim2.getProgress() != 0) {
                expandViewAnim2.reverseAnimationSpeed();
            }
            expandViewAnim2.playAnimation();
        });
        expandViewAnim2.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.i(TAG, "expandViewAnim2 update value:" + animation.getAnimatedValue() + ",progress:" + expandViewAnim2.getProgress());
            }
        });
        expandViewAnim2.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e(TAG, "expandViewAnim2 end progress:" + expandViewAnim2.getProgress() + ",frame:" + expandViewAnim2.getFrame());
            }
        });

    }

    private void initLottieViews() {
        LottieAnimationView wifiViewAnim = findViewById(R.id.wifi_view);
        AtomicBoolean isOn = new AtomicBoolean(false);
        wifiViewAnim.setOnClickListener(v -> {
            if (isOn.get()) {
                wifiViewAnim.setAnimation(R.raw.signal_on_2_off);
            } else {
                wifiViewAnim.setAnimation(R.raw.signal_off_2_on);
            }
            wifiViewAnim.playAnimation();
            isOn.set(!isOn.get());
        });
//        wifiViewAnim.setProgress(0f);

        LottieAnimationView playViewAnim = findViewById(R.id.play_view);
        playViewAnim.setOnClickListener(v -> playViewAnim.playAnimation());
//        playViewAnim.setProgress(0.5f);

        LottieAnimationView pauseViewAnim = findViewById(R.id.pause_view);
        pauseViewAnim.setOnClickListener(v -> pauseViewAnim.playAnimation());
//        pauseViewAnim.setProgress(1f);

        LottieAnimationView expandViewAnim = findViewById(R.id.expand_view);
        expandViewAnim.setOnClickListener(v -> expandViewAnim.playAnimation());

        LottieAnimationView retractViewAnim = findViewById(R.id.retract_view);
        retractViewAnim.setOnClickListener(v -> retractViewAnim.playAnimation());

        SeekBar seekBar = findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float pro = progress / 100f;
                wifiViewAnim.setProgress(pro);
                playViewAnim.setProgress(pro);
                pauseViewAnim.setProgress(pro);
                expandViewAnim.setProgress(pro);
                retractViewAnim.setProgress(pro);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
