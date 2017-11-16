package com.sothree.slidinguppanel.demo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextPaint;

/**
 * 油价
 *
 * @author zhaozy
 * @date 2017/11/17
 */

public class OilPriceBean extends ARefreshable {
    Paint mLinePaint;
    /**
     * 文本的宽度
     */
    private final int width;

    //文本的高度
    private int height;

    private String title;


    private String note;
    private String oilType;
    private String oilPrice;

    private TextPaint textPaint;

    private TextArtist mNameTextArtist;
    private TextArtist mNoteTextArtist;
    private TextArtist mTypeTextArtist;
    private TextArtist mPriceTextArtist;
    private int SP14 = LayoutUtils.getPxByDimens(R.dimen.F2);
    private int DP36 = LayoutUtils.getPxByDimens(R.dimen.dp36);
    private int DP15 = LayoutUtils.getPxByDimens(R.dimen.dp15);
    private int DP7 = LayoutUtils.getPxByDimens(R.dimen.dp7);
    private int DP10 = LayoutUtils.getPxByDimens(R.dimen.dp10);

    private Point mNameTextPoint;
    private Point mTypeTextPoint;
    private Point mPriceTextPoint;
    private Point mNoteTextPoint;
    private int mDescTextHeight;

    public OilPriceBean(int width) {
        this.width = width - DP15;
    }

    public static OilPriceBean demoBean(int width) {
        OilPriceBean bean = new OilPriceBean(width);
        bean.title = "油价";
        bean.note = "仅供参考,请以实地为准";
        bean.oilType = "92#汽油";
        bean.oilPrice = "6.2/升";
        return bean;
    }

    @Override
    void initAssemble() {

        mLinePaint = new Paint();

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mNameTextPoint = new Point();
        mTypeTextPoint = new Point();
        mPriceTextPoint = new Point();
        mNoteTextPoint = new Point();
        mContentBound = new Rect();

        TextArtist.TextArtistSetting nameArtistSetting = getTitleTextArtistSetting(textPaint);
        mNameTextArtist = new TextArtist(nameArtistSetting);

        TextArtist.TextArtistSetting noteArtistSetting = getNoteTextArtistSetting(textPaint);
        mNoteTextArtist = new TextArtist(noteArtistSetting);

        TextArtist.TextArtistSetting descArtistSetting = getTypeTextArtistSetting(textPaint);
        mTypeTextArtist = new TextArtist(descArtistSetting);

        TextArtist.TextArtistSetting priceArtistSetting = getPriceTextArtistSetting(textPaint);
        mPriceTextArtist = new TextArtist(priceArtistSetting);

        updateSize();
    }


    private void updateSize() {
        mDescTextHeight = mTypeTextArtist.getHeight();
        height = DP36 + DP10 * 2 + mDescTextHeight;
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
    private TextArtist.TextArtistSetting getNoteTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_RC);
        nameArtistSetting.append(note).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.system_gray));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(width);
        return nameArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getTypeTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting priceArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        priceArtistSetting.setAlign(TextArtist.ALIGN_LC);
        priceArtistSetting.append(oilType).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        priceArtistSetting.setMaxLineCount(1);
        priceArtistSetting.setOuterWidth((int) width);
        return priceArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getPriceTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting priceArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        priceArtistSetting.setAlign(TextArtist.ALIGN_RC);
        priceArtistSetting.append(oilPrice).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        priceArtistSetting.setMaxLineCount(1);
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

        //step 2.2、备注
        int noteTextX = width - DP15;
        mNoteTextPoint.set(noteTextX, DP36 / 2 + top);
        mNoteTextArtist.setAlignReferencePoint(mNoteTextPoint);
        mNoteTextArtist.draw(canvas);

        //step 2.3、画分割线
        mLinePaint.setColor(GlobalUtil.getContext().getResources().getColor(R.color.system_gray));
        mLinePaint.setStrokeWidth(2);
        int mLineY = top + DP36;
        canvas.drawLine(left, mLineY, width, mLineY, mLinePaint);

        //step 2.4、油类型
        top += DP10 + DP36;
        mTypeTextPoint.set(left, mDescTextHeight / 2 + top);
        mTypeTextArtist.setAlignReferencePoint(mTypeTextPoint);
        mTypeTextArtist.draw(canvas);

        //step 2.5、油价格
        int priceTextX = width - DP15;
        mPriceTextPoint.set(priceTextX, mDescTextHeight / 2 + top);
        mPriceTextArtist.setAlignReferencePoint(mPriceTextPoint);
        mPriceTextArtist.draw(canvas);


    }
}
