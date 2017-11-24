package com.sothree.slidinguppanel.demo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;

/**
 * 收费情况
 */

class BriefUnit extends ARefreshable {
    /**
     * 文本宽度
     */
    private int mContentWidth;

    /**
     * 文本高度
     */
    private int height;

    private TextPaint textPaint;
    private TextArtist titleTextArtist;
    private TextArtist evaluateArtist;
    private TextArtist merchantBriefArtist;

    private TextArtist businessHourArtist;
    private TextArtist payModeArtist;

    private String title;
    private String payMode;
    private String businessHour;
    private String merchantBrief;


    private Point titleTextPoint;
    private Point payModePoint;
    private Point businessHourPoint;
    private Point merchantBriefPoint;
    private Point evaluatePoint;


    private int businessHourHeight;
    private int payModeHeight;
    private int merchantBriefHeight;
    private int evaluateHeight;


    private int SP14 = LayoutUtils.getPxByDimens(R.dimen.F2);
    private int DP36 = LayoutUtils.getPxByDimens(R.dimen.dp36);
    private int DP15 = LayoutUtils.getPxByDimens(R.dimen.dp15);
    private int DP5 = LayoutUtils.getPxByDimens(R.dimen.dp5);
    private int DP10 = LayoutUtils.getPxByDimens(R.dimen.dp10);

    private Paint mLinePaint;

    /**
     * 口味
     */
    private String toast;
    private String toastValue;
    /**
     * 环境
     */
    private String envi;
    private String enviValue;
    /**
     * 服务
     */
    private String service;
    private String serviceValue;


    public BriefUnit(int mContentWidth) {
        this.mContentWidth = mContentWidth;
    }

    public static BriefUnit demoBean(int width) {
        BriefUnit bean = new BriefUnit(width);
        bean.title = "简介";
        bean.businessHour = "营业时间：9：:00~22:00";
        bean.merchantBrief = "商户简介：店铺于2013年成立";
        bean.payMode = "支付方式：现金/储值卡";

        bean.toast = "口味";
        bean.toastValue = "9.0";
        bean.envi = "环境";
        bean.enviValue = "9.0";
        bean.service = "服务";
        bean.serviceValue = "8.7";


        return bean;
    }

    @Override
    protected void initAssemble() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        TextArtist.TextArtistSetting titleTextArtistSetting = getTitleTextArtistSetting(textPaint);
        titleTextArtist = new TextArtist(titleTextArtistSetting);

        TextArtist.TextArtistSetting businessHourArtistSetting = getBusinessHourArtistSetting(textPaint);
        businessHourArtist = new TextArtist(businessHourArtistSetting);

        TextArtist.TextArtistSetting payModeArtistSetting = gePayModeArtistSetting(textPaint);
        payModeArtist = new TextArtist(payModeArtistSetting);

        TextArtist.TextArtistSetting evaluateArtistSetting = getEvaluateArtistSetting(textPaint);
        evaluateArtist = new TextArtist(evaluateArtistSetting);

        TextArtist.TextArtistSetting merchantBriefArtistSetting = getMerchantBriefArtistSetting(textPaint);
        merchantBriefArtist = new TextArtist(merchantBriefArtistSetting);


        TextArtist.TextArtistSetting evaluateArtistSetting1 = getEvaluateArtistSetting(textPaint);
        evaluateArtist = new TextArtist(evaluateArtistSetting1);


        titleTextPoint = new Point();
        payModePoint = new Point();
        businessHourPoint = new Point();
        merchantBriefPoint = new Point();
        evaluatePoint = new Point();

