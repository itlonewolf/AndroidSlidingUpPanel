package com.sothree.slidinguppanel.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SimpleSlideableActivity extends AppCompatActivity {
    View mView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_slideable);
        mView = findViewById(R.id.cus_vg);
        
    }
    
    public void invalidateBtn(View view) {
        mView.invalidate();
    }
    
    public void requestLayoutBtn(View view) {
        mView.requestLayout();
    }
}
