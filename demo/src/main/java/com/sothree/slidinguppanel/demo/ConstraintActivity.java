package com.sothree.slidinguppanel.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.log.Logger;

public class ConstraintActivity extends AppCompatActivity {
    
    View                 mView;
    View                 mbtn_navi;
    SlidingUpPanelLayout mPanelLayout;
    
    View mParent;
    
    //    private int marginBottomInit;
    private int base_bottom;
    ConstraintLayout.LayoutParams mBaselineParams;
    
    Guideline mGuideline;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint);
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar));
    
        mParent = findViewById(R.id.container_constraint);
    
        mParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            
            }
        });
        
        TextView t = (TextView) findViewById(R.id.name);
        t.setText(Html.fromHtml(getString(R.string.hello)));
        Button f = (Button) findViewById(R.id.follow);
        f.setText(Html.fromHtml(getString(R.string.follow)));
        f.setMovementMethod(LinkMovementMethod.getInstance());
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://www.twitter.com/umanoapp"));
                startActivity(i);
            }
        });
    
        mView = findViewById(R.id.layout_under_slide);
        mbtn_navi = findViewById(R.id.btn_navi);
    
        mGuideline = (Guideline) findViewById(R.id.base_line);
        mBaselineParams = (ConstraintLayout.LayoutParams) mGuideline.getLayoutParams();
        if (Logger.isTagEnabled("drag")) {
            Logger.d("drag", "guide line 的初始值为: %s", base_bottom);
        }
    
        base_bottom = mBaselineParams.guideEnd;
//        mGuideline.set


//        mView = findViewById(R.id.layout_under_slide);
//        mParams = (ConstraintLayout.LayoutParams) mView.getLayoutParams();
//        marginBottomInit = mParams.bottomMargin;
    
        mPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
    
        mPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.SimplePanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset, float slideRange) {
            
                final int slideDis = (int) (slideOffset * slideRange);
                if (Logger.isTagEnabled("slide")) {
                    Logger.d("slide", "滑动的距离为: %s", slideDis);
                }

//                mBaselineParams.guideEnd = base_bottom + slideDis;
//                mGuideline.setLayoutParams(mBaselineParams);
//                ViewCompat.postInvalidateOnAnimation(mGuideline);
    
                mbtn_navi.setTranslationY(-slideDis);
                mView.setTranslationY(-slideDis);
//                ViewCompat.postInvalidateOnAnimation(mbtn_navi);
//                ViewCompat.postInvalidateOnAnimation(mView);
            
            }
        });
        
    }
    
    public void underBtn(View view) {
        Toast.makeText(getBaseContext(), "underBtn", Toast.LENGTH_SHORT).show();
    }
}
