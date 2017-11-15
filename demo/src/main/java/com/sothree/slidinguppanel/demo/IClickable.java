package com.sothree.slidinguppanel.demo;

import android.view.View;

/**
 * Created by xiaoyee on 2017/11/15.
 */

public interface IClickable {
    boolean isClickable();
    
    View.OnClickListener onClickListener();
    
}