        mLinePaint = new Paint();
        updateSize();
    }

    @Override
    public int height() {
        return mContentWidth;
    }

    @Override
    public int width() {
        return height;
    }

    private void updateSize() {
        businessHourHeight = businessHourArtist.getHeight();

        evaluateHeight = evaluateArtist.getHeight();

        height = DP36 + DP10 + businessHourHeight;

        if (!TextUtils.isEmpty(merchantBrief)) {
            merchantBriefHeight = merchantBriefArtist.getHeight();
            height += merchantBriefHeight + DP5;
        }
        if (!TextUtils.isEmpty(payMode)) {
            payModeHeight = payModeArtist.getHeight();
            height += payModeHeight + DP5;
        }
        if (!TextUtils.isEmpty(envi)) {
            evaluateHeight = evaluateArtist.getHeight();
            height += evaluateHeight + DP5;
        }

        updateBoundsInner(mContentWidth, height);
    }


    @Override
    protected void drawContentInner(Canvas canvas) {

        //step 1、先将画布移动到对应位置
        canvas.translate(DP15, 0);

        int left = mContentBound.left;
        int top = mContentBound.top;

        //step 2、再绘制

        //step 2.1、先绘制 title
        titleTextPoint.set(left, DP36 / 2 + top);
        titleTextArtist.setAlignReferencePoint(titleTextPoint);
        titleTextArtist.draw(canvas);

        //step 2.2、画分割线
        mLinePaint.setColor(GlobalUtil.getContext().getResources().getColor(R.color.system_gray));
        mLinePaint.setStrokeWidth(2);
        int mLineY = top + DP36;
        canvas.drawLine(left, mLineY, mContentWidth, mLineY, mLinePaint);


        //step 2.3 营业时间
        top += DP36 + DP5;
        businessHourPoint.set(left, businessHourHeight / 2 + top);
        businessHourArtist.setAlignReferencePoint(businessHourPoint);
        businessHourArtist.draw(canvas);

        //step 2.3 支付方式

        if (!TextUtils.isEmpty(payMode)) {
            top += businessHourHeight + DP5;
            payModePoint.set(left, payModeHeight / 2 + top);
            payModeArtist.setAlignReferencePoint(payModePoint);
            payModeArtist.draw(canvas);
        } else {
            payModeHeight = 0;
        }

        //step 2.4 商户简介
        if (!TextUtils.isEmpty(merchantBrief)) {
            top += payModeHeight + DP5;
            merchantBriefPoint.set(left, merchantBriefHeight / 2 + top);
            merchantBriefArtist.setAlignReferencePoint(merchantBriefPoint);
            merchantBriefArtist.draw(canvas);
        } else {
            merchantBriefHeight = 0;
        }


        //step 2.5 评价

        if (!TextUtils.isEmpty(merchantBrief)) {
            top += merchantBriefHeight + DP5;
            evaluatePoint.set(left, evaluateHeight / 2 + top);
            evaluateArtist.setAlignReferencePoint(evaluatePoint);
            evaluateArtist.draw(canvas);
        }

    }

    /**
     * 标题
     *
     * @param textPaint
     * @return
     */
    @NonNull
    private TextArtist.TextArtistSetting getTitleTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(title).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC29));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(mContentWidth);
        return nameArtistSetting;
    }

    /**
     * 营业时间
     *
     * @param textPaint
     * @return
     */
    @NonNull
    private TextArtist.TextArtistSetting getBusinessHourArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(businessHour).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(mContentWidth);
        return nameArtistSetting;
    }


    /**
     * 支付方式
     *
     * @param textPaint
     * @return
     */
    @NonNull
    private TextArtist.TextArtistSetting gePayModeArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(payMode).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(mContentWidth);
        return nameArtistSetting;
    }

    /**
     * 商户简介
     *
     * @param textPaint
     * @return
     */
    @NonNull
    private TextArtist.TextArtistSetting getMerchantBriefArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(merchantBrief).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(mContentWidth);
        return nameArtistSetting;
    }


    /**
     * 评价（口味、环境、服务）
     *
     * @param textPaint
     * @return
     */
    @NonNull
    private TextArtist.TextArtistSetting getEvaluateArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(toast).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.append(toastValue).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.search_detail_evaluate));
        nameArtistSetting.append(envi).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.append(enviValue).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.search_detail_evaluate));
        nameArtistSetting.append(service).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        nameArtistSetting.append(serviceValue).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.search_detail_evaluate));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(mContentWidth);
        return nameArtistSetting;
    }


}
