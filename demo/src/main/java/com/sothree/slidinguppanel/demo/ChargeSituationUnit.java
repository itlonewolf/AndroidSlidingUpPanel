package com.sothree.slidinguppanel.demo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * $收费情况$
 *
 * @author zhaozy
 * @date 2017/11/17
 */

public class ChargeSituationUnit extends ARefreshable {
    private TextPaint textPaint;
    private String mchargeInfo;

    private String mChargeStandard;
    private String mChargeMode;
    private String mPayMode;
    private String title;
    private int SP14 = LayoutUtils.getPxByDimens(R.dimen.F2);
    private int DP36 = LayoutUtils.getPxByDimens(R.dimen.dp36);
    private int DP15 = LayoutUtils.getPxByDimens(R.dimen.dp15);
    private int DP5 = LayoutUtils.getPxByDimens(R.dimen.dp5);
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

    private int mContentHeight;
    private int mContentWidth;
    private String freeText;
    private String chargeType;
    private String chargePrice;

    private List<TextArtist> typeArtistList = new ArrayList<>();
    private List<TextArtist> priceArtistList = new ArrayList<>();
    private List<String> tpyeStrList;
    private List<String> priceStrList;


    public ChargeSituationUnit(int width) {
        this.mContentWidth = width;
    }

    public static ChargeSituationUnit demoBean(int width) {
        ChargeSituationUnit bean = new ChargeSituationUnit(width);
        bean.title = "收费情况";
        bean.mchargeInfo = "收费信息:";
        bean.mChargeStandard = "收费标准：计时/分段计价";
        bean.mChargeMode = "收费方式：入口收费";
        bean.mPayMode = "支付方式：现金";
        bean.freeText = "小车:7:00-21:00,0.5元/15分钟";
        return bean;
    }

    public void refresh() {
        TextArtist.TextArtistSetting titleTextArtistSetting = getTitleTextArtistSetting(textPaint);
        titleTextArtist = new TextArtist(titleTextArtistSetting);

        TextArtist.TextArtistSetting infoTextArtistSetting = getInfoTextArtistSetting(textPaint);
        mchargeInfoTextArtist = new TextArtist(infoTextArtistSetting);

        TextArtist.TextArtistSetting standardTextArtistSetting = getStandardTextArtistSetting(textPaint);
        mChargeStandardTextArtist = new TextArtist(standardTextArtistSetting);
        tpyeStrList = new ArrayList<>();
        priceStrList = new ArrayList<>();


        if (!TextUtils.isEmpty(freeText)) {

            String[] freeTextList = freeText.split("\\|");


            for (int i = 0; i < freeTextList.length; i++) {

                String[] tempFreeText = freeTextList[i].split(";");

                for (int j = 0; j < tempFreeText.length; j++) {

                    String[] itemFreeText = tempFreeText[j].split(",");

                    tpyeStrList.add(itemFreeText[0]);
                    priceStrList.add(itemFreeText[1]);
                }
            }

            for (int m = 0; m < tpyeStrList.size(); m++) {


                TextArtist.TextArtistSetting typeTextArtistSetting = getTypeTextArtistSetting(textPaint, tpyeStrList.get(m));
                TextArtist typeArtist = new TextArtist(typeTextArtistSetting);
                typeArtistList.add(typeArtist);

                TextArtist.TextArtistSetting priceTextArtistSetting = getPriceTextArtistSetting(textPaint, priceStrList.get(m));
                TextArtist priceArtist = new TextArtist(priceTextArtistSetting);
                priceArtistList.add(priceArtist);
            }
        }


        TextArtist.TextArtistSetting chargeModeTextArtistSetting = getChargeModeTextArtistSetting(textPaint);
        mChargeModeTextArtist = new TextArtist(chargeModeTextArtistSetting);

        TextArtist.TextArtistSetting payModeTextArtistSetting = getPayModeTextArtistSetting(textPaint);
        mPayModeTextArtist = new TextArtist(payModeTextArtistSetting);

        updateSize();


    }


