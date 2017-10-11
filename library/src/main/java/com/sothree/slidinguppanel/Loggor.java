package com.sothree.slidinguppanel;


import com.orhanobut.logger.*;


/**
 * Created by xiaoyee on 10/10/2017.
 * 日志
 */

public final class Loggor {
    static {
        FormatStrategy supFormatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(2)         // (Optional) How many method line to show. Default 2
                .methodOffset(2)        // (Optional) Hides internal method calls up to offset. Default 5
//                .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("SUPYEEs")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
    
        LogAdapter supLogAdapter = new AndroidLogAdapter(supFormatStrategy){
            @Override
            public boolean isLoggable(int priority, String tag) {
                return super.isLoggable(priority, tag);
            }
        };
        
    
        FormatStrategy defaultFormatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .build();
    
        LogAdapter defaultLogAdapter = new AndroidLogAdapter(defaultFormatStrategy){
            @Override
            public boolean isLoggable(int priority, String tag) {
                return super.isLoggable(priority, tag);
            }
        };
        
        Logger.addLogAdapter(supLogAdapter);
        Logger.addLogAdapter(defaultLogAdapter);
    }
    public static void voidMethod(){
    
    }
    
    
    public static void log(String message, Object... args) {
        Logger.d(message, args);
    }
    public static void log(String info) {
        Logger.d(info);
    }
    
    public static void log(String tag, String info) {
        Logger.t(tag).d(info);
    }
    
    public static void log(String tag, String message, Object... args) {
        Logger.t(tag).d(message, args);
    }
}
