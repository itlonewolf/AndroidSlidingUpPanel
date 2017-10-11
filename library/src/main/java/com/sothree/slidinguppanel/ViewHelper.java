package com.sothree.slidinguppanel;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xiaoyee on 11/10/2017.
 */

public class ViewHelper {
    /**
     * Find the topmost child under the given point within the parent view's coordinate system.
     * The child order is determined using {@link ViewDragHelper.Callback#getOrderedChildIndex(int)}.
     *
     * @param x X position to test in the parent's coordinate system
     * @param y Y position to test in the parent's coordinate system
     * @return The topmost child view under (x, y) or null if none found.
     */
    public static View findTopChildUnder(View rootView, int x, int y) {
        if (rootView instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) rootView;
            final int childCount = parentView.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = parentView.getChildAt(i);
                if (x >= child.getLeft() && x < child.getRight() &&
                        y >= child.getTop() && y < child.getBottom()) {
                    return child;
                }
            }
        } else {
            if (x >= rootView.getLeft() && x < rootView.getRight() &&
                    y >= rootView.getTop() && y < rootView.getBottom()) {
                return rootView;
            }
        }
        return null;
    }
}
