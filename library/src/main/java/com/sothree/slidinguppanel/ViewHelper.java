package com.sothree.slidinguppanel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Created by xiaoyee on 11/10/2017.
 */

public class ViewHelper {
    /**
     * 查找最上层
     * @param rootView 父 view
     * @param x        触摸点 x
     * @param y        触摸点 y
     * @return 如果父 view 是可滚动的,那么直接返回;如果父 view 不是 ViewGroup 的子类,那么直接返回;如果是 ViewGroup 的子类,那么获取到 index 为 0 的子 view
     */
    public static View findScrollableViewUnder(View rootView, int x, int y) {
        if (rootView instanceof ScrollView || rootView instanceof ListView || rootView instanceof RecyclerView) {
            return rootView;
        }
        
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
