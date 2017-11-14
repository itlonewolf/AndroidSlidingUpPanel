package com.sothree.slidinguppanel.demo;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by zhaozy on 2017/11/9.
 * 可以被组装的
 */

public interface IAssembleable {
    
    void addRefreshListener(IRefreshListener listener);
    
    /**
     * 初始化组件
     * <p>
     * 计算宽高尺寸,初始化画笔等
     * </p>
     */
    void initAssemble();
    
    /**
     * 获取组件的宽度
     */
    float getWidth();
    
    /**
     * 获取组件的高度
     */
    float getHeight();
    
    Rect getRect();
    
    /**
     * 此方法在主线程中,且调用特别频繁,避免在此方法中 new 对象
     */
    void drawContent(Canvas canvas, Rect rect);
}
