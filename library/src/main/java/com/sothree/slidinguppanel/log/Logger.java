package com.sothree.slidinguppanel.log;

import android.util.Log;

/**
 * Created by xiaoyee on 23/10/2017.
 */

public class Logger {
    private static final String      LOGGER_TAG  = "Logger";
    private final static ITagManager mTagManager = new TagManager();
    
    public static final int OFFSET_ALL_METHOD = -1024;
    
    
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
    
    
    public static void d(String tag, int methodOffset) {
        StackTraceElement[] traces      = Thread.currentThread().getStackTrace();
        final int           traceLength = traces.length;
        if (methodOffset == OFFSET_ALL_METHOD) {
        
        }

//        if (traceLength > methodOffset) {
//
//        }
//
//        final int length = Math.min(traceLength, Math.abs(traceLength - methodOffset));
        final StringBuilder builder = new StringBuilder("trace info:");
        for (int index = traceLength - 1; index > 0; index--) {
            StackTraceElement trace = traces[index];
            builder.append("\r\n\t")
                    .append(' ')
                    .append(getSimpleClassName(trace.getClassName()))
                    .append(".")
                    .append(trace.getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace.getFileName())
                    .append(":")
                    .append(trace.getLineNumber())
                    .append(")");
        }
        d(tag, builder.toString());
    }
    
    private static String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }
}