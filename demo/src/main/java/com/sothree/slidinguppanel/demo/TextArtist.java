package com.sothree.slidinguppanel.demo;

/**
 * Created by xiaoyee on 2017/11/14.
 */

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.*;
import android.text.Layout.Alignment;
import android.text.style.*;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author jingzuo
 *         <p/>
 *         复杂文字渲染辅助工具类
 */
public class TextArtist {
    
    /**
     * 对齐左上
     */
    public static final int ALIGN_LT = 1;
    /**
     * 对齐左中
     */
    public static final int ALIGN_LC = 2;
    /**
     * 对齐左下
     */
    public static final int ALIGN_LB = 3;
    /**
     * 对齐中上
     */
    public static final int ALIGN_CT = 4;
    /**
     * 对齐中间
     */
    public static final int ALIGN_CC = 5;
    /**
     * 对齐中下
     */
    public static final int ALIGN_CB = 6;
    /**
     * 对齐右上
     */
    public static final int ALIGN_RT = 7;
    /**
     * 对齐右中
     */
    public static final int ALIGN_RC = 8;
    /**
     * 对齐右下
     */
    public static final int ALIGN_RB = 9;
    
    /**
     *
     */
    private StaticLayout staticLayout;
    
    private int width  = -1;
    private int height = -1;
    
    private TextArtistSetting setting;
    
    /**
     * 左上角的点（渲染所需）
     */
    private Point point;
    
    /**
     * 构造方法，将setting对象传入
     *
     * @param s
     */
    public TextArtist(TextArtistSetting s) {
        
        // 克隆
        setting = new TextArtistSetting(s);
        
        measure();
        
    }
    
