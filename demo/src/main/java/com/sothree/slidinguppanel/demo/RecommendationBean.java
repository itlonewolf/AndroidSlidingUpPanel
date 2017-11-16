package com.sothree.slidinguppanel.demo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.Log;

/**
 * 推荐菜
 */

public class RecommendationBean extends ARefreshable {
    Paint mLinePaint;
    private final int width;//文本的宽度
    private int height;//文本的高度

    private String title;

    private String description;//

    private TextPaint textPaint;

    private TextArtist mNameTextArtist;
    private TextArtist mDescTextArtist;
    private int SP14 = LayoutUtils.getPxByDimens(R.dimen.F2);
    private int DP36 = LayoutUtils.getPxByDimens(R.dimen.dp36);
    private int DP15 = LayoutUtils.getPxByDimens(R.dimen.dp15);
    private int DP7 = LayoutUtils.getPxByDimens(R.dimen.dp7);
    private int DP10 = LayoutUtils.getPxByDimens(R.dimen.dp10);

    private Point mNameTextPoint;
    private Point mDescTextPoint;
    private int priceTextHeight;

    public RecommendationBean(int width) {
        this.width = width - DP15;
    }

    public static RecommendationBean demoBean(int width) {
        RecommendationBean bean = new RecommendationBean(width);
        bean.title = "推荐菜";
        bean.description = "招牌羊肉串、糖醋里脊、干锅手撕包菜、懒人版糖醋排骨、卤三鸡、招牌羊肉串、糖醋里脊、干锅手撕包菜、懒人版糖醋排骨、卤三鸡、招牌羊肉串、糖醋里脊、干锅手撕包菜、懒人版糖醋排骨、卤三鸡、招牌羊肉串、糖醋里脊、干锅手撕包菜、懒人版糖醋排骨、卤三鸡、招牌羊肉串、糖醋里脊、干锅手撕包菜、懒人版糖醋排骨、卤三鸡、招牌羊肉串、糖醋里脊、干锅手撕包菜、懒人版糖醋排骨、卤三鸡、招牌羊肉串、糖醋里脊、干锅手撕包菜、懒人版糖醋排骨、卤三鸡";
        return bean;
    }

    @Override
    void initAssemble() {

        mLinePaint = new Paint();

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mNameTextPoint = new Point();
        mDescTextPoint = new Point();
        mContentBound = new Rect();

        TextArtist.TextArtistSetting nameArtistSetting = getTitleTextArtistSetting(textPaint);
        mNameTextArtist = new TextArtist(nameArtistSetting);

        TextArtist.TextArtistSetting descArtistSetting = getDescTextArtistSetting(textPaint);
        mDescTextArtist = new TextArtist(descArtistSetting);

        updateSize();
    }


    private void updateSize() {
        priceTextHeight = mDescTextArtist.getHeight();
        height = DP36 + DP10 * 2 + priceTextHeight + DP7;
        updateBoundsInner(width, height);
    }

    @NonNull
    private TextArtist.TextArtistSetting getTitleTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(title).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC29));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(width);
        return nameArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getDescTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting priceArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        priceArtistSetting.setAlign(TextArtist.ALIGN_LC);
        priceArtistSetting.append(description).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        priceArtistSetting.setMaxLineCount(100);
        priceArtistSetting.setOuterWidth((int) width);
        return priceArtistSetting;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    protected void drawContentInner(Canvas canvas) {

        //step 1、先将画布移动到对应位置
        canvas.translate(DP15, 0);

        int left = mContentBound.left;
        int top = mContentBound.top;


        //step 2、再绘制
        //step 2.1、先绘制 title
        mNameTextPoint.set(left, DP36 / 2 + top);

        mNameTextArtist.setAlignReferencePoint(mNameTextPoint);
        mNameTextArtist.draw(canvas);

        //step 2.2、画分割线
        mLinePaint.setColor(GlobalUtil.getContext().getResources().getColor(R.color.system_gray));
        mLinePaint.setStrokeWidth(2);
        int mLineY = top + DP36;
        canvas.drawLine(left, mLineY, width, mLineY, mLinePaint);

        //step 2.3、再绘制菜谱一行
        top += DP10 + DP36;
        mDescTextPoint.set(left, priceTextHeight / 2 + top);
        mDescTextArtist.setAlignReferencePoint(mDescTextPoint);
        mDescTextArtist.draw(canvas);

        //恢复canvas
        canvas.translate(-DP15, 0);

        //step 2.4、画文字最底部线(肉眼不易看到，但确实存在)
        int mtextLineY = top + priceTextHeight + DP10;
        canvas.drawLine(0, mtextLineY, screenWidth, mtextLineY, mLinePaint);

        //step 2.4、画分割线
        mLinePaint.setColor(GlobalUtil.getContext().getResources().getColor(R.color.BC16));
        mLinePaint.setStrokeWidth(DP7);
        int mBottomLineY = top + priceTextHeight + DP10 + DP7 / 2;
        canvas.drawLine(left, mBottomLineY, screenWidth, mBottomLineY, mLinePaint);

    }
}
