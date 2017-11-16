package com.sothree.slidinguppanel.demo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.text.TextPaint;

/**
 * $收费情况$
 *
 * @author zhaozy
 * @date 2017/11/17
 */

public class ChargeSituationBean extends ARefreshable {
    /**
     * 文本宽度
     */
    private int width;

    /**
     * 文本高度
     */
    private int height;

    private TextPaint textPaint;
    private String mchargeInfo;

    private String mChargeStandard;
    private String mChargeMode;
    private String mPayMode;
    private String title;
    private int SP14 = LayoutUtils.getPxByDimens(R.dimen.F2);
    private int DP36 = LayoutUtils.getPxByDimens(R.dimen.dp36);
    private int DP15 = LayoutUtils.getPxByDimens(R.dimen.dp15);
    private int DP7 = LayoutUtils.getPxByDimens(R.dimen.dp7);
    private int DP10 = LayoutUtils.getPxByDimens(R.dimen.dp10);
    private Paint mLinePaint;

    private TextArtist titleTextArtist;
    private TextArtist mchargeInfoTextArtist;
    private TextArtist mChargeStandardTextArtist;
    private TextArtist mChargeModeTextArtist;
    private TextArtist mPayModeTextArtist;

    private Point mTitleoPoint;
    private Point mchargeInfoPoint;
    private Point mChargeStandardPoint;
    private Point mChargeModePoint;
    private Point mPayModePoint;

    private int mchargeInfoHeight;
    private int mChargeStandardHeight;
    private int mChargeModeHeight;
    private int mPayModeHeight;


    public ChargeSituationBean(int width) {
        this.width = width;
    }

    public static ChargeSituationBean demoBean(int width) {
        ChargeSituationBean bean = new ChargeSituationBean(width);
        bean.title = "收费情况";
        bean.mchargeInfo = "收费信息";
        bean.mChargeStandard = "收费标准：计时/分段计价";
        bean.mChargeMode = "收费方式：入口收费";
        bean.mPayMode = "支付方式：现金";
        return bean;
    }

    @Override
    void initAssemble() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        TextArtist.TextArtistSetting titleTextArtistSetting = getTitleTextArtistSetting(textPaint);
        titleTextArtist = new TextArtist(titleTextArtistSetting);

        TextArtist.TextArtistSetting infoTextArtistSetting = getInfoTextArtistSetting(textPaint);
        mchargeInfoTextArtist = new TextArtist(infoTextArtistSetting);

        TextArtist.TextArtistSetting standardTextArtistSetting = getStandardTextArtistSetting(textPaint);
        mChargeStandardTextArtist = new TextArtist(standardTextArtistSetting);

        TextArtist.TextArtistSetting chargeModeTextArtistSetting = getChargeModeTextArtistSetting(textPaint);
        mChargeModeTextArtist = new TextArtist(chargeModeTextArtistSetting);

        TextArtist.TextArtistSetting payModeTextArtistSetting = getPayModeTextArtistSetting(textPaint);
        mPayModeTextArtist = new TextArtist(payModeTextArtistSetting);

        mLinePaint = new Paint();


        mTitleoPoint = new Point();
        mchargeInfoPoint = new Point();
        mChargeStandardPoint = new Point();
        mChargeModePoint = new Point();
        mPayModePoint = new Point();

        updateSize();
    }

    private void updateSize() {
        mchargeInfoHeight = mchargeInfoTextArtist.getHeight();
        mChargeStandardHeight = mChargeStandardTextArtist.getHeight();
        mChargeModeHeight = mChargeModeTextArtist.getHeight();
        mPayModeHeight = mPayModeTextArtist.getHeight();
        height = DP36 + DP10 * 5 + mchargeInfoHeight + mChargeStandardHeight + mChargeModeHeight + mPayModeHeight;
        updateBoundsInner(width, height);
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

        //step 2.1、先绘制 title
        mTitleoPoint.set(left, DP36 / 2 + top);
        titleTextArtist.setAlignReferencePoint(mTitleoPoint);
        titleTextArtist.draw(canvas);

        //step 2.2、画分割线
        mLinePaint.setColor(GlobalUtil.getContext().getResources().getColor(R.color.system_gray));
        mLinePaint.setStrokeWidth(2);
        int mLineY = top + DP36;
        canvas.drawLine(left, mLineY, width, mLineY, mLinePaint);

        //step 2.3、再绘制收费信息一行
        top += DP10 + DP36;
        mchargeInfoPoint.set(left, mchargeInfoHeight / 2 + top);
        mchargeInfoTextArtist.setAlignReferencePoint(mchargeInfoPoint);
        mchargeInfoTextArtist.draw(canvas);

        //step 2.4、再绘制收费标准一行
        top += DP10 + mchargeInfoHeight;
        mChargeStandardPoint.set(left, mChargeStandardHeight / 2 + top);
        mChargeStandardTextArtist.setAlignReferencePoint(mChargeStandardPoint);
        mChargeStandardTextArtist.draw(canvas);

        //step 2.4、再绘制收费方式一行
        top += DP10 + mChargeStandardHeight;
        mChargeModePoint.set(left, mChargeModeHeight / 2 + top);
        mChargeModeTextArtist.setAlignReferencePoint(mChargeModePoint);
        mChargeModeTextArtist.draw(canvas);

        //step 2.5、再绘制支付方式一行
        top += DP10 + mChargeModeHeight;
        mPayModePoint.set(left, mPayModeHeight / 2 + top);
        mPayModeTextArtist.setAlignReferencePoint(mPayModePoint);
        mPayModeTextArtist.draw(canvas);
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
    private TextArtist.TextArtistSetting getInfoTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(mchargeInfo).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(width);
        return nameArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getStandardTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(mChargeStandard).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(width);
        return nameArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getChargeModeTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(mChargeMode).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(width);
        return nameArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getPayModeTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(mPayMode).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(width);
        return nameArtistSetting;
    }
}
