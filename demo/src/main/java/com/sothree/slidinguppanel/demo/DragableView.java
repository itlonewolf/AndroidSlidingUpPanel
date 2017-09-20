package com.sothree.slidinguppanel.demo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * Created by xiaoyee on 12/09/2017.
 */

public class DragableView extends FrameLayout {
    ViewDragHelper mDragHelper;
    private View mDragableView;
    
    private PanelState mPanelState = PanelState.COLLAPSED;
    
    /**
     * Current state of the slideable view.
     */
    public enum PanelState {
        EXPANDED,
        COLLAPSED,
        ANCHORED,
        HIDDEN,
        DRAGGING
    }
    
    
    public DragableView(Context context) {
        super(context);
        initDragHelper();
    }
    
    public DragableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDragHelper();
    }
    
    public DragableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDragHelper();
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragableView = findViewWithTag("dragable_v");
        log("onFinishInflate>>>"+ mDragableView.getTop());
    }
    
    private void initDragHelper(){
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                Log.d("capture", "view call back tryCaptureView");
//                return child == mDragableView;
                return true;
            }
            
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return child.getLeft();
            }
            
            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                Log.d("capture", "top >>>" + top + ";; dy >>>" + dy);
                if (getPaddingTop() > top) {
                    return getPaddingTop();
                }
//
                if (getHeight() - 380 < top) {
                    return getHeight() - 380;
                }
//
//                log("ViewDragHelper.Callback  clampViewPositionVertical top >>> " + top);
//
////                return super.clampViewPositionVertical(child, top, dy);
                return top;


//                final int topBound = getPaddingTop();
//                final int bottomBound = getHeight() - mDragableView.getHeight();
//
//                final int newTop = Math.min(Math.max(top, topBound), bottomBound);
//
//                return newTop;
            }
            
            
            @Override
            public void onViewDragStateChanged(int state) {
                switch (finalTop) {
                    case FULL_POINT:
                        Log.d("capture", "onViewDragStateChanged COLLAPSED");
                        changePanelState(PanelState.COLLAPSED);
                        break;
                }
                
                switch (state) {
                    case ViewDragHelper.STATE_DRAGGING:
                        log("STATE_DRAGGING");
                        log("change to dragging");
                        changePanelState(PanelState.DRAGGING);
                        break;
                }
                super.onViewDragStateChanged(state);
            }
            
            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                Log.d("capture", "onViewPositionChanged >>> dy:" + dy);
                log("ViewDragHelper.Callback  onViewPositionChanged top >>> " + top);
                mSlideOffset = top;
                finalTop = top;
                if (finalTop <= 0) {
                    Log.d("caputre", "on view pos changed expanded 展开了");
                    changePanelState(PanelState.EXPANDED);
                }
                Log.d("capture", "mSlideOffset >>> " + mSlideOffset);
                if (changedView == mDragableView) {
                    mDragableView.requestLayout();
//                    invalidate();
                }
