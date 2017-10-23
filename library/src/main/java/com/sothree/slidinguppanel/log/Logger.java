package com.sothree.slidinguppanel.log;

import android.util.Log;

/**
 * Created by xiaoyee on 23/10/2017.
 */

public class Logger {
    private static final String      LOGGER_TAG  = "Logger";
    private final static ITagManager mTagManager = new TagManager();
    
    
    public static void initLogger(String tagsJson) {
        Log.d(LOGGER_TAG, String.format("初始化 tag json 为: %s", tagsJson));
        mTagManager.loadTags(tagsJson);
    }
    
    public static boolean isTagEnabled(String tag) {
        return mTagManager.isTagEnable(tag);
    }
    
    public static void d(String tag, String info) {
        if (mTagManager.containTag(tag)) {
            Log.d(tag, info);
        } else {
            Log.w(LOGGER_TAG, String.format("为初始化此 tag: %s", tag));
        }
    }
    
    public static void d(String tag, String format, Object... args) {
        d(tag, String.format(format, args));
    }
}