    private void measure() {
        
        Alignment alignment = Alignment.ALIGN_NORMAL;
        switch (setting.align) {
            case ALIGN_LT:
            case ALIGN_LC:
            case ALIGN_LB:
                alignment = Alignment.ALIGN_NORMAL;
                break;
            case ALIGN_CT:
            case ALIGN_CC:
            case ALIGN_CB:
                alignment = Alignment.ALIGN_CENTER;
                break;
            case ALIGN_RT:
            case ALIGN_RC:
            case ALIGN_RB:
                alignment = Alignment.ALIGN_OPPOSITE;
                break;
        }
        
        staticLayout = new StaticLayout(setting.ssb, 0, setting.ssb.length(), setting.textPaint, setting.outerWidth,//
                                        alignment, setting.spacingmult, 0, true, TextUtils.TruncateAt.END, setting.outerWidth
        );
        
        int lineCount = staticLayout.getLineCount();
        
        for (Iterator<VariableData> iterator = setting.variableDataList.iterator(); iterator.hasNext(); ) {
            VariableData variableData = iterator.next();
            if (variableData instanceof VariableRelativeSizeData) {
                VariableRelativeSizeData vsd = (VariableRelativeSizeData) variableData;
                if (setting.maxLineCount >= lineCount) {
                    vsd.minProportion = vsd.currentProportion;
                } else {
                    vsd.maxProportion = vsd.currentProportion;
                }
                int   maxSize = (int) (vsd.maxProportion * setting.textPaint.getTextSize());
                int   minSize = (int) (vsd.minProportion * setting.textPaint.getTextSize());
                int   diff    = maxSize - minSize;
                float size    = 0;
                if (diff >= 2) {
                    size = minSize + diff / 2;
                } else {
                    size = minSize;
                    iterator.remove();
                }
                vsd.currentProportion = size / setting.textPaint.getTextSize();
                setting.ssb.removeSpan(vsd.span);
                RelativeSizeSpan what = new RelativeSizeSpan(vsd.currentProportion);
                setting.ssb.setSpan(what, vsd.indexs.start, vsd.indexs.end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                vsd.span = what;
            } else if (variableData instanceof VariableAbsoluteSizeData) {
                VariableAbsoluteSizeData vsd = (VariableAbsoluteSizeData) variableData;
                if (setting.maxLineCount >= lineCount) {
                    vsd.minSize = vsd.currentSize;
                } else {
                    vsd.maxSize = vsd.currentSize;
                }
                int maxSize = vsd.maxSize;
                int minSize = vsd.minSize;
                int diff    = maxSize - minSize;
                int size    = 0;
                if (diff >= 2) {
                    size = minSize + diff / 2;
                } else {
                    size = minSize;
                    iterator.remove();
                }
                vsd.currentSize = size;
                setting.ssb.removeSpan(vsd.span);
                AbsoluteSizeSpan what = new AbsoluteSizeSpan(size);
                setting.ssb.setSpan(what, vsd.indexs.start, vsd.indexs.end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                vsd.span = what;
            }
            measure();
            return;
        }
        
        if (setting.maxLineCount < lineCount) {// 最大行后续还有文字
            
            int maxLineNum = setting.maxLineCount - 1;// 最大行号
            
            int     ellipsizedLength = staticLayout.getLineVisibleEnd(maxLineNum);// 最大行号最后一个字符位置
            int     ellipsizedWidth  = (int) staticLayout.getLineMax(maxLineNum);
            boolean ln               = false;
            for (int i = ellipsizedLength; i < setting.ssb.length(); i++) {// 逐个文字
                CharSequence measureChar  = setting.ssb.subSequence(i, i + 1);
                int          charWidth    = (int) StaticLayout.getDesiredWidth(setting.ssb, i, i + 1, setting.textPaint);
                int          newLineWidth = ellipsizedWidth + charWidth;
                if (newLineWidth >= setting.outerWidth) {
                    ellipsizedLength = i;
                    break;
                } else if (measureChar.toString().equals("\r") || measureChar.toString().equals("\n")) {
                    ellipsizedLength = i;
                    ln = true;
                    break;
                } else {
                    ellipsizedWidth = newLineWidth;
                }
            }
            if (ln) {
                setting.ssb.insert(ellipsizedLength, "一");
                ellipsizedWidth += StaticLayout.getDesiredWidth(setting.ssb, ellipsizedLength, ++ellipsizedLength, setting.textPaint);
            }
            ellipsizedWidth--;
            staticLayout = new StaticLayout(setting.ssb, 0, ellipsizedLength, setting.textPaint, setting.outerWidth,//
                                            alignment, setting.spacingmult, 0, true, TextUtils.TruncateAt.END, ellipsizedWidth
            );
        }
        
    }
    
    /**
     * 记得先测量，其它看测量的方法注释
     *
     * @param canvas
     */
    public void draw(Canvas canvas) {
        if (null == point) {
            throw new RuntimeException("need alignReferencePoint");
        }
        
        int count = canvas.save();
        canvas.translate(point.x, point.y);
        staticLayout.draw(canvas);
        canvas.restoreToCount(count);
    }
    
    /**
     * 获取整体宽度
     *
     * @return
     */
    public int getWidth() {
        if (width < 0 && null != staticLayout) {
            width = staticLayout.getWidth();
        }
        return width;
    }
    
    /**
     * 获取整体高度
     *
     * @return
     */
    public int getHeight() {
        if (height < 0 && null != staticLayout) {
            height = staticLayout.getHeight();
        }
        return height;
    }
    
    /**
     * 获取指定行的底部位置
     *
     * @param line
     * @return
     */
    public int getLineButtom(int line) {
        if (staticLayout != null) {
            return staticLayout.getLineBottom(line);
        }
        return 0;
    }
    
    /**
     * 获取指定行的基线位置
     *
     * @param line
     * @return
     */
    public int getLineBaseLine(int line) {
        if (staticLayout != null) {
            return staticLayout.getLineBaseline(line);
        }
        return 0;
    }
    
    /**
     * 设置对齐参照点(默认值：左上)
     * 注意事项：测量/getHeight时可以不设置，绘制前必须设置参照点
     * 相关：9种对齐方式
     */
    public void setAlignReferencePoint(Point alignReferencePoint) {
        switch (setting.align) {
            case ALIGN_LT:
                point = new Point(alignReferencePoint);
                break;
            case ALIGN_LC:
                point = new Point(alignReferencePoint);
                point.y -= getHeight() / 2;
                break;
            case ALIGN_LB:
                point = new Point(alignReferencePoint);
                point.y -= getHeight();
                break;
            case ALIGN_CT:
                point = new Point(alignReferencePoint);
                point.x -= getWidth() / 2;
                break;
            case ALIGN_CC:
                point = new Point(alignReferencePoint);
                point.x -= getWidth() / 2;
                point.y -= getHeight() / 2;
                break;
            case ALIGN_CB:
                point = new Point(alignReferencePoint);
                point.x -= getWidth() / 2;
                point.y -= getHeight();
                break;
            case ALIGN_RT:
                point = new Point(alignReferencePoint);
                point.x -= getWidth();
                break;
            case ALIGN_RC:
                point = new Point(alignReferencePoint);
                point.x -= getWidth();
                point.y -= getHeight() / 2;
                break;
            case ALIGN_RB:
                point = new Point(alignReferencePoint);
                point.x -= getWidth();
                point.y -= getHeight();
                break;
        }
    }
    
    /**
     * 开始、结束位置的指针
     */
    public static class Indexs {
        public int start;
        public int end;
        
        public Indexs(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
    
    /**
     * 设置项
     */
    public static class TextArtistSetting {
        
        /**
         *
         */
        private SpannableStringBuilder ssb = new SpannableStringBuilder();
        
        /**
         * 可变尺寸数据集合
         */
        private ArrayList<VariableData> variableDataList = new ArrayList<VariableData>();
        
        /**
         * 画笔
         */
        private TextPaint textPaint;
        
        /**
         * 对齐方式（决定文本左中右的对齐方式；决定对齐参照点在哪）
         */
        private int align = ALIGN_LT;
        
        /**
         * 最大行数（默认不限行数）
         */
        private int maxLineCount = Integer.MAX_VALUE;
        
        /**
         * 行高（文字尺寸的倍数）
         */
        private float spacingmult = 1f;
        
        /**
         * 最大允许宽度（超过会换行）
         */
        private int outerWidth = 0;
        
        /**
         * @param setting
         */
        private TextArtistSetting(@NonNull TextArtistSetting setting) {
            this(setting.textPaint, setting.align, setting.maxLineCount, setting.spacingmult, setting.outerWidth);
            this.ssb = new SpannableStringBuilder(setting.ssb);
            this.variableDataList = new ArrayList<VariableData>(setting.variableDataList);
        }
        
        /**
         * @param textPaint    默认画笔(*非空)
         * @param align        相对于参考点的对齐方式(默认左上对齐) {@link TextArtist#ALIGN_LT}等9种
         * @param maxLineCount 最大行数(默认值：Integer.MAX_VALUE)
         * @param spacingmult  行高(文字尺寸的倍数；默认值：1f；单位：倍数)
         * @param outerWidth   绘制区域的宽度
         */
        public TextArtistSetting(@NonNull TextPaint textPaint, int align, int maxLineCount, float spacingmult, int outerWidth) {
            checkTextPaint(textPaint);
            this.textPaint = new TextPaint(textPaint);
            this.align = align;
            this.maxLineCount = maxLineCount;
            this.spacingmult = spacingmult;
            this.outerWidth = outerWidth;
        }
        
        public TextArtistSetting(@NonNull TextPaint textPaint) {
            checkTextPaint(textPaint);
            this.textPaint = new TextPaint(textPaint);
        }
        
        /**
         * 检查画笔
         *
         * @param textPaint
         */
        private void checkTextPaint(@NonNull TextPaint textPaint) {
            if (textPaint.getTextAlign() != Paint.Align.LEFT) {
                throw new RuntimeException("text must align left");
            }
        }
        
        /**
         * 将字符串添加进绘制队列并返回子字符串在整个绘制队列的位置
         *
         * @param text
         * @return InnerIndex对象 {@link InnerIndex}
         */
        public InnerIndex append(String text) {
            int start = ssb.length();
            ssb.append(text);
            int end = ssb.length();
            return new InnerIndex(start, end);
        }
        
        /**
         * 为指定位置的文字添加颜色
         *
         * @param indexs 文字的起止位置
         * @param color  颜色值
         */
        public void appendColor(Indexs indexs, int color) {
            ForegroundColorSpan what = new ForegroundColorSpan(color);
            ssb.setSpan(what, indexs.start, indexs.end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        /**
         * 添加图片, 小图标
         *
         * @param rescourceId 对应图标 id
         */
        public void appendImage(int rescourceId) {
            Drawable d = GlobalUtil.getResources().getDrawable(rescourceId);
            
            appendImage(d);
        }
        
        /**
         * 添加图片
         *
         * @param drawable
         */
        public void appendImage(Drawable drawable) {
            appendImage(drawable, DynamicDrawableSpan.ALIGN_BASELINE);
        }
        
        /**
         * 添加图片, 小图标
         *
         * @param rescourceId 对应图标 id
         * @param align       DynamicDrawableSpan.ALIGN_BOTTOM 或者 DynamicDrawableSpan.ALIGN_BASELINE, 默认 baseLine
         */
        public void appendImage(int rescourceId, int align) {
            Drawable d = GlobalUtil.getResources().getDrawable(rescourceId);
            
            appendImage(d, align);
        }
        
        /**
         * 添加图片
         *
         * @param drawable
         * @param align    DynamicDrawableSpan.ALIGN_BOTTOM 或者 DynamicDrawableSpan.ALIGN_BASELINE, 默认 baseLine
         */
        public void appendImage(Drawable drawable, int align) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan what = new ImageSpan(drawable, align);
            
            int len = ssb.length();
            ssb.append("\uFFFC");
            ssb.setSpan(what, len, ssb.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                       );
        }
    
    
        public void appendImageWithHeight(@DrawableRes int iconId, int height) {
            final Drawable drawable = GlobalUtil.getResources().getDrawable(iconId);
            int            width    = drawable.getIntrinsicWidth();
            if (height > 0) {
                final int intrinsicW = drawable.getIntrinsicWidth();
                final int intrinsicH = drawable.getIntrinsicHeight();
                width = (intrinsicW * height) / intrinsicH;
            }
            appendImage(drawable, null, width, height, DynamicDrawableSpan.ALIGN_BASELINE);
        }
    
        /**
         * 添加图片,且按照给定高度等比例缩放
         */
        public void appendImage(Drawable drawable, String source, int width, int height, int align) {
            if (height > 0 && width > 0) {
                drawable.setBounds(0, 0, width, height);
            }
            final ImageSpan what = new ImageSpan(drawable, source, align);
        
            int len = ssb.length();
            ssb.append("\uFFFC");
            ssb.setSpan(what, len, ssb.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                       );
        
        }
        
        
        /**
         * 为指定位置的文字设置相对字体大小
         *
         * @param indexs     文字的起止位置
         * @param proportion 相对与默认TextPaint中TextSize的倍数
         */
        public void appendRelativeSize(Indexs indexs, float proportion) {
            RelativeSizeSpan what = new RelativeSizeSpan(proportion);
            ssb.setSpan(what, indexs.start, indexs.end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        /**
         * 为指定位置的文字设置可变相对字体大小的范围
         *
         * @param indexs
         * @param minProportion 相对与默认TextPaint中TextSize的倍数
         * @param maxProportion
         */
        public void appendVariableRelativeSize(Indexs indexs, float minProportion, float maxProportion) {
            RelativeSizeSpan what = new RelativeSizeSpan(maxProportion);
            ssb.setSpan(what, indexs.start, indexs.end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            variableDataList.add(new VariableRelativeSizeData(indexs, minProportion, maxProportion, what));
        }
        
        /**
         * 为指定位置的文字设置绝对尺寸
         *
         * @param indexs
         * @param size   单位px 使用{@link LayoutUtils#getPxByDimens(int)} 从dimens文件中获取
         */
        public Indexs appendAbsoluteSize(Indexs indexs, int size) {
            AbsoluteSizeSpan what = new AbsoluteSizeSpan(size);
            ssb.setSpan(what, indexs.start, indexs.end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            return indexs;
        }
        
        /**
         * 为指定位置的文字设置可变绝对尺寸的范围
         *
         * @param indexs
         * @param minSize 单位px 使用{@link LayoutUtils#getPxByDimens(int)} 从dimens文件中获取
         * @param maxSize
         */
        public void appendVariableAbsoluteSize(Indexs indexs, int minSize, int maxSize) {
            AbsoluteSizeSpan what = new AbsoluteSizeSpan(maxSize);
            ssb.setSpan(what, indexs.start, indexs.end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            variableDataList.add(new VariableAbsoluteSizeData(indexs, minSize, maxSize, what));
        }
        
        /**
         * 清理文字与效果
         */
        public void clearTextAndSpan() {
            ssb.clear();
            variableDataList.clear();
        }
        
        /**
         * 设置对齐方式
         * 参考点：{@link TextArtist#setAlignReferencePoint(Point)}
         */
        public void setAlign(int align) {
            this.align = align;
        }
        
        /**
         * 行高（文字尺寸的倍数）
         */
        public void setSpacingmult(float spacingmult) {
            this.spacingmult = spacingmult;
        }
        
        /**
         * 最大允许宽度
         *
         * @return int
         */
        public int getOuterWidth() {
            return outerWidth;
        }
        
        /**
         * 最大允许宽度（超过会换行）
         */
        public void setOuterWidth(int outerWidth) {
            if (outerWidth < 0) {
                outerWidth = 0;
            }
            this.outerWidth = outerWidth;
        }
        
        /**
         * 设置默认的画笔
         */
        public void setTextPaint(TextPaint textPaint) {
            checkTextPaint(textPaint);
            this.textPaint = new TextPaint(textPaint);
        }
        
        /**
         * 获取当前设置的最大行数
         *
         * @return int
         */
        public int getMaxLineCount() {
            return maxLineCount;
        }
        
        /**
         * 最大行数(默认不限行数)，超过会显示"..."
         */
        public void setMaxLineCount(int maxLineCount) {
            this.maxLineCount = maxLineCount;
        }
        
        /**
         * 添加加粗的文本，如果isBlod为false则按照append进行文本的追加，否则对文本进行加粗处理
         */
        public InnerIndex appendBlodText(String text, boolean isBlod) {
            InnerIndex innerIndex = append(text);
            if (isBlod) {
                StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                ssb.setSpan(styleSpan, innerIndex.start, innerIndex.end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return innerIndex;
        }
        
        /**
         * 开始、结束位置的指针（内部类）
         */
        public class InnerIndex extends Indexs {
            public InnerIndex(int start, int end) {
                super(start, end);
            }
            
            public InnerIndex color(int color) {
                appendColor(this, color);
                return this;
            }
            
            public InnerIndex relativeSize(float proportion) {
                appendRelativeSize(this, proportion);
                return this;
            }
            
            public InnerIndex variableRelativeSize(float minProportion, float maxProportion) {
                appendVariableRelativeSize(this, minProportion, maxProportion);
                return this;
            }
            
            public InnerIndex absoluteSize(int size) {
                appendAbsoluteSize(this, size);
                return this;
            }
            
            public InnerIndex variableAbsoluteSize(int minSize, int maxSize) {
                appendVariableAbsoluteSize(this, minSize, maxSize);
                return this;
            }
        }
        
    }
    
    /**
     * 可变尺寸数据（父类）
     */
    public static class VariableData {
        public Indexs              indexs;
        public MetricAffectingSpan span;
        
        public VariableData(Indexs indexs, MetricAffectingSpan span) {
            this.indexs = indexs;
            this.span = span;
        }
    }
    
    /**
     * 可变相对尺寸数据
     */
    public static class VariableRelativeSizeData extends VariableData {
        public float currentProportion;
        public float minProportion;
        public float maxProportion;
        
        public VariableRelativeSizeData(Indexs indexs, float minProportion, float maxProportion, MetricAffectingSpan span) {
            super(indexs, span);
            this.currentProportion = maxProportion;
            this.minProportion = minProportion;
            this.maxProportion = maxProportion;
        }
    }
    
    /**
     * 可变绝对尺寸数据
     */
    public static class VariableAbsoluteSizeData extends VariableData {
        public int currentSize;
        public int minSize;
        public int maxSize;
        
        public VariableAbsoluteSizeData(Indexs indexs, int minSize, int maxSize, MetricAffectingSpan span) {
            super(indexs, span);
            this.currentSize = maxSize;
            this.minSize = minSize;
            this.maxSize = maxSize;
        }
    }
    
}
