package com.sothree.slidinguppanel.demo;

import android.graphics.*;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 第一部分
 */

public class TitleBean extends ARefreshable {
    
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
    
    private final int width;//文本的宽度
    private       int height;//文本的高度
    
    private TextArtist mNameTextArtist;
    private Point      mNameTextPoint;
    private int        nameTextHeight;
    
    private TextArtist mPriceTextArtist;
    private Point      mPriceTextPoint;
    private int        priceTextHeight;
    
    private TextPaint textPaint;
    
    public TitleBean(int width) {
        this.width = width;
    }
    
    public static TitleBean demoBean(int width) {
        TitleBean bean = new TitleBean(width);
        bean.name = "正在逆地理";
        bean.price = 0;
        bean.typeName = "XX  XXX";
        return bean;
    }
    
    public void refresh() {
        this.name = "串亭烧烤居酒屋(东直门店)";
        this.price = 66;
        this.typeName = "川菜  中餐馆";
        
        TextArtist.TextArtistSetting nameArtistSetting = getNameTextArtistSetting(textPaint);
        mNameTextArtist = new TextArtist(nameArtistSetting);
        
        TextArtist.TextArtistSetting priceArtistSetting = getDIstanceTextArtistSetting(textPaint);
        mPriceTextArtist = new TextArtist(priceArtistSetting);
    
        updateSize();
        if (mRefreshListener != null) {
            mRefreshListener.onRefresh(mContentBound);
        }
    }
    
    
    @Override
    public void initAssemble() {
    
        // ▼ //demo xiaoyee ▼
    
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(1);
        // ▲ //demo xiaoyee ▲
        
        
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    
        mNameTextPoint = new Point();
        mPriceTextPoint = new Point();
        mContentBound = new Rect();
    
        TextArtist.TextArtistSetting nameArtistSetting = getNameTextArtistSetting(textPaint);
        mNameTextArtist = new TextArtist(nameArtistSetting);
    
    
        TextArtist.TextArtistSetting priceArtistSetting = getDIstanceTextArtistSetting(textPaint);
        mPriceTextArtist = new TextArtist(priceArtistSetting);
    
        updateSize();
    }
    
    @Override
    public int height() {
        return height;
    }
    
    @Override
    public int width() {
        return width;
    }
    
    private void updateSize() {
        nameTextHeight = mNameTextArtist.getHeight();
        priceTextHeight = mPriceTextArtist.getHeight();
        height = DP30 * 2 + DP5 + nameTextHeight + priceTextHeight;
        updateBoundsInner(width, height);
    }
    
    @NonNull
    private TextArtist.TextArtistSetting getDIstanceTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting priceArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        priceArtistSetting.setAlign(TextArtist.ALIGN_LT);
        priceArtistSetting.appendImage(R.drawable.star_1);
        priceArtistSetting.appendImage(R.drawable.star_1);
        priceArtistSetting.appendImage(R.drawable.star_1);
        priceArtistSetting.appendImage(R.drawable.star_2);
        priceArtistSetting.appendImage(R.drawable.star_2);
        priceArtistSetting.append(String.format("人均:¥ %s  %s", price, typeName)).absoluteSize(SP14);
        
        priceArtistSetting.setMaxLineCount(1);
        priceArtistSetting.setOuterWidth(width);
        return priceArtistSetting;
    }
    
    @NonNull
    private TextArtist.TextArtistSetting getNameTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_LT);
        nameArtistSetting.append(name).absoluteSize(SP18);
        if (!TextUtils.isEmpty(poiLoc)) {
            nameArtistSetting.append(poiLoc).absoluteSize(SP14);
        }
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(width);
        return nameArtistSetting;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public void drawContentInner(Canvas canvas) {
        //step 1、先将画布移动到对应位置
        canvas.translate(DP30, DP15);
    
        int left = mContentBound.left;
        int top  = mContentBound.top;
    
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
    
        // ▼ //demo xiaoyee ▼
        mLinePaint.setTextSize(40);
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStrokeWidth(3);
//        canvas.drawText(String.format("w:%s X h:%s", mContentBound.width(), mContentBound.height()), left, top, mLinePaint);
        // ▲ //demo xiaoyee ▲
    }
    
    Paint mLinePaint;
    
    @Override
    public void onClick() {
        Toast.makeText(GlobalUtil.getContext(), "点击了 title bean", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean isClickable() {
        return true;
    }
}
