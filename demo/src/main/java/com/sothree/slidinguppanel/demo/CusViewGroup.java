package com.sothree.slidinguppanel.demo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.sothree.slidinguppanel.log.Logger;

/**
 * Created by xiaoyee on 31/10/2017.
 */

public class CusViewGroup extends LinearLayout {
    public CusViewGroup(Context context) {
        super(context);
    }
    
    public CusViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    
    public CusViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (Logger.isTagEnabled("measure")) {
            Logger.d("measure", Logger.OFFSET_ALL_METHOD);
        }
        
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (Logger.isTagEnabled("layout")) {
            Logger.d("layout", Logger.OFFSET_ALL_METHOD);
        }
        super.onLayout(changed, l, t, r, b);
    }
}