//                changePanelState(PanelState.DRAGGING);
//                log("change to dragging");
            }
            
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
//                log("yvel >>> " + yvel);
                int targetTop;
                if (yvel > 0) {
                    //下拉
                    targetTop = computeTopWhenDown();
                } else {
                    //上拉
                    targetTop = computeTopWhenUp();
                }
                Log.d("capture", String.format("target top >> %s", targetTop));
                finalTop = targetTop;
                if (mDragHelper != null) {
                    mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), targetTop);
                }
                invalidate();
                requestLayout();
            }
            
            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                Log.d("capture", "onViewCaptured child");
                super.onViewCaptured(capturedChild, activePointerId);
            }
        });
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
//        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0), resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
//    }
    
    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
        if (child == mDragableView) {
            log("measureChild >>> mDragableView");
        }
    }
    
    private int heightD;
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        log("on Measure ++++++++++++++++");
//        if (mPanelState == PanelState.DRAGGING) {
        final int widthMode  = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        
        final int widthSize  = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        int layoutWidth  = widthSize - getPaddingLeft() - getPaddingRight();
        int layoutHeight = heightSize - getPaddingTop() - getPaddingBottom();
        
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            
            int width = layoutWidth;
            int height = layoutHeight;
            
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            
            int childWidthSpec;
            if (params.width == LayoutParams.WRAP_CONTENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            } else if (params.width == LayoutParams.MATCH_PARENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
            }
            
            int childHeightSpec;
            
            
            if (child == mDragableView) {
                switch (mPanelState) {
//                        case EXPANDED:
//                            height = FULL_POINT;
//                            break;
                    case COLLAPSED:
                        height = 380;
//                            logD("on measure COLLAPSED height>>>" + height);
                        break;
//                        case ANCHORED:
//                            height = ANCHOR_POINT;
//                            break;
                    case DRAGGING:
                        Log.d("capture", "onMeasure slideState>>>" + slideState);
                        height = getMeasuredHeight() - mSlideOffset;
//                            logD("on measure DRAGGING height>>>" + height);
                        break;
                    default:
//                            logD("on measure default  height>>>" + height);
                        break;
                    
                }
//                    height = ANCHOR_POINT;
                heightD = height;
                cLogd("height onmeasure >>>" + height);
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//                    log("height = ANCHOR_POINT");
            
            } else {
//                    log("非 dragable view");
                if (params.height == LayoutParams.WRAP_CONTENT) {
                    childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
                } else if (params.height == LayoutParams.MATCH_PARENT) {
                    childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                } else {
                    childHeightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
                }
            }
            child.measure(childWidthSpec, childHeightSpec);
        }
        
        setMeasuredDimension(widthSize, heightSize);
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
        log("on Layout ++++++++++++++++");
        
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

//        if (mPanelState == PanelState.DRAGGING) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child == mDragableView) {
                cLogd("height onLayout >>>" + child.getMeasuredHeight());
                
                log("parent measure height>>>" + getMeasuredHeight());
                int childTop;
                
                if (mPanelState == PanelState.COLLAPSED) {
                    childTop = getMeasuredHeight() - 380;
                    child.layout(left, childTop, right, childTop + heightD);
                } else {
                    childTop = mSlideOffset;
                    child.layout(left, childTop, right, childTop + heightD);
                }

//                    if (mSlideOffset <= 300) {
//                        childTop = getMeasuredHeight() - 300;
//                        child.layout(left, getMeasuredHeight() - 300, right, childTop + heightD);
//                    } else {
//                        childTop = mSlideOffset;
//                        child.layout(left, childTop, right, childTop + heightD);
//                    }
                log(String.format("on layout left:%s, top:%s, right:%s, bottom:%s", left, top, right, bottom));
            } else {
                log("on layout 非 dragable view");
                child.layout(left, top, right, bottom);
            }
//                LayoutParams params = (LayoutParams) child.getLayoutParams();
//                int measurcdHeight = child.getMeasuredHeight();
        
        }
    }
    
    private void changePanelState(PanelState state) {
        if (mPanelState == state) {
            return;
        }
        mPanelState = state;
    }
    
    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    
    private int computeTopWhenDown(){
        if (mSlideOffset >= FULL_POINT) {
            return FULL_POINT;
        } else if (mSlideOffset > ANCHOR_POINT) {
            if (mSlideOffset > (FULL_POINT + ANCHOR_POINT) / 2) {
                return FULL_POINT;
            } else {
                return ANCHOR_POINT;
            }
        } else {
            if (mSlideOffset > ANCHOR_POINT / 2) {
                return ANCHOR_POINT;
            } else {
                return 0;
            }
        }
    }
    
    private int computeTopWhenUp(){
        return computeTopWhenDown();
    }
    
    
    private int finalTop;
    private int mSlideOffset;
    private static final int ANCHOR_POINT = 980;
    private static final int FULL_POINT   = 1380;
    
    
    
    private float lastY;
    
    private int slideState = SLIDE_IDLE;
    
    private static final int SLIDE_IDLE = 0;
    private static final int SLIDE_UP   = 1;
    private static final int SLIDE_DOWN = 2;
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        
        final float y = ev.getY();
        cLogd("dispatch touch event >>>y:" + y);
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
//                mDragHelper.cancel();
                logDD("ACTION_CANCEL");
                slideState = SLIDE_IDLE;
                lastY = 0;
                break;
            case MotionEvent.ACTION_DOWN:
