package com.sothree.slidinguppanel.demo;

import android.graphics.*;
import android.text.TextPaint;
import android.util.Log;

/**
 * Created by xiaoyee on 2017/11/14.
 */

public class DistanceBean extends ARefreshable {
    Paint mLinePaint;

    TextPaint textPaint;

    TextArtist mCarTextArtist;
    Point mCarTextPoint;

    private String time;
    private String distance;

    private int width;
    private int height;

    private int SP16 = LayoutUtils.getPxByDimens(R.dimen.sp16);
    private int DP16 = LayoutUtils.getPxByDimens(R.dimen.dp16);
    private int DP18 = LayoutUtils.getPxByDimens(R.dimen.dp18);
    private int DP20 = LayoutUtils.getPxByDimens(R.dimen.dp20);
    private int DP44 = LayoutUtils.getPxByDimens(R.dimen.dp44);

    public DistanceBean(int width) {
        this.width = width;
    }

    public static DistanceBean demoBean(int width) {
        final DistanceBean distanceBean = new DistanceBean(width);
        distanceBean.time = "agfpqdt";
        distanceBean.distance = "60公里";
        return distanceBean;
    }

//    public void refresh() {
//        this.time = "1小时 30分钟";
//        if (mRefreshListener != null) {
//            mRefreshListener.onRefresh(getBoundsInner());
//        }
//    }

    @Override
    public void initAssemble() {

        mLinePaint = new Paint();
        mLinePaint.setColor(GlobalUtil.getContext().getResources().getColor(R.color.system_gray));
        mLinePaint.setStrokeWidth(1);


        mCarTextPoint = new Point();

        textPaint = new TextPaint();
        TextArtist.TextArtistSetting priceArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        priceArtistSetting.setAlign(TextArtist.ALIGN_CC);
        priceArtistSetting.appendImageWithHeight(R.drawable.car, DP18);
//        priceArtistSetting.appendImageWithHeight(R.drawable.rectangle, DP18);
        priceArtistSetting.append(String.format("  %s  %s", time, distance)).absoluteSize(SP16);

        priceArtistSetting.setMaxLineCount(1);
        priceArtistSetting.setOuterWidth(width);
        mCarTextArtist = new TextArtist(priceArtistSetting);

        height = DP44;
        updateBoundsInner(width, height);
    }

    @Override
    public int height() {
        return 0;
    }

    @Override
    public int width() {
        return 0;
    }


    @Override
    public void drawContentInner(Canvas canvas) {

        int left = mContentBound.left;

        int top = mContentBound.top;
        int right = mContentBound.right;
        int bottom = mContentBound.bottom;


        canvas.drawLine(left, top, right, top, mLinePaint);
        canvas.drawLine(left, bottom - 2, right, bottom - 2, mLinePaint);

        mCarTextPoint.set(right / 2, bottom / 2 + top);
        mCarTextArtist.setAlignReferencePoint(mCarTextPoint);
        mCarTextArtist.draw(canvas);
    }
}
