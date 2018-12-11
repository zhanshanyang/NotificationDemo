package com.yzs.demo.notificationdemo.utils;

import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;

public class InterpolatorUtils {

    /**
     * ControlPanelView's animator controlX/Y's points
     * 控制中心面板
     */
    public static final float[] ControlPanelViewPoints = new float[]{0.33f, 1.25f, 0.47f, 1f};
    /**
     * background's scrim
     * 背景遮罩
     */
    public static final float[] ControlPanelViewBGPoints = new float[]{0.33f, 0f, 0.12f, 1f};

    /**
     * generate interpolator by control's points
     * @param points control's points
     * @return interpolator
     */
    public static Interpolator pathInterpolator(float[] points) {
        if (points != null ) {
            if (points.length == 2) {
                return PathInterpolatorCompat.create(points[0], points[1]);
            } else if(points.length == 4) {
                return PathInterpolatorCompat.create(points[0], points[1], points[2], points[3]);
            }
        }
        return null;
    }


}
