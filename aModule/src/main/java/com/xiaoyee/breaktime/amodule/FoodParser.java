package com.xiaoyee.breaktime.amodule;

import java.util.ArrayList;

/**
 * Created by xiaoyee on 2017/11/28.
 */

public class FoodParser extends ATopicParser {
    Poi mPoi;
    
    public FoodParser(Poi poi) {
        mPoi = poi;
    }
    
    @Override
    public ArrayList<ARefreshable> parse() {
        return null;
    }
}