//                mDragHelper.cancel();
                lastY = y;
                logDD("ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                
                if (y - lastY > 0) {
                    //下滑
                    slideState = SLIDE_DOWN;
                } else if (y - lastY < 0) {
                    //上滑
                    slideState = SLIDE_UP;
                } else {
                
                }
                lastY = y;
                logDD("ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                logDD("ACTION_UP");
                preY = 0;
                lastY = 0;
                slideState = SLIDE_IDLE;
                mDragHelper.setDragState(ViewDragHelper.STATE_IDLE);
                break;
            default:
                logDD("ACTION_" + action);
        }
        
        return super.dispatchTouchEvent(ev);
    }
    
    
    float preY;
    
    float currentDy;
    
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        
        final float x = ev.getX();
        final float y = ev.getY();
        logDD("on intercept >>>" + y);
        llogD("onInterceptTouchEvent currentDy>>>  " + currentDy);
        
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        
        if (mDragHelper.isViewUnder(mDragableView, (int) x, (int) y)) {
            
            Log.d("capture", "mPanelState >>" + mPanelState);
            
            if (mPanelState == PanelState.EXPANDED) {
                logD("展开状态 onInterceptTouchEvent");
                Log.d("capture", "展开状态 onInterceptTouchEvent");
                //idea 展开状态
                if (slideState == SLIDE_UP) {
                    return false;
                } else if (slideState == SLIDE_DOWN){
//                    return true;
//                    logD("onInterceptTouchEvent 下滑");
//                    //下滑
                    if (isListScrollTop(mDragableView)) {
                        Log.d("capture", "isListScrollEnd true");
                        // If the dragView is still dragging when we get here, we need to call processTouchEvent
                        // so that the view is settled
                        // Added to make scrollable views work (tokudu)
//                        if (mDragHelper.isDragging()) {
//                            mDragHelper.processTouchEvent(ev);
//                            return true;
//                        }
                        return true;
                    } else {
                        Log.d("capture", "isListScrollEnd false");
                        return false;
                    }
                }
            } else {
                Log.d("capture", "非展开状态");
                logD("非展开状态");
                //idea 非展开状态时由 viewGroup 处理,实现拖动等效果
                return true;
            }
        } else {
            logD("卧槽,竟然不是 list view 的一部分???");
        }
        
        
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }
    
    private boolean isListScroolEnd(View view) {
        ListView list = (ListView) view;
        if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1 &&
                list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isListScrollTop(View view) {
        if (!(view instanceof ListView)) {
            return true;
        }
        ListView listView = (ListView) view;
        if (listView.getChildCount() == 0) {
            return true;
        }
        return listView.getFirstVisiblePosition() <= 1;
//        return listView.getChildAt(0).getTop() == 0;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }
    
    private void cLogd(String info) {
        Log.d(this.getClass().getSimpleName(), info);
    }
    
    private void llogD(String info) {
//        Log.d(this.getClass().getSimpleName(), info);
    }
    
    private void logDD(String info) {
//        Log.d(this.getClass().getSimpleName(), info);
    }
    
    private void logD(String info) {
        Log.d(this.getClass().getSimpleName(), info);
    }
    
    private void log(String info){
//        Log.d(this.getClass().getSimpleName(), info);
    }
}
