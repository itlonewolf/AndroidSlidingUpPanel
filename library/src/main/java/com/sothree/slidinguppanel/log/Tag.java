package com.sothree.slidinguppanel.log;

/**
 * Created by xiaoyee on 23/10/2017.
 */

public class Tag {
    private String  tag;
    private boolean isEnable;
    
    private Tag() {
    }
    
    public Tag(String tag) {
        this.tag = tag;
        this.isEnable = true;
    }
    
    public Tag(String tag, boolean isEnable) {
        this.tag = tag;
        this.isEnable = isEnable;
    }
    
    public String getTag() {
        return tag;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public boolean isEnable() {
        return isEnable;
    }
    
    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
