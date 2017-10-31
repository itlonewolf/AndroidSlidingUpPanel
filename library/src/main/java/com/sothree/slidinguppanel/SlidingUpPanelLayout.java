package com.sothree.slidinguppanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.sothree.slidinguppanel.library.R;
import com.sothree.slidinguppanel.log.Logger;
import com.sothree.slidinguppanel.util.ViewUtil;

import java.util.List;
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
//    private static final int DEFAULT_FADE_COLOR = 0x99000000;


    private static final int DEFAULT_FADE_COLOR = 0;

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
    private static final int DEFAULT_PARALLAX_OFFSET = 100;

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
    private int mCollapsedPanelHeight = -1;
    
    private int mCustomPanelHeight = -1;
    
    /**
     * 扩展状态下,与上边缘的距离
     */
    private int mExpandedStateTop = 0;

    /**
     * The size of the shadow in pixels.
     */
    private int mShadowHeight = -1;

    /**
     * Parallax offset
     */
    private int mParallaxOffset = -1;

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

    /**
     * If provided, the panel will transfer the scroll from this view to itself when needed.
     * 可以自己滚动(且想让其滚动)的子 view;比如 listview、scrollview 等
     */
    private View mScrollableView;
    private int mScrollableViewResId;
    private IScrollableViewHelper mDefaultScrollableViewHelper = new DefaultScrollableViewHelper();
    
    private View mCollapsedView;
    private int  mCollapsedViewResId;
    
    /**
     * The child view that can slide, if any.
     */
    private View mSlideableView;
    private int  mSlideableViewResId;

    /**
     * The main view
     */
    private View mMainView;
    private int  mMainViewResId;
    
    private View mParallaxView;
    private int  mParallaxViewResId;
    
    private int mPanleCollapsedTop = -1;
    private int mPanleExpandedTop  = -1;
    private int mPanleAnchorTop    = -1;

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
     */
    private float mSlideOffset;

    /**
     * How far in pixels the slideable panel may move.
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
    private       float          mDragSlop;

    /**
     * Stores whether or not the pane was expanded the last time it was slideable.
     * If expand/collapse operations are invoked this state is modified. Used by
     * instance state save/restore.
     */
    private boolean mFirstLayout = true;

    private final Rect mTmpRect = new Rect();
    
    private OnViewVisibilityChangeListener mVisibilityChangeListener;
    
    private OnClickListener mOnDragViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSlideState != PanelState.COLLAPSED || mCollapsedView == null) {
                changePanelState();
            }
        }
    };
    
    private OnClickListener mOnCollapsedViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            changePanelState();
        }
    };
    
    private void changePanelState() {
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
    
    public interface OnViewVisibilityChangeListener {
        /**
         * 目前的 visibility 只有两种,{@link View#GONE} 和 {@link View#VISIBLE}
         */
        void onVisibilityChange(int visibility);
    }

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
        void onPanelSlide(View panel, float slideOffset, float slideRange);

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
        public void onPanelSlide(View panel, float slideOffset, float slideRange) {
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
                int gravity = defAttrs.getInt(0, Gravity.BOTTOM);
                setGravity(gravity);
                defAttrs.recycle();
            }


            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingUpPanelLayout);

            if (ta != null) {
                mCollapsedPanelHeight = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_umanoPanelHeight, -1);
                mExpandedStateTop = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_umanoExpandedStateTop, 0);
                
                mShadowHeight = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_umanoShadowHeight, -1);
                mParallaxOffset = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_umanoParallaxOffset, -1);

                mMinFlingVelocity = ta.getInt(R.styleable.SlidingUpPanelLayout_umanoFlingVelocity, DEFAULT_MIN_FLING_VELOCITY);
                mCoveredFadeColor = ta.getColor(R.styleable.SlidingUpPanelLayout_umanoFadeColor, DEFAULT_FADE_COLOR);

                mDragViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoDragView, -1);
                mScrollableViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoScrollableView, -1);
    
                mCollapsedViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoCollapsedView, -1);
                mSlideableViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoSlideableView, -1);
                mMainViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_umanoMainView, -1);
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
        if (mCollapsedPanelHeight == -1) {
            mCollapsedPanelHeight = (int) (DEFAULT_PANEL_HEIGHT * density + 0.5f);
        }
    
        if (mShadowHeight == -1) {
            mShadowHeight = (int) (DEFAULT_SHADOW_HEIGHT * density + 0.5f);
        }
        if (mParallaxOffset == -1) {
            mParallaxOffset = (int) (DEFAULT_PARALLAX_OFFSET * density);
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
    
        mDragHelper = ViewDragHelper.create(this, 1f, scrollerInterpolator, new DragHelperCallback());
        mDragHelper.setMinVelocity(mMinFlingVelocity * density);

        mIsTouchEnabled = true;
    }

    /**
     * Set the Drag View after the view is inflated
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mDragViewResId != -1) {
            setDragView(findViewById(mDragViewResId));
        }
        if (mScrollableViewResId != -1) {
            setScrollableView(findViewById(mScrollableViewResId));
        }
    
        if (mCollapsedViewResId != -1) {
            setCollapsedView(findViewById(mCollapsedViewResId));
        }
    
        if (mParallaxViewResId != -1) {
            setTopParallaxView(findViewById(mParallaxViewResId));
        }
    
        if (mMainViewResId != -1) {
            mMainView = findViewById(mMainViewResId);
        }
    
        if (mSlideableViewResId != -1) {
            mSlideableView = findViewById(mSlideableViewResId);
        }
    
        if (mSlideableView == null) {
            throw new IllegalArgumentException("mSlideableView 是必须设置的");
        }
    
        mDragHelper.setNotContainView(mCollapsedView);
    
        mDragSlop = mDragHelper.getTouchSlop();
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
     * 展开状态时,与顶部的距离,单位是 px
     */
    public void setExpandedStateTop(@IntRange(from = 0) int expandedStateTop) {
        mExpandedStateTop = expandedStateTop;
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
        return mIsTouchEnabled && mSlideableView != null && mSlideState != PanelState.HIDDEN;
    }

    /**
     * Set the collapsed panel height in pixels
     *
     * @param val A height in pixels
     */
    public void setPanelHeight(int val) {
        mCustomPanelHeight = val;
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
        return mCollapsedPanelHeight;
    }

    /**
     * @return The current parallax offset
     */
    public int getCurrentParallaxOffset() {
        // Clamp slide offset at zero for parallax computation;
        int offset = (int) (mParallaxOffset * Math.max(mSlideOffset, 0));
        return mIsSlidingUp ? -offset : offset;
    }

    /**
     * Set parallax offset for the panel
     *
     * @param val A height in pixels
     */
    public void setParallaxOffset(int val) {
        mParallaxOffset = val;
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
    
    public void setOnCollapsedViewVisibilityChangeListener(OnViewVisibilityChangeListener listener) {
        this.mVisibilityChangeListener = listener;
    }

    /**
     * Removes a panel slide listener
     *
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
            mDragView.setOnClickListener(mOnDragViewClickListener);
        }
    }
    
    /**
     * 设置顶部想实现视差效果的 view,此 view 必须在 main content 中
     */
    public void setTopParallaxView(View parallaxView) {
        this.mParallaxView = parallaxView;
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
     * Set the scrollable child of the sliding layout. If set, scrolling will be transfered between
     * the panel and the view when necessary
     *
     * @param scrollableView The scrollable view
     */
    public void setScrollableView(View scrollableView) {
        mScrollableView = scrollableView;
    }
    
    public void setCollapsedView(View collapsedView) {
        this.mCollapsedView = collapsedView;
    }

    /**
     * Sets the current scrollable view helper. See ScrollableViewHelper description for details.
     */
    public void setScrollableViewHelper(IScrollableViewHelper helper) {
        mDefaultScrollableViewHelper = helper;
    }

    /**
     * Set an anchor point where the panel can stop during sliding
     *
     * @param anchorPoint A value between 0 and 1, determining the position of the anchor point
     *                    starting from the top of the layout.
     */
    public void setAnchorPoint(float anchorPoint) {
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
                l.onPanelSlide(panel, mSlideOffset, mSlideRange);
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

    void updateObscuredViewVisibility() {
        //idea 修改此处解决了不设置 main view 时, slideable view 也无法显示的问题
        if (mMainView == null) {
            return;
        }
        final int leftBound = getPaddingLeft();
        final int rightBound = getWidth() - getPaddingRight();
        final int topBound = getPaddingTop();
        final int bottomBound = getHeight() - getPaddingBottom();
        final int left;
        final int right;
        final int top;
        final int bottom;
        if (mSlideableView != null && hasOpaqueBackground(mSlideableView)) {
            left = mSlideableView.getLeft();
            right = mSlideableView.getRight();
            top = mSlideableView.getTop();
            bottom = mSlideableView.getBottom();
        } else {
            left = right = top = bottom = 0;
        }
        View child = getChildAt(0);
        final int clampedChildLeft = Math.max(leftBound, child.getLeft());
        final int clampedChildTop = Math.max(topBound, child.getTop());
        final int clampedChildRight = Math.min(rightBound, child.getRight());
        final int clampedChildBottom = Math.min(bottomBound, child.getBottom());
        final int vis;
        if (clampedChildLeft >= left && clampedChildTop >= top &&
                clampedChildRight <= right && clampedChildBottom <= bottom) {
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

    private static boolean hasOpaqueBackground(View v) {
        final Drawable bg = v.getBackground();
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (Logger.isTagEnabled("measure")) {
//            Logger.d("measure", "called onMeasure method");
            Logger.d("measure", Logger.OFFSET_ALL_METHOD);
        }
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

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
            mSlideState = PanelState.HIDDEN;
        }
    
        int layoutHeight = heightSize - getPaddingTop() - getPaddingBottom();
        int layoutWidth  = widthSize - getPaddingLeft() - getPaddingRight();

        // First pass. Measure based on child LayoutParams width/height.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            // We always measure the sliding panel in order to know it's height (needed for show panel)
            if (child.getVisibility() == GONE && i == 0) {
                continue;
            }

            int height = layoutHeight;
            int width  = layoutWidth;
            if (child == mMainView) {
                if (!mOverlayContent && mSlideState != PanelState.HIDDEN) {
                    height -= mCollapsedPanelHeight;
                }

                width -= lp.leftMargin + lp.rightMargin;
            } else if (child == mSlideableView) {
                // The slideable view should be aware of its top margin.
                // See https://github.com/umano/AndroidSlidingUpPanel/issues/412.
                height -= lp.topMargin;
                height -= mExpandedStateTop;
            }

            int childWidthSpec;
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            } else if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            }

            int childHeightSpec;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
            } else {
                // Modify the height based on the weight.
                if (lp.weight > 0 && lp.weight < 1) {
                    height = (int) (height * lp.weight);
                } else if (lp.height != LayoutParams.MATCH_PARENT) {
                    height = lp.height;
                }
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }

            child.measure(childWidthSpec, childHeightSpec);
        }

        setMeasuredDimension(widthSize, heightSize);
    }
    
    private void updatePanelAboutTop() {
        int slidingViewHeight = mSlideableView != null ? mSlideableView.getMeasuredHeight() : 0;
        // Compute the top of the panel if its collapsed
        
        final int topWithoutOffset = mIsSlidingUp
                ? getMeasuredHeight() - getPaddingBottom() - mCollapsedPanelHeight
                : getPaddingTop() - slidingViewHeight + mCollapsedPanelHeight;
        
        mPanleCollapsedTop = topWithoutOffset;
        
        if (mIsSlidingUp) {
            mPanleExpandedTop = topWithoutOffset - mSlideRange;
            mPanleAnchorTop = (int) (topWithoutOffset - mAnchorPoint * mSlideRange);
        } else {
            mPanleExpandedTop = topWithoutOffset + mSlideRange;
            mPanleAnchorTop = (int) (topWithoutOffset + mAnchorPoint * mSlideRange);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (Logger.isTagEnabled("layout")) {
//            Logger.d("layout", "called onLayout method");
            Logger.d("layout", Logger.OFFSET_ALL_METHOD);
        }
    
        mSlideRange = mSlideableView.getMeasuredHeight() - mCollapsedPanelHeight;
    
        //idea 优先使用自定义的高度
        if (mCustomPanelHeight != -1) {
            mCollapsedPanelHeight = mCustomPanelHeight;
        } else if (mCollapsedView != null) {
            //idea 如果未自定义高度,且指定了 collapsed view ,那么使用 collapsed view 的高度
            mCollapsedPanelHeight = mCollapsedView.getMeasuredHeight();
        }
        //idea 否则使用默认的(默认值在初始化中就赋值了)
        
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
    
        final int childCount = getChildCount();
    
        if (mFirstLayout) {
            switch (mSlideState) {
                case EXPANDED:
                    mSlideOffset = 1.0f;
                    break;
                case ANCHORED:
                    mSlideOffset = mAnchorPoint;
                    break;
                case HIDDEN:
                    int newTop = computePanelTopPosition(0.0f) + (mIsSlidingUp ? +mCollapsedPanelHeight : -mCollapsedPanelHeight);
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
                continue;
            }

            final int childHeight = child.getMeasuredHeight();
            int childTop = paddingTop;
    
            if (child == mCollapsedView) {
                childTop = computePanelTopPosition(0);
                if (Logger.isTagEnabled("layout")) {
                    Logger.d("layout", String.format("collapsed view top: %s", childTop));
                }
        
            }
            
            if (child == mSlideableView) {
                childTop = computePanelTopPosition(mSlideOffset);
            }

            if (!mIsSlidingUp) {
                if (child == mMainView && !mOverlayContent) {
                    childTop = computePanelTopPosition(mSlideOffset) + mSlideableView.getMeasuredHeight();
                }
            }
    
            final int childBottom = childTop + childHeight;
            final int childLeft   = paddingLeft + lp.leftMargin;
            final int childRight  = childLeft + child.getMeasuredWidth();

            child.layout(childLeft, childTop, childRight, childBottom);
        }
    
        //idea 此步一定要在子 view 都 layout 完成之后再计算,否则可能出现计算异常的情况
        updatePanelAboutTop();
        if (mFirstLayout) {
            updateObscuredViewVisibility();
        }
        applyParallaxForCurrentSlideOffset();
    
        if (mCollapsedView != null) {
            if (mSlideableView.getTop() != mPanleCollapsedTop) {
                collapsedViewGone();
            }
        }

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
    
        if (Logger.isTagEnabled("touch")) {
            Logger.d("touch", "onInterceptTouchEvent");
        }

        final int   action = MotionEventCompat.getActionMasked(ev);
        final float x      = ev.getX();
        final float y      = ev.getY();
        final float adx    = Math.abs(x - mInitialMotionX);
        final float ady    = Math.abs(y - mInitialMotionY);
        
        String actionType = "未知";

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                actionType = "down";
                mIsUnableToDrag = false;
                mInitialMotionX = x;
                mInitialMotionY = y;
                
                if (!isViewUnder(mDragView, (int) x, (int) y)) {
                    if (Logger.isTagEnabled("drag")) {
                        Logger.d("drag", "触摸点不在 drag view 范围内");
                    }
                    
                    mDragHelper.cancel();
                    mIsUnableToDrag = true;
                    return false;
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                actionType = "move";
                if (ady > mDragSlop && adx > ady) {
                    mDragHelper.cancel();
                    mIsUnableToDrag = true;
                    return false;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                actionType = "cancel | up";
                // If the dragView is still dragging when we get here, we need to call processTouchEvent
                // so that the view is settled
                // Added to make scrollable views work (tokudu)
                if (mDragHelper.isDragging()) {
                    mDragHelper.processTouchEvent(ev);
                    return true;
                }
    
                //idea 在"滑动阈值"以内
                final boolean underDragSlop = ady <= mDragSlop && adx <= mDragSlop;
                
                // Check if this was a click on the faded part of the screen, and fire off the listener if there is one.
    
                final boolean notUnderSlideableView = !isViewUnder(mSlideableView, (int) mInitialMotionX, (int) mInitialMotionY);
    
                if (underDragSlop && mSlideOffset > 0 && notUnderSlideableView && mFadeOnClickListener != null) {
        
                    if (Logger.isTagEnabled("touch")) {
                        Logger.d("touch", "fade view clicked");
                    }
                    
                    playSoundEffect(android.view.SoundEffectConstants.CLICK);
                    mFadeOnClickListener.onClick(this);
                    return true;
                }
                break;
        }
    
        final boolean shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev);
        if (Logger.isTagEnabled("touch")) {
            Logger.d("touch", "on intercept event 方法中是否需要拦截:%s, 事件类型:%s", shouldIntercept, actionType);
        
        }
    
        return shouldIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled() || !isTouchEnabled()) {
            return super.onTouchEvent(ev);
        }
    
        if (Logger.isTagEnabled("touch")) {
            Logger.d("touch", "onTouchEvent");
        }
    
        final float x    = ev.getX();
        final float y    = ev.getY();
        final float rawX = ev.getRawX();
        final float rawY = ev.getRawY();
        final float adx  = Math.abs(x - mInitialMotionX);
        final float ady  = Math.abs(y - mInitialMotionY);
    
        if (Logger.isTagEnabled("touch")) {
            Logger.d("touch", "called onTouchEvent");
        }
    
    
        //idea 非拖拽状态判断触摸发生在 collapsed view 范围内
        if (ev.getAction() == MotionEvent.ACTION_UP) {
        
            final float dragSlop = mDragHelper.getTouchSlop();
            //idea 在"滑动阈值"以内
            final boolean underDragSlop = ady <= dragSlop && adx <= dragSlop;
            if (!mDragHelper.isDragging() && underDragSlop) {
                final View childOfCollapsedUnderTouch = ViewUtil.findChildUnderThis(mCollapsedView, (int) rawX, (int) rawY);
    
                if (childOfCollapsedUnderTouch != null) {
                    final View touchableViewInCollapsedView = ViewUtil.getTouchTarget(mCollapsedView, (int) rawX, (int) rawY);
                    if (touchableViewInCollapsedView != null) {
                        touchableViewInCollapsedView.callOnClick();
                        if (Logger.isTagEnabled("touch")) {
                            Logger.d("touch", "collapsed view 中的 %s 被点中了", touchableViewInCollapsedView);
                        }
                    } else {
                        mOnCollapsedViewClickListener.onClick(this);
                        if (Logger.isTagEnabled("touch")) {
                            Logger.d("touch", "collapsed view 被点击了");
                        }
                    }
                    return true;
                }
            }
        }
        
        try {
            mDragHelper.processTouchEvent(ev);
            return true;
        } catch (Exception ex) {
            // Ignore the pointer out of range exception
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (!isEnabled() || !isTouchEnabled() || (mIsUnableToDrag && action != MotionEvent.ACTION_DOWN)) {
            mDragHelper.abort();
            return super.dispatchTouchEvent(ev);
        }
    
        if (Logger.isTagEnabled("touch")) {
            Logger.d("touch", "dispatchTouchEvent");
        }
    
        final float x    = ev.getX();
        final float y    = ev.getY();
        final float adx  = Math.abs(x - mInitialMotionX);
        final float ady  = Math.abs(y - mInitialMotionY);
        final float rawX = ev.getRawX();
        final float rawY = ev.getRawY();
    
        //idea 如果未设置 main view ,且触摸点不在 slideable view 中,那么不做任何处理
        if (!mDragHelper.isDragging() && mMainView == null && !ViewUtil.isTouchPointInView(mSlideableView, (int) rawX, (int) rawY)) {
            if (Logger.isTagEnabled("touch")) {
                Logger.d("touch", "未设置 main view ,且触摸点不在 slideable view 中");
            }
            return false;
        }

        if (action == MotionEvent.ACTION_DOWN) {
            if (Logger.isTagEnabled("drag")) {
                Logger.d("drag", "dispatchTouchEvent down");
            }
            
            mIsScrollableViewHandlingTouch = false;
            mPrevMotionY = y;
        } else if (action == MotionEvent.ACTION_MOVE) {
            float dy = y - mPrevMotionY;
            mPrevMotionY = y;
    
            //idea 向上滑动时才判断,否则无意义
    
            final boolean underDragSlop = ady <= mDragSlop && adx <= mDragSlop;
    
            if (mCollapsedView != null && dy < 0 && !underDragSlop) {
                final boolean underCollapsedView = isViewUnder(mCollapsedView, (int) mInitialMotionX, (int) mInitialMotionY);
                if (underCollapsedView && !isCollapsedGone) {
                    if (Logger.isTagEnabled("drag")) {
                        Logger.d("drag", "dispatchTouchEvent move collapsed view");
                    }
                }
            }
    
            // If the scroll view isn't under the touch, pass the
            // event along to the dragView.
            if (!isViewUnder(mScrollableView, (int) mInitialMotionX, (int) mInitialMotionY)) {
                if (Logger.isTagEnabled("drag")) {
                    Logger.d("drag", "not under scrollable view");
                }
                return super.dispatchTouchEvent(ev);
            }

            // Which direction (up or down) is the drag moving?
            if (dy * (mIsSlidingUp ? 1 : -1) > 0) { // Collapsing
                //case 正在收起
                // Is the child less than fully scrolled?
                // Then let the child handle it.
                if (mDefaultScrollableViewHelper.getScrollableViewScrollPosition(mScrollableView, mIsSlidingUp) > 0) {
                    mIsScrollableViewHandlingTouch = true;
                    return super.dispatchTouchEvent(ev);
                }

                // Was the child handling the touch previously?
                // Then we need to rejigger things so that the
                // drag panel gets a proper down event.
                if (mIsScrollableViewHandlingTouch) {
                    // Send an 'UP' event to the child.
                    MotionEvent up = MotionEvent.obtain(ev);
                    up.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(up);
                    up.recycle();

                    // Send a 'DOWN' event to the panel. (We'll cheat
                    // and hijack this one)
                    ev.setAction(MotionEvent.ACTION_DOWN);
                }

                mIsScrollableViewHandlingTouch = false;
    
                if (Logger.isTagEnabled("drag")) {
                    Logger.d("drag", "onTouchEvent");
                }
                return this.onTouchEvent(ev);
            } else if (dy * (mIsSlidingUp ? 1 : -1) < 0) { // Expanding
                //case 正在展开
                if (Logger.isTagEnabled("drag")) {
                    Logger.d("drag", "expanding");
                }
                
                // Is the panel less than fully expanded?
                // Then we'll handle the drag here.
                if (mSlideOffset < 1.0f) {
                    mIsScrollableViewHandlingTouch = false;
                    if (Logger.isTagEnabled("drag")) {
                        Logger.d("drag", "the panel less than fully expanded");
                    }
                    return this.onTouchEvent(ev);
                }

                // Was the panel handling the touch previously?
                // Then we need to rejigger things so that the
                // child gets a proper down event.
                if (!mIsScrollableViewHandlingTouch && mDragHelper.isDragging()) {
                    mDragHelper.cancel();
                    ev.setAction(MotionEvent.ACTION_DOWN);
                }
    
                if (Logger.isTagEnabled("drag")) {
                    Logger.d("drag", "mIsScrollableViewHandlingTouch = true");
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
    
    
            final boolean underDragSlop = ady <= mDragSlop && adx <= mDragSlop;
            //idea 发生在 collapsed view 上的非滑动事件由 collapsed view 自己处理
//            final boolean underCollapsedView = ViewUtil.isTouchPointInView(mCollapsedView, (int) rawX, (int) rawY);
            final View childOfCollapsedUnderTouch = ViewUtil.findChildUnderThis(mCollapsedView, (int) rawX, (int) rawY);
            if (underDragSlop && (childOfCollapsedUnderTouch != null)) {
                if (Logger.isTagEnabled("touch")) {
                    Logger.d("touch", "非滑动,且在 collapsed view 范围内");
                }
        
                mDragHelper.cancel();
                return this.onTouchEvent(ev);
            } else {
                if (Logger.isTagEnabled("touch")) {
                    Logger.d("touch", "滑动或者不在 collapsed view 范围内");
                }
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

    /*
     * Computes the top position of the panel based on the slide offset.
     */
    private int computePanelTopPosition(float slideOffset) {
        int slidingViewHeight = mSlideableView != null ? mSlideableView.getMeasuredHeight() : 0;
        int slidePixelOffset = (int) (slideOffset * mSlideRange);
        // Compute the top of the panel if its collapsed
        return mIsSlidingUp
                ? getMeasuredHeight() - getPaddingBottom() - mCollapsedPanelHeight - slidePixelOffset
                : getPaddingTop() - slidingViewHeight + mCollapsedPanelHeight + slidePixelOffset;
    }

    /*
     * Computes the slide offset based on the top position of the panel
     */
    private float computeSlideOffset(int topPosition) {
        // Compute the panel top position if the panel is collapsed (offset 0)
        final int topBoundCollapsed = mPanleCollapsedTop;
//        final int topBoundCollapsed = computePanelTopPosition(0);

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
    
        if (Logger.isTagEnabled("drag")) {
            Logger.d("drag", "想设置当前 panel state 为: %s", state);
        }
        
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
                    int newTop = mPanleCollapsedTop + (mIsSlidingUp ? +mCollapsedPanelHeight : -mCollapsedPanelHeight);
//                    int newTop = computePanelTopPosition(0.0f) + (mIsSlidingUp ? +mCollapsedPanelHeight : -mCollapsedPanelHeight);
                    smoothSlideTo(computeSlideOffset(newTop), 0);
                    break;
            }
        }
    }
    
    boolean isCollapsedGone = false;
    
    private void collapsedViewGone() {
        if (mCollapsedView != null) {
            if (View.GONE != mCollapsedView.getVisibility()) {
                mCollapsedView.setVisibility(GONE);
                if (mVisibilityChangeListener != null) {
                    mVisibilityChangeListener.onVisibilityChange(GONE);
                }
            }
        }
        isCollapsedGone = true;
    }
    
    private void resetCollapsedView() {
        if (mCollapsedView != null) {
            if (View.VISIBLE != mCollapsedView.getVisibility()) {
                mCollapsedView.setVisibility(VISIBLE);
                if (mVisibilityChangeListener != null) {
                    mVisibilityChangeListener.onVisibilityChange(VISIBLE);
                }
            }
        }
        isCollapsedGone = false;
    }

    private void setPanelStateInternal(PanelState state) {
        if (mSlideState == state) return;
        PanelState oldState = mSlideState;
        mSlideState = state;
        dispatchOnPanelStateChanged(this, oldState, state);
    }

    /**
     * Update the parallax based on the current slide offset.
     */
    private void applyParallaxForCurrentSlideOffset() {
        if (mParallaxView != null) {
            if (mParallaxOffset > 0) {
                int mainViewOffset = getCurrentParallaxOffset();
                ViewCompat.setTranslationY(mParallaxView, mainViewOffset);
            }
        }
    }
    
    private void onPanelDragged(int newTop) {
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
            LayoutParams lp            = (LayoutParams) mMainView.getLayoutParams();
            int          defaultHeight = getHeight() - getPaddingBottom() - getPaddingTop() - mCollapsedPanelHeight;
        
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
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result;
    
        @SuppressLint("WrongConstant") final int save = canvas.save(Canvas.CLIP_SAVE_FLAG);
    
        if (child == mMainView) { // if main view
            // Clip against the slider; no sense drawing what will immediately be covered,
            // Unless the panel is set to overlay content
            canvas.getClipBounds(mTmpRect);
            if (!mOverlayContent) {
                if (mIsSlidingUp) {
                    mTmpRect.bottom = Math.min(mTmpRect.bottom, mSlideableView.getTop());
                } else {
                    mTmpRect.top = Math.max(mTmpRect.top, mSlideableView.getBottom());
                }
            }
            if (mClipPanel) {
                canvas.clipRect(mTmpRect);
            }

            result = super.drawChild(canvas, child, drawingTime);

            if (mCoveredFadeColor != 0 && mSlideOffset > 0) {
                final int baseAlpha = (mCoveredFadeColor & 0xff000000) >>> 24;
                final int imag = (int) (baseAlpha * mSlideOffset);
                final int color = imag << 24 | (mCoveredFadeColor & 0xffffff);
                mCoveredFadePaint.setColor(color);
                canvas.drawRect(mTmpRect, mCoveredFadePaint);
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

        // draw the shadow
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
        if (Logger.isTagEnabled("attribute")) {
            Logger.d("attribute", "attrs: %s", attrs);
        }
        
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putSerializable(SLIDING_STATE, mSlideState != PanelState.DRAGGING ? mSlideState : mLastNotDraggingSlideState);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mSlideState = (PanelState) bundle.getSerializable(SLIDING_STATE);
            mSlideState = mSlideState == null ? DEFAULT_SLIDE_STATE : mSlideState;
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return !mIsUnableToDrag && child == mSlideableView;

        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (Logger.isTagEnabled("drag")) {
                Logger.d("drag", String.format("onViewDragStateChanged current state: %s", state));
            }
            
            if (mDragHelper != null && mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                mSlideOffset = computeSlideOffset(mSlideableView.getTop());
                applyParallaxForCurrentSlideOffset();

                if (mSlideOffset == 1) {
                    updateObscuredViewVisibility();
                    setPanelStateInternal(PanelState.EXPANDED);
                } else if (mSlideOffset == 0) {
                    setPanelStateInternal(PanelState.COLLAPSED);
                } else if (mSlideOffset < 0) {
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
            if (capturedChild == mCollapsedView) {
                if (Logger.isTagEnabled("drag")) {
                    Logger.d("drag", String.format("captured view: %s", capturedChild));
                }
            }
            
            setAllChildrenVisible();
        }
    
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mCollapsedView) {
                if (Logger.isTagEnabled("drag")) {
                    Logger.d("drag", "collapsed view dragging");
                }
            }
        
            if (changedView.getId() == mSlideableViewResId) {
                if (top == mPanleCollapsedTop) {
                    if (Logger.isTagEnabled("change")) {
                        Logger.d("change", "mPanleCollapsedTop:%s, top:%s, calc top:%s", mPanleCollapsedTop, top, mPanleCollapsedTop);
                    }
        
                    resetCollapsedView();
                } else {
                    collapsedViewGone();
                }
                if (Logger.isTagEnabled("drag")) {
                    Logger.d("drag", "Slideable view dragging top:%s", top);
                }
            }
    
            onPanelDragged(top);
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int target = 0;

            // direction is always positive if we are sliding in the expanded direction
            float direction = mIsSlidingUp ? -yvel : yvel;

            if (direction > 0 && mSlideOffset <= mAnchorPoint) {
                // swipe up -> expand and stop at anchor point
                target = mPanleAnchorTop;
//                target = computePanelTopPosition(mAnchorPoint);
            } else if (direction > 0 && mSlideOffset > mAnchorPoint) {
                // swipe up past anchor -> expand
                target = mExpandedStateTop;
//                target = computePanelTopPosition(1.0f);
            } else if (direction < 0 && mSlideOffset >= mAnchorPoint) {
                // swipe down -> collapse and stop at anchor point
                target = mPanleAnchorTop;
//                target = computePanelTopPosition(mAnchorPoint);
            } else if (direction < 0 && mSlideOffset < mAnchorPoint) {
                // swipe down past anchor -> collapse
                target = mPanleCollapsedTop;
//                target = computePanelTopPosition(0.0f);
            } else if (mSlideOffset >= (1.f + mAnchorPoint) / 2) {
                // zero velocity, and far enough from anchor point => expand to the top
                target = mExpandedStateTop;
//                target = computePanelTopPosition(1.0f);
            } else if (mSlideOffset >= mAnchorPoint / 2) {
                // zero velocity, and close enough to anchor point => go to anchor
                target = mPanleAnchorTop;
//                target = computePanelTopPosition(mAnchorPoint);
            } else {
                // settle at the bottom
                target = mPanleCollapsedTop;
//                target = computePanelTopPosition(0.0f);
            }

            if (mDragHelper != null) {
                mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), target);
            }
            invalidate();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mSlideRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int collapsedTop = mPanleCollapsedTop;
//            final int collapsedTop = computePanelTopPosition(0.f);
            final int expandedTop = mExpandedStateTop;
//            final int expandedTop = computePanelTopPosition(1.0f);
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
