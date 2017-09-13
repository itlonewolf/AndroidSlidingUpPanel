package com.sothree.slidinguppanel.demo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
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
    
//                return child == mDragableView;
                return true;
            }
    
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return child.getLeft();
            }
    
            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (getPaddingTop() > top) {
                    return getPaddingTop();
                }

                if (getHeight() - child.getHeight() < top) {
                    return getHeight() - child.getHeight();
                }
    
                log("ViewDragHelper.Callback  clampViewPositionVertical top >>> " + top);

//                return super.clampViewPositionVertical(child, top, dy);
                return top;
            }
            
    
            @Override
            public void onViewDragStateChanged(int state) {
                switch (finalTop) {
                    case FULL_POINT:
                        log("onViewDragStateChanged COLLAPSED");
                        changePanelState(PanelState.COLLAPSED);
                        break;
                    case ANCHOR_POINT:
                        log("onViewDragStateChanged ANCHORED");
                        changePanelState(PanelState.ANCHORED);
                        break;
                    case 0:
                        log("onViewDragStateChanged EXPANDED");
                        changePanelState(PanelState.EXPANDED);
                        break;
                }
                
                switch (state) {
                    case ViewDragHelper.STATE_IDLE:
                        log("STATE_IDLE");
                        break;
                    case ViewDragHelper.STATE_DRAGGING:
                        log("STATE_DRAGGING");
                        log("change to dragging");
                        changePanelState(PanelState.DRAGGING);
                        break;
                    case ViewDragHelper.STATE_SETTLING:
                        log("STATE_SETTLING");
                        break;
                }
                super.onViewDragStateChanged(state);
            }
    
            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                log("ViewDragHelper.Callback  onViewPositionChanged top >>> " + top);
                mSlideOffset = top;
                finalTop = top;
                logD("mSlideOffset >>> " + mSlideOffset);
                if (changedView == mDragableView) {
                    mDragableView.requestLayout();
                }
//                changePanelState(PanelState.DRAGGING);
//                log("change to dragging");
            }
    
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
//                log("yvel >>> " + yvel);
//                int targetTop;
//                if (yvel > 0) {
//                    //下拉
//                    targetTop = computeTopWhenDown();
//                } else {
//                    //上拉
//                    targetTop = computeTopWhenUp();
//                }
//                log(String.format("target top >> %s", targetTop));
//                finalTop = targetTop;
//                if (mDragHelper != null) {
//                    mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), targetTop);
//                }
                invalidate();
                requestLayout();
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
                            logD("on measure COLLAPSED height>>>" + height);
                            break;
//                        case ANCHORED:
//                            height = ANCHOR_POINT;
//                            break;
                        case DRAGGING:
                            height = getMeasuredHeight() - mSlideOffset;
                            logD("on measure DRAGGING height>>>" + height);
                            break;
                        default:
                            logD("on measure default  height>>>" + height);
                                break;
                                
                    }
//                    height = ANCHOR_POINT;
                    heightD = height;
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
//        } else {
//            log("非 DRAGGING");
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        }

    }
    
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
////        super.onLayout(changed, left, top, right, bottom);
//        int topf = Math.max(mSlideOffset, ANCHOR_POINT);
//        mDragableView.layout(getPaddingLeft(), topf, getPaddingRight(), topf + bottom);
//
//    }


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
    
//    private int computeTopWhenUp(){
//        return computeTopWhenDown();
//    }
    
    
    private int finalTop;
    private int mSlideOffset;
    private static final int ANCHOR_POINT = 980;
    private static final int FULL_POINT   = 1380;
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
    
        final float x = ev.getX();
        final float y = ev.getY();
        
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
//                break;
            case MotionEvent.ACTION_DOWN:
                mDragHelper.cancel();
                break;
        }
        if (mDragHelper.isViewUnder(mDragableView, (int) x, (int) y)) {
            
            if (mPanelState == PanelState.EXPANDED) {
                //idea 展开状态
                if (isListScroolEnd(mDragableView)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                //idea 非展开状态时由 viewGroup 处理,实现拖动等效果
                return true;
            }
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
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }
    
    private void logD(String info) {
        Log.d(this.getClass().getSimpleName(), info);
    }
    
    private void log(String info){
//        Log.d(this.getClass().getSimpleName(), info);
    }
}
