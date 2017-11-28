package com.sothree.slidinguppanel.demo;

import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * poi 摘要
 *
 * @author zhaozy
 */

public class PoisummaryUnit extends ARefreshable {

    /**
     * POI名称
     */

    private String name;

    private String address;
    private String code;
    /**
     * 距离
     */
    private float distance;
    private String poiDistance;

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
    private int SP17 = LayoutUtils.getPxByDimens(R.dimen.sp17);
    private int SP14 = LayoutUtils.getPxByDimens(R.dimen.F2);
    private int SP13 = LayoutUtils.getPxByDimens(R.dimen.sp13);

    private int DP110 = LayoutUtils.getPxByDimens(R.dimen.dp110);
    private int DP60 = LayoutUtils.getPxByDimens(R.dimen.dp60);
    private int DP30 = LayoutUtils.getPxByDimens(R.dimen.dp30);
    private int DP15 = LayoutUtils.getPxByDimens(R.dimen.dp15);
    private int DP16 = LayoutUtils.getPxByDimens(R.dimen.dp16);
    private int DP26 = LayoutUtils.getPxByDimens(R.dimen.dp26);
    private int DP5 = LayoutUtils.getPxByDimens(R.dimen.dp5);
    private int DP1 = LayoutUtils.getPxByDimens(R.dimen.dp1);
    private int DP12 = LayoutUtils.getPxByDimens(R.dimen.dp12);
    private int DP6 = LayoutUtils.getPxByDimens(R.dimen.dp6);
    private int DP91 = LayoutUtils.getPxByDimens(R.dimen.dp91);

    private final int width;//文本的宽度
    private int height;//文本的高度

    private Point mNameTextPoint;


    private TextArtist mNameTextArtist;
    private int nameTextHeight;

    private TextArtist distanceArtist;
    private int distanceArtistHeight;

    private TextArtist phoneArtist;
    private Point phonePoint;


    private TextArtist mPriceTextArtist;
    private TextArtist commonSingleArtist;
    private Point mPriceTextPoint;
    private int priceTextHeight;

    private TextPaint textPaint;
    private TextPaint secondTextPaint;

    private Point distancePoint;


    private Point commonSinglePoint;
    private int commonSingleHeight;

    private List<TextArtist> itemArtistList = new ArrayList<>();
    private List<Point> itemPointList = new ArrayList<>();
    private List<String> itemStringList = new ArrayList<>();
    private int itemTotalHeight;
    private String phone;

    private int phoneHeight;
    private Bitmap phoneBitmap;
    private Bitmap arrowBitmap;
    private Bitmap collectionSelectedBitmap;
    private Bitmap collectionCancelBitmap;
    private TextArtist parkingArtist;
    private Point parkingPoint;
    private int parkingArtistHeight;
    private int type = 5;
    private TextArtist locationDistanceArtist;
    private int locationDistanceHeight;
    private TextArtist locationAddressArtist;
    private int locationAddressArtistHeight;
    private TextArtist locationCodeArtist;
    private Point loacationCodePoint;
    private Point locationAddressPoint;
    private int locationCodeArtistHeight;
    private int phoneTop;
    private TextArtist.TextArtistSetting distanceSetting;

    public PoisummaryUnit(int width) {
        this.width = width;
    }

    public static PoisummaryUnit demoBean(int width) {
        PoisummaryUnit bean = new PoisummaryUnit(width);
        bean.name = "正在逆地理";
        bean.price = 0;
        bean.distance = 72;
        bean.typeName = "";
        bean.phone = "010-7118719、010-7118719、010-7118719、010-7118719、010-7118719、";

        switch (bean.type) {
            case 0:
                bean.name = "我的位置";
                bean.poiDistance = "精确到65米";
                bean.address = "东城区东直门南大街6号";
                bean.code = "定位编码：1234567890";

                break;
            case 1:
                bean.typeName = "中菜馆、香锅";
                bean.price = 66;

                break;
            case 2:
                bean.name = "中国石化东大桥中国石油加油站";
                bean.typeName = "中国石化";
                break;
            case 3:
                bean.name = "云南保山交通运输4s店";
                bean.typeName = "上海通用4s店";

                break;
            case 4:
                bean.name = "东方花园饭店停车场";
                bean.parkingPoint = new Point();
                bean.typeName = "地上停车场";

                break;
            case 5:
                bean.name = "北纬路50号";
                bean.poiDistance = "据您65米";
                bean.address = "东城区东直门南大街6号";
                bean.code = "定位编码：0987654321";
                break;
            default:
                break;
        }


        return bean;
    }

    public void refresh() {
        type = 4;
        //idea type 0:我的位置 1：美食专区 2：加油站  3:4S店 4：停车场 5:POI位置
        switch (type) {
            case 0:
                name = "我的位置";
                poiDistance = "精确到65米";
                address = "东城区东直门南大街6号";
                code = "定位编码：1234567890";

                break;
            case 1:
                //step  通用的二级标题\\
                typeName = "中菜馆、香锅";
                this.price = 66;

                break;
            case 2:
                name = "中国石化东大桥中国石油加油站";
                typeName = "中国石化";
                break;
            case 3:
                name = "云南保山交通运输4s店";
                typeName = "上海通用4s店";

                break;
            case 4:
                name = "东方花园饭店停车场";
                parkingPoint = new Point();
                typeName = "地上停车场";

                break;
            case 5:
                name = "北纬路52号";
                poiDistance = "据您65米";
                address = "东城区东直门南大街6号";
                code = "定位编码：1234567890";
                break;
            default:
                break;
        }
        initAssemble();


        if (mRefreshListener != null) {
            mRefreshListener.onRefresh(mContentBound);
        }
    }

    @Override
    public void initAssemble() {

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        secondTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        secondTextPaint.setTextSize(LayoutUtils.dp2px(17));


        mContentBound = new Rect();

        commonSinglePoint = new Point();
        distancePoint = new Point();
    
    
        phoneBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_arrow)).getBitmap();
        arrowBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_arrow)).getBitmap();
        collectionSelectedBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_like_selected)).getBitmap();
        collectionCancelBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_like_cancel)).getBitmap();


        //idea type 0:我的位置 1：美食专区 2：加油站  3:4S店 4：停车场 5:POI位置
        switch (type) {
            case 0:

                locationDistanceArtist = new TextArtist(getCommonSetting(textPaint, poiDistance));
                locationAddressArtist = new TextArtist(getCommonSetting(textPaint, address));
                locationCodeArtist = new TextArtist(getItemTextSetting(textPaint, code));

                locationAddressPoint = new Point();
                loacationCodePoint = new Point();

                break;
            case 1:
                //step  通用的二级标题\\
                distanceSetting = getDistanceSetting(String.format("据您%s 千米", distance), typeName, textPaint);
                distanceArtist = new TextArtist(distanceSetting);
                name = "串亭烧烤居酒屋";
                this.price = 66;
                mPriceTextPoint = new Point();
                TextArtist.TextArtistSetting priceArtistSetting = getPriceTextArtistSetting(textPaint);
                mPriceTextArtist = new TextArtist(priceArtistSetting);

                break;
            case 2:
                commonSingleArtist = new TextArtist(getCommonSetting(textPaint, "油价"));

                //step  通用的二级标题
                distanceSetting = getDistanceSetting(String.format("据您%s 千米", distance), typeName, textPaint);
                distanceArtist = new TextArtist(distanceSetting);

                itemStringList.clear();
                itemPointList.clear();
                itemStringList.add("92#汽油：6.2/升");
                itemStringList.add("95#汽油：6.2/升");
                itemStringList.add("仅供参考，请以实际为准");

                for (String itemStr : itemStringList) {
                    TextArtist.TextArtistSetting itemTextSetting = getItemTextSetting(textPaint, itemStr);
                    TextArtist itemTextArtist = new TextArtist(itemTextSetting);
                    itemArtistList.add(itemTextArtist);
                    Point point = new Point();
                    itemPointList.add(point);
                }
                break;
            case 3:
                //step  通用的二级标题
                distanceSetting = getDistanceSetting(String.format("据您%s 千米", distance), typeName, textPaint);
                distanceArtist = new TextArtist(distanceSetting);

                break;
            case 4:
                parkingPoint = new Point();
                //step  通用的二级标题
                distanceSetting = getDistanceSetting(String.format("据您%s 千米", distance), typeName, textPaint);
                distanceArtist = new TextArtist(distanceSetting);

                TextArtist.TextArtistSetting parkingSetting = getParkingSetting("9", "20", textPaint);
                parkingArtist = new TextArtist(parkingSetting);

                commonSingleArtist = new TextArtist(getCommonSetting(textPaint, "收费信息"));

                itemStringList.clear();
                itemPointList.clear();
                itemStringList.add("白天(7:00-21:00): 2元／15分钟");
                itemStringList.add("晚上(21:00-次日7:00): 1元／2小时");

                for (String itemStr : itemStringList) {
                    TextArtist.TextArtistSetting itemTextSetting = getItemTextSetting(textPaint, itemStr);
                    TextArtist itemTextArtist = new TextArtist(itemTextSetting);
                    itemArtistList.add(itemTextArtist);
                    Point point = new Point();
                    itemPointList.add(point);
                }
                break;
            case 5:
                locationDistanceArtist = new TextArtist(getCommonSetting(textPaint, poiDistance));
                locationAddressArtist = new TextArtist(getCommonSetting(textPaint, address));
                locationCodeArtist = new TextArtist(getItemTextSetting(textPaint, code));

                locationAddressPoint = new Point();
                loacationCodePoint = new Point();
                break;
            default:
                break;
        }


        //step  通用的标题
        mNameTextPoint = new Point();
        TextArtist.TextArtistSetting nameArtistSetting = getNameTextArtistSetting(textPaint);
        mNameTextArtist = new TextArtist(nameArtistSetting);


        //step  通用的电话
        phoneArtist = new TextArtist(getTextArtistSetting(textPaint));
        phonePoint = new Point();

        updateSize();
        if (mRefreshListener != null) {
            mRefreshListener.onRefresh(mContentBound);
        }

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

        //idea type 0:我的位置 1：美食专区 2：加油站  3:4S店 4：停车场

        nameTextHeight = mNameTextArtist.getHeight();
        switch (type) {
            case 0:
                locationDistanceHeight = locationDistanceArtist.getHeight();
                locationAddressArtistHeight = locationAddressArtist.getHeight();
                locationCodeArtistHeight = locationCodeArtist.getHeight();
                this.height = DP15 + nameTextHeight + DP15 + locationDistanceHeight + DP5 + locationAddressArtistHeight + DP5 + locationCodeArtistHeight;

                break;
            case 1:
                priceTextHeight = mPriceTextArtist.getHeight();
                distanceArtistHeight = distanceArtist.getHeight();
                phoneHeight = phoneArtist.getHeight();

                this.height = DP15 + nameTextHeight + DP5 + priceTextHeight + DP15 + distanceArtistHeight + DP15 + phoneHeight;
                break;
            case 2:
                distanceArtistHeight = distanceArtist.getHeight();
                commonSingleHeight = commonSingleArtist.getHeight();
                phoneHeight = phoneArtist.getHeight();


                itemTotalHeight = 0;
                for (int i = 0; i < itemArtistList.size(); i++) {
                    itemTotalHeight += DP5;
                    itemTotalHeight += itemArtistList.get(i).getHeight();
                }
                this.height = DP15 + +nameTextHeight + DP15 + distanceArtistHeight + DP15 + commonSingleHeight + itemTotalHeight + DP15 + phoneHeight;
                break;
            case 3:
                distanceArtistHeight = distanceArtist.getHeight();
                phoneHeight = phoneArtist.getHeight();


                this.height = DP15 + nameTextHeight + DP15 + distanceArtistHeight + DP15 + phoneHeight;
                break;
            case 4:
                distanceArtistHeight = distanceArtist.getHeight();
                parkingArtistHeight = parkingArtist.getHeight();
                commonSingleHeight = commonSingleArtist.getHeight();
                phoneHeight = phoneArtist.getHeight();

                itemTotalHeight = 0;
                for (int i = 0; i < itemArtistList.size(); i++) {
                    itemTotalHeight += DP5;
                    itemTotalHeight += itemArtistList.get(i).getHeight();
                }

                this.height = DP15 + nameTextHeight + DP15 + distanceArtistHeight + DP5 + parkingArtistHeight + DP15 + commonSingleHeight + itemTotalHeight + DP15 + phoneHeight;
                break;

            case 5:
                phoneHeight = phoneArtist.getHeight();
                locationDistanceHeight = locationDistanceArtist.getHeight();
                locationAddressArtistHeight = locationAddressArtist.getHeight();
                locationCodeArtistHeight = locationCodeArtist.getHeight();
                this.height = DP15 + nameTextHeight + DP15 + locationDistanceHeight + DP5 + locationAddressArtistHeight + DP5 + locationCodeArtistHeight + DP15 + phoneHeight;
                break;
            default:
                break;
        }

        updateBoundsInner(width, this.height);


    }

    @NonNull
    private TextArtist.TextArtistSetting getPriceTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting setting = new TextArtist.TextArtistSetting(textPaint);
        setting.setAlign(TextArtist.ALIGN_LC);
        setting.appendImage(R.drawable.star_1);
        setting.appendImage(R.drawable.star_1);
        setting.appendImage(R.drawable.star_1);
        setting.appendImage(R.drawable.star_2);
        setting.appendImage(R.drawable.star_2);
        setting.append(String.format("  ¥%s/人 ", price)).absoluteSize(SP17).color(Color.WHITE);

        setting.setMaxLineCount(1);
        setting.setOuterWidth(width);
        return setting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getNameTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting setting = new TextArtist.TextArtistSetting(textPaint);
        setting.setAlign(TextArtist.ALIGN_LC);
        setting.append(name).absoluteSize(SP18).color(Color.WHITE);
        setting.setMaxLineCount(1);
        setting.setOuterWidth(width);
        return setting;
    }

    /**
     * 中间有间隔，同时拥有多个描述信息
     *
     * @param textPaint
     * @return
     */
    @NonNull
    private TextArtist.TextArtistSetting getDistanceSetting(String distance, String typeName, TextPaint textPaint) {
        TextArtist.TextArtistSetting setting = new TextArtist.TextArtistSetting(textPaint);
        setting.setAlign(TextArtist.ALIGN_LC);
        setting.append(distance).absoluteSize(SP17).color(Color.WHITE);
        if (!TextUtils.isEmpty(typeName)) {
            setting.appendImage(new CustomLineDrawable());
            setting.append(typeName).absoluteSize(SP17).color(Color.WHITE);

        }
        setting.setMaxLineCount(1);
        setting.setOuterWidth(width);
        return setting;
    }

    /**
     * 停车场
     *
     * @param textPaint
     * @return
     */
    @NonNull
    private TextArtist.TextArtistSetting getParkingSetting(String freeNum, String totalNum, TextPaint textPaint) {
        TextArtist.TextArtistSetting setting = new TextArtist.TextArtistSetting(textPaint);
        setting.setAlign(TextArtist.ALIGN_LC);
        setting.append("空车位: ").absoluteSize(SP17).color(Color.WHITE);
        setting.append(freeNum).absoluteSize(SP17).color(Color.parseColor("#00B300"));
        setting.appendImage(new CustomLineDrawable());
        setting.append(String.format("总车位: %s", totalNum)).absoluteSize(SP17).color(Color.WHITE);
        setting.setMaxLineCount(1);
        setting.setOuterWidth(width);
        return setting;
    }


    /**
     * 同时只有一个描述信息
     *
     * @param textPaint
     * @return
     */
    @NonNull
    private TextArtist.TextArtistSetting getCommonSetting(TextPaint textPaint, String name) {
        TextArtist.TextArtistSetting setting = new TextArtist.TextArtistSetting(textPaint);
        setting.setAlign(TextArtist.ALIGN_LC);
        setting.append(name).absoluteSize(SP17).color(Color.WHITE);
        setting.setMaxLineCount(3);
        setting.setOuterWidth(width);
        return setting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getItemTextSetting(TextPaint textPaint, String itemText) {
        TextArtist.TextArtistSetting setting = new TextArtist.TextArtistSetting(textPaint);
        setting.setAlign(TextArtist.ALIGN_LC);
        setting.append(String.format("%s ", itemText)).absoluteSize(SP13).color(Color.parseColor("#AAAAAA"));
        setting.setMaxLineCount(1);
        setting.setOuterWidth(width);
        return setting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting phoneArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        phoneArtistSetting.setAlign(TextArtist.ALIGN_LC);
        phoneArtistSetting.append(phone).absoluteSize(SP17).color(Color.WHITE);
        phoneArtistSetting.setMaxLineCount(1);
        phoneArtistSetting.setOuterWidth(320);
        return phoneArtistSetting;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取文字所在矩形的宽度
     *
     * @param paint
     * @param text
     * @return
     */
    private int getTextWidth(String text, Paint paint) {
        Rect tmpRect = new Rect();
        //获取文字的宽度
        paint.getTextBounds(text, 0, text.length(), tmpRect);

        return tmpRect.width();
    }

    @Override
    public void drawContentInner(Canvas canvas) {
        //step 1、先将画布移动到对应位置
        canvas.translate(DP15, DP15);

        int left = mContentBound.left;
        int top = mContentBound.top;

        //step 2.1、先绘制 name、收藏按钮（可通用）
        mNameTextPoint.set(left, nameTextHeight / 2 + top);
        mNameTextArtist.setAlignReferencePoint(mNameTextPoint);
        mNameTextArtist.draw(canvas);

        //idea type 0:我的位置 1：美食专区 2：加油站  3:4S店 4：停车场
        switch (type) {
            case 0:
                drawMyLocation(canvas, top, left);

                break;
            case 1:
                drawFoodUnit(canvas, top, left);
                break;
            case 2:
                drawOilUnit(canvas, top, left);
                break;
            case 3:
                drawCarStoreUnit(canvas, top, left);
                break;
            case 4:
                drawParkingUnit(canvas, top, left);

                break;
            case 5:
                drawPoiLocationUnit(canvas, top, left);
                break;
            default:
                break;
        }


    }

    /**
     * poi位置
     *
     * @param canvas 画布
     * @param top    上边距
     * @param left   左边距
     */
    private void drawPoiLocationUnit(Canvas canvas, int top, int left) {

        //step  收藏
        int starTop = top + (nameTextHeight - phoneBitmap.getWidth()) / 2;
        canvas.drawBitmap(collectionSelectedBitmap, width - DP15 * 3, starTop, textPaint);
        //step 绘制距离
        top += DP15 + nameTextHeight;
        distancePoint.set(left, top + locationDistanceHeight / 2);
        locationDistanceArtist.setAlignReferencePoint(distancePoint);
        locationDistanceArtist.draw(canvas);


        //step 绘制地址
        top += DP5 + locationDistanceHeight;
        locationAddressPoint.set(left, top + locationAddressArtistHeight / 2);
        locationAddressArtist.setAlignReferencePoint(locationAddressPoint);
        locationAddressArtist.draw(canvas);


        //step 绘制编码
        top += DP5 + locationAddressArtistHeight;
        loacationCodePoint.set(left, top + locationCodeArtistHeight / 2);
        locationCodeArtist.setAlignReferencePoint(loacationCodePoint);
        locationCodeArtist.draw(canvas);

        //step  电话（可通用）
        left += DP26;
        top += DP15 + locationCodeArtistHeight;
        phoneTop = top + (phoneHeight - phoneBitmap.getWidth()) / 2;
        phoneBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_phone)).getBitmap();
        arrowBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_arrow)).getBitmap();
        //step  电话图片
        canvas.drawBitmap(phoneBitmap, 0, phoneTop, textPaint);

        phonePoint.set(left, top + phoneHeight / 2);
        phoneArtist.setAlignReferencePoint(phonePoint);
        phoneArtist.draw(canvas);


    }

    /**
     * 停车场专区
     *
     * @param canvas 画布
     * @param top    上边距
     * @param left   左边距
     */
    private void drawParkingUnit(Canvas canvas, int top, int left) {
        //step  绘制距离、品种一行（各个专题可通用，中间有间隔符号；我的位置中地址也可以通用）
        top += DP15 + nameTextHeight;
        distancePoint.set(left, top + distanceArtistHeight / 2);
        distanceArtist.setAlignReferencePoint(distancePoint);
        distanceArtist.draw(canvas);


        top += DP5 + distanceArtistHeight;
        parkingPoint.set(left, top + parkingArtistHeight / 2);
        parkingArtist.setAlignReferencePoint(parkingPoint);
        parkingArtist.draw(canvas);


        //step 一个头部标题+多个单行显示（停车场+加油站）
        top += DP15 + parkingArtistHeight;
        commonSinglePoint.set(left, top + commonSingleHeight / 2);
        commonSingleArtist.setAlignReferencePoint(commonSinglePoint);
        commonSingleArtist.draw(canvas);
        top += commonSingleHeight;

        for (int i = 0; i < itemArtistList.size(); i++) {
            TextArtist textArtist = itemArtistList.get(i);
            Point itemPoint = itemPointList.get(i);
            top += DP5;
            itemPoint.set(left, top + textArtist.getHeight() / 2);
            textArtist.setAlignReferencePoint(itemPoint);
            textArtist.draw(canvas);
            top += textArtist.getHeight();

        }

        //step  电话（可通用）
        left += DP26;
        top += DP15;
        phoneTop = top + (phoneHeight - phoneBitmap.getWidth()) / 2;
        phoneBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_phone)).getBitmap();
        arrowBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_arrow)).getBitmap();
        //step  电话图片
        canvas.drawBitmap(phoneBitmap, 0, phoneTop, textPaint);

        phonePoint.set(left, top + phoneHeight / 2);
        phoneArtist.setAlignReferencePoint(phonePoint);
        phoneArtist.draw(canvas);

        //step  右箭头
        canvas.drawBitmap(arrowBitmap, width - DP15 * 3, phoneTop, textPaint);

    }

    /**
     * 4s店专区
     *
     * @param canvas 画布
     * @param top    上边距
     * @param left   左边距
     */
    private void drawCarStoreUnit(Canvas canvas, int top, int left) {
        //step  绘制距离、品种一行（各个专题可通用，中间有间隔符号；我的位置中地址也可以通用）
        top += DP15 + nameTextHeight;
        distancePoint.set(left, top + distanceArtistHeight / 2);
        distanceArtist.setAlignReferencePoint(distancePoint);
        distanceArtist.draw(canvas);

        //step  电话（可通用）
        left += DP26;
        top += DP15 + distanceArtistHeight;
        phoneTop = top + (phoneHeight - phoneBitmap.getWidth()) / 2;
        phoneBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_phone)).getBitmap();
        arrowBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_arrow)).getBitmap();
        //step  电话图片
        canvas.drawBitmap(phoneBitmap, 0, phoneTop, textPaint);

        phonePoint.set(left, top + phoneHeight / 2);
        phoneArtist.setAlignReferencePoint(phonePoint);
        phoneArtist.draw(canvas);

        //step  右箭头
        canvas.drawBitmap(arrowBitmap, width - DP15 * 3, phoneTop, textPaint);
    }

    /**
     * 加油站专区
     *
     * @param canvas 画布
     * @param top    上边距
     * @param left   左边距
     */
    private void drawOilUnit(Canvas canvas, int top, int left) {
        //step  绘制距离、品种一行（各个专题可通用，中间有间隔符号；我的位置中地址也可以通用）
        top += DP15 + nameTextHeight;
        distancePoint.set(left, top + distanceArtistHeight / 2);
        distanceArtist.setAlignReferencePoint(distancePoint);
        distanceArtist.draw(canvas);

        //step 一个头部标题+多个单行显示（停车场+加油站）
        top += DP15 + distanceArtistHeight;
        commonSinglePoint.set(left, top + commonSingleHeight / 2);
        commonSingleArtist.setAlignReferencePoint(commonSinglePoint);
        commonSingleArtist.draw(canvas);
        top += commonSingleHeight;

        for (int i = 0; i < itemArtistList.size(); i++) {
            TextArtist textArtist = itemArtistList.get(i);
            Point itemPoint = itemPointList.get(i);
            top += DP5;
            itemPoint.set(left, top + textArtist.getHeight() / 2);
            textArtist.setAlignReferencePoint(itemPoint);
            textArtist.draw(canvas);
            top += textArtist.getHeight();

        }

        //step  电话（可通用）
        left += DP26;
        top += DP15;
        phoneTop = top + (phoneHeight - phoneBitmap.getWidth()) / 2;
        phoneBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_phone)).getBitmap();
        arrowBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_arrow)).getBitmap();
        //step  电话图片
        canvas.drawBitmap(phoneBitmap, 0, phoneTop, textPaint);

        phonePoint.set(left, top + phoneHeight / 2);
        phoneArtist.setAlignReferencePoint(phonePoint);
        phoneArtist.draw(canvas);

        //step  右箭头
        canvas.drawBitmap(arrowBitmap, width - DP15 * 3, phoneTop, textPaint);
    }

    /**
     * 美食专区
     *
     * @param canvas 画布
     * @param top    上边距
     * @param left   左边距
     */
    private void drawFoodUnit(Canvas canvas, int top, int left) {
        //step 2.2、星星一行
        top += DP5 + nameTextHeight;
        mPriceTextPoint.set(left, priceTextHeight / 2 + top);
        mPriceTextArtist.setAlignReferencePoint(mPriceTextPoint);
        mPriceTextArtist.draw(canvas);

        //step  绘制距离、品种一行（各个专题可通用，中间有间隔符号；我的位置中地址也可以通用）

        top += DP15 + priceTextHeight;
        distancePoint.set(left, top + distanceArtistHeight / 2);
        distanceArtist.setAlignReferencePoint(distancePoint);
        distanceArtist.draw(canvas);


        //step  电话（可通用）
        left += DP26;
        top += DP15 + distanceArtistHeight;
        phoneTop = top + (phoneHeight - phoneBitmap.getWidth()) / 2;
        phoneBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_phone)).getBitmap();
        arrowBitmap = ((BitmapDrawable) GlobalUtil.getContext().getResources().getDrawable(R.drawable.ic_arrow)).getBitmap();
        //step  电话图片
        canvas.drawBitmap(phoneBitmap, 0, phoneTop, textPaint);

        phonePoint.set(left, top + phoneHeight / 2);
        phoneArtist.setAlignReferencePoint(phonePoint);
        phoneArtist.draw(canvas);

        //step  右箭头
        canvas.drawBitmap(arrowBitmap, width - DP15 * 3, phoneTop, textPaint);
    }

    /**
     * 美食专区
     *
     * @param canvas 画布
     * @param top    上边距
     * @param left   左边距
     */
    private void drawMyLocation(Canvas canvas, int top, int left) {
        //step 绘制距离
        top += DP15 + nameTextHeight;
        distancePoint.set(left, top + locationDistanceHeight / 2);
        locationDistanceArtist.setAlignReferencePoint(distancePoint);
        locationDistanceArtist.draw(canvas);


        //step 绘制地址
        top += DP5 + locationDistanceHeight;
        locationAddressPoint.set(left, top + locationAddressArtistHeight / 2);
        locationAddressArtist.setAlignReferencePoint(locationAddressPoint);
        locationAddressArtist.draw(canvas);

        //step 绘制编码
        top += DP5 + locationAddressArtistHeight;
        loacationCodePoint.set(left, top + locationCodeArtistHeight / 2);
        locationCodeArtist.setAlignReferencePoint(loacationCodePoint);
        locationCodeArtist.draw(canvas);
    }


    @Override
    public void onClick() {
        refresh();
    }

    @Override
    public boolean isInteractive() {
        return true;
    }

    /**
     * 竖线
     */
    class CustomLineDrawable extends Drawable {

        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);


        @Override

        public void draw(@NonNull Canvas canvas) {


            paint.setColor(Color.parseColor("#cccccc"));
            paint.setStrokeWidth(1);
            Rect rect = new Rect(0, 0, DP12, DP12);

            //idea 文字基准线上移四分之一处开始绘制
            canvas.drawLine(rect.width() / 2, -rect.height() / 4, rect.width() / 2, rect.height() / 2 + rect.height() / 4, paint);
        }

        @Override
        public void setAlpha(int alpha) {

        }


        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }


        @Override
        public int getIntrinsicHeight() {
            return DP12;
        }

        @Override
        public int getIntrinsicWidth() {
            return DP1 + DP5 * 2;
        }

    }


}
