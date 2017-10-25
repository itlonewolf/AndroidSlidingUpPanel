package com.sothree.slidinguppanel.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import com.sothree.slidinguppanel.log.Logger;

import java.util.ArrayList;

/**
 * Created by xiaoyee on 23/10/2017.
 */

public class ViewUtil {
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float dpTpPx(float dp, Context context) {
        Resources      resources = context.getResources();
        DisplayMetrics metrics   = resources.getDisplayMetrics();
        float          px        = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
    
    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float pxToDp(float px, Context context) {
        Resources      resources = context.getResources();
        DisplayMetrics metrics   = resources.getDisplayMetrics();
        float          dp        = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
    
    
    public static View getTouchTarget(View parrentView, int rawX, int rawY) {
        final ArrayList<View> touchableViews = parrentView.getTouchables();
        for (View touchableView : touchableViews) {
            if (isTouchPointInView(touchableView, rawX, rawY)) {
                if (Logger.isTagEnabled("drag")) {
                    Logger.d("drag", "此 view 在触摸范围内,且可以被点击 :%s", touchableView.getId());
                }
                
                return touchableView;
            }
        }
        
        return null;
    }
    
    /**
     * 查找 rawX 与 rawY 点中的 parent 中的子 view;如果 parent 是 view ,那么直接返回
     */
    public static View findChildUnderThis(View parent, int rawX, int rawY) {
        if (parent instanceof ViewGroup) {
            ViewGroup vgParent   = (ViewGroup) parent;
            final int childCount = vgParent.getChildCount();
            for (int index = 0; index < childCount; index++) {
                View childView = vgParent.getChildAt(index);
                if (isTouchPointInView(childView, rawX, rawY)) {
                    return childView;
                }
            }
            return null;
        }
        return parent;
    }
    
    /**
     * 是否在view的区域内
     */
    public static boolean isTouchPointInView(View view, int rawX, int rawY) {
        if (view == null) {
            return false;
        }
        
        if (view.getVisibility() == View.GONE) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left   = location[0];
        int top    = location[1];
        int right  = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        //view.isClickable() &&
        if (rawY >= top && rawY <= bottom && rawX >= left
                && rawX <= right) {
            return true;
        }
        return false;
    }
    
    
    public static boolean isViewUnder(View view, int x, int y) {
        if (x >= view.getLeft() && x < view.getRight() &&
                y >= view.getTop() && y < view.getBottom()) {
            return true;
        }
        return false;
    }
}
