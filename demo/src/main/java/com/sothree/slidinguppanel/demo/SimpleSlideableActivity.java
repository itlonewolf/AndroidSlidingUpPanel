package com.sothree.slidinguppanel.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

public class SimpleSlideableActivity extends AppCompatActivity {
    FrameLayout linearLayout;
    
    AssembleView mAssembleView;
//    Button btnInvalidate;
    
    TitleUnit refreshTitle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_slideable);
        linearLayout = (FrameLayout) findViewById(R.id.cus_vg);
    
        mAssembleView = (AssembleView) findViewById(R.id.assembleView);
        demoCase();
    
        mAssembleView.setClickable(true);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                refreshTitle();
//            }
//        }, 5000);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mAssembleView.requestLayout();
//            }
//        }, 10000);
    }
    
    public void refreshTitle() {
        refreshTitle.refresh();
    }
    
    
    public void demoCase() {
        DisplayMetrics dm = GlobalUtil.getResources().getDisplayMetrics();
    
        refreshTitle = TitleUnit.demoBean(dm.widthPixels);
        ARefreshable distanceBean = DistanceUnit.demoBean(dm.widthPixels);
//        ARefreshable bean2        = RecommendationBean.demoBean(dm.widthPixels);
        mAssembleView.addItems(refreshTitle, distanceBean);
//        mAssembleView.addItems(refreshTitle, distanceBean, distanceBean2);
    }
}
