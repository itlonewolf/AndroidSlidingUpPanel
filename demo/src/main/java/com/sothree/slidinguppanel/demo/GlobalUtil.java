package com.sothree.slidinguppanel.demo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

public class GlobalUtil {
    
    private static Activity mainActivity;
    
    private static Context context;
    
    //
    public static Resources getResources() {
        Context context = getMainActivity();
        if (context == null) {
            context = getContext();
        }
        return context.getResources();
    }
    
    public static Activity getMainActivity() {
        return mainActivity;
    }
    
    public static void setMainActivity(Activity mainActivity) {
        GlobalUtil.mainActivity = mainActivity;
    }
    
    public static Context getContext() {
        return GlobalUtil.context;
    }
}