    @Override
    protected void initAssemble() {


        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        TextArtist.TextArtistSetting titleTextArtistSetting = getTitleTextArtistSetting(textPaint);
        titleTextArtist = new TextArtist(titleTextArtistSetting);

        TextArtist.TextArtistSetting infoTextArtistSetting = getInfoTextArtistSetting(textPaint);
        mchargeInfoTextArtist = new TextArtist(infoTextArtistSetting);

        TextArtist.TextArtistSetting standardTextArtistSetting = getStandardTextArtistSetting(textPaint);
        mChargeStandardTextArtist = new TextArtist(standardTextArtistSetting);
        tpyeStrList = new ArrayList<>();
        priceStrList = new ArrayList<>();


        if (!TextUtils.isEmpty(freeText)) {

            String[] freeTextList = freeText.split("\\|");


            for (int i = 0; i < freeTextList.length; i++) {
                String[] tempFreeText = freeTextList[i].split(";");

                for (int j = 0; j < tempFreeText.length; j++) {

                    String[] itemFreeText = tempFreeText[j].split(",");

                    tpyeStrList.add(itemFreeText[0]);
                    priceStrList.add(itemFreeText[1]);
                }
            }

            for (int m = 0; m < tpyeStrList.size(); m++) {


                TextArtist.TextArtistSetting typeTextArtistSetting = getTypeTextArtistSetting(textPaint, tpyeStrList.get(m));
                TextArtist typeArtist = new TextArtist(typeTextArtistSetting);
                typeArtistList.add(typeArtist);

                TextArtist.TextArtistSetting priceTextArtistSetting = getPriceTextArtistSetting(textPaint, priceStrList.get(m));
                TextArtist priceArtist = new TextArtist(priceTextArtistSetting);
                priceArtistList.add(priceArtist);
            }
        }


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

    @Override
    public int height() {
        return mContentWidth;
    }

    @Override
    public int width() {
        return mContentHeight;
    }

    private void updateSize() {
        mchargeInfoHeight = mchargeInfoTextArtist.getHeight();
        mChargeStandardHeight = mChargeStandardTextArtist.getHeight();
        mChargeModeHeight = mChargeModeTextArtist.getHeight();
        mPayModeHeight = mPayModeTextArtist.getHeight();


        int itemHeight = 0;
        if (priceArtistList == null && priceArtistList.size() > 0) {
            for (int i = 0; i < priceArtistList.size(); i++) {
                int height = priceArtistList.get(i).getHeight();

                itemHeight += height + DP5;

            }

        }


        mContentHeight = DP36 + DP5 + itemHeight + DP5 + mChargeStandardHeight + DP5 + mChargeModeHeight + DP5 + mPayModeHeight + DP10;


        updateBoundsInner(mContentWidth, mContentHeight);
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
        canvas.drawLine(left, mLineY, mContentWidth, mLineY, mLinePaint);

        //step 2.3、再绘制收费信息一行
        top += DP5 + DP36;
        mchargeInfoPoint.set(left, mchargeInfoHeight / 2 + top);
        mchargeInfoTextArtist.setAlignReferencePoint(mchargeInfoPoint);
        mchargeInfoTextArtist.draw(canvas);

        if (typeArtistList != null && typeArtistList.size() > 0) {

            for (int i = 0; i < typeArtistList.size(); i++) {

                //step 类型
                left = DP36 + DP10 * 3;
                TextArtist textArtist = typeArtistList.get(i);
                Point point = new Point();
                point.set(left, textArtist.getHeight() / 2 + top);
                textArtist.setAlignReferencePoint(point);
                textArtist.draw(canvas);

                //step 价格
                int priceTextX = mContentWidth - DP15 * 2;
                point.set(priceTextX, textArtist.getHeight() / 2 + top);
                priceArtistList.get(i).setAlignReferencePoint(point);
                priceArtistList.get(i).draw(canvas);

                top += DP5 + textArtist.getHeight();


            }
        } else {
            top += mchargeInfoHeight;
        }


        //step 2.4、再绘制收费标准一行
        left = 0;
        top += DP5;
        mChargeStandardPoint.set(left, mChargeStandardHeight / 2 + top);
        mChargeStandardTextArtist.setAlignReferencePoint(mChargeStandardPoint);
        mChargeStandardTextArtist.draw(canvas);

        //step 2.4、再绘制收费方式一行
        top += DP5 + mChargeStandardHeight;
        mChargeModePoint.set(left, mChargeModeHeight / 2 + top);
        mChargeModeTextArtist.setAlignReferencePoint(mChargeModePoint);
        mChargeModeTextArtist.draw(canvas);

        //step 2.5、再绘制支付方式一行
        top += DP5 + mChargeModeHeight;
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
        nameArtistSetting.setOuterWidth(mContentWidth);
        return nameArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getInfoTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(mchargeInfo).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(mContentWidth);
        return nameArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getTypeTextArtistSetting(TextPaint textPaint, String chargeType) {
        TextArtist.TextArtistSetting priceArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        priceArtistSetting.setAlign(TextArtist.ALIGN_LC);
        priceArtistSetting.append(chargeType).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        priceArtistSetting.setMaxLineCount(1);
        priceArtistSetting.setOuterWidth((int) mContentWidth);
        return priceArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getPriceTextArtistSetting(TextPaint textPaint, String chargePrice) {
        TextArtist.TextArtistSetting priceArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        priceArtistSetting.setAlign(TextArtist.ALIGN_RC);
        priceArtistSetting.append(chargePrice).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        priceArtistSetting.setMaxLineCount(1);
        priceArtistSetting.setOuterWidth((int) mContentWidth);
        return priceArtistSetting;
    }


    @NonNull
    private TextArtist.TextArtistSetting getStandardTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(mChargeStandard).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(mContentWidth);
        return nameArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getChargeModeTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(mChargeMode).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(mContentWidth);
        return nameArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getPayModeTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(mPayMode).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(mContentWidth);
        return nameArtistSetting;
    }
}
