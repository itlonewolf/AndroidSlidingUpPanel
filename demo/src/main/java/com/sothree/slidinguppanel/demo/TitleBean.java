package com.sothree.slidinguppanel.demo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;

/**
 * 第一部分
 */

public class TitleBean implements IAssembleable {
    
    /**
     * POI名称
     */
    
    private String name;
    
    private String poiLoc;
    
    /**
     * 类型名称
     */
    private String typeName;
    
    /**
     * 星级
     */
    private String rank;
    
    private float price;
    
    private int SP18 = LayoutUtils.getPxByDimens(R.dimen.F4);
    private int SP14 = LayoutUtils.getPxByDimens(R.dimen.F2);
    
    private int DP110 = LayoutUtils.getPxByDimens(R.dimen.dp110);
    private int DP60  = LayoutUtils.getPxByDimens(R.dimen.dp60);
    private int DP30  = LayoutUtils.getPxByDimens(R.dimen.dp30);
    private int DP15  = LayoutUtils.getPxByDimens(R.dimen.dp15);
    private int DP5   = LayoutUtils.getPxByDimens(R.dimen.dp5);
    
    float width;//文本的宽度
    int   height;//文本的高度
    
    TextArtist mNameTextArtist;
    Point      mNameTextPoint;
    int        nameTextHeight;
    
    TextArtist mPriceTextArtist;
    Point      mPriceTextPoint;
    int        priceTextHeight;
    
    Rect mContentBound;
    
    Drawable starA;
    Drawable starB;
    Drawable starC;
    
    
    public TitleBean(int width) {
        this.width = width;
    }
    
    public static TitleBean demoBean(int width) {
        TitleBean bean = new TitleBean(width);
        bean.name = "串亭烧烤居酒屋(东直门店)";
        bean.price = 66;
        bean.typeName = "川菜  中餐馆";
        return bean;
    }
    
    @Override
    public void initAssemble() {
        final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        
        mNameTextPoint = new Point();
        mPriceTextPoint = new Point();
        mContentBound = new Rect();
        
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LC);
        nameArtistSetting.append(name).absoluteSize(SP18);
        if (!TextUtils.isEmpty(poiLoc)) {
            nameArtistSetting.append(poiLoc).absoluteSize(SP14);
        }
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth((int) width);
        mNameTextArtist = new TextArtist(nameArtistSetting);
        
        
        TextArtist.TextArtistSetting priceArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        priceArtistSetting.setAlign(TextArtist.ALIGN_LC);
        priceArtistSetting.appendImage(R.drawable.star_1);
        priceArtistSetting.appendImage(R.drawable.star_1);
        priceArtistSetting.appendImage(R.drawable.star_1);
        priceArtistSetting.appendImage(R.drawable.star_2);
        priceArtistSetting.appendImage(R.drawable.star_2);
        priceArtistSetting.append(String.format("人均:¥ %s  %s", price, typeName)).absoluteSize(SP14);
        
        priceArtistSetting.setMaxLineCount(1);
        priceArtistSetting.setOuterWidth((int) width);
        mPriceTextArtist = new TextArtist(priceArtistSetting);
        
        Rect nameBounds = new Rect();
        textPaint.setTextSize(SP18);
        textPaint.getTextBounds(name, 0, name.length(), nameBounds);
        nameTextHeight = nameBounds.height();
        
        Rect priceBounds = new Rect();
        textPaint.setTextSize(SP14);
        textPaint.getTextBounds(name, 0, name.length(), priceBounds);
        priceTextHeight = priceBounds.height();
        
        starA = GlobalUtil.getResources().getDrawable(R.drawable.star_1);
        starB = GlobalUtil.getResources().getDrawable(R.drawable.star_2);
        starC = GlobalUtil.getResources().getDrawable(R.drawable.star_3);
        
        height = DP30 * 2 + DP5 + nameTextHeight + priceTextHeight;
        
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public void drawContent(Canvas canvas, Rect rect) {
        mContentBound.set(rect);
        
        final int titleFlag = canvas.save();
        
        //step 1、先将画布移动到对应位置
        canvas.translate(DP30, DP15);
        int left = rect.left;
        int top  = rect.top;
        
        
        //step 2、再绘制
        //step 2.1、先绘制 name
        mNameTextPoint.set(left, nameTextHeight / 2 + top);
        mNameTextArtist.setAlignReferencePoint(mNameTextPoint);
        mNameTextArtist.draw(canvas);
        
        //step 2.2、再绘制星星一行
        top += DP5 + nameTextHeight;
        mPriceTextPoint.set(left, priceTextHeight / 2 + top);
        mPriceTextArtist.setAlignReferencePoint(mPriceTextPoint);
        mPriceTextArtist.draw(canvas);
        
        canvas.restoreToCount(titleFlag);
    }
    
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return DP110;
    }
    
    public Rect getRect() {
        return mContentBound;
    }
    
}
