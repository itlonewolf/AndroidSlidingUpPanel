package com.sothree.slidinguppanel.demo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.Log;

/**
 * $车位信息$
 *
 * @author zhaozy
 * @date 2017/11/17
 */
public class ParkingBean extends ARefreshable {
    /**
     * 文本宽度
     */
    private int width;

    /**
     * 文本高度
     */
    private int height;

    private TextPaint textPaint;
    private TextArtist mTitleTextArtist;
    private TextArtist mFreeTextArtist;
    private TextArtist mTotalTextArtist;
    private TextArtist mForeCastTextArtist;

    private Point mTitlePoint;
    private Point mFreePoint;
    private Point mTotalPoint;
    private Point mForeCastPoint;

    private int mFreeHeight;
    private int mTotalHeight;
    private int mForeCastHeight;

    private String title;
    /**
     * 空车位
     */
    private String freeParking;
    private String freeParkingNum;
    /**
     * 总车位
     */
    private String totalParking;
    /**
     * 车位预测
     */
    private String foreCastParking;
    private int SP14 = LayoutUtils.getPxByDimens(R.dimen.F2);
    private int DP36 = LayoutUtils.getPxByDimens(R.dimen.dp36);
    private int DP15 = LayoutUtils.getPxByDimens(R.dimen.dp15);
    private int DP7 = LayoutUtils.getPxByDimens(R.dimen.dp7);

    private int DP10 = LayoutUtils.getPxByDimens(R.dimen.dp10);


    private Paint mLinePaint;

    public ParkingBean(int width) {
        this.width = width;
    }

    public static ParkingBean demoBean(int width) {
        ParkingBean bean = new ParkingBean(width);
        bean.title = "收费情况";
        bean.foreCastParking = "未来2个小时车位预测：充足";
        bean.freeParking = "空车位：";
        bean.freeParkingNum = "8个";
        bean.totalParking = "总车位：20个";
        return bean;
    }

    @Override
protected     void initAssemble() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        TextArtist.TextArtistSetting titleTextArtistSetting = getTitleTextArtistSetting(textPaint);
        mTitleTextArtist = new TextArtist(titleTextArtistSetting);

        TextArtist.TextArtistSetting freeTextArtistSetting = getFreeTextArtistSetting(textPaint);
        mFreeTextArtist = new TextArtist(freeTextArtistSetting);

        TextArtist.TextArtistSetting totalTextArtistSetting = getTotalTextArtistSetting(textPaint);
        mTotalTextArtist = new TextArtist(totalTextArtistSetting);

        TextArtist.TextArtistSetting foreCastTextArtistSetting = getForeCastTextArtistSetting(textPaint);
        mForeCastTextArtist = new TextArtist(foreCastTextArtistSetting);


        mLinePaint = new Paint();

        mTitlePoint = new Point();
        mFreePoint = new Point();
        mForeCastPoint = new Point();
        mTotalPoint = new Point();


        updateSize();
    }

    @Override
    public int height() {
        return 0;
    }

    @Override
    public int width() {
        return 0;
    }

    private void updateSize() {
        mFreeHeight = mFreeTextArtist.getHeight();
        mTotalHeight = mTotalTextArtist.getHeight();
        mForeCastHeight = mFreeTextArtist.getHeight();

        height = DP36 + DP10 * 4 + mFreeHeight + mTotalHeight + mForeCastHeight;
        updateBoundsInner(width, height);
    }




    @Override
    protected void drawContentInner(Canvas canvas) {
        //step 1、先将画布移动到对应位置
        canvas.translate(DP15, 0);

        int left = mContentBound.left;
        int top = mContentBound.top;

        //step 2、再绘制
        //step 2.1、先绘制 title
        mTitlePoint.set(left, DP36 / 2 + top);

        mTitleTextArtist.setAlignReferencePoint(mTitlePoint);
        mTitleTextArtist.draw(canvas);
        //step 2.2、画分割线
        mLinePaint.setColor(GlobalUtil.getContext().getResources().getColor(R.color.system_gray));
        mLinePaint.setStrokeWidth(2);
        int mLineY = top + DP36;
        canvas.drawLine(left, mLineY, width, mLineY, mLinePaint);

        //step 2.3、再绘制空车位一行
        top += DP10 + DP36;
        mFreePoint.set(left, mFreeHeight / 2 + top);
        mFreeTextArtist.setAlignReferencePoint(mFreePoint);
        mFreeTextArtist.draw(canvas);


        //step 2.4、再绘制总车位一行
        top += DP10 + mFreeHeight;
        mTotalPoint.set(left, mTotalHeight / 2 + top);
        mTotalTextArtist.setAlignReferencePoint(mTotalPoint);
        mTotalTextArtist.draw(canvas);

        //step 2.4、再绘制预测车位一行
        top += DP10 + mTotalHeight;
        mForeCastPoint.set(left, mForeCastHeight / 2 + top);
        mForeCastTextArtist.setAlignReferencePoint(mForeCastPoint);
        mForeCastTextArtist.draw(canvas);
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
    private TextArtist.TextArtistSetting getFreeTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(freeParking).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.append(freeParkingNum).absoluteSize(SP14).color(Color.parseColor("#00B300"));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(width);
        return nameArtistSetting;
    }


    @NonNull
    private TextArtist.TextArtistSetting getTotalTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(totalParking).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(width);
        return nameArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getForeCastTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(foreCastParking).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(width);
        return nameArtistSetting;
    }


}
