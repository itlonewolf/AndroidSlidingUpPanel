package com.sothree.slidinguppanel.log;

/**
 * Created by xiaoyee on 23/10/2017.
 */

public interface ITagManager {
    void loadTags(String tagsJson);
    
    /**
     * tag 是否打开了
     */
    boolean isTagEnable(String tag);
    
    void addTag(Tag... tags);
    
    void changeTag(String tag, boolean isEnable);
    
    boolean containTag(String tag);
    
}
