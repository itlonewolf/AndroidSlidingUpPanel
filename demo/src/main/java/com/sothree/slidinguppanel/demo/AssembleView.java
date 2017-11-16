package com.sothree.slidinguppanel.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * custom view
 */

public class AssembleView extends View {
    
    public static final int UNSET_POSTION = -1024;
    
    /**
     * 需要绘制的组件的集合
     */
    private TreeMap<Integer, ARefreshable> mRefreshables;
    
    /**
     * 所有可以点击或者触摸有效果的组件区域集合;
     * <ul>
     *      <li>key:{@link ARefreshable#getId()}</li>
     *      <li>value:{@link ARefreshable#getBounds()}</li>
     * </ul>
     */
    private SparseArray<Rect> mTouchableBounds;
    /**
     * 所有可以点击或者触摸有效果的组件的区域
     */
    private Region            mTouchableRegion;
    
    Rect mClipBounds;
    
    private float mInitialMotionX;
    private float mInitialMotionY;
    
    private GestureDetector mGestureDetector;
    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (pressingItem != null) {
                pressingItem.onClick();
                return true;
            }
            return super.onSingleTapConfirmed(e);
        }
        
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    };
    
    //idea 由内部自己实现了点击操作,所以不用再覆写 performClick 方法
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionX = x;
                mInitialMotionY = y;
                pressingItem = null;
    
                //step 1、先判断整个可点击区域中是否包含此触摸点
                if (mTouchableRegion.contains(x, y)) {
                    //step 2、如果包含,那么就要遍历出到底是哪个"可点击区域"被点中了
                    final int boundsSize = mTouchableBounds.size();
                    for (int i = 0; i < boundsSize; i++) {
                        Rect bounds = mTouchableBounds.valueAt(i);
                        if (bounds.contains(x, y)) {
                            final int          touchIndex = mTouchableBounds.keyAt(i);
                            final ARefreshable item       = mRefreshables.get(touchIndex);
                            item.setPressed(true);
                            pressingItem = item;
                            invalidate(item.getBounds());
                            //idea 找到之后就停止遍历
                            Log.d("AssembleViewDraw", String.format("pressing bounds:%s, we need refresh this area", item.getBounds()));
                            break;
                        }
                    }
                } else {
                    //不包含的话 do nothing
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final ViewConfiguration vc = ViewConfiguration.get(GlobalUtil.getContext());
                final int touchSlop = vc.getScaledTouchSlop();
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                //idea 在"滑动阈值"以内的也算是滑动
                final boolean underDragSlop = ady <= touchSlop && adx <= touchSlop;
                if (!underDragSlop) {
                    resetPressingItem();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                resetPressingItem();
                break;
            case MotionEvent.ACTION_UP:
                resetPressingItem();
                break;
        }
        
        return mGestureDetector.onTouchEvent(event);
    }
    
    
    private ARefreshable pressingItem = null;
    
    private void resetPressingItem() {
        if (pressingItem != null) {
            pressingItem.setPressed(false);
            invalidate(pressingItem.getBounds());
        }
    }
    
    private IRefreshListener mRefreshListener = new IRefreshListener() {
        @Override
        public void onRefresh(Rect refreshBounds) {
            invalidate(refreshBounds);
        }
    };
    
    public AssembleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initVariable();
    }
    
    public AssembleView(Context context) {
        super(context);
        initVariable();
    }
    
    private void initVariable() {
        mRefreshables = new TreeMap<>();
        mTouchableBounds = new SparseArray<>();
        mTouchableRegion = new Region();
        
        mClipBounds = new Rect();
    
        mGestureDetector = new GestureDetector(mGestureListener);
    }
    
    public void addRefreshableItem(ARefreshable item) {
        addRefreshableItem(item, UNSET_POSTION);
    }
    
    public void addRefreshableItem(ARefreshable item, int position) {
        
        item.initAssemble();
        item.addRefreshListener(mRefreshListener);
        
        if (position == UNSET_POSTION) {
            mRefreshables.put(item.getId(), item);
            return;
        }
        
        final int size = mRefreshables.size();
        if (position >= size || position < 0) {
            mRefreshables.put(item.getId(), item);
            return;
        }
    
        mRefreshables.put(item.getId(), item);
        // FIXME: 2017/11/15 判断是否需要 requestLayout
    }
    
    public void addRefreshableItem(ARefreshable... refreshableItems) {
        for (ARefreshable refreshable : refreshableItems) {
            addRefreshableItem(refreshable);
        }
    }
    
    /**
     * 设置新的组件集合;此操作会将之前的所有组件清空
     */
    public void setRefreshables(ArrayList<ARefreshable> items) {
        mRefreshables.clear();
        for (ARefreshable refreshable : items) {
            addRefreshableItem(refreshable);
        }
        requestLayout();
    }
    
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        
        final boolean hasClipBounds = canvas.getClipBounds(mClipBounds);
        
        if (hasClipBounds) {
            canvas.clipRect(mClipBounds);
            Log.d("AssembleViewDraw", String.format("有裁切区域,且裁切区域为:%s", mClipBounds));
    
            for (ARefreshable refreshable : mRefreshables.values()) {
                if (Rect.intersects(refreshable.getBounds(), mClipBounds)) {
                    refreshable.drawContent(canvas);
                }
            }
        } else {
            Log.d("AssembleDraw", String.format("没有 clip bounds: %s", mClipBounds));
            for (ARefreshable refreshable : mRefreshables.values()) {
                refreshable.drawContent(canvas);
            }
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("AssembleDraw", "onMeasure");
        
        int width  = MeasureSpec.getSize(widthMeasureSpec);
    
        int height = 0;
        for (ARefreshable refreshable : mRefreshables.values()) {
            height += refreshable.height();
        }
        
        setMeasuredDimension(width, height);
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d("AssembleDraw", "onLayout");
    
        int leftPos = 0;
        int topPos  = 0;
    
        for (ARefreshable refreshable : mRefreshables.values()) {
            refreshable.updatePosition(leftPos, topPos);
            if (refreshable.isClickable()) {
                mTouchableBounds.put(refreshable.getId(), refreshable.getBounds());
                mTouchableRegion.union(refreshable.getBounds());
            }
            topPos += refreshable.height();
        }
    }
}