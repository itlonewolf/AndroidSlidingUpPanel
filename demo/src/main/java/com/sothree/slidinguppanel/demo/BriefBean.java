package com.sothree.slidinguppanel.demo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

/**
 * 简介
 */

public class BriefBean implements IAssembleable {
    
    TextPaint paint         = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    Rect      firstLineRect = new Rect();
    
    private Rect myBounds;
    
    public static String title   = "简介";
    private       String time    = "营业时间： 9:00-22:00";
    private       String profile = "商户简介：店铺于2013年成立";
    private       String envi    = "环境：9.0";
    private       String service = "服务：9.0";
    public static String toast   = "口味：9.0";
    int marginLeft = LayoutUtils.dp2px(15);
    int width;
    int height;
    private Rect mRect;
    
    @Override
    public void addRefreshListener(IRefreshListener listener) {
    
    }
    
    @Override
    public void initAssemble() {
    
    }
    
    @Override
    public float getWidth() {
        return width;
    }
    
    @Override
    public float getHeight() {
        return height;
    }
    
    @Override
    public Rect getRect() {
        return null;
    }
    
    @Override
    public void drawContent(Canvas canvas, Rect rect) {
    
    }

//    public Rect getClipRect(){
//
//    }
    
    public void drawContent(Canvas canvas) {
        
        
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);
        paint.setColor(Color.parseColor("#333333"));
        paint.setTextSize(LayoutUtils.dp2px(14));
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        
        // 画title
        canvas.drawText(title, marginLeft, fontMetrics.descent + 60, paint);
        
        paint.setColor(Color.parseColor("#cccccc"));
        int lineTop = LayoutUtils.dp2px(36);
        
        canvas.drawLine(marginLeft, lineTop, canvas.getWidth(), lineTop, paint);
        
        // 画第一行
        
        paint.getTextBounds(time, 0, time.length(), firstLineRect);
        int titleHeight = LayoutUtils.px2dp(firstLineRect.height());//文本的高度
        paint.setColor(Color.parseColor("#888888"));
        canvas.drawText(time, marginLeft, LayoutUtils.dp2px(titleHeight + 46), paint);
        
        // 画第二行
        paint.getTextBounds(profile, 0, profile.length(), firstLineRect);
        int profileHeight = LayoutUtils.px2dp(firstLineRect.height());//文本的高度
        canvas.drawText(profile, marginLeft, LayoutUtils.dp2px(titleHeight + profileHeight + 51), paint);
        
        // 画第三行（1）
        paint.getTextBounds(toast, 0, toast.length(), firstLineRect);
        int toastHeight = LayoutUtils.px2dp(firstLineRect.height());//文本的高度
        canvas.drawText(toast, marginLeft, LayoutUtils.dp2px(titleHeight + profileHeight + toastHeight + 56), paint);
        // 画第三行（2）
        int toastWidth = (int) getTextWidth(toast, paint);
        canvas.drawText(envi, marginLeft + toastWidth + 15, LayoutUtils.dp2px(titleHeight + profileHeight + toastHeight + 56), paint);
        
        // 画第三行（3）
        int enviWidth = (int) getTextWidth(envi, paint);
        canvas.drawText(service, marginLeft + toastWidth + 15 + enviWidth + 15, LayoutUtils.dp2px(titleHeight + profileHeight + toastHeight + 56), paint);
    }
    
    Rect tmpRectInCalc = new Rect();
    
    /**
     * 获取文字所在矩形的宽度
     *
     * @param paint
     * @param text
     * @return
     */
    private float getTextWidth(String text, Paint paint) {
        //获取文字的宽度
        paint.getTextBounds(text, 0, text.length(), tmpRectInCalc);
        
        return tmpRectInCalc.width();
    }
}
