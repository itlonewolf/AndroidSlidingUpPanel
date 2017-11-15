package com.sothree.slidinguppanel.demo;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by xiaoyee on 2017/11/14.
 * 可以刷新的 bean
 */

public abstract class ARefreshable {
    IRefreshListener mRefreshListener;
    Rect mContentBound = new Rect();
    private View.OnClickListener mOnClickListener;
    private boolean mIsClickable = false;
    private int id;
    
    public ARefreshable() {
        id = System.identityHashCode(this);
    }
    
    public int getId() {
        return id;
    }
    
    public void setOnClickListener(View.OnClickListener listener) {
        this.mOnClickListener = listener;
        mIsClickable = true;
    }
    
    public void removeOnClickListener() {
        this.mOnClickListener = null;
        mIsClickable = false;
    }
    
    public boolean isClickable() {
        return mIsClickable;
    }
    
    public View.OnClickListener getOnClickListener() {
        return mOnClickListener;
    }
    
    public void addRefreshListener(IRefreshListener listener) {
        mRefreshListener = listener;
    }
    
    /**
     * 初始化组件
     * <p>
     * 计算宽高尺寸,初始化画笔等
     * </p>
     */
    abstract void initAssemble();
    
    public final void drawContent(Canvas canvas) {
        //idea save 和 restore 的使用是防止在本组件中做的变换影响到其他组件
        final int saveFlag = canvas.save();
        drawContentInner(canvas);
        canvas.restoreToCount(saveFlag);
    }
    
    protected void updateBoundsInner(int width, int height) {
        int left = mContentBound.left;
        int top  = mContentBound.top;
        mContentBound.set(left, top, left + width, top + height);
    }
    
    public Rect getBounds() {
        return mContentBound;
    }
    
    public abstract int height();
    
    public abstract int width();
    
    public void updatePosition(int left, int top) {
        //idea 宽高以自身为准,位置以外部为准
        final int height = mContentBound.height();
        final int width  = mContentBound.width();
        mContentBound.set(left, top, left + width, top + height);
    }
    
    protected abstract void drawContentInner(Canvas canvas);
}
