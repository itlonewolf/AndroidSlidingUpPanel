package com.sothree.slidinguppanel.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * custom view
 */

public class AssembleView extends View {
    
    public static final int UNSET_POSTION = -1024;
    
    private ArrayList<ARefreshable> mRefreshables;
    private SparseArray<Rect>       mTouchableBounds;
    private Region                  mTouchableRegion;
    
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
    
        mRefreshables = new ArrayList<>();
        mTouchableBounds = new SparseArray<>();
        mTouchableRegion = new Region();
        
        mClipBounds = new Rect();
    }
    
    public void addRefreshableItem(ARefreshable item) {
        addRefreshableItem(item, UNSET_POSTION);
    }
    
    public void addRefreshableItem(ARefreshable item, int position) {
        
        item.initAssemble();
        item.addRefreshListener(mRefreshListener);
        
        if (position == UNSET_POSTION) {
            mRefreshables.add(item);
            return;
        }
        
        final int size = mRefreshables.size();
        if (position >= size || position < 0) {
            mRefreshables.add(item);
            return;
        }
        
        mRefreshables.add(position, item);
        // FIXME: 2017/11/15 判断是否需要 requestLayout
    }
    
    public void addRefreshableItem(ARefreshable... refreshableItems) {
        for (ARefreshable refreshable : refreshableItems) {
            addRefreshableItem(refreshable);
        }
    }
    
    public void setRefreshables(ArrayList<ARefreshable> items) {
        mRefreshables.clear();
        for (ARefreshable refreshable : items) {
            addRefreshableItem(refreshable);
        }
    }
    
    public void refreshTitle() {
        ARefreshable refreshable = mRefreshables.get(0);
        if (refreshable instanceof TitleBean) {
            TitleBean bean = (TitleBean) refreshable;
            bean.refresh();
        } else if (refreshable instanceof DistanceBean) {
            DistanceBean bean = (DistanceBean) refreshable;
            bean.refresh();
        }
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
    
            for (ARefreshable refreshable : mRefreshables) {
                if (Rect.intersects(refreshable.getBounds(), mClipBounds)) {
                    refreshable.drawContent(canvas);
                }
            }
        } else {
            Log.d("AssembleDraw", String.format("没有 clip bounds: %s", mClipBounds));
            for (ARefreshable refreshable : mRefreshables) {
                refreshable.drawContent(canvas);
            }
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("AssembleDraw", "onMeasure");
        
        int width  = MeasureSpec.getSize(widthMeasureSpec);
    
        int height = 0;
        for (ARefreshable refreshable : mRefreshables) {
            height += refreshable.height();
        }
        
        setMeasuredDimension(width, height);
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d("AssembleDraw", "onLayout");
    
        int leftPos = 0;
        int topPos  = 0;
    
        for (ARefreshable refreshable : mRefreshables) {
            refreshable.updatePosition(leftPos, topPos);
            mTouchableBounds.put(refreshable.getId(), refreshable.getBounds());
            mTouchableRegion.union(refreshable.getBounds());
            topPos += refreshable.height();
        }
    }
}