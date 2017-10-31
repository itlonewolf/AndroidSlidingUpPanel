package com.sothree.slidinguppanel.demo;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import com.sothree.slidinguppanel.log.Logger;

/**
 * Created by xiaoyee on 30/10/2017.
 */

public class CusConstraintView extends ConstraintLayout {
    public CusConstraintView(Context context) {
        super(context);
    }
    
    public CusConstraintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public CusConstraintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (Logger.isTagEnabled("measure")) {
            Logger.d("measure", "CusConstraintView >>> onMeasure");
        }
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (Logger.isTagEnabled("layout")) {
            Logger.d("layout", "CusConstraintView >>> onlayout");
        }
    }
}
