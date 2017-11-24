package com.sothree.slidinguppanel.demo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.DisplayMetrics;

/**
 * Created by xiaoyee on 2017/11/14.
 * 可以刷新的 bean
 */

public abstract class ARefreshable implements IInteractive {
    IRefreshListener mRefreshListener;
    Rect mContentBound = new Rect();
    private int     id;
    private boolean isPressing;
    protected int screenWidth;//屏幕宽度（）
    public ARefreshable() {
        id = System.identityHashCode(this);
        DisplayMetrics dm = GlobalUtil.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
    }
    
    /**
     * 获取组件的唯一标识;默认算法是{@link System#identityHashCode(Object)}
     */
    public final int getId() {
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
    
    /**
     * 绘制整个组件
     */
    public final void drawContent(Canvas canvas) {
        //idea save 和 restore 的使用是防止在本组件中做的变换影响到其他组件
        
        //step 首先绘制点击区域
        drawPressingBounds(canvas);
        final int saveFlag = canvas.save();
        //step 然后再绘制内容区域
        drawContentInner(canvas);
        canvas.restoreToCount(saveFlag);
    }
    
    /**
     * 更新组件的宽高,由内部计算得出自身宽高
     * <p>
     * 正常来讲都是在 {@link #initAssemble()} 方法中,计算出组件的宽高后,以计算后的宽高为参数调用此方法,更新组件宽高
     * </p>
     *
     * @see #initAssemble()
     */
    protected void updateBoundsInner(int width, int height) {
        int left = mContentBound.left;
        int top  = mContentBound.top;
        mContentBound.set(left, top, left + width, top + height);
    }
    
    /**
     * 获取组件的内容区域
     */
    public Rect getContentBounds() {
        return mContentBound;
    }
    
    public abstract int height();
    
    public abstract int width();
    
    /**
     * 更新自身位置;由外部来觉得自身位置
     * @param left 左顶点位置
     * @param top  上顶点位置
     */
    public void updatePosition(int left, int top) {
        //idea 宽高以自身为准,位置以外部为准
        final int height = mContentBound.height();
        final int width  = mContentBound.width();
        mContentBound.set(left, top, left + width, top + height);
    }
    
    /**
     * 组件被点击之后的操作
     * <p>
     *     默认是无任何操作;如果想接受点击事件,那么首先覆写{@link #isInteractive()} 方法,且返回 true,并覆写本方法,且在本方法中添加相应操作
     * </p>
     */
    @Override
    public void onClick() {
        //do nothing
    }
    
    /**
     * 默认是不可交互的,如果想为此组件设置触摸反馈及点击事件,必须覆写此方法,且返回 true
     */
    @Override
    public boolean isInteractive() {
        return false;
    }

    /**
     *
     * @param isPressing
     */
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
    
    /**
     * 获取组件可交互的区域;默认可交互区域为整个组件
     */
    protected Rect getInteractiveBounds() {
        return mContentBound;
    }
    
    /**
     * 绘制按压时的区域
     */
    protected void drawPressingBounds(Canvas canvas) {
        if (isPressing()) {
            final int saveFlag = canvas.save();
            //idea 只绘制可交互的区域
            canvas.clipRect(getInteractiveBounds());
            canvas.drawColor(pressingColor());
            canvas.restoreToCount(saveFlag);
        }
    }
}