package com.sothree.slidinguppanel.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * custom view
 */

public class AssembleView extends View {
    
    TitleBean    mTitleBean;
    DistanceBean mDistanceBean;
    
    Rect mClipBounds;
    
    public AssembleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    
    private IRefreshListener mRefreshListener = new IRefreshListener() {
        @Override
        public void onRefresh(Rect refreshBounds) {
            invalidate(refreshBounds);
        }
    };
    
    public AssembleView(Context context) {
        super(context);
        DisplayMetrics dm = GlobalUtil.getResources().getDisplayMetrics();
        mTitleBean = TitleBean.demoBean(dm.widthPixels);
        mTitleBean.initAssemble();
        mTitleBean.updatePosition(0, 0);
        
        mDistanceBean = DistanceBean.demoBean(dm.widthPixels);
        mDistanceBean.initAssemble();
        mDistanceBean.updatePosition(0, mTitleBean.height());
        
        mClipBounds = new Rect();
        
        mTitleBean.addRefreshListener(mRefreshListener);
        mDistanceBean.addRefreshListener(mRefreshListener);
    }
    
    public void refreshTitle() {
        mTitleBean.refresh();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        return super.onTouchEvent(event);
    }
    
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        
        final boolean hasClipBounds = canvas.getClipBounds(mClipBounds);
        
        if (hasClipBounds) {
            canvas.clipRect(mClipBounds);
            Log.d("AssembleDraw", String.format("有 clip bounds: %s", mClipBounds));
            if (Rect.intersects(mClipBounds, mTitleBean.getBounds())) {
                Log.d("AssembleDraw", "clip bounds 与 title 相交");
                mTitleBean.drawContent(canvas);
            } else {
                Log.d("AssembleDraw", "clip bounds 不与 title 相交");
            }
            if (Rect.intersects(mClipBounds, mDistanceBean.getBounds())) {
                Log.d("AssembleDraw", "clip bounds 与 distance 相交");
                mDistanceBean.drawContent(canvas);
            } else {
                Log.d("AssembleDraw", "clip bounds 不与 distance 相交");
            }
        } else {
            Log.d("AssembleDraw", String.format("没有 clip bounds: %s", mClipBounds));
    
            mTitleBean.drawContent(canvas);
            mDistanceBean.drawContent(canvas);
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("AssembleDraw", "onMeasure");
        
        int width  = MeasureSpec.getSize(widthMeasureSpec);
        int height = mDistanceBean.height() + mTitleBean.height();
        
        setMeasuredDimension(width, height);
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d("AssembleDraw", "onLayout");
    }
}