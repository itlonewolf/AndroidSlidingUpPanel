package com.sothree.slidinguppanel.log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xiaoyee on 23/10/2017.
 */

public class TagManager implements ITagManager {
    private final ConcurrentHashMap<String, Tag> mTagArray;
    private static final String KEY_LOG_OPENED    = "isLogOpened";
    private static final String KEY_TAG_LIST      = "tags";
    private static final String KEY_TAG           = "tag";
    private static final String KEY_IS_TAG_ENABLE = "isEnable";
    
    private static boolean isLogOpened = false;
    
    public TagManager() {
        mTagArray = new ConcurrentHashMap<>();
    }
    
    public boolean containTag(String tag) {
        return mTagArray.containsKey(tag);
    }
    
    @Override
    public void loadTags(String tagsJson) {
        try {
            JSONObject root = new JSONObject(tagsJson);
            
            //step 1、isLogOpened
            if (root.has(KEY_LOG_OPENED)) {
                isLogOpened = root.optBoolean(KEY_LOG_OPENED);
            } else {
                throw new IllegalArgumentException("未定义 isLogOpened 属性");
            }
            
            //step 2、tag list
            if (root.has(KEY_TAG_LIST)) {
                JSONArray tagArray = root.getJSONArray(KEY_TAG_LIST);
                
                if (tagArray != null) {
                    final int tagArrayLength = tagArray.length();
                    for (int index = 0; index < tagArrayLength; index++) {
                        JSONObject tagObj = tagArray.getJSONObject(index);
                        
                        String  tag      = null;
                        boolean isEnable = false;
                        
                        if (tagObj.has(KEY_TAG)) {
                            tag = tagObj.optString(KEY_TAG);
                        }
                        
                        if (tag != null) {
                            if (tagObj.has(KEY_IS_TAG_ENABLE)) {
                                isEnable = tagObj.optBoolean(KEY_IS_TAG_ENABLE);
                            }
                            
                            mTagArray.put(tag, new Tag(tag, isEnable));
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("未定义 tag 列表");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean isTagEnable(String tag) {
        if (!isLogOpened) {
            return false;
        }
        if (!mTagArray.containsKey(tag)) {
            return false;
        }
        return mTagArray.get(tag).isEnable();
    }
    
    public void setTags(List<Tag> tags) {
        if (tags != null) {
            for (Tag tag : tags) {
                if (tag != null) {
                    mTagArray.put(tag.getTag(), tag);
                }
            }
        }
    }
    
    @Override
    public void addTag(Tag... tags) {
        if (tags != null) {
            for (Tag tag : tags) {
                if (tag != null) {
                    mTagArray.put(tag.getTag(), tag);
                }
            }
        }
    }
    
    @Override
    public void changeTag(String tag, boolean isEnable) {
        if (mTagArray.containsKey(tag)) {
            mTagArray.put(tag, new Tag(tag, isEnable));
        }
    }
}