package com.sothree.slidinguppanel.demo;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.*;

/**
 * 布局工具类
 */
public class LayoutUtils {
    
    public static final int CENTER            = 0;
    public static final int CENTER_VERTICAL   = 2;
    public static final int CENTER_HORIZONTAL = 1;
    
    public static final int WIDTH  = 0;
    public static final int HEIGHT = 1;
    
    /**
     * 横屏补充用 FLAG
     */
    public static final int ITEM_TYPE_LANDSCAPE_FLAG = 1;
    
    /**
     * 屏幕宽度所对应的dp个数
     */
    private static final float  WIDTH_DP_COUNT         = 360;
    private static final Object ORIENTATION_CHANGE_SYN = new Object();
    /**
     * 宽高
     */
    private static       int[]  wh                     = null;
    /**
     * 调整后的 dp 与 px 的比值
     */
    private static       float  adjustedDensity        = 0;
    
    /**
     * 默认未指定方向
     */
    private static final int DEFAULT_ORIENTATION = Configuration.ORIENTATION_UNDEFINED;
    /**
     * 初始值为"未指定"
     */
    private static       int activityOrientation = DEFAULT_ORIENTATION;
    
    /**
     * 屏幕是否方屏的判断标准, 屏幕宽高比(短边/长边)大于此值表示为方屏
     */
    private static float MIN_SQUARE_SCREEN_RATIO = 0.8f;
    
    /**
     * 表明当前屏幕方向是否方屏
     */
    private static boolean isScreenSquare;
    
    /**
     * 切换到方屏的后, 对应的像素密度需要调整像素比例
     *
     * @see {@link #WIDTH_DP_COUNT}
     */
    private static float SQUARE_WIDTH_DP_COUNT = 600;
    
    /**
     * 通过本方法使得所有分辨率下均以屏幕宽度除以 360 个 dp 的方式来确定每个 dp 对应多少 px <br>
     * 也就是说 36dp 将含义将变为屏幕宽度的三百六十分之三十六，也就是十分之一<br>
     * UI 线程限定
     */
    public static void proportional() {
        
        float currentEnvironmentDensity = GlobalUtil.getResources().getDisplayMetrics().density;// 当前环境密度
        if (adjustedDensity == currentEnvironmentDensity) {// 如果已经正确
            return;
        }
        
        int width = getScreenWH()[WIDTH];

//        width = correctWidth(width,currentEnvironmentDensity);// 纠正宽度（未来觉得不合适时再考虑使用）
        
        if (isScreenSquare) {
            changeDensity(getScreenHeight() / SQUARE_WIDTH_DP_COUNT);
        } else {
            changeDensity(width / WIDTH_DP_COUNT);
        }
    }
    
    
    /**
     * 纠正宽度
     *
     * @param width
     * @param density
     * @return
     */
    private static int correctWidth(int width, float density) {
        int statusBarHeight = (int) (density * 48);// 状态栏高度（一般48dp）
        int possibleWidth   = width + statusBarHeight;// 可能的高度
        switch (possibleWidth) {
            case 320:
            case 480:
            case 600:
            case 640:
            case 720:
            case 1080:
            case 1440:
                return possibleWidth;
        }
        return width;
    }
    
    /**
     * 更改 dp 与 px 的转换比值<br>
     * UI 线程限定
     *
     * @param density 目标密度
     */
    private static void changeDensity(float density) {
        
        DisplayMetrics displayMetrics = GlobalUtil.getResources().getDisplayMetrics();
        
        if (density == displayMetrics.density) {// 如果已经正确
            adjustedDensity = density;
            return;
        }
        
        float ratio = density / displayMetrics.density;// 计算变化比例
        displayMetrics.scaledDensity *= ratio;// 按变化比例修改
        displayMetrics.densityDpi *= ratio;// 按变化比例修改
        
        displayMetrics.density = density;// 直接修改
        
        adjustedDensity = density;// 记录调整后的 dp 与 px 的比值
    }
    
    
    /**
     * 转化为含横竖屏信息的 item type
     *
     * @param sourceItemType 原 item type
     * @param isLandscape    是否横屏
     * @return 含横竖屏信息的 item type
     */
    public static int landPortItemType(int sourceItemType, boolean isLandscape) {
        int landPortType = sourceItemType << 1;
        if (isLandscape) {
            landPortType |= ITEM_TYPE_LANDSCAPE_FLAG;
        } else {
            landPortType &= ~ITEM_TYPE_LANDSCAPE_FLAG;
        }
        return landPortType;
    }
    
