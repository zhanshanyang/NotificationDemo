package com.yzs.demo.notificationdemo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 从顶部下滑的DrawerLayout
 */
public class TopDrawerLayout extends ViewGroup {
    private static final String TAG = "LeftDrawerLayout";

    private static final int MIN_DRAWER_MARGIN = 54; // dp
    /**
     * Minimum velocity that will be detected as a fling
     */
    private static final int MIN_FLING_VELOCITY = 400; // dips per second

    /**
     * drawer离父容器右边的最小外边距
     */
    private int mMinDrawerMargin;

    private View mDragDrawerView;
    private View mContentView;

    private ViewDragHelper mDragHelper;
    /**
     * drawer显示出来的占自身的百分比
     */
    private float mDrawerFraction;


    public TopDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setup drawer's minMargin
        final float density = getResources().getDisplayMetrics().density;
        final float minVel = MIN_FLING_VELOCITY * density;
        mMinDrawerMargin = (int) (MIN_DRAWER_MARGIN * density + 0.5f);

        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
        //设置edge_left track
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
        //设置minVelocity
        mDragHelper.setMinVelocity(minVel);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);

        View leftMenuView = getChildAt(1);
        MarginLayoutParams lp = (MarginLayoutParams)
                leftMenuView.getLayoutParams();

        final int drawerWidthSpec = getChildMeasureSpec(widthMeasureSpec,
                lp.leftMargin + lp.rightMargin,
                lp.width);
        final int drawerHeightSpec = getChildMeasureSpec(heightMeasureSpec,
                mMinDrawerMargin + lp.topMargin + lp.bottomMargin,
                lp.height);
        leftMenuView.measure(drawerWidthSpec, drawerHeightSpec);


        View contentView = getChildAt(0);
        lp = (MarginLayoutParams) contentView.getLayoutParams();
        final int contentWidthSpec = MeasureSpec.makeMeasureSpec(
                widthSize - lp.leftMargin - lp.rightMargin, MeasureSpec.EXACTLY);
        final int contentHeightSpec = MeasureSpec.makeMeasureSpec(
                heightSize - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY);
        contentView.measure(contentWidthSpec, contentHeightSpec);

        mDragDrawerView = leftMenuView;
        mContentView = contentView;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View menuView = mDragDrawerView;
        View contentView = mContentView;

        MarginLayoutParams lp = (MarginLayoutParams) contentView.getLayoutParams();
        contentView.layout(lp.leftMargin, lp.topMargin,
                lp.leftMargin + contentView.getMeasuredWidth(),
                lp.topMargin + contentView.getMeasuredHeight());

        lp = (MarginLayoutParams) menuView.getLayoutParams();

        final int menuHeight = menuView.getMeasuredHeight();
        int childTop = -menuHeight + (int) (menuHeight * mDrawerFraction);

        float newOffset = (float) (menuHeight + childTop) / menuHeight;

        Log.i(TAG, "onLayout: newOffset:" + newOffset + ",oldOffset:" + mDrawerFraction);

        menuView.layout(lp.leftMargin, childTop,
                lp.leftMargin + menuView.getMeasuredWidth(),
                childTop + menuHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    public void closeDrawer() {
        View menuView = mDragDrawerView;
        mDrawerFraction = 0.f;
        mDragHelper.smoothSlideViewTo(menuView, menuView.getLeft(), -menuView.getHeight());
    }

    public void openDrawer() {
        View menuView = mDragDrawerView;
        mDrawerFraction = 1.0f;
        mDragHelper.smoothSlideViewTo(menuView, menuView.getLeft(), 0);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return Math.max(-child.getHeight(), Math.min(top, 0));
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mDragDrawerView;
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            mDragHelper.captureChildView(mDragDrawerView, pointerId);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final int childHeight = releasedChild.getHeight();
            float offset = (childHeight + releasedChild.getTop()) * 1.0f / childHeight;

            Log.i(TAG, "onViewReleased: xvel:" + xvel + ",yvel:" + yvel + ",offset:" + offset);
            mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), yvel > 0 || yvel == 0 && offset > 0.5f ? 0 : -childHeight);

            invalidate();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            final int childHeight = changedView.getHeight();
            float offset = (float) (childHeight + top) / childHeight;

            mDrawerFraction = offset;
            //offset can callback here
            changedView.setVisibility(offset == 0 ? View.INVISIBLE : View.VISIBLE);
            invalidate();
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mDragDrawerView == child ? child.getWidth() : 0;
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return mDragDrawerView == child ? child.getHeight() : 0;
        }
    }

}
