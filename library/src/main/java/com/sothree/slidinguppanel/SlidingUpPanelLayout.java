package com.sothree.slidinguppanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.orhanobut.logger.Logger;
import com.sothree.slidinguppanel.library.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class SlidingUpPanelLayout extends ViewGroup {

    private static final String TAG = SlidingUpPanelLayout.class.getSimpleName();

    /**
     * Default peeking out panel height
     */
    private static final int DEFAULT_PANEL_HEIGHT = 68; // dp;

    /**
     * Default anchor point height
     */
    private static final float DEFAULT_ANCHOR_POINT = 1.0f; // In relative %

    /**
     * Default initial state for the component
     */
    private static PanelState DEFAULT_SLIDE_STATE = PanelState.COLLAPSED;

    /**
     * Default height of the shadow above the peeking out panel
     */
    private static final int DEFAULT_SHADOW_HEIGHT = 4; // dp;

    /**
     * If no fade color is given by default it will fade to 80% gray.
     */
    private static final int DEFAULT_FADE_COLOR = 0x99000000;

    /**
     * Default Minimum velocity that will be detected as a fling
     */
    private static final int DEFAULT_MIN_FLING_VELOCITY = 400; // dips per second
    /**
     * Default is set to false because that is how it was written
     */
    private static final boolean DEFAULT_OVERLAY_FLAG = false;
    /**
     * Default is set to true for clip panel for performance reasons
     */
    private static final boolean DEFAULT_CLIP_PANEL_FLAG = true;
    /**
     * Default attributes for layout
     */
    private static final int[] DEFAULT_ATTRS = new int[]{
            android.R.attr.gravity
    };
    /**
     * Tag for the sliding state stored inside the bundle
     */
    public static final String SLIDING_STATE = "sliding_state";

    /**
     * Minimum velocity that will be detected as a fling
     */
    private int mMinFlingVelocity = DEFAULT_MIN_FLING_VELOCITY;

    /**
     * The fade color used for the panel covered by the slider. 0 = no fading.
     */
    private int mCoveredFadeColor = DEFAULT_FADE_COLOR;

    /**
     * Default parallax length of the main view
     */
    private static final int DEFAULT_PARALLAX_OFFSET = 0;

    /**
     * The paint used to dim the main layout when sliding
     */
    private final Paint mCoveredFadePaint = new Paint();

    /**
     * Drawable used to draw the shadow between panes.
     */
    private final Drawable mShadowDrawable;

    /**
     * The size of the overhang in pixels.
     */
    private int mPanelHeight = -1;

    /**
     * The size of the shadow in pixels.
     */
    private int mShadowHeight = -1;

    /**
     * Parallax offset
     */
    private int mMainViewParallaxOffset = -1;

    /**
     * True if the collapsed panel should be dragged up.
     */
    private boolean mIsSlidingUp;

    /**
     * Panel overlays the windows instead of putting it underneath it.
     */
    private boolean mOverlayContent = DEFAULT_OVERLAY_FLAG;

    /**
     * The main view is clipped to the main top border
     */
    private boolean mClipPanel = DEFAULT_CLIP_PANEL_FLAG;

    /**
     * If provided, the panel can be dragged by only this view. Otherwise, the entire panel can be
     * used for dragging.
     */
    private View mDragView;

    /**
     * If provided, the panel can be dragged by only this view. Otherwise, the entire panel can be
     * used for dragging.
     */
    private int mDragViewResId = -1;
    
    private Set<Integer> mScrollableIds ;
    private ScrollableViewHelper mScrollableViewHelper = new ScrollableViewHelper();

    /**
     * The child view that can slide, if any.
     */
    private View mSlideableView;
    private int mSlideableViewResId = -1;

    /**
     * The main view
     */
    private View mMainView;
    private int mMainViewResId = -1;
    
    private View mParallaxView;
    private int mParallaxViewResId = -1;

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

    private PanelState mSlideState = DEFAULT_SLIDE_STATE;

    /**
     * If the current slide state is DRAGGING, this will store the last non dragging state
     */
    private PanelState mLastNotDraggingSlideState = DEFAULT_SLIDE_STATE;

    /**
     * How far the panel is offset from its expanded position.
     * range [0, 1] where 0 = collapsed, 1 = expanded.
     * 0 代表完全收起; 1 代表完全展开
     * //idea 为啥要使用比例而不是直接的数值?
     * //idea 比例适用范围更广,而且在与其他动作进行并发时,可以很容易的进行变换,不论是距离类型的动
     */
    private float mSlideOffset;

    /**
     * How far in pixels the slideable panel may move.
     * 从完全收起到完全展开之间的范围,以像素为单位
     */
    private int mSlideRange;

    /**
     * An anchor point where the panel can stop during sliding
     */
    private float mAnchorPoint = 1.f;

    /**
     * A panel view is locked into internal scrolling or another condition that
     * is preventing a drag.
     */
    private boolean mIsUnableToDrag;

    /**
     * Flag indicating that sliding feature is enabled\disabled
     */
    private boolean mIsTouchEnabled;

    private float mPrevMotionY;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private boolean mIsScrollableViewHandlingTouch = false;

    private final List<PanelSlideListener> mPanelSlideListeners = new CopyOnWriteArrayList<>();
    private View.OnClickListener mFadeOnClickListener;

    private final ViewDragHelper mDragHelper;

    /**
     * Stores whether or not the pane was expanded the last time it was slideable.
     * If expand/collapse operations are invoked this state is modified. Used by
     * instance state save/restore.
     */
    private boolean mFirstLayout = true;
    
    /**
     * 裁切遮盖部分时使用的 windowRect 对象
     */
    private final Rect mTmpRect = new Rect();

    /**
     * Listener for monitoring events about sliding panes.
     */
    public interface PanelSlideListener {
        /**
         * Called when a sliding pane's position changes.
         *
         * @param panel       The child view that was moved
         * @param slideOffset The new offset of this sliding pane within its range, from 0-1
         */
        void onPanelSlide(View panel, float slideOffset);

        /**
         * Called when a sliding panel state changes
         *
         * @param panel The child view that was slid to an collapsed position
         */
        void onPanelStateChanged(View panel, PanelState previousState, PanelState newState);
    }

    /**
     * No-op stubs for {@link PanelSlideListener}. If you only want to implement a subset
     * of the listener methods you can extend this instead of implement the full interface.
     */
    public static class SimplePanelSlideListener implements PanelSlideListener {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
        }

        @Override
        public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        }
    }

    public SlidingUpPanelLayout(Context context) {
        this(context, null);
    }

    public SlidingUpPanelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (isInEditMode()) {
            mShadowDrawable = null;
            mDragHelper = null;
            return;
        }

        
        Interpolator scrollerInterpolator = null;
        if (attrs != null) {
            TypedArray defAttrs = context.obtainStyledAttributes(attrs, DEFAULT_ATTRS);

            if (defAttrs != null) {
                int gravity = defAttrs.getInt(0, Gravity.NO_GRAVITY);
                setGravity(gravity);
                defAttrs.recycle();
            }


            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingUpPanelLayout);

            if (ta != null) {
                mPanelHeight = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_umanoPanelHeight, -1);
                mShadowHeight = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_umanoShadowHeight, -1);
                mMainViewParallaxOffset = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_umanoParallaxOffset, -1);

                mMinFlingVelocity = ta.getInt(R.styleable.SlidingUpPanelLayout_umanoFlingVelocity, DEFAULT_MIN_FLING_VELOCITY);
                mCoveredFadeColor = ta.getColor(R.styleable.SlidingUpPanelLayout_umanoFadeColor, DEFAULT_FADE_COLOR);

                mDragViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoDragView, -1);
    
                mMainViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoMainView, -1);
                mSlideableViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoSlideableView, -1);
                mParallaxViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoParallaxView, -1);

                mOverlayContent = ta.getBoolean(R.styleable.SlidingUpPanelLayout_umanoOverlay, DEFAULT_OVERLAY_FLAG);
                mClipPanel = ta.getBoolean(R.styleable.SlidingUpPanelLayout_umanoClipPanel, DEFAULT_CLIP_PANEL_FLAG);

                mAnchorPoint = ta.getFloat(R.styleable.SlidingUpPanelLayout_umanoAnchorPoint, DEFAULT_ANCHOR_POINT);

                mSlideState = PanelState.values()[ta.getInt(R.styleable.SlidingUpPanelLayout_umanoInitialState, DEFAULT_SLIDE_STATE.ordinal())];

                int interpolatorResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoScrollInterpolator, -1);
                if (interpolatorResId != -1) {
                    scrollerInterpolator = AnimationUtils.loadInterpolator(context, interpolatorResId);
                }
                ta.recycle();
            }
        }

        final float density = context.getResources().getDisplayMetrics().density;
        if (mPanelHeight == -1) {
            mPanelHeight = (int) (DEFAULT_PANEL_HEIGHT * density + 0.5f);
        }
        if (mShadowHeight == -1) {
            mShadowHeight = (int) (DEFAULT_SHADOW_HEIGHT * density + 0.5f);
        }
        if (mMainViewParallaxOffset == -1) {
            mMainViewParallaxOffset = (int) (DEFAULT_PARALLAX_OFFSET * density);
        }
        // If the shadow height is zero, don't show the shadow
        if (mShadowHeight > 0) {
            if (mIsSlidingUp) {
                mShadowDrawable = getResources().getDrawable(R.drawable.above_shadow);
            } else {
                mShadowDrawable = getResources().getDrawable(R.drawable.below_shadow);
            }
        } else {
            mShadowDrawable = null;
        }

        setWillNotDraw(false);

        mDragHelper = ViewDragHelper.create(this, 0.5f, scrollerInterpolator, new DragHelperCallback());
        mDragHelper.setMinVelocity(mMinFlingVelocity * density);

        mIsTouchEnabled = true;
    }

    /**
     * 设置各个 view
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mMainViewResId != -1) {
            mMainView = findViewById(mMainViewResId);
        }
    
        if (mSlideableViewResId != -1) {
            mSlideableView = findViewById(mSlideableViewResId);
        }
    
        if (mParallaxViewResId != -1) {
            mParallaxView = findViewById(mParallaxViewResId);
        }
        
        if (mDragViewResId != -1) {
            setDragView(findViewById(mDragViewResId));
        }
    
        if (mSlideableView == null) {
            throw new IllegalStateException("必须指定 slideable view");
        }
    
        Log.d("inflate", "onFinishInflate");
    }

    public void setGravity(int gravity) {
        if (gravity != Gravity.TOP && gravity != Gravity.BOTTOM) {
            throw new IllegalArgumentException("gravity must be set to either top or bottom");
        }
        mIsSlidingUp = gravity == Gravity.BOTTOM;
        if (!mFirstLayout) {
            requestLayout();
        }
    }

    /**
     * Set the color used to fade the pane covered by the sliding pane out when the pane
     * will become fully covered in the expanded state.
     *
     * @param color An ARGB-packed color value
     */
    public void setCoveredFadeColor(int color) {
        mCoveredFadeColor = color;
        requestLayout();
    }

    /**
     * @return The ARGB-packed color value used to fade the fixed pane
     */
    public int getCoveredFadeColor() {
        return mCoveredFadeColor;
    }

    /**
     * Set sliding enabled flag
     *
     * @param enabled flag value
     */
    public void setTouchEnabled(boolean enabled) {
        mIsTouchEnabled = enabled;
    }

    public boolean isTouchEnabled() {
        Log.d("dispatch", String.format("mIsTouchEnabled: %s, mSlideableView != null:%s, mSlideState != PanelState.HIDDEN:%s", mIsTouchEnabled, mSlideableView != null, mSlideState != PanelState.HIDDEN));
        return mIsTouchEnabled && mSlideableView != null && mSlideState != PanelState.HIDDEN;
    }

    /**
     * Set the collapsed panel height in pixels
     *
     * @param val A height in pixels
     */
    public void setPanelHeight(int val) {
        if (getPanelHeight() == val) {
            return;
        }

        mPanelHeight = val;
        if (!mFirstLayout) {
            requestLayout();
        }

        if (getPanelState() == PanelState.COLLAPSED) {
            smoothToBottom();
            invalidate();
        }
    }

    protected void smoothToBottom() {
        smoothSlideTo(0, 0);
    }

    /**
     * @return The current shadow height
     */
    public int getShadowHeight() {
        return mShadowHeight;
    }

    /**
     * Set the shadow height
     *
     * @param val A height in pixels
     */
    public void setShadowHeight(int val) {
        mShadowHeight = val;
        if (!mFirstLayout) {
            invalidate();
        }
    }

    /**
     * @return The current collapsed panel height
     */
    public int getPanelHeight() {
        return mPanelHeight;
    }

    /**
     * @return The current parallax offset
     */
    public int getCurrentParallaxOffset() {
        // Clamp slide offset at zero for parallax computation;
        int offset = (int) (mMainViewParallaxOffset * Math.max(mSlideOffset, 0));
        return mIsSlidingUp ? -offset : offset;
    }

    /**
     * Set parallax offset for the panel
     *
     * @param val A height in pixels
     */
    public void setMainViewParallaxOffset(int val) {
        mMainViewParallaxOffset = val;
        if (!mFirstLayout) {
            requestLayout();
        }
    }

    /**
     * @return The current minimin fling velocity
     */
    public int getMinFlingVelocity() {
        return mMinFlingVelocity;
    }

    /**
     * Sets the minimum fling velocity for the panel
     *
     * @param val the new value
     */
    public void setMinFlingVelocity(int val) {
        mMinFlingVelocity = val;
    }

    /**
     * Adds a panel slide listener
     */
    public void addPanelSlideListener(PanelSlideListener listener) {
        synchronized (mPanelSlideListeners) {
            mPanelSlideListeners.add(listener);
        }
    }

    /**
     * Removes a panel slide listener
     */
    public void removePanelSlideListener(PanelSlideListener listener) {
        synchronized (mPanelSlideListeners) {
            mPanelSlideListeners.remove(listener);
        }
    }

    /**
     * Provides an on click for the portion of the main view that is dimmed. The listener is not
     * triggered if the panel is in a collapsed or a hidden position. If the on click listener is
     * not provided, the clicks on the dimmed area are passed through to the main layout.
     */
    public void setFadeOnClickListener(View.OnClickListener listener) {
        mFadeOnClickListener = listener;
    }

    /**
     * Set the draggable view portion. Use to null, to allow the whole panel to be draggable
     *
     * @param dragView A view that will be used to drag the panel.
     */
    public void setDragView(View dragView) {
        if (mDragView != null) {
            mDragView.setOnClickListener(null);
        }
        mDragView = dragView;
        if (mDragView != null) {
            mDragView.setClickable(true);
            mDragView.setFocusable(false);
            mDragView.setFocusableInTouchMode(false);
            mDragView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEnabled() || !isTouchEnabled()) return;
                    if (mSlideState != PanelState.EXPANDED && mSlideState != PanelState.ANCHORED) {
                        if (mAnchorPoint < 1.0f) {
                            setPanelState(PanelState.ANCHORED);
                        } else {
                            setPanelState(PanelState.EXPANDED);
                        }
                    } else {
                        setPanelState(PanelState.COLLAPSED);
                    }
                }
            });
        }
    }

    /**
     * Set the draggable view portion. Use to null, to allow the whole panel to be draggable
     *
     * @param dragViewResId The resource ID of the new drag view
     */
    public void setDragView(int dragViewResId) {
        mDragViewResId = dragViewResId;
        setDragView(findViewById(dragViewResId));
    }
    
    /**
     * 添加可滚动的 view,支持多个;如果不设置,默认认为 umanoSlideableView 指定的 view 为可滚动的
     */
    public void addScrollableViewId(@IdRes int...scrollableViewIds){
        if (mScrollableIds == null) {
            mScrollableIds = new HashSet<>();
        }
        for (int resId : scrollableViewIds) {
            if (!mScrollableIds.contains(resId)) {
                mScrollableIds.add(resId);
            }
        }
    }

    /**
     * Sets the current scrollable view helper. See ScrollableViewHelper description for details.
     */
    public void setScrollableViewHelper(ScrollableViewHelper helper) {
        mScrollableViewHelper = helper;
    }

    /**
     * Set an anchor point where the panel can stop during sliding
     *
     * @param anchorPoint A value between 0 and 1, determining the position of the anchor point
     *                    starting from the top of the layout.
     */
    public void setAnchorPoint(@FloatRange(from = 0,fromInclusive = false, to = 1.0f) float anchorPoint) {
        if (anchorPoint > 0 && anchorPoint <= 1) {
            mAnchorPoint = anchorPoint;
            mFirstLayout = true;
            requestLayout();
        }
    }

    /**
     * Gets the currently set anchor point
     *
     * @return the currently set anchor point
     */
    public float getAnchorPoint() {
        return mAnchorPoint;
    }

    /**
     * Sets whether or not the panel overlays the content
     * 是否将 slideable view 覆盖在 main view 上;一般用在 slideable view 使用半透明效果时
     */
    public void setOverlayed(boolean overlayed) {
        mOverlayContent = overlayed;
    }

    /**
     * Check if the panel is set as an overlay.
     */
    public boolean isOverlayed() {
        return mOverlayContent;
    }

    /**
     * Sets whether or not the main content is clipped to the top of the panel
     */
    public void setClipPanel(boolean clip) {
        mClipPanel = clip;
    }

    /**
     * Check whether or not the main content is clipped to the top of the panel
     */
    public boolean isClipPanel() {
        return mClipPanel;
    }


    void dispatchOnPanelSlide(View panel) {
        synchronized (mPanelSlideListeners) {
            for (PanelSlideListener l : mPanelSlideListeners) {
                l.onPanelSlide(panel, mSlideOffset);
            }
        }
    }


    void dispatchOnPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        synchronized (mPanelSlideListeners) {
            for (PanelSlideListener l : mPanelSlideListeners) {
                l.onPanelStateChanged(panel, previousState, newState);
            }
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }
    
    /**
     * 更新被遮盖住的 view 的显示状态
     */
    void updateObscuredViewVisibility() {
        //idea SUP 初衷应该是在 sliding view 完全遮挡住 main view 时,将 main view 设置为 INVISIBLE,目前只对 main view 做支持
        if (mMainView == null) {
            return;
        }
    
        final int height = getHeight();
        final int width  = getWidth();
    
        //1865,1080
        Log.d("update", String.format("height:%s, width:%s", height, width));
        
        final int leftBound   = getPaddingLeft();
        final int topBound    = getPaddingTop();
        final int rightBound  = width - getPaddingRight();
        final int bottomBound = height - getPaddingBottom();
    
        //0,1080,1678,3543
        Log.d("update", String.format("leftBound:%s, topBound:%s, rightBound:%s, bottomBound:%s", leftBound, topBound, rightBound, bottomBound));
        
        int left;
        int right;
        int top;
        int bottom;
        
        if (mSlideableView != null && hasOpaqueBackground(mSlideableView)) {
            left    = mSlideableView.getLeft();
            top     = mSlideableView.getTop();
            right   = mSlideableView.getRight();
            bottom  = mSlideableView.getBottom();
    
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (mParallaxView != null) {
                    top = (int) mParallaxView.getY();
                }
            }
        } else {
            left = right = top = bottom = 0;
        }
        //0,1080,1678,3545
        Log.d("update", String.format("left:%s, top:%s, top:%s, bottom:%s", left, top, right, bottom));
        View child = mMainView;
        final int clampedChildLeft   = Math.max(leftBound,   child.getLeft());
        final int clampedChildTop    = Math.max(topBound,    child.getTop());
        final int clampedChildRight  = Math.min(rightBound,  child.getRight());
        final int clampedChildBottom = Math.min(bottomBound, child.getBottom());
        
        //0,1678,1080,1865
        Log.d("update", String.format("clampedChildLeft:%s, clampedChildTop:%s, clampedChildRight:%s, clampedChildBottom:%s", clampedChildLeft, clampedChildTop, clampedChildRight, clampedChildBottom));
        
        final int vis;
        if (clampedChildLeft >= left && clampedChildTop >= top &&
                clampedChildRight <= right && clampedChildBottom <= bottom) {
            //idea 如果完全被挡住了,那么就设置为 INVISIBLE
            Log.d("update", "set child invisible");
            vis = INVISIBLE;
        } else {
            vis = VISIBLE;
        }
        child.setVisibility(vis);
    }

    void setAllChildrenVisible() {
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == INVISIBLE) {
                child.setVisibility(VISIBLE);
            }
        }
    }
    
    /**
     * 是否有一个不透明的背景
     */
    private static boolean hasOpaqueBackground(View v) {
        final Drawable bg = v.getBackground();
        if (bg != null) {
            Log.d("backgroundYee", "含有 background");
            switch (bg.getOpacity()) {
                case PixelFormat.OPAQUE:
                    Log.d("backgroundYee", "Opacity >>> OPAQUE");
                    break;
                case PixelFormat.TRANSLUCENT:
                    Log.d("backgroundYee", "Opacity >>> TRANSLUCENT");
                    break;
                case PixelFormat.TRANSPARENT:
                    Log.d("backgroundYee", "Opacity >>> TRANSPARENT");
                    break;
                default:
                    Log.d("backgroundYee", "Opacity >>> unknown");
                    break;
            }
        } else {
            Log.d("backgroundYee", "无 background");
        }
        return bg != null && bg.getOpacity() == PixelFormat.OPAQUE;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFirstLayout = true;
    }
    
    
    boolean isMeasureMehod = false;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    
        Logger.t("SUPYEEs").d("onMeasureonMeasure >>>heightSize : %s", heightSize);
    
        Log.d("onMeasureYee", String.format("heightSize : %s", heightSize));

        if (widthMode != MeasureSpec.EXACTLY && widthMode != MeasureSpec.AT_MOST) {
            throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
        } else if (heightMode != MeasureSpec.EXACTLY && heightMode != MeasureSpec.AT_MOST) {
            throw new IllegalStateException("Height must have an exact value or MATCH_PARENT");
        }

        final int childCount = getChildCount();
        if (mDragView == null) {
            setDragView(mSlideableView);
        }

        // If the sliding panel is not visible, then put the whole view in the hidden state
        if (mSlideableView.getVisibility() != VISIBLE) {
            Log.w("measure", "mSlideableView >>> not visible");
            mSlideState = PanelState.HIDDEN;
        }

        //idea 去掉上下左右的 padding
        int layoutHeight = heightSize - getPaddingTop() - getPaddingBottom();
        int layoutWidth = widthSize - getPaddingLeft() - getPaddingRight();
    
        Logger.d("layoutHeight : %s", layoutHeight);
