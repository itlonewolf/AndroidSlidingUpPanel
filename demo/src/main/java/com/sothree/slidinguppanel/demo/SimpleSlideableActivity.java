package com.sothree.slidinguppanel.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

public class SimpleSlideableActivity extends AppCompatActivity {
    FrameLayout linearLayout;
    
    AssembleView mAssembleView;
//    Button btnInvalidate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_slideable);
        linearLayout = (FrameLayout) findViewById(R.id.cus_vg);
//        btnInvalidate = (Button) findViewById(R.id.btnInvalidate);
        mAssembleView = new AssembleView(this);
        mAssembleView.setClickable(true);
        linearLayout.addView(mAssembleView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAssembleView.refreshTitle();
            }
        }, 5000);
    }
}
