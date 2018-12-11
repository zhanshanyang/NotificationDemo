package com.yzs.demo.notificationdemo.viewdrag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class ViewDragLayout extends LinearLayout {

    private static final String TAG = "ViewDragLayout";

    private ViewDragHelper viewDragHelper;

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
    }

}
