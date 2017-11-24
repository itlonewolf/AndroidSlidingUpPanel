package com.sothree.slidinguppanel.demo;

/**
 * Created by xiaoyee on 2017/11/15.
 *
 */

public interface IInteractive {

    /**
     * 是否是可交互的
     */
    boolean isInteractive();

    /**
     * 此组件的点击区域被点击之后的操作
     */
    void onClick();

    /**
     * 被按压下去时的颜色
     */
    int pressingColor();

    /**
     * 是否处于按压状态
     */
    boolean isPressing();

    /**
     * 设置是否正在处于按压状态
     */
    void setPressed(boolean isPressing);
}

