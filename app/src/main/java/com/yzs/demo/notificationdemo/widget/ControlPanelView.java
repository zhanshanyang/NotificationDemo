package com.yzs.demo.notificationdemo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

import com.yzs.demo.notificationdemo.R;

public class ControlPanelView extends RelativeLayout {

    private static final String TAG = "ControlPanelView";

    private static final int MIN_PANEL_HEIGHT = 52;
    private static final float MIN_VELOCITY_DP_PER_SECOND = 250;
    private float mMinVelocityPxPerSecond;

    private Context mContext;

    /**
     * 下拉的过程需要渐隐渐显
     */
    private View mViewBg;
    /**
     * control panel bg.
     */
    private View mViewPanelBg;
    /**
     * control bar view
     */
    private View mViewPanelBar;
    /**
     * panel's height
     */
    private int mQsExpansionHeight;
    /**
     * panel's height when changing height
     */
    private float mTempQsExpansion;
    /**
     * pointer index.
     */
    private int mTrackingPointer;
    private float mInitialTouchY;
    private float mInitialTouchX;
    /**
     * qs panel is tracking ?
     */
    private boolean mBarTracking;
    /**
     * fling velocity
     */
    private VelocityTracker mQsVelocityTracker;

    public ControlPanelView(Context context) {
        this(context, null);
    }

