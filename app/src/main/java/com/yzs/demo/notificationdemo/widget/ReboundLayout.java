package com.yzs.demo.notificationdemo.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Scroller;

import com.yzs.demo.notificationdemo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 此viewGroup负责下拉和上拉底部回弹效果.
 */
public class ReboundLayout extends ViewGroup {

    private static final int TOTAL_DRAG_DISTANCE = 140;

    private static final String TAG = "RefreshLayout";
    private static final float DRAG_RATE = 0.5f;
    private static final int INVALID_POINTER = -1;

    // scroller duration
    private static final int SCROLL_TO_TOP_DURATION = 500;
    private static final int SCROLL_TO_REFRESH_DURATION = 250;
    private static final long SHOW_COMPLETED_TIME = 500;

//    private View refreshHeader;
    private View target;
    private int currentTargetOffsetTop; // target/header偏移距离
    private int lastTargetOffsetTop;

    private int touchSlop;
    private int totalDragDistance;  // 需要下拉这个距离才进入松手刷新状态，默认和header高度一致
    private int maxDragDistance;
    private int activePointerId;
    private boolean isTouch;
    private boolean hasSendCancelEvent;
    private float lastMotionX;
    private float lastMotionY;
    private float initDownY;
    private float initDownX;
    private static final int START_POSITION = 0;
    private MotionEvent lastEvent;
    private boolean mIsBeginDragged;
    private AutoScroll autoScroll;
    private State state = State.RESET;
    private boolean isAutoRefresh;
    private List<OnUserTouchListener> onUserTouchListeners;


    // 刷新成功，显示500ms成功状态再滚动回顶部
    private Runnable delayToScrollTopRunnable = new Runnable() {
        @Override
        public void run() {
            autoScroll.scrollTo(START_POSITION, SCROLL_TO_TOP_DURATION);
        }
    };

