package com.sothree.slidinguppanel.demo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.solver.Goal;
import android.text.TextPaint;


/**
 * $位置$
 *
 * @author zhaozy
 * @date 2017/11/17
 */
public class LocationUnit extends ARefreshable {
    /**
     * 文本宽度                                               `
     */
    private int width;

    /**
     * 文本高度
     */
    private int height;

    private TextPaint textPaint;
    private TextArtist mTitleTextArtist;
    private TextArtist mCodeTextTartist;

    private Point mTitlePoint;
    private Point mCodePoint;


    private int titleHeight;
    private int codeHeight;

    private String distance;
    private String address;
    private String code;

    private Paint mLinePaint;


    private int SP14 = LayoutUtils.getPxByDimens(R.dimen.F2);
    private int SP16 = LayoutUtils.getPxByDimens(R.dimen.sp16);
    private int DP36 = LayoutUtils.getPxByDimens(R.dimen.dp36);
    private int DP15 = LayoutUtils.getPxByDimens(R.dimen.dp15);
    private int DP7 = LayoutUtils.getPxByDimens(R.dimen.dp7);
    private int DP5 = LayoutUtils.getPxByDimens(R.dimen.dp5);
    private int DP10 = LayoutUtils.getPxByDimens(R.dimen.dp10);
    private int DP18 = LayoutUtils.getPxByDimens(R.dimen.dp18);
    private int DP16 = LayoutUtils.getPxByDimens(R.dimen.dp16);

    private Bitmap mBitmap;
    private Bitmap phoneBitmap;




    public LocationUnit(int width) {
        this.width = width - DP15;
    }

    public static LocationUnit demoBean(int width) {
        LocationUnit bean = new LocationUnit(width);
        bean.address = "北京市海淀区西北旺镇北清路和永丰路交叉口东行30米永丰路永丰产业基地";
        bean.distance = "72公里";
        bean.code = "123456789";
        return bean;
    }

    @Override
    protected void initAssemble() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_location)).getBitmap();
        phoneBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_location)).getBitmap();
        TextArtist.TextArtistSetting titleTextArtistSetting = getAddressTextArtistSetting(textPaint);
        mTitleTextArtist = new TextArtist(titleTextArtistSetting);

        TextArtist.TextArtistSetting codeTextArtistSetting = getCodeTextArtistSetting(textPaint);
        mCodeTextTartist = new TextArtist(codeTextArtistSetting);

        mLinePaint = new Paint();

        mTitlePoint = new Point();
        mCodePoint = new Point();

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

        titleHeight = mTitleTextArtist.getHeight();
        codeHeight = mCodeTextTartist.getHeight();
        height = DP15 + titleHeight + DP5 + codeHeight + DP15;
        updateBoundsInner(width, height);
    }


    @Override
    protected void drawContentInner(Canvas canvas) {
        //step 1、先将画布移动到对应位置
        canvas.translate(DP15, 0);

        int left =mContentBound.left;
        int top = mContentBound.top;
        Rect rect = new Rect(0, DP15, mBitmap.getWidth(), mBitmap.getHeight() + DP15);
        canvas.drawBitmap(mBitmap, 0, DP15, textPaint);




        //step 2.1 画地址
        int textMarginLeft = DP5;

        mTitlePoint.set(mBitmap.getWidth() + textMarginLeft, DP15 / 2 + top);
        mTitleTextArtist.setAlignReferencePoint(mTitlePoint);
        mTitleTextArtist.draw(canvas);

        //step 2.2 画编码格式
        mCodePoint.set(mBitmap.getWidth() + textMarginLeft, DP15 + titleHeight + codeHeight / 2);
        mCodeTextTartist.setAlignReferencePoint(mCodePoint);
        mCodeTextTartist.draw(canvas);







    }

    @NonNull
    private TextArtist.TextArtistSetting getAddressTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting titleArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        titleArtistSetting.setAlign(TextArtist.ALIGN_LT);
        titleArtistSetting.append("距您").absoluteSize(SP16).color(GlobalUtil.getContext().getResources().getColor(R.color.FC29));
        titleArtistSetting.append(distance).absoluteSize(SP16).color(GlobalUtil.getContext().getResources().getColor(R.color.FC29));
        titleArtistSetting.append(".").absoluteSize(SP16).color(GlobalUtil.getContext().getResources().getColor(R.color.FC29));
        titleArtistSetting.append(address).absoluteSize(SP16).color(GlobalUtil.getContext().getResources().getColor(R.color.FC29));
        titleArtistSetting.setMaxLineCount(5);
        titleArtistSetting.setOuterWidth(width - DP15 - mBitmap.getWidth() - DP5);
        return titleArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getCodeTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting setting = new TextArtist.TextArtistSetting(textPaint);
        setting.setAlign(TextArtist.ALIGN_LC);
        setting.append("编码：").absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        setting.append(code).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        setting.setMaxLineCount(5);
        setting.setOuterWidth(width);
        return setting;
    }
}