    public ControlPanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mMinVelocityPxPerSecond = MIN_VELOCITY_DP_PER_SECOND * context.getResources().getDisplayMetrics().density;
        inflate(context, R.layout.view_control_panel, this);
        mViewBg = findViewById(R.id.view_bg);
        mViewPanelBg = findViewById(R.id.view_panel_bg);
        mViewPanelBar = findViewById(R.id.view_panel_bar);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewBg.setOnTouchListener(bgTouchListener);
        mViewPanelBg.setOnTouchListener(panelBgTouchListener);
        mViewPanelBar.setOnTouchListener(panelBarTouchListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "onMeasure is run.height:" + getHeight() + ",measureHeight:" + getMeasuredHeight());
        mQsExpansionHeight  = mViewPanelBg.getMeasuredHeight();
        mTempQsExpansion = MIN_PANEL_HEIGHT;
        initPanelView();
    }

    private void initPanelView() {
        float tY = mTempQsExpansion - mQsExpansionHeight;
        Log.i(TAG, "initPanelView ,tY:" + tY);
        setTranslationY(tY);
        mViewBg.setAlpha(0f);
        mViewPanelBg.setAlpha(0f);
    }

    private OnTouchListener bgTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i(TAG, "viewBg is touched.");
            // TODO: 2018/11/30 close panel.
            return true;
        }
    };

    private OnTouchListener panelBgTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i(TAG, "panelBg is touched.");
            if(getQsExpansionFraction() > 0)
                onQsTouch(event);
            return true;
        }
    };

    private OnTouchListener panelBarTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //当面板bar被触摸上之后，有反馈:显示出来panelBg颜色
            Log.i(TAG, "panelBar is touched.");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mBarTracking = true;
                    updatePanelView(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mBarTracking = false;
                    updatePanelView(false);
                    break;
            }
            onQsTouch(event);
            return true;
        }
    };

    private void onQsTouch(MotionEvent event) {
        int pointerIndex = event.findPointerIndex(mTrackingPointer);
        if (pointerIndex < 0) {
            pointerIndex = 0;
            mTrackingPointer = event.getPointerId(pointerIndex);
        }
        final float y = event.getY(pointerIndex);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mInitialTouchY = event.getY(pointerIndex);
                mInitialTouchX = event.getX(pointerIndex);
                onQsExpansionStarted();
                initVelocityTracker();
                trackMovement(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                final int upPointer = event.getPointerId(event.getActionIndex());
                if (mTrackingPointer == upPointer) {
                    // gesture is ongoing, find a new pointer to track
                    final int newIndex = event.getPointerId(0) != upPointer ? 0 : 1;
                    final float newY = event.getY(newIndex);
                    final float newX = event.getX(newIndex);
                    mTrackingPointer = event.getPointerId(newIndex);
                    mInitialTouchY = newY;
                    mInitialTouchX = newX;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float h = y - mInitialTouchY;
                mTempQsExpansion += h;
                setQsExpansion(mTempQsExpansion);

                trackMovement(event);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTrackingPointer = -1;
                trackMovement(event);
                float fraction = getQsExpansionFraction();
                if (fraction != 0f || y >= mInitialTouchY) {
                    Log.i(TAG, "onQsTouch up/cancel is run.fraction:" + fraction + ",y:" + y + ",initY:" + mInitialTouchY);
                    flingQsWithCurrentVelocity(event.getActionMasked() == MotionEvent.ACTION_CANCEL);
                }
                if (mQsVelocityTracker != null) {
                    mQsVelocityTracker.clear();
                    mQsVelocityTracker.recycle();
                    mQsVelocityTracker = null;
                }
                break;
        }
    }

    private ValueAnimator mQsExpansionAnimator;

    /**
     * 当手指在面板上时，停止动画
     */
    private void onQsExpansionStarted() {
        if (mQsExpansionAnimator != null) {
            mQsExpansionAnimator.cancel();
        }
    }

    private void updatePanelView(boolean barTouch) {
        float fraction = getQsExpansionFraction();
        Log.i(TAG, "updatePanelView barTouch:" + barTouch + ",fraction:" + fraction);
        mViewBg.setAlpha(fraction);
        mViewPanelBg.setAlpha((mBarTracking || barTouch || fraction > 0) ? 1f : 0f);
    }

    /**
     * 设置panel的高度
     * 根据手的滑动动态展开面板
     * @param height
     */
    private void setQsExpansion(float height) {
        Log.i(TAG, "setQsExpansion mTempQsExpansion:" + mTempQsExpansion);
        mTempQsExpansion = height;
        updatePanelView(false);
        Log.i(TAG, "setQsExpansion height:" + height + ",mTempQsExpansion:" + mTempQsExpansion);

        if (mTempQsExpansion < MIN_PANEL_HEIGHT) {
            mTempQsExpansion = MIN_PANEL_HEIGHT;
        } else if (mTempQsExpansion > mQsExpansionHeight) {
            mTempQsExpansion = mQsExpansionHeight;
        }

        setTranslationY(mTempQsExpansion - mQsExpansionHeight);
    }

    protected void flingSettings(float vel, boolean expand) {
        //target 表示达到的目的地
        float target = expand ? mQsExpansionHeight : MIN_PANEL_HEIGHT;
        Log.i(TAG, "flingSettings is run.vel:" + vel + ",expand:" + expand + ",target:" + target
                + ",mQsExpansionHeight:" + mQsExpansionHeight + ",mTempQsExpansion:" + mTempQsExpansion);

        if (mTempQsExpansion == target) return;

        // If we move in the opposite direction, reset velocity and use a different duration.
        boolean oppositeDirection = false;
        if (vel > 0 && !expand || vel < 0 && expand) {
            vel = 0;
            oppositeDirection = true;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(mTempQsExpansion, target);

        apply(animator, mTempQsExpansion, target, vel);

        if (oppositeDirection) {
            animator.setDuration(350);
        }
        animator.addUpdateListener(animation -> setQsExpansion((float) animation.getAnimatedValue()));

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mQsExpansionAnimator = null;
            }
        });
        animator.start();
        mQsExpansionAnimator = animator;
    }

    public void apply(Animator animator, float currValue, float endValue, float velocity) {
        float maxDistance = Math.abs(endValue - currValue);

//        AnimatorProperties properties = getProperties(currValue, endValue, velocity, maxDistance);

//        animator.setDuration(properties.duration);
//        animator.setInterpolator(properties.interpolator);
        animator.setDuration(300);
        animator.setInterpolator(Interpolators.ACCELERATE_DECELERATE);
    }


    /**
     * 根据滑动的惯性和查看当前滑动的
     * @param isCancelMotionEvent true:滑动无效
     */
    private void flingQsWithCurrentVelocity(boolean isCancelMotionEvent) {
        float vel = getCurrentQSVelocity();
        final boolean expandsQs = flingExpandsQs(vel);
        Log.i(TAG, "flingQsWithCurrentVelocity is run. isCancel:" + isCancelMotionEvent
                + ",vel:" + vel + ",expandQs:" + expandsQs);
        //动效打开面板
        flingSettings(vel, expandsQs && !isCancelMotionEvent);
    }

    /**
     * 判断打开还是管理panel
     * @param vel 惯性
     * @return true:expand panel       false:close panel
     */
    private boolean flingExpandsQs(float vel) {
        //滑动惯性与预定值比较
        if (Math.abs(vel) < mMinVelocityPxPerSecond) {
            //惯性太小，判断当前已经展开的高度是否大于总高度的一半
            return getQsExpansionFraction() > 0.5f;
        } else {
            //惯性太大，val>0:朝下展开面板的惯性
            //val < 0:朝上关闭面板的惯性
            return vel > 0;
        }
    }

    /**
     * 下拉的距离是否超过高度的一半
     * @return
     */
    private float getQsExpansionFraction() {
        return Math.min(1f, (getTempQsMaxExpansion() - MIN_PANEL_HEIGHT)
                / (mQsExpansionHeight - MIN_PANEL_HEIGHT));
    }

    /**
     * 获取当前panel展开的height
     * @return
     */
    private float getTempQsMaxExpansion() {
        return mTempQsExpansion;
    }

    /* 滑动惯性的使用 */
    private void initVelocityTracker() {
        if (mQsVelocityTracker != null) {
            mQsVelocityTracker.recycle();
        }
        mQsVelocityTracker = VelocityTracker.obtain();
    }

    private void trackMovement(MotionEvent event) {
        if (mQsVelocityTracker != null) mQsVelocityTracker.addMovement(event);
    }

    private float getCurrentQSVelocity() {
        if (mQsVelocityTracker == null) {
            return 0;
        }
        mQsVelocityTracker.computeCurrentVelocity(1000);
        return mQsVelocityTracker.getYVelocity();
    }

    private static class AnimatorProperties {
        Interpolator interpolator;
        long duration;
    }
}
