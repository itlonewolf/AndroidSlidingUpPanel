package com.sothree.slidinguppanel.demo;

import android.graphics.*;
import android.text.TextPaint;

/**
 * Created by xiaoyee on 2017/11/14.
 */

public class DistanceBean implements IAssembleable {
    Paint mLinePaint;
    
    TextPaint textPaint;
    
    TextArtist mCarTextArtist;
    Point      mCarTextPoint;
    int        mCarTextHeight;
    
    private String time;
    private String distance;
    
    private float width;
    Rect clipBound;
    
    private int SP16 = LayoutUtils.getPxByDimens(R.dimen.sp16);
    private int DP44 = LayoutUtils.getPxByDimens(R.dimen.dp44);
    
    public DistanceBean(float width) {
        this.width = width;
    }
    
    public static DistanceBean demoBean(float width) {
        final DistanceBean distanceBean = new DistanceBean(width);
        distanceBean.time = "1小时30分钟";
        distanceBean.distance = "60公里";
        return distanceBean;
    }
    
    @Override
    public void initAssemble() {
        
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(1);
        
        
        mCarTextPoint = new Point();
        
        clipBound = new Rect();
        textPaint = new TextPaint();
        TextArtist.TextArtistSetting priceArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        priceArtistSetting.setAlign(TextArtist.ALIGN_CC);
//        priceArtistSetting.appendImage(R.drawable.car, ImageSpan.ALIGN_BOTTOM);
        priceArtistSetting.append(String.format("  %s  %s", time, distance)).absoluteSize(SP16);
        
        priceArtistSetting.setMaxLineCount(1);
        priceArtistSetting.setOuterWidth((int) width);
        mCarTextArtist = new TextArtist(priceArtistSetting);
        
        Rect nameBounds = new Rect();
        textPaint.setTextSize(SP16);
        textPaint.getTextBounds(time, 0, time.length(), nameBounds);
        mCarTextHeight = nameBounds.height();
    }
    
    @Override
    public float getWidth() {
        return width;
    }
    
    @Override
    public float getHeight() {
        return DP44;
    }
    
    @Override
    public Rect getRect() {
        return null;
    }
    
    @Override
    public void drawContent(Canvas canvas, Rect rect) {
        int left   = rect.left;
        int top    = rect.top;
        int right  = rect.right;
        int bottom = rect.bottom;
        
        canvas.drawLine(left, top, right, top, mLinePaint);
        canvas.drawLine(left, bottom - 2, right, bottom - 2, mLinePaint);
        
        mCarTextPoint.set(rect.width() / 2, rect.height() / 2 + top);
        mCarTextArtist.setAlignReferencePoint(mCarTextPoint);
        mCarTextArtist.draw(canvas);
    }
}
