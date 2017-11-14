package com.sothree.slidinguppanel.demo;

/**
 * Created by xiaoyee on 2017/11/14.
 * 可以刷新的 bean
 */

public abstract class ARefreshable implements IAssembleable {
    protected IRefreshListener mRefreshListener;
    
    @Override
    public void addRefreshListener(IRefreshListener listener) {
        mRefreshListener = listener;
    }
    
    public abstract void refresh();
}
