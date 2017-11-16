package com.sothree.slidinguppanel.demo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

/**
 * Created by xiaoyee on 2017/11/14.
 * 可以刷新的 bean
 */

public abstract class ARefreshable implements IInteractive {
    IRefreshListener mRefreshListener;
    Rect mContentBound = new Rect();
    private int     id;
    private boolean isPressing;
    
    public ARefreshable() {
        id = System.identityHashCode(this);
    }
    
    public int getId() {
        return id;
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
    
        //step 首先绘制点击区域
        drawPressingBounds(canvas);
        final int saveFlag = canvas.save();
        //step 然后再绘制内容区域
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
    
    @Override
    public void onClick() {
        //do nothing
    }
    
    @Override
    public boolean isClickable() {
        return false;
    }
    
    public void setPressed(boolean isPressing) {
        this.isPressing = isPressing;
    }
    
    @Override
    public boolean isPressing() {
        return isPressing;
    }
    
    @Override
    public int pressingColor() {
        return Color.LTGRAY;
    }
    
    protected abstract void drawContentInner(Canvas canvas);
    
    protected Rect getPressingBounds() {
        return mContentBound;
    }
    
    protected void drawPressingBounds(Canvas canvas) {
        if (isPressing()) {
            final int saveFlag = canvas.save();
            canvas.clipRect(getPressingBounds());
            canvas.drawColor(pressingColor());
            canvas.restoreToCount(saveFlag);
        }
    }
}