//        Log.d("onMeasureYee", String.format("layoutHeight : %s", layoutHeight));
        // First pass. Measure based on child LayoutParams width/height.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            // We always measure the sliding panel in order to know it's height (needed for show panel)
            if (child.getVisibility() == GONE && i == 0) {
                continue;
            }

            int height = layoutHeight;
            int width = layoutWidth;
            if (child == mMainView) {
                if (!mOverlayContent && mSlideState != PanelState.HIDDEN) {
                    //idea panel 非隐藏状态时,main view 的高度等于父控件的高度减去 mPanelHeight
                    height -= mPanelHeight;
                }
                //idea panel 隐藏状态时,main view 的高度其实也等于父控件的高度减去 panel 显示的高度(但是 mPanelHeight 并不等于 panel 显示的高度)

                //idea main view 的宽度要去掉左右 margin
                width -= lp.leftMargin + lp.rightMargin;
            } else if (child == mSlideableView) {
                // The slideable view should be aware of its top margin.
                // See https://github.com/umano/AndroidSlidingUpPanel/issues/412.
                //idea  wrap_content 和 match_parent 且未设置 weigth 时,需要去除 top_margin
                height -= lp.topMargin;
                Log.d("height", "---------mSlideableView >>> height " + height);
            } else if (child == mParallaxView) {
                isMeasureMehod = true;
//                height = Math.abs(computePanelTopPosition(1) - computePanelTopPosition(mAnchorPoint));
                height = parallaxHeight();
                isMeasureMehod = false;
                Log.d("height", "mParallaxView >>> height " + height);
            }

            int childWidthSpec;
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            } else if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            } else {
                //idea 如果指定了确切的值,那么就设置为确切的值
                childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            }

            int childHeightSpec;
    
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
            } else {
                // Modify the height based on the weight.
                if (lp.weight > 0 && lp.weight < 1) {
                    //idea 如果设置了 weight,那么就按照 weight 计算高度
                    height = (int) (height * lp.weight);
                } else if (lp.height != LayoutParams.MATCH_PARENT) {
                    //idea 既没设置 weight 又不是 match_parent,那么应该是设置了确切的值,那么直接使用确切值
                    height = lp.height;
                }
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }

            child.measure(childWidthSpec, childHeightSpec);

            if (child == mSlideableView) {
                mSlideRange = mSlideableView.getMeasuredHeight() - mPanelHeight;
                Log.d("parallaxY", "mSlideRange >>>>>>> " + mSlideRange);
            }
        }

        setMeasuredDimension(widthSize, heightSize);
    }
    
    final Rect windowRect = new Rect();
    private int parallaxHeight(){
        getWindowVisibleDisplayFrame(windowRect);
        return Math.abs((int) ((windowRect.height() - mPanelHeight) * (1 - mAnchorPoint)));
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        final int childCount = getChildCount();

        //idea 大小变化\第一次进来计算,其他情况不计算
        if (mFirstLayout) {
            switch (mSlideState) {
                case EXPANDED:
                    mSlideOffset = 1.0f;
                    break;
                case ANCHORED:
                    mSlideOffset = mAnchorPoint;
                    break;
                case HIDDEN:
                    int newTop = computePanelTopPosition(0.0f) + (mIsSlidingUp ? + mPanelHeight : -mPanelHeight);
                    mSlideOffset = computeSlideOffset(newTop);
                    break;
                default:
                    mSlideOffset = 0.f;
                    break;
            }
        }

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            // Always layout the sliding view on the first layout
            if (child.getVisibility() == GONE && (i == 0 || mFirstLayout)) {
                Log.d("layout", "onlayout visible gone");
                continue;
            }
    
            Log.d("layout", "into layout sure");

            final int childHeight = child.getMeasuredHeight();
            int childTop = paddingTop;
            int childBottom;
            int childLeft  ;
            int childRight ;
    
            if (child == mSlideableView) {
                childTop = computePanelTopPosition(mSlideOffset);
                Log.d("childTop", "mSlideableView >>top" + childTop);
                
            }
            if (child == mParallaxView) {
                childTop = computePanelTopPosition(0);
                Log.d("parallaY", String.format("parallax 的 top 将被设置为: %s", childTop));
                
                Log.d("parallax", " on layout top:" + childTop);
                Log.d("onLayout", "在 onLayout 方法中 mParallaxView 的 top >>>" + childTop);
            }

            if (!mIsSlidingUp) {
                if (child == mMainView && !mOverlayContent) {
                    childTop = computePanelTopPosition(mSlideOffset) + mSlideableView.getMeasuredHeight();
                }
            }
            childBottom  = childTop + childHeight;
            childLeft    = paddingLeft + lp.leftMargin;
            childRight   = childLeft + child.getMeasuredWidth();
    
            if (child == mSlideableView) {
                Log.d("onLayout", String.format("childLeft:%s, childTop:%s, childRight:%s, childBottom:%s, ", childLeft, childTop, childRight, childBottom));
            }
            child.layout(childLeft, childTop, childRight, childBottom);
        }

        if (mFirstLayout) {
            updateObscuredViewVisibility();
        }
        applyParallaxForCurrentSlideOffset();
        applyParallax();
        mFirstLayout = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Recalculate sliding panes and their details
        if (h != oldh) {
            mFirstLayout = true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // If the scrollable view is handling touch, never intercept
        if (mIsScrollableViewHandlingTouch || !isTouchEnabled()) {
            mDragHelper.abort();
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);
        final float x = ev.getX();
        final float y = ev.getY();
        final float adx = Math.abs(x - mInitialMotionX);
        final float ady = Math.abs(y - mInitialMotionY);
        final int dragSlop = mDragHelper.getTouchSlop();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsUnableToDrag = false;
                mInitialMotionX = x;
                mInitialMotionY = y;
                if (!isViewUnder(mDragView, (int) x, (int) y)) {
                    Log.d("sliding", "不在 mdrag view 区域中");
                    mDragHelper.cancel();
                    mIsUnableToDrag = true;
                    return false;
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                //idea 判断为倾向于横向滑动
                if (ady > dragSlop && adx > ady) {
                    mDragHelper.cancel();
                    mIsUnableToDrag = true;
                    return false;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // If the dragView is still dragging when we get here, we need to call processTouchEvent
                // so that the view is settled
                // Added to make scrollable views work (tokudu)
                if (mDragHelper.isDragging()) {
                    mDragHelper.processTouchEvent(ev);
                    return true;
                }
                // Check if this was a click on the faded part of the screen, and fire off the listener if there is one.
                if (ady <= dragSlop
                        && adx <= dragSlop
                        && mSlideOffset > 0 && !isViewUnder(mSlideableView, (int) mInitialMotionX, (int) mInitialMotionY) && mFadeOnClickListener != null) {
                    playSoundEffect(android.view.SoundEffectConstants.CLICK);
                    mFadeOnClickListener.onClick(this);
                    return true;
                }
                break;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled() || !isTouchEnabled()) {
            return super.onTouchEvent(ev);
        }
        try {
            mDragHelper.processTouchEvent(ev);
            return true;
        } catch (Exception ex) {
            // Ignore the pointer out of range exception
            return false;
        }
    }
    
    private boolean isScrollableViewsUnder(Set<Integer> ids, int x, int y) {
        if (ids == null) {
            return ViewHelper.isViewUnder(mSlideableView, x, y);
        }
        View touchingView = ViewHelper.findScrollableViewUnder(mSlideableView, x, y);
        
        if (touchingView == null) {
            Log.d("scrollableYee", "touchingView == null");
            return false;
        }
        Log.d("scrollableYee", String.format("正在触摸的 view:%s", touchingView.getClass().getSimpleName()));
    
        int touchViewId = touchingView.getId();
        if (touchViewId == View.NO_ID) {
            Log.d("scrollableYee", "touchViewId == View.NO_ID");
            return false;
        }
    
        Log.d("scrollableYee", String.format("ids.contains(touchViewId): %s", ids.contains(touchViewId)));
        return ids.contains(touchViewId);
    }
    
    private int getTouchingScrollableViewScrollPos(int x, int y){
        View touchingView = ViewHelper.findScrollableViewUnder(mSlideableView, x, y);
        if (touchingView != null) {
            Log.d("scrollableYee", String.format("正在触摸的 view:%s", touchingView.getClass().getSimpleName()));
        }
        final int scrollPos = mScrollableViewHelper.getScrollableViewScrollPosition(touchingView, mIsSlidingUp);
        Log.d("scrollableYee", String.format("scrollPos :%s", scrollPos));
        return scrollPos;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (!isEnabled() || !isTouchEnabled() || (mIsUnableToDrag && action != MotionEvent.ACTION_DOWN)) {
            Log.d("dispatch", String.format("isEnabled: %s, isTouchEnabled:%s, mIsUnableToDrag:%s", isEnabled(), isTouchEnabled(), mIsUnableToDrag));
            mDragHelper.abort();
            return super.dispatchTouchEvent(ev);
        }
    
        final float y = ev.getY();
        Log.d("dispatch", "dispatchTouchEvent >>>");
        
        if (action == MotionEvent.ACTION_DOWN) {
            
            mIsScrollableViewHandlingTouch = false;
            mPrevMotionY = y;
    
            Log.d("dispatch", "dispatchTouchEvent >>> ACTION_DOWN");
        } else if (action == MotionEvent.ACTION_MOVE) {
            float dy = y - mPrevMotionY;
            mPrevMotionY = y;
    
            Log.d("dispatch", "dispatchTouchEvent >>> ACTION_MOVE");
            // If the scroll view isn't under the touch, pass the
            // event along to the dragView.
            //yee
            if (!isScrollableViewsUnder(mScrollableIds, (int) mInitialMotionX, (int) mInitialMotionY)) {
//            if (!isViewUnder(mScrollableView, (int) mInitialMotionX, (int) mInitialMotionY)) {
                return super.dispatchTouchEvent(ev);
            }

            // Which direction (up or down) is the drag moving?
            if (dy * (mIsSlidingUp ? 1 : -1) > 0) { // Collapsing //idea 意为收起(收起分为两种:1、向上收起,2、向下收起;由 gravity 指定)
                // Is the child less than fully scrolled?
                // Then let the child handle it.
                //idea 比如手指往下滑动时,如果 listview 的第一条未到达顶部,那么先交给 listview 让其自己先滑动到顶部
                //yee
                if (getTouchingScrollableViewScrollPos((int) mInitialMotionX, (int) mInitialMotionY) > 0) {
//                if (mScrollableViewHelper.getScrollableViewScrollPosition(mScrollableView, mIsSlidingUp) > 0) {
                    mIsScrollableViewHandlingTouch = true;
                    return super.dispatchTouchEvent(ev);
                }

                // Was the child handling the touch previously?
                // Then we need to rejigger things so that the
                // drag panel gets a proper down event.
                //idea 比如上次 listview 的第一条未到达顶部,这次已经到达了,那么现在要交给 parent 来处理了,需要告诉子 view CANCEL 掉后续事件
                //idea 此段代码使过度更加顺畅,不会有卡顿;
                //idea 过度阶段:下滑 listview 时,先滚动 listview 内部,然后将 listview 外部整个滚动
                if (mIsScrollableViewHandlingTouch) {
                    // Send an 'UP' event to the child.
                    // ▼ //idea 这段重要 xiaoyee ▼
                    MotionEvent up = MotionEvent.obtain(ev);
                    up.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(up);
                    up.recycle();
                    // ▲ //idea 这段重要 xiaoyee ▲

                    // Send a 'DOWN' event to the panel. (We'll cheat
                    // and hijack this one)
                    //todo 目前测试时去除掉此行也并未造成明显影响
                    ev.setAction(MotionEvent.ACTION_DOWN);
                }

                mIsScrollableViewHandlingTouch = false;
                return this.onTouchEvent(ev);
            } else if (dy * (mIsSlidingUp ? 1 : -1) < 0) { // Expanding //idea 展开状态
                // Is the panel less than fully expanded?
                // Then we'll handle the drag here.
                //idea 尚未完全展开时,由自己先把它展开
                if (mSlideOffset < 1.0f) {
                    mIsScrollableViewHandlingTouch = false;
                    return this.onTouchEvent(ev);
                }

                // Was the panel handling the touch previously?
                // Then we need to rejigger things so that the
                // child gets a proper down event.
                if (!mIsScrollableViewHandlingTouch && mDragHelper.isDragging()) {
                    mDragHelper.cancel();
                    ev.setAction(MotionEvent.ACTION_DOWN);
                }

                mIsScrollableViewHandlingTouch = true;
                return super.dispatchTouchEvent(ev);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            // If the scrollable view was handling the touch and we receive an up
            // we want to clear any previous dragging state so we don't intercept a touch stream accidentally
            if (mIsScrollableViewHandlingTouch) {
                mDragHelper.setDragState(ViewDragHelper.STATE_IDLE);
            }
        }

        // In all other cases, just let the default behavior take over.
        return super.dispatchTouchEvent(ev);
    }

    private boolean isViewUnder(View view, int x, int y) {
        if (view == null) return false;
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    /**
     * Computes the top position of the panel based on the slide offset.
     */
    private int computePanelTopPosition(float slideOffset) {
        int slidingViewHeight = mSlideableView != null ? mSlideableView.getMeasuredHeight() : 0;
        int slidePixelOffset = (int) (slideOffset * mSlideRange);
    
        Log.d("parallax", String.format("slidingViewHeight : %s; slide range: %s", slidingViewHeight, mSlideRange));
        int vgMeasuredHeight = getMeasuredHeight();
    
        Log.d("computeYee", "--------------------");
        Log.d("computeYee", String.format("\tslidingViewHeight:%s,    VG measured height:%s", slidingViewHeight,  vgMeasuredHeight));
        Log.d("computeYee", String.format("\tpanelHeight:%s,          slidePixelOffset:%s",   mPanelHeight,       slidePixelOffset));
        Log.d("computeYee", String.format("\tslideOffset:%s,          mSlideRange:%s",        slideOffset,        mSlideRange));
    
        int retult = vgMeasuredHeight - getPaddingBottom() - mPanelHeight - slidePixelOffset;
        if (isMeasureMehod) {
            Loggor.log("\tslidingViewHeight:%s,    VG measured height:%s,\r\n\tpanelHeight:%s,          slidePixelOffset:%s, \r\n\tslideOffset:%s,          mSlideRange:%s, \r\n\t result:%s",
                       slidingViewHeight,  vgMeasuredHeight,
                       mPanelHeight, slidePixelOffset,
                       slideOffset,        mSlideRange,
                       retult);
        }
        return mIsSlidingUp
                ? retult
                : getPaddingTop() - slidingViewHeight + mPanelHeight + slidePixelOffset;
    }

    /**
     * Computes the slide offset based on the top position of the panel
     */
    private float computeSlideOffset(int topPosition) {
        // Compute the panel top position if the panel is collapsed (offset 0)
        final int topBoundCollapsed = computePanelTopPosition(0);

        // Determine the new slide offset based on the collapsed top position and the new required
        // top position
        return (mIsSlidingUp
                ? (float) (topBoundCollapsed - topPosition) / mSlideRange
                : (float) (topPosition - topBoundCollapsed) / mSlideRange);
    }

    /**
     * Returns the current state of the panel as an enum.
     *
     * @return the current panel state
     */
    public PanelState getPanelState() {
        return mSlideState;
    }

    /**
     * Change panel state to the given state with
     *
     * @param state - new panel state
     */
    public void setPanelState(PanelState state) {
        if (state == null || state == PanelState.DRAGGING) {
            throw new IllegalArgumentException("Panel state cannot be null or DRAGGING.");
        }
        if (!isEnabled()
                || (!mFirstLayout && mSlideableView == null)
                || state == mSlideState
                || mSlideState == PanelState.DRAGGING) return;

        if (mFirstLayout) {
            setPanelStateInternal(state);
        } else {
            if (mSlideState == PanelState.HIDDEN) {
                mSlideableView.setVisibility(View.VISIBLE);
                requestLayout();
            }
            switch (state) {
                case ANCHORED:
                    smoothSlideTo(mAnchorPoint, 0);
                    break;
                case COLLAPSED:
                    smoothSlideTo(0, 0);
                    break;
                case EXPANDED:
                    smoothSlideTo(1.0f, 0);
                    break;
                case HIDDEN:
                    int newTop = computePanelTopPosition(0.0f) + (mIsSlidingUp ? +mPanelHeight : -mPanelHeight);
                    smoothSlideTo(computeSlideOffset(newTop), 0);
                    break;
            }
        }
    }

    private void setPanelStateInternal(PanelState state) {
        if (mSlideState == state) return;
        PanelState oldState = mSlideState;
        mSlideState = state;
        dispatchOnPanelStateChanged(this, oldState, state);
        //idea panel 状态切换为 ANCHORED 时,需要刷新位置
    }

    /**
     * Update the parallax based on the current slide offset.
     */
    @SuppressLint("NewApi")
    private void applyParallaxForCurrentSlideOffset() {
        if (mMainViewParallaxOffset > 0 && mMainView != null) {
            int mainViewOffset = getCurrentParallaxOffset();
            ViewCompat.setTranslationY(mMainView, mainViewOffset);
        }
    }
    
    private void onPanelDragged(int newTop) {
    
        Log.d("height", "onPanelDragged >>> new Top:" + newTop);
        if (mSlideState != PanelState.DRAGGING) {
            mLastNotDraggingSlideState = mSlideState;
        }
        setPanelStateInternal(PanelState.DRAGGING);
        // Recompute the slide offset based on the new top position
        mSlideOffset = computeSlideOffset(newTop);
        applyParallaxForCurrentSlideOffset();
        // Dispatch the slide event
        dispatchOnPanelSlide(mSlideableView);
        if (mMainView != null) {
            // If the slide offset is negative, and overlay is not on, we need to increase the
            // height of the main content
            LayoutParams lp = (LayoutParams) mMainView.getLayoutParams();
            int defaultHeight = getHeight() - getPaddingBottom() - getPaddingTop() - mPanelHeight;

            if (mSlideOffset <= 0 && !mOverlayContent) {
                // expand the main view
                lp.height = mIsSlidingUp ? (newTop - getPaddingBottom()) : (getHeight() - getPaddingBottom() - mSlideableView.getMeasuredHeight() - newTop);
                if (lp.height == defaultHeight) {
                    lp.height = LayoutParams.MATCH_PARENT;
                }
                mMainView.requestLayout();
            } else if (lp.height != LayoutParams.MATCH_PARENT && !mOverlayContent) {
                lp.height = LayoutParams.MATCH_PARENT;
                mMainView.requestLayout();
            }
        }
        
        applyParallax();
    
        Log.d("parallax", "onPanelDragged before requestLayout");
    }
    
    
    private void applyParallax(){
        if (mParallaxView != null) {
                Log.d("height", "getHeight >>>" + getHeight());
                Log.d("height", "getPanelHeight >>>" + getPanelHeight());
                Log.d("height", "mSlideOffset >>>>" + mSlideOffset);
                float y = computeParallaxViewY();
                Log.d("onLayout", "onPanelDragged 在 y 轴方向上需要移动的距离为:" + y);
                Log.d("parallaxY", String.format("computeParallaxViewY 值为: %s,  mParallaxView 的 top: %s", y, mParallaxView.getTop()));
                ViewCompat.setTranslationY(mParallaxView, y);
        }
    }
    
    private void logPanel(String info){
        float expand   = computePanelTopPosition(1);
    
        float collapse = computePanelTopPosition(0);
    
        Log.d("panelTopPos", String.format("调用时机: %s;  expand:%s; collapse:%s", info, expand, collapse));
    }
    
    private float computeParallaxViewY(){
        //idea 如果滚动的 slideOffset 大于"锚点对应的 offset",那么应该使用"锚点对应的 offset"
        float currentOffset = mSlideOffset >= mAnchorPoint ? mAnchorPoint : mSlideOffset;
        float offset = currentOffset / mAnchorPoint;
        offset = mIsSlidingUp ? -offset : offset;
    
        float slideRange = Math.abs(computePanelTopPosition(1) - computePanelTopPosition(0));
    
        mSlideRange = (int) slideRange;
        Log.d("parallaxY", String.format("slideRange >> %s", slideRange));
//        float slideRange = mParallaxView.getMeasuredHeight();
        logPanel("compute parallax view y");
        
        return offset * slideRange;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean                                  result;
        @SuppressLint("WrongConstant") final int save = canvas.save(Canvas.CLIP_SAVE_FLAG);

        if (mSlideableView != null && mSlideableView != child) { // if main view or parallax view
            
            // Clip against the slider; no sense drawing what will immediately be covered,
            // Unless the panel is set to overlay content
    
            canvas.getClipBounds(mTmpRect);
            
            // ▼ //idea mOverlayContent 属性的设置,实际影响此段代码 xiaoyee ▼
            if (!mOverlayContent) {
                //idea 按照文档的说法,如果设置这个属性为 true,那么就会将 slideable view 悬浮(遮盖、附加一层)在 main view 之上(目前),目前仅用在需要 slideable view 需要半透明时
                //idea 如果 main view 不需要半透明,建议使用默认设置,也就是此属性设置为 false,降低过度绘制
                if (mIsSlidingUp) {
                    if (mParallaxView == null || child == mParallaxView) {
                        mTmpRect.bottom = Math.min(mTmpRect.bottom, mSlideableView.getTop());
                    } else {
                        //idea 对于 Main View 来讲,纵向实际绘制的范围时: 0~mParallaxView.getY()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            mTmpRect.bottom = (int) Math.min(mTmpRect.bottom, mParallaxView.getY());
                        } else {
                            mTmpRect.bottom = Math.min(mTmpRect.bottom, mSlideableView.getTop());
                        }
                    }
                } else {
                    mTmpRect.top = Math.max(mTmpRect.top, mSlideableView.getBottom());
                }
            } else if (child == mParallaxView) {
                //idea 避免半透明模式时,收起状态下 slideable view 与 parallax view 会一起显示
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mTmpRect.top = (int) Math.max(mTmpRect.top, mParallaxView.getY());
                }
                mTmpRect.bottom = Math.min(mTmpRect.bottom, mSlideableView.getTop());
            }
            // ▲ //idea mOverlayContent 属性的设置,实际影响此段代码 xiaoyee ▲
            
            if (mClipPanel) {
                canvas.clipRect(mTmpRect);
            }

            result = super.drawChild(canvas, child, drawingTime);
    
            if (child == mMainView) {
                //idea 渐变颜色效果仅对 Main View 生效
                if (mCoveredFadeColor != 0 && mSlideOffset > 0) {
                    final int baseAlpha = (mCoveredFadeColor & 0xff000000) >>> 24;
                    final int imag = (int) (baseAlpha * mSlideOffset);
                    final int color = imag << 24 | (mCoveredFadeColor & 0xffffff);
                    mCoveredFadePaint.setColor(color);
                    canvas.drawRect(mTmpRect, mCoveredFadePaint);
                }
            }
        } else {
            result = super.drawChild(canvas, child, drawingTime);
        }

        canvas.restoreToCount(save);

        return result;
    }

    /**
     * Smoothly animate mDraggingPane to the target X position within its range.
     *
     * @param slideOffset position to animate to
     * @param velocity    initial velocity in case of fling, or 0.
     */
    boolean smoothSlideTo(float slideOffset, int velocity) {
        if (!isEnabled() || mSlideableView == null) {
            // Nothing to do.
            return false;
        }

        int panelTop = computePanelTopPosition(slideOffset);
        if (mDragHelper.smoothSlideViewTo(mSlideableView, mSlideableView.getLeft(), panelTop)) {
            setAllChildrenVisible();
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper != null && mDragHelper.continueSettling(true)) {
            if (!isEnabled()) {
                mDragHelper.abort();
                return;
            }

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    
    @Override
    public void draw(Canvas c) {
        super.draw(c);
        
        //idea 绘制阴影; 阴影的绘制要晚于背景、主体及前景的绘制,所以放在此处
        if (mShadowDrawable != null && mSlideableView != null) {
            final int right = mSlideableView.getRight();
            final int top;
            final int bottom;
            if (mIsSlidingUp) {
                top = mSlideableView.getTop() - mShadowHeight;
                bottom = mSlideableView.getTop();
            } else {
                top = mSlideableView.getBottom();
                bottom = mSlideableView.getBottom() + mShadowHeight;
            }
            final int left = mSlideableView.getLeft();
            mShadowDrawable.setBounds(left, top, right, bottom);
            mShadowDrawable.draw(c);
        }
    }

    /**
     * Tests scrollability within child views of v given a delta of dx.
     *
     * @param v      View to test for horizontal scrollability
     * @param checkV Whether the view v passed should itself be checked for scrollability (true),
     *               or just its children (false).
     * @param dx     Delta scrolled in pixels
     * @param x      X coordinate of the active touch point
     * @param y      Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            // Count backwards - let topmost views consume scroll distance first.
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                        y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                        canScroll(child, true, dx, x + scrollX - child.getLeft(),
                                y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }
        return checkV && ViewCompat.canScrollHorizontally(v, -dx);
    }


    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams
                ? new LayoutParams((MarginLayoutParams) p)
                : new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Log.d("instaceState", "onSaveInstanceState");
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putSerializable(SLIDING_STATE, mSlideState != PanelState.DRAGGING ? mSlideState : mLastNotDraggingSlideState);
        return bundle;
    }
    
    
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Log.d("instaceState", "onRestoreInstanceState");
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mSlideState = (PanelState) bundle.getSerializable(SLIDING_STATE);
            mSlideState = mSlideState == null ? DEFAULT_SLIDE_STATE : mSlideState;
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
        logPanel("onRestoreInstanceState");
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return !mIsUnableToDrag && child == mSlideableView;

        }

        @Override
        public void onViewDragStateChanged(int state) {
            Log.d("parallax", "onViewDragStateChanged");
            if (mDragHelper != null && mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                mSlideOffset = computeSlideOffset(mSlideableView.getTop());
                applyParallaxForCurrentSlideOffset();

                if (mSlideOffset == 1) {
                    updateObscuredViewVisibility();
                    setPanelStateInternal(PanelState.EXPANDED);
                } else if (mSlideOffset == 0) {
                    setPanelStateInternal(PanelState.COLLAPSED);
                } else if (mSlideOffset < 0) {
                    Log.w("measure", "onViewDragStateChanged >>> mSlideOffset < 0e");
                    setPanelStateInternal(PanelState.HIDDEN);
                    mSlideableView.setVisibility(View.INVISIBLE);
                } else {
                    updateObscuredViewVisibility();
                    setPanelStateInternal(PanelState.ANCHORED);
                }
            }
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            setAllChildrenVisible();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            onPanelDragged(top);
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int target;

            // direction is always positive if we are sliding in the expanded direction
            float direction = mIsSlidingUp ? -yvel : yvel;
    
            Log.d("viewRelease", String.format("mSlideOffset:%s; mAnchorPoint:%s, direction:%s", mSlideOffset, mAnchorPoint, direction));
            
            if (direction > 0 && mSlideOffset <= mAnchorPoint) {
                // swipe up -> expand and stop at anchor point
                target = computePanelTopPosition(mAnchorPoint);
            } else if (direction > 0 && mSlideOffset > mAnchorPoint) {
                // swipe up past anchor -> expand
                target = computePanelTopPosition(1.0f);
            } else if (direction < 0 && mSlideOffset >= mAnchorPoint) {
                // swipe down -> collapse and stop at anchor point
                target = computePanelTopPosition(mAnchorPoint);
            } else if (direction < 0 && mSlideOffset < mAnchorPoint) {
                // swipe down past anchor -> collapse
                target = computePanelTopPosition(0.0f);
            } else if (mSlideOffset >= (1.f + mAnchorPoint) / 2) {
                // zero velocity, and far enough from anchor point => expand to the top
                target = computePanelTopPosition(1.0f);
            } else if (mSlideOffset >= mAnchorPoint / 2) {
                // zero velocity, and close enough to anchor point => go to anchor
                target = computePanelTopPosition(mAnchorPoint);
            } else {
                // settle at the bottom
                target = computePanelTopPosition(0.0f);
            }
    
            Log.d("viewRelease", "onViewReleased >>> target " + target);
            Log.d("height", "onViewReleased >>> target " + target);

            if (mDragHelper != null) {
                mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), target);
            }
    
            Log.d("parallax", "onViewReleased");
            invalidate();
            logPanel("onViewReleased");
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mSlideRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int collapsedTop = computePanelTopPosition(0.f);
            final int expandedTop = computePanelTopPosition(1.0f);
            if (mIsSlidingUp) {
                return Math.min(Math.max(top, expandedTop), collapsedTop);
            } else {
                return Math.min(Math.max(top, collapsedTop), expandedTop);
            }
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final int[] ATTRS = new int[]{
                android.R.attr.layout_weight
        };

        public float weight = 0;

        public LayoutParams() {
            super(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height);
            this.weight = weight;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray ta = c.obtainStyledAttributes(attrs, ATTRS);
            if (ta != null) {
                this.weight = ta.getFloat(0, 0);
                ta.recycle();
            }


        }
    }
}