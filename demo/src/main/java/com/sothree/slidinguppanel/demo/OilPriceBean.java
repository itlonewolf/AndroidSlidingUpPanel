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
 * 油价
 *
 * @author zhaozy
 * @date 2017/11/17
 */

public class OilPriceBean extends ARefreshable {
    Paint mLinePaint;
    /**
     * 文本的宽度
     */
    private final int width;

    //文本的高度
    private int height;

    private String title;


    private String note;
    private String oilType;
    private String oilPrice;

    private TextPaint textPaint;

    private TextArtist mNameTextArtist;
    private TextArtist mNoteTextArtist;
    private int SP14 = LayoutUtils.getPxByDimens(R.dimen.F2);
    private int DP36 = LayoutUtils.getPxByDimens(R.dimen.dp36);
    private int DP5 = LayoutUtils.getPxByDimens(R.dimen.dp5);
    private int DP15 = LayoutUtils.getPxByDimens(R.dimen.dp15);
    private int DP7 = LayoutUtils.getPxByDimens(R.dimen.dp7);
    private int DP10 = LayoutUtils.getPxByDimens(R.dimen.dp10);

    private Point mNameTextPoint;
    private Point mTypeTextPoint;
    private Point mPriceTextPoint;
    private Point mNoteTextPoint;
    private List<String> parseOilTypeList = new ArrayList<>();
    private List<String> parseOilPriceList = new ArrayList<>();
    private List<TextArtist> typeArtistList = new ArrayList<>();
    private List<TextArtist> priceArtistList = new ArrayList<>();

    public OilPriceBean(int width) {
        this.width = width - DP15;
    }

    public static OilPriceBean demoBean(int width) {
        OilPriceBean bean = new OilPriceBean(width);
        bean.title = "油价";
        bean.note = "仅供参考,请以实地为准";
        bean.oilPrice = "92:6.78,95:7.22,98:暂无";
        return bean;
    }

    @Override
    protected void initAssemble() {


        if (!TextUtils.isEmpty(oilPrice)) {
            String[] parseOilArray = oilPrice.split(",");

            for (int i = 0; i < parseOilArray.length; i++) {
                String item = parseOilArray[i];
                String[] strings = item.split(":");
                parseOilTypeList.add(strings[0]);
                parseOilPriceList.add(strings[1]);


            }

        }


        mLinePaint = new Paint();

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mNameTextPoint = new Point();
        mTypeTextPoint = new Point();
        mPriceTextPoint = new Point();
        mNoteTextPoint = new Point();

        TextArtist.TextArtistSetting nameArtistSetting = getTitleTextArtistSetting(textPaint);
        mNameTextArtist = new TextArtist(nameArtistSetting);

        TextArtist.TextArtistSetting noteArtistSetting = getNoteTextArtistSetting(textPaint);
        mNoteTextArtist = new TextArtist(noteArtistSetting);

        for (int i = 0; i < parseOilTypeList.size(); i++) {
            TextArtist.TextArtistSetting descArtistSetting = getTypeTextArtistSetting(textPaint, parseOilTypeList.get(i) + "#汽油");
            TextArtist mTypeTextArtist = new TextArtist(descArtistSetting);
            typeArtistList.add(mTypeTextArtist);


            TextArtist.TextArtistSetting priceArtistSetting = getPriceTextArtistSetting(textPaint, parseOilPriceList.get(i) + "元/升");
            TextArtist mPriceTextArtist = new TextArtist(priceArtistSetting);
            priceArtistList.add(mPriceTextArtist);

        }


        updateSize();
    }


    @Override
    public int height() {
        return width;
    }

    @Override
    public int width() {
        return height;
    }


    private void updateSize() {

        int itemHeight = 0;
        if (priceArtistList == null && priceArtistList.size() > 0) {
            for (int i = 0; i < priceArtistList.size(); i++) {
                int height = priceArtistList.get(i).getHeight();

                itemHeight += height + DP5;

            }

        }


        height = DP36  + itemHeight;
        updateBoundsInner(width, height);
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
    private TextArtist.TextArtistSetting getNoteTextArtistSetting(TextPaint textPaint) {
        TextArtist.TextArtistSetting nameArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        nameArtistSetting.setAlign(TextArtist.ALIGN_RC);
        nameArtistSetting.append(note).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.system_gray));
        nameArtistSetting.setMaxLineCount(1);
        nameArtistSetting.setOuterWidth(width);
        return nameArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getTypeTextArtistSetting(TextPaint textPaint, String oilType) {
        TextArtist.TextArtistSetting priceArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        priceArtistSetting.setAlign(TextArtist.ALIGN_LC);
        priceArtistSetting.append(oilType).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        priceArtistSetting.setMaxLineCount(1);
        priceArtistSetting.setOuterWidth((int) width);
        return priceArtistSetting;
    }

    @NonNull
    private TextArtist.TextArtistSetting getPriceTextArtistSetting(TextPaint textPaint, String oilPrice) {
        TextArtist.TextArtistSetting priceArtistSetting = new TextArtist.TextArtistSetting(textPaint);
        priceArtistSetting.setAlign(TextArtist.ALIGN_RC);
        priceArtistSetting.append(oilPrice).absoluteSize(SP14).color(GlobalUtil.getContext().getResources().getColor(R.color.FC23));
        priceArtistSetting.setMaxLineCount(1);
        priceArtistSetting.setOuterWidth((int) width);
        return priceArtistSetting;
    }


    @Override
    protected void drawContentInner(Canvas canvas) {

        //step 1、先将画布移动到对应位置
        canvas.translate(DP15, 0);

        int left = mContentBound.left;
        int top = mContentBound.top;


        //step 2、再绘制
        //step 2.1、先绘制 title
        mNameTextPoint.set(left, DP36 / 2 + top);

        mNameTextArtist.setAlignReferencePoint(mNameTextPoint);
        mNameTextArtist.draw(canvas);

        //step 2.2、备注
        int noteTextX = width - DP15;
        mNoteTextPoint.set(noteTextX, DP36 / 2 + top);
        mNoteTextArtist.setAlignReferencePoint(mNoteTextPoint);
        mNoteTextArtist.draw(canvas);

        //step 2.3、画分割线
        mLinePaint.setColor(GlobalUtil.getContext().getResources().getColor(R.color.system_gray));
        mLinePaint.setStrokeWidth(2);
        int mLineY = top + DP36;
        canvas.drawLine(left, mLineY, width, mLineY, mLinePaint);
        top += DP5 + DP36;

        for (int i = 0; i < typeArtistList.size(); i++) {
            TextArtist typeArtist = typeArtistList.get(i);
            //step 2.4、油类型
            top += DP5;
            mTypeTextPoint.set(left, typeArtist.getHeight() / 2 + top);
            typeArtist.setAlignReferencePoint(mTypeTextPoint);
            typeArtist.draw(canvas);

            //step 2.5、油价格
            TextArtist priceTextArtist = priceArtistList.get(i);
            int priceTextX = width - DP15;
            mPriceTextPoint.set(priceTextX, priceTextArtist.getHeight() / 2 + top);
            priceTextArtist.setAlignReferencePoint(mPriceTextPoint);
            priceTextArtist.draw(canvas);

            top += typeArtist.getHeight();

        }


    }
}
