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
import java.util.LinkedHashMap;

/**
 * 组件集合
 */

public class AssembleView extends View {
    
    /**
     * 需要绘制的组件的集合
     */
    private LinkedHashMap<Integer, ARefreshable> mRefreshables;
    
    /**
     * 所有可以点击或者触摸有效果的组件区域集合;
     * <ul>
     *      <li>key:{@link ARefreshable#getId()}</li>
     *      <li>value:{@link ARefreshable#getContentBounds()}</li>
     * </ul>
     */
    private SparseArray<Rect> mTouchableBounds;
    /**
     * 所有可以点击或者触摸有效果的组件的区域
     */
    private Region            mTouchableRegion;
    
    /**
     * 当前要刷新的区域
     */
    Rect mClipBounds;
    
    private float mInitialMotionX;
    private float mInitialMotionY;
    
    private ViewConfiguration mViewConfiguration;
    private int               TOUCH_SLOP;
    private ARefreshable pressingItem = null;
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
                        final Rect bounds = mTouchableBounds.valueAt(i);
                        if (bounds.contains(x, y)) {
                            final int          touchIndex = mTouchableBounds.keyAt(i);
                            final ARefreshable item       = mRefreshables.get(touchIndex);
                            item.setPressed(true);
                            pressingItem = item;
                            invalidate(item.getContentBounds());
                            //idea 找到之后就停止遍历
                            Log.d("AssembleViewDraw", String.format("pressing bounds:%s, we need refresh this area", item.getContentBounds()));
                            break;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                //idea 在"滑动阈值"以内的也算是滑动
                final boolean underDragSlop = ady <= TOUCH_SLOP && adx <= TOUCH_SLOP;
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
    
    private void resetPressingItem() {
        if (pressingItem != null) {
            pressingItem.setPressed(false);
            invalidate(pressingItem.getContentBounds());
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
        mRefreshables = new LinkedHashMap<>();
        mTouchableBounds = new SparseArray<>();
        mTouchableRegion = new Region();
        
        mClipBounds = new Rect();
    
        mViewConfiguration = ViewConfiguration.get(GlobalUtil.getContext());
        TOUCH_SLOP = mViewConfiguration.getScaledTouchSlop();
        mGestureDetector = new GestureDetector(mGestureListener);
    }
    
    private void addItem(ARefreshable item) {
        item.initAssemble();
        item.addRefreshListener(mRefreshListener);
        mRefreshables.put(item.getId(), item);
    }
    
    /**
     * 初始化时添加组件使用此方法
     *
     * @param refreshableItems 初始状态要显示的 item
     */
    public void addItems(ARefreshable... refreshableItems) {
        mRefreshables.clear();
        for (ARefreshable refreshable : refreshableItems) {
            addItem(refreshable);
        }
    }
    
    
    /**
     * 初始化时添加组件使用此方法;
     * <p>
     * 注意此方法不会调用 {@link #requestLayout()} 方法,不会改变界面,只会在 {@link #onMeasure(int, int)} 和 {@link #onLayout(boolean, int, int, int, int)} 方法调用完之后才会生效
     * </p>
     *
     * @param items 初始状态要显示的 item
     */
    public void addItems(ArrayList<ARefreshable> items) {
        mRefreshables.clear();
        for (ARefreshable item : items) {
            addItem(item);
        }
    }
    
    /**
     * 设置新的组件集合;此操作会将之前的所有组件清空,并且会调用{@link #requestLayout()} 方法,刷新
     */
    public void setRefreshables(ArrayList<ARefreshable> items) {
        mRefreshables.clear();
        for (ARefreshable refreshable : items) {
            addItem(refreshable);
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
                //idea 要刷新的区域有可能是"横跨"几个区域的,所以要遍历整个集合
                if (Rect.intersects(refreshable.getContentBounds(), mClipBounds)) {
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
        //idea 计算容器 view 的整个高度(宽度为屏幕宽度)
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
            //step 首先更新各组件的位置
            refreshable.updatePosition(leftPos, topPos);
        
            //step 然后将可交互
            if (refreshable.isInteractive()) {
                mTouchableBounds.put(refreshable.getId(), refreshable.getContentBounds());
                mTouchableRegion.union(refreshable.getContentBounds());
            }
            topPos += refreshable.height();
        }
    }
}