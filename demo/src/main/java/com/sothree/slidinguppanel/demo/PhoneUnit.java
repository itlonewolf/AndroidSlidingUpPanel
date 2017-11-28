package com.sothree.slidinguppanel.demo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.text.TextPaint;

/**
 * 联系方式
 */
public class PhoneUnit extends ARefreshable {
    /**
     * 文本宽度
     */
    private int width;

    /**
     * 文本高度
     */
    private int height;

    private TextPaint textPaint;
    private TextArtist titleTextArtist;

    private String title;
    private int titleHeight;
    private int SP14 = LayoutUtils.getPxByDimens(R.dimen.F2);
    private int DP36 = LayoutUtils.getPxByDimens(R.dimen.dp36);
    private int DP15 = LayoutUtils.getPxByDimens(R.dimen.dp15);
    private int DP7 = LayoutUtils.getPxByDimens(R.dimen.dp7);
    private int DP10 = LayoutUtils.getPxByDimens(R.dimen.dp10);
    private int DP44 = LayoutUtils.getPxByDimens(R.dimen.dp44);

    private Paint mLinePaint;


    private Point mPoint;


    public PhoneUnit(int width) {
        this.width = width;
    }

    public static PhoneUnit demoBean(int width) {
        PhoneUnit bean = new PhoneUnit(width);
        bean.title = "010-7118719、010-7118719、010-7118719、010-7118719、010-7118719、";
        return bean;
    }

    @Override
    protected void initAssemble() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        TextArtist.TextArtistSetting titleTextArtistSetting = getTextArtistSetting(textPaint);
        titleTextArtist = new TextArtist(titleTextArtistSetting);

        mPoint = new Point();
        mLinePaint = new Paint();
        updateSize();
    }

    @Override
    public int height() {
        return width;
    }

    @Override
    public int width() {
        return height;
    }

    private void updateSize() {
        titleHeight = titleTextArtist.getHeight();
        height = DP44;
        updateBoundsInner(width, height);
    }

    private Bitmap mBitmap;
    private Bitmap arrowBitmap;

    @Override
    protected void drawContentInner(Canvas canvas) {
//        //step 1、画分割线
        int textMarginLeft = DP44;
        int textTop = DP15 + titleHeight / 2;
        //step 2.1 手机图片
        mBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_arrow)).getBitmap();
        arrowBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_arrow)).getBitmap();
        canvas.drawBitmap(mBitmap, DP15, DP15, textPaint);

        //step 2.2 手机号
        mPoint.set(textMarginLeft, textTop);
        titleTextArtist.setAlignReferencePoint(mPoint);
        titleTextArtist.draw(canvas);

        //step 2.3 右箭头
        canvas.drawBitmap(arrowBitmap, width - DP15 * 2, DP15, textPaint);
    }

    @NonNull
    private TextArtist.TextArtistSetting getTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting phoneArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        phoneArtistSetting.setAlign(TextArtist.ALIGN_LC);
        phoneArtistSetting.append(title).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC29));
        phoneArtistSetting.setMaxLineCount(1);
        phoneArtistSetting.setOuterWidth(width - DP44 - DP15 * 2);
        return phoneArtistSetting;
    }
}
