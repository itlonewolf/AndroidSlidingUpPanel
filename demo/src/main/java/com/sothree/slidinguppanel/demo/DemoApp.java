package com.sothree.slidinguppanel.demo;

import android.app.Application;
import com.sothree.slidinguppanel.log.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xiaoyee on 23/10/2017.
 */

public class DemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    
        try {
            InputStream  in     = getAssets().open("tags.json");
            final int    size   = in.available();
            final byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            final String tagJson = new String(buffer, "utf-8");
            Logger.initLogger(tagJson);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    
        GlobalUtil.setContext(getApplicationContext());
    }
}
