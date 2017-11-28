package com.xiaoyee.breaktime.amodule;

import java.util.ArrayList;

/**
 * Created by xiaoyee on 2017/11/28.
 */

public class ParserFactory {
    public static final int FOOD = 0;
    
    public static ATopicParser getTopicParser(Poi poi) {
        switch (poi.getType()) {
            case FOOD:
                return new FoodParser(poi);
            default:
                return null;
        }
    }
    
    public void parse() {
        ATopicParser            parser = getTopicParser(new Poi());
        ArrayList<ARefreshable> units  = parser.parse();
        
    }
}
