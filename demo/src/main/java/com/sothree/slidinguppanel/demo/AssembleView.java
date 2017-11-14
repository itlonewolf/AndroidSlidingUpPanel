package com.sothree.slidinguppanel.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * custom view
 */

public class AssembleView extends View {
    TitleBean    mTitleBean;
    DistanceBean mDistanceBean;
    Drawable     starA;
    
    Rect mClipBounds;
    
    private int DP30  = LayoutUtils.getPxByDimens(R.dimen.dp30);
    private int DP110 = LayoutUtils.getPxByDimens(R.dimen.dp110);
    private int DP44  = LayoutUtils.getPxByDimens(R.dimen.dp44);
    private int DP15  = LayoutUtils.getPxByDimens(R.dimen.dp15);
    
    public AssembleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    
    private int viewWidth;
    
    Rect titleRect    = new Rect();
    Rect distanceRect = new Rect();
    
    private IRefreshListener mRefreshListener = new IRefreshListener() {
        @Override
        public void onRefresh(Rect refreshBounds) {
            invalidate(refreshBounds);
        }
    };
    
    public AssembleView(Context context) {
        super(context);
        DisplayMetrics dm = GlobalUtil.getResources().getDisplayMetrics();
        viewWidth = dm.widthPixels;
        mTitleBean = TitleBean.demoBean(dm.widthPixels);
        mTitleBean.initAssemble();
        
        mDistanceBean = DistanceBean.demoBean(dm.widthPixels);
        mDistanceBean.initAssemble();
        
        starA = GlobalUtil.getResources().getDrawable(R.drawable.star_1);

//        setClipBounds(new Rect(0, 0, 400, 400));
        mClipBounds = new Rect();
        
        titleRect.set(0, 0, viewWidth, DP110);
        distanceRect.set(0, DP110, viewWidth, DP110 + DP44);
        Log.d("AssembleDraw", String.format("title bounds: %s", titleRect));
        Log.d("AssembleDraw", String.format("distance bounds: %s", distanceRect));
    
        mTitleBean.addRefreshListener(mRefreshListener);
        mDistanceBean.addRefreshListener(mRefreshListener);
    }
    
    public void refreshTitle() {
//        invalidate(titleRect);
//        invalidate();
//        postInvalidate();
//        invalidate(titleRect);
        mTitleBean.refresh();
    }
    
    boolean isFirst = true;
    
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        
        final boolean hasClipBounds = canvas.getClipBounds(mClipBounds);
        
        if (hasClipBounds) {
            canvas.clipRect(mClipBounds);
            Log.d("AssembleDraw", String.format("有 clip bounds: %s", mClipBounds));
            if (Rect.intersects(mClipBounds, titleRect)) {
                Log.d("AssembleDraw", "clip bounds 与 title 相交");
                if (!isFirst) {
                    canvas.drawColor(Color.GRAY);
                }
                mTitleBean.drawContent(canvas, titleRect);
            } else {
                Log.d("AssembleDraw", "clip bounds 不与 title 相交");
            }
            if (Rect.intersects(mClipBounds, distanceRect)) {
                Log.d("AssembleDraw", "clip bounds 与 distance 相交");
                mDistanceBean.drawContent(canvas, distanceRect);
            } else {
                Log.d("AssembleDraw", "clip bounds 不与 distance 相交");
            }
        } else {
            Log.d("AssembleDraw", String.format("没有 clip bounds: %s", mClipBounds));
            
            mTitleBean.drawContent(canvas, titleRect);
            mDistanceBean.drawContent(canvas, distanceRect);
        }
        isFirst = false;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("AssembleDraw", "onMeasure");
        
        int width  = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (mDistanceBean.getHeight() + mTitleBean.getHeight());
        
        setMeasuredDimension(width, height);
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d("AssembleDraw", "onLayout");
    }
}
