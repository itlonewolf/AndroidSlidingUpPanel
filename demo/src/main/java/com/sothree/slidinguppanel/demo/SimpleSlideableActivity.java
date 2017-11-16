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
    
        refreshTitle = TitleBean.demoBean(dm.widthPixels);
        ARefreshable   distanceBean = DistanceBean.demoBean(dm.widthPixels);
        ARefreshable   oilPriceBean = OilPriceBean.demoBean(dm.widthPixels);
        ARefreshable   parkingBean = ParkingBean.demoBean(dm.widthPixels);
        ARefreshable   landUnit = PoisummaryUnit.demoBean(dm.widthPixels);
        ARefreshable   rechargeInfoBean = ChargeSituationUnit.demoBean(dm.widthPixels);
        mAssembleView.addItems(oilPriceBean);
        refreshTitle = TitleBean.TitleUnit.demoBean(dm.widthPixels);
//        mAssembleView.addItems(refreshTitle, distanceBean, distanceBean2);
    }
}
