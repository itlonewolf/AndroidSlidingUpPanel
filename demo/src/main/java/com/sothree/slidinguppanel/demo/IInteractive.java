package com.sothree.slidinguppanel.demo;

/**
 * Created by xiaoyee on 2017/11/15.
 *
 */

public interface IInteractive {
    
    boolean isClickable();
    
    void onClick();
    
    int pressingColor();
    
    boolean isPressing();
    
    void setPressed(boolean isPressing);
}
