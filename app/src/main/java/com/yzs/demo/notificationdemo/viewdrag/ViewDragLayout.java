package com.yzs.demo.notificationdemo.viewdrag;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 本ViewGroup的所以子childView不能设置onclick事件，否则不能被拖动，但是childView包裹的view可以设置点击事件
 */
public class ViewDragLayout extends LinearLayout {

    private static final String TAG = "ViewDragLayout";

    private ViewDragHelper viewDragHelper;
    private Point mAutoBackOriginPos = new Point();

    public ViewDragLayout(Context context) {
        super(context, null);
    }

    public ViewDragLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mAutoBackOriginPos.x = 10;
        mAutoBackOriginPos.y = 10;
    }

    public class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull android.view.View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            final int leftBound = getPaddingLeft();
            final int rightBound = getWidth() - getPaddingRight() - leftBound - child.getWidth();
            final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
            Log.i(TAG, "clampViewPositionHorizontal leftBound:" + leftBound + ",rightBound:" + rightBound + ",newLeft:" + newLeft + ",left:" + left + ",dx:" + dx);
            return newLeft;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - getPaddingBottom() - topBound - child.getHeight();
            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            Log.i(TAG, "clampViewPositionVertical topBound:" + topBound + ",bottomBound:" + bottomBound + ",newTop:" + newTop + "top:" + top + ",dy:" + dy);
            return newTop;
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            Log.i(TAG, "onViewReleased: xvel:" + xvel + ",yvel:" + yvel);
            viewDragHelper.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
            invalidate();
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
        }
    }

}