    /**
     * 还原为原 item type
     *
     * @param landPortItemType 含横竖屏信息的 item type
     * @return 原 item type
     */
    public static int sourceItemType(int landPortItemType) {
        int sourceType = landPortItemType >> 1;
        return sourceType;
    }
    
    public static int dp2px(float dipValue) {
        // INFO 工具/dip转px
        final float scale = GlobalUtil.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    
    public static int px2dp(float pxValue) {
        // INFO 工具/px转dip
        final float scale = GlobalUtil.getResources().getDisplayMetrics().density;
        
        return (int) (pxValue / scale + 0.5f);
    }
    
    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue) {
        final float fontScale = GlobalUtil.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
    
    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale = GlobalUtil.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    
    /**
     * 注意使用常量取值！
     *
     * @return 宽总是短的，高总是长的
     */
    public static synchronized int[] getScreenWH() {
        
        if (null != wh) {
            return wh;
        }
        
        wh = new int[2];
        
        WindowManager windowManager = GlobalUtil.getMainActivity().getWindowManager();
        
        Display display = windowManager.getDefaultDisplay();
        mergeWH(display.getWidth(), display.getHeight());
        
        // since SDK_INT = 1;
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mergeWH(metrics.widthPixels, metrics.heightPixels);
        
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                // includes window decorations (statusbar bar/menu bar)
                int a = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                int b = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
                mergeWH(a, b);
            }
            catch (Exception e) {
            }
        }
        
        if (Build.VERSION.SDK_INT >= 17) {
            // includes window decorations (statusbar bar/menu bar)
            Point realSize = new Point();
            display.getRealSize(realSize);
            mergeWH(realSize.x, realSize.y);
        }
        