    private Runnable autoRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            // 标记当前是自动刷新状态，finishScroll调用时需要判断
            // 在actionDown事件中重新标记为false
            isAutoRefresh = true;
            changeState(State.PULL);
            autoScroll.scrollTo(totalDragDistance, SCROLL_TO_REFRESH_DURATION);
        }
    };


    public ReboundLayout(Context context) {
        this(context, null);
    }

    public ReboundLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        onUserTouchListeners = new ArrayList<>();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        autoScroll = new AutoScroll();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (target == null) {
            ensureTarget();
        }

        if (target == null) {
            return;
        }

        // ----- measure target -----
        // target占满整屏
        target.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));

        totalDragDistance = Utils.dp2px(getContext(), TOTAL_DRAG_DISTANCE);//headerHeight;   // 需要pull这个距离才进入松手刷新状态
        if (maxDragDistance == 0) {  // 默认最大下拉距离为控件高度的五分之四
            maxDragDistance = totalDragDistance*4/5;
        }
        Log.i(TAG, "onMeasure is run.totalDragDistance:" + totalDragDistance +
                ",maxDragDistance:" + maxDragDistance);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "onLayout is run.");
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }

        if (target == null) {
            ensureTarget();
        }
        if (target == null) {
            return;
        }

        // target铺满屏幕
        final View child = target;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop() + currentTargetOffsetTop;
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        try {
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        } catch (Exception e) {
            // TODO: 2018/9/14 to fix exception.
        }
    }

    /**
     * 将第一个Child作为target
     */
    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (target == null) {
            if (getChildCount() > 0) {
                target =  getChildAt(0);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (onUserTouchListeners != null && onUserTouchListeners.size() > 0) {
            for (OnUserTouchListener listener : onUserTouchListeners) {
                listener.onUserTouch(ev);
            }
        }
        if (!isEnabled() || target == null) {
            try {
                return super.dispatchTouchEvent(ev);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final int actionMasked = ev.getActionMasked(); // support Multi-touch
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = ev.getPointerId(0);
                isAutoRefresh = false;
                isTouch = true;
                hasSendCancelEvent = false;
                mIsBeginDragged = false;
                lastTargetOffsetTop = currentTargetOffsetTop;
                currentTargetOffsetTop = target.getTop();
                initDownX = lastMotionX = ev.getX(0);
                initDownY = lastMotionY = ev.getY(0);
                autoScroll.stop();
                removeCallbacks(delayToScrollTopRunnable);
                removeCallbacks(autoRefreshRunnable);
                super.dispatchTouchEvent(ev);
                return true;    // return true，否则可能接受不到move和up事件

            case MotionEvent.ACTION_MOVE:
                if (activePointerId == INVALID_POINTER) {
                    Log.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return super.dispatchTouchEvent(ev);
                }
                lastEvent = ev;
                float x = ev.getX(MotionEventCompat.findPointerIndex(ev, activePointerId));
                float y = ev.getY(MotionEventCompat.findPointerIndex(ev, activePointerId));
                float yDiff = y - lastMotionY;
                float offsetY = yDiff * DRAG_RATE;
                lastMotionX = x;
                lastMotionY = y;

                if (currentTargetOffsetTop > 0 || (!mIsBeginDragged && Math.abs(y - initDownY) > touchSlop)) {
                    mIsBeginDragged = true;
                }
                Log.i(TAG, "Touch Move. mIsBeginDragged:" + mIsBeginDragged + ",y - initDownY=" + (y - initDownY)
                        + "y:" + y + ",initDownY:" + initDownY
                        + ",touchSlop:" + touchSlop);

                if (mIsBeginDragged) {
                    boolean moveDown = offsetY > 0; // ↓
                    boolean canMoveDown = canChildScrollUp();
                    boolean moveUp = !moveDown;     // ↑
                    boolean canMoveUp = currentTargetOffsetTop > START_POSITION;

                    // 判断是否拦截事件
                    if ((moveDown && !canMoveDown) || (moveUp && canMoveUp)) {
                        moveSpinner(offsetY);
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "Touch up is run. currentTargetOffsetTop:" + currentTargetOffsetTop + ",START_POSITION:" + START_POSITION);
                isTouch = false;
                if (currentTargetOffsetTop > START_POSITION) {
                    finishSpinner();
                }
                activePointerId = INVALID_POINTER;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                int pointerIndex = MotionEventCompat.getActionIndex(ev);
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return super.dispatchTouchEvent(ev);
                }
                lastMotionX = ev.getX(pointerIndex);
                lastMotionY = ev.getY(pointerIndex);
                lastEvent = ev;
                activePointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                lastMotionY = ev.getY(ev.findPointerIndex(activePointerId));
                lastMotionX = ev.getX(ev.findPointerIndex(activePointerId));
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    private void moveSpinner(float diff) {
        int offset = Math.round(diff);
        if (offset == 0) {
            return;
        }

        // 发送cancel事件给child
        if (!hasSendCancelEvent && isTouch && currentTargetOffsetTop > START_POSITION) {
            sendCancelEvent();
            hasSendCancelEvent = true;
        }

        int targetY = Math.max(0, currentTargetOffsetTop + offset); // target不能移动到小于0的位置……
        // y = x - (x/2)^2
        float extraOS = targetY - totalDragDistance;
        float slingshotDist = totalDragDistance;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
        float tensionPercent = (float) (tensionSlingshotPercent - Math.pow(tensionSlingshotPercent / 2, 2));

        if (offset > 0) { // 下拉的时候才添加阻力
            offset = (int) (offset * (1f - tensionPercent));
            targetY = Math.max(0, currentTargetOffsetTop + offset);
        }

        // 1. 在RESET状态时，第一次下拉出现header的时候，设置状态变成PULL
        if (state == State.RESET && currentTargetOffsetTop == START_POSITION && targetY > 0) {
            changeState(State.PULL);
        }

        // 2. 在PULL或者COMPLETE状态时，header回到顶部的时候，状态变回RESET
        if (currentTargetOffsetTop > START_POSITION && targetY <= START_POSITION) {
            if (state == State.PULL || state == State.COMPLETE) {
                changeState(State.RESET);
            }
        }

//        // 3. 如果是从底部回到顶部的过程(往上滚动)，并且手指是松开状态, 并且当前是PULL状态，状态变成LOADING，这时候我们需要强制停止autoScroll
//        if (state == State.PULL && !isTouch && currentTargetOffsetTop > totalDragDistance && targetY <= totalDragDistance) {
//            autoScroll.stop();
//            changeState(LOADING);
//            // 因为判断条件targetY <= totalDragDistance，会导致不能回到正确的刷新高度（有那么一丁点偏差），调整change
//            int adjustOffset = totalDragDistance - targetY;
//            offset += adjustOffset;
//        }

        setTargetOffsetTopAndBottom(offset);
    }

    private void finishSpinner() {
        // 由于在setTargetOffsetTopAndBottom()中，是按照target.getTop()的方式计算距离，此处初始化时使用currentTargetOffsetTop来计算
        // 两者在finishSpinner瞬间值不相等，导致有时候scroll后，高度不对，所以在这里给currentTargetOffsetTop重新赋值，保持一致
        if (null != target && target.getTop() >= 0) {
            currentTargetOffsetTop = target.getTop();
        }

//        if (currentTargetOffsetTop > totalDragDistance) {
//            Log.i(TAG, "finishSpinner is run. totalDragDistance:" +totalDragDistance);
//            autoScroll.scrollTo(totalDragDistance, SCROLL_TO_REFRESH_DURATION);
//        } else {
        Log.i(TAG, "finishSpinner is run. 2");
        autoScroll.scrollTo(START_POSITION, SCROLL_TO_TOP_DURATION);
//        }
    }


    private void changeState(State state) {
        Log.i(TAG, "changeState is run.state:" + state);
        this.state = state;
    }

    private void setTargetOffsetTopAndBottom(int offset) {
        if (offset == 0) {
            return;
        }

        int targetTop = target.getTop();
        Log.i(TAG, "moveSpinner: targetTop:" + targetTop + ",offset:" + offset);
        if (offset < 0 && Math.abs(targetTop) < Math.abs(offset)) {
            offset = -targetTop;
        }
        target.offsetTopAndBottom(offset);

        lastTargetOffsetTop = currentTargetOffsetTop;
        currentTargetOffsetTop = targetTop;
        Log.i(TAG, "moveSpinner: currentTargetOffsetTop = "+ currentTargetOffsetTop + ",offset:" + offset);
        invalidate();
    }

    private void sendCancelEvent() {
        if (lastEvent == null) {
            return;
        }
        MotionEvent ev = MotionEvent.obtain(lastEvent);
        ev.setAction(MotionEvent.ACTION_CANCEL);
        super.dispatchTouchEvent(ev);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == activePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            lastMotionY = ev.getY(newPointerIndex);
            lastMotionX = ev.getX(newPointerIndex);
            activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (target instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) target;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return target.canScrollVertically(-1) || target.getScrollY() > 0;
            }
        } else {
            return target.canScrollVertically(-1);
        }
    }


    private class AutoScroll implements Runnable {
        private Scroller scroller;
        private int lastY;

        public AutoScroll() {
            scroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            boolean finished = !scroller.computeScrollOffset() || scroller.isFinished();
            if (!finished) {
                int currY = scroller.getCurrY();
                int offset = currY - lastY;
                lastY = currY;
                moveSpinner(offset);
                post(this);
                onScrollFinish(false);
            } else {
                stop();
                onScrollFinish(true);
            }
        }

        public void scrollTo(int to, int duration) {
            int from = currentTargetOffsetTop;
            int distance = to - from;
            stop();
            if (distance == 0) {
                return;
            }
            scroller.startScroll(0, 0, 0, distance, duration);
            post(this);
        }

        private void stop() {
            removeCallbacks(this);
            if (!scroller.isFinished()) {
                scroller.forceFinished(true);
            }
            lastY = 0;
        }
    }

    /**
     * 在scroll结束的时候会回调这个方法
     *
     * @param isForceFinish 是否是强制结束的
     */
    private void onScrollFinish(boolean isForceFinish) {
        if (isAutoRefresh && !isForceFinish) {
            isAutoRefresh = false;
//            changeState(LOADING);
            finishSpinner();
        }
    }

    public void addOnUserTouchListener(OnUserTouchListener listener) {
        if (onUserTouchListeners != null && listener != null) {
            onUserTouchListeners.add(listener);
        }
    }

    public void removeOnUserTouchListener(OnUserTouchListener listener) {
        if (onUserTouchListeners != null && listener != null) {
            onUserTouchListeners.remove(listener);
        }
    }

    /**
     * 用户触摸到通知中心面板时，表示用户已经操作过通知中心。
     *
     */
    public interface OnUserTouchListener{
        void onUserTouch(MotionEvent ev);
    }

    public enum State {
        //初始状态
        RESET,
        //下拉状态
        PULL,
        //加载中状态
        LOADING,
        //加载完成状态
        COMPLETE
    }

}