        return wh;
    }
    
    private static int getScreenHeight() {
        int           height        = 0;
        WindowManager windowManager = GlobalUtil.getMainActivity().getWindowManager();
        
        Display display = windowManager.getDefaultDisplay();
        
        height = Math.max(height, display.getHeight());
        
        // since SDK_INT = 1;
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        height = Math.max(height, metrics.heightPixels);
        
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                // includes window decorations (statusbar bar/menu bar)
                int b = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
                height = Math.max(height, b);
            }
            catch (Exception e) {
            }
        }
        
        if (Build.VERSION.SDK_INT >= 17) {
            // includes window decorations (statusbar bar/menu bar)
            Point realSize = new Point();
            display.getRealSize(realSize);
            height = Math.max(height, realSize.y);
        }
        
        return height;
    }
    
    /**
     * 1、判断哪个是宽哪个是高<br>
     * 2、宽高分别只留下最大值
     *
     * @param a
     * @param b
     */
    private static void mergeWH(int a, int b) {
        if (a < b) {
            wh[WIDTH] = Math.max(wh[WIDTH], a);
            wh[HEIGHT] = Math.max(wh[HEIGHT], b);
        } else {
            wh[WIDTH] = Math.max(wh[WIDTH], b);
            wh[HEIGHT] = Math.max(wh[HEIGHT], a);
        }
    }
    
    /**
     * 给定父矩形和子矩形的宽高，自动将子矩形修改为居中区域
     *
     * @param centerType 1:水平居中 2:垂直居中 0:全部居中
     */
    public static Rect getCenter(Rect outer, Rect inner, int centerType) {
        return getCenter(outer.left, outer.top, outer.right, outer.bottom, inner, centerType);
    }
    
    /**
     * 给定父矩形和子矩形的宽高，自动将子矩形修改为居中区域
     *
     * @param centerType 1:水平居中 2:垂直居中 0:全部居中
     */
    public static Rect getCenter(int left, int top, int right, int bottom, Rect inner, int centerType) {
        int w = inner.width();
        int h = inner.height();
        switch (centerType) {
            case CENTER:
                inner.top = (bottom - top - h) / 2 + top;
                inner.bottom = inner.top + h;
            case CENTER_HORIZONTAL:
                inner.left = (right - left - w) / 2 + left;
                inner.right = inner.left + w;
                break;
            case CENTER_VERTICAL:
                inner.top = (bottom - top - h) / 2 + top;
                inner.bottom = inner.top + h;
                break;
        }
        return inner;
    }
    
    /**
     * 获取定义在 dimens 的尺寸
     *
     * @param id
     * @return 像素值
     */
    public static int getPxByDimens(int id) {
        return GlobalUtil.getResources().getDimensionPixelSize(id);
    }
    
    /**
     * 获取定义在 dimens 的尺寸
     *
     * @param res
     * @param id
     * @return 像素值
     */
    public static int getPxByDimens(Resources res, int id) {
        return res.getDimensionPixelSize(id);
    }
    
    /**
     * 获取定义在 color 的值
     *
     * @param id
     * @return 颜色值
     */
    public static int getColorById(int id) {
        return GlobalUtil.getResources().getColor(id);
    }
    
    /**
     * 对齐顶部绘制文字时拿 top 加上此距离，得出便是绘制文字所需的 baseline 的 y 值
     *
     * @param p 文字画笔
     * @return baseline 和 top 之间的距离
     */
    public static int distanceOfBaselineAndTop(TextPaint p) {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return -fontMetrics.ascent;
    }
    
    /**
     * 垂直居中绘制文字时拿 centerY 加上此距离，得出便是绘制文字所需的 baseline 的 y 值
     *
     * @param p 文字画笔
     * @return baseline 和 centerY 之间的距离
     */
    public static int distanceOfBaselineAndCenterY(TextPaint p) {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
    }
    
    /**
     * 对齐顶部绘制文字时拿 bottom 减去此距离，得出便是绘制文字所需的 baseline 的 y 值
     *
     * @param p 文字画笔
     * @return baseline 和 bottom 之间的距离
     */
    public static int distanceOfBaselineAndBottom(TextPaint p) {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return fontMetrics.descent;
    }
    
    /**
     * @param p 文字画笔
     * @return 文字高度
     */
    public static int textHeight(TextPaint p) {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return fontMetrics.descent - fontMetrics.ascent;
    }
    
    /**
     * NOTE:仅限 {com.mapbar.android.mapbarmap.core.page.BaseFragmentActivity#onConfigurationChanged(Configuration)} 中使用,其他地方目前不许使用
     */
    public static void activityOrientationChanged(int newOrientation) {
        activityOrientation = newOrientation;
        
        boolean newSquare = judgeSquare();
        
        if (isScreenSquare != newSquare) {
            isScreenSquare = newSquare;
            proportional();
        }
    }
    
    /**
     * 是否横屏, 与页面框架逻辑有关系, 随业务也可能调整, 如目前方屏统一认为是 true
     * 该方法没有明确需求不建议使用。如果想获取横竖屏状态：
     * 1. 在Viewer中，或者能拿到任意当前显示的Viewer，应该通过Viewer的isLandscape()方法进行获取吧、
     * 2. 不在Viewer中，考虑使用BackStackManager.getInstance().getCurrent().getViewer().isLandscape();
     * 3. 如果以上办法不能满足需求或有明确原因，才考虑使用这种方案。
     */
    @Deprecated
    public static boolean isLandscape() {
        return isScreenSquare || isRealLandscape();
    }
    
    /**
     * 返回当前传感器返回的屏幕的方向, 是否横屏, 与业务逻辑无关
     *
     * @return
     */
    public static boolean isRealLandscape() {
        return activityOrientation == Configuration.ORIENTATION_LANDSCAPE;
    }
    
    
    /**
     * 判断当前屏幕是否符合方屏条件
     *
     * @return
     */
    private static boolean judgeSquare() {
        float ratio = getScreenWH()[WIDTH] / (float) getScreenWH()[HEIGHT];
        return ratio >= MIN_SQUARE_SCREEN_RATIO;
    }
    
    
    /**
     * 设置是否全屏显示
     *
     * @param isFull
     */
    public static void setFullScreen(boolean isFull) {
        Window window = GlobalUtil.getMainActivity().getWindow();
        if (isFull) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            hideSystemUI(window.getDecorView());
        } else {
            WindowManager.LayoutParams attr = window.getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(attr);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            showSystemUI(window.getDecorView());
        }
    }
    
    private static void hideSystemUI(View mDecorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int vibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                vibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
            }
            mDecorView.setSystemUiVisibility(vibility);
        }
    }
    
    private static void showSystemUI(View mDecorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mDecorView.setSystemUiVisibility(View.VISIBLE);
        }
    }
    
    /**
     * 获取View，尽量使用该方法，系统的方法会得出跟应用框架所需不同的布局文件，该方法可以跟框架同步
     *
     * @param portraitLayId 竖屏的布局资源ID
     * @param landLayId     横屏的布局资源ID
     * @param parent        父容器
     * @return
     */
    public static View inflate(@LayoutRes int portraitLayId, @LayoutRes int landLayId, @Nullable ViewGroup parent) {
        final Context context = GlobalUtil.getMainActivity();
        if (isLandscape()) {
            return View.inflate(context, landLayId, parent);
        } else {
            return View.inflate(context, portraitLayId, parent);
        }
    }
}
