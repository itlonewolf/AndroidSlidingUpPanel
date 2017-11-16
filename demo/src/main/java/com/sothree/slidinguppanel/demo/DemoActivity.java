package com.sothree.slidinguppanel.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemoActivity extends AppCompatActivity {
    private static final String TAG = "DemoActivity";
    
    private SlidingUpPanelLayout mLayout;
    private Button               btnInCollapsed;
    
    
    private RecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
    private List<Integer> mDatas;
    AssembleView mAssembleView;
    
    TitleUnit refreshTitle;
//    private View leadingView;

//    private View viewWaiting2Show;
    
    private void initDatas() {
        mDatas = new ArrayList<>(Arrays.asList(R.drawable.ic_launcher,
                                               R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher,
                                               R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher
                                              ));
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalUtil.setMainActivity(this);
        setContentView(R.layout.activity_demo);
    
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar));
    
        //case ▼ recyclerview 相关初始化 xiaoyee ▼
//        initDatas();
//        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview_horizontal);
        //设置布局管理器
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        mRecyclerView.setLayoutManager(linearLayoutManager);
//        //设置适配器
//        mAdapter = new GalleryAdapter(this, mDatas);
//        mRecyclerView.setAdapter(mAdapter);
        //case ▲ recyclerview 相关初始化 xiaoyee ▲

//        ListView lv = (ListView) findViewById(R.id.list);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(DemoActivity.this, "onItemClick", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        List<String> your_array_list = Arrays.asList(
//                "This",
//                "Is",
//                "An",
//                "Example",
//                "ListView",
//                "That",
//                "You",
//                "Can",
//                "Scroll",
//                ".",
//                "It",
//                "Shows",
//                "How",
//                "Any",
//                "Scrollable",
//                "View",
//                "Can",
//                "Be",
//                "Included",
//                "As",
//                "A",
//                "Child",
//                "Of",
//                "SlidingUpPanelLayout"
//        );
//
//        // This is the array adapter, it takes the context of the activity as a
//        // first parameter, the type of list view as a second parameter and your
//        // array as a third parameter.
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                this,
//                android.R.layout.simple_list_item_1,
//                your_array_list );
//
//        lv.setAdapter(arrayAdapter);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

//        mAssembleView = new AssembleView(this);
        mAssembleView = (AssembleView) findViewById(R.id.assembleView);
        demoCase();
        
//
//
//        btnInCollapsed = (Button) findViewById(R.id.btnInCollapsed);
//        btnInCollapsed.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(DemoActivity.this, ConstraintActivity.class));
////                Toast.makeText(getApplicationContext(), "btnInCollapsed", Toast.LENGTH_SHORT).show();
////                mLayout.demoListener();
//            }
//        });
////        leadingView = findViewById(R.id.collapsed_view);
////        viewWaiting2Show = findViewById(R.id.sliding_view);
////
////        mLayout.addPanelSlideListener(new PanelSlideListener() {
////            @Override
////            public void onPanelSlide(View panel, float slideOffset) {
////                if (slideOffset > 0) {
////                    if (leadingView.getVisibility() != View.INVISIBLE) {
////                        leadingView.setVisibility(View.INVISIBLE);
////                        viewWaiting2Show.setVisibility(View.VISIBLE);
////                        mLayout.requestLayout();
////                    }
////                } else {
////                    if (leadingView.getVisibility() != View.VISIBLE) {
////                        leadingView.setVisibility(View.VISIBLE);
////                        viewWaiting2Show.setVisibility(View.INVISIBLE);
////                        mLayout.requestLayout();
////                    }
////                }
////
////            }
////
////            @Override
////            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
////
////            }
////        });
//
//        mLayout.setAnchorPoint(0.7f);
//        mLayout.setFadeOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mLayout.setPanelState(PanelState.COLLAPSED);
//            }
//        });
//
//        TextView t = (TextView) findViewById(R.id.name);
//        t.setText(Html.fromHtml(getString(R.string.hello)));
//        Button f = (Button) findViewById(R.id.follow);
//        f.setText(Html.fromHtml(getString(R.string.follow)));
//        f.setMovementMethod(LinkMovementMethod.getInstance());
//        f.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse("http://www.twitter.com/umanoapp"));
//                startActivity(i);
//            }
//        });
    }
    
    public void demoCase() {
        DisplayMetrics dm = GlobalUtil.getResources().getDisplayMetrics();
        
        refreshTitle = TitleUnit.demoBean(dm.widthPixels);
        ARefreshable distanceBean  = DistanceUnit.demoBean(dm.widthPixels);
        ARefreshable distanceBean2 = DistanceUnit.demoBean(dm.widthPixels);
        mAssembleView.addItems(distanceBean2, refreshTitle, distanceBean);
//        mAssembleView.addItems(refreshTitle, distanceBean, distanceBean2);
    }
    
    public void btnFloat(View view) {
//        Toast.makeText(getApplicationContext(), "btnFloat", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(DemoActivity.this, SimpleSlideableActivity.class));
        
    }
    
    public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
        private LayoutInflater mInflater;
        private List<Integer>  mDatas;
        
        public GalleryAdapter(Context context, List<Integer> datats) {
            mInflater = LayoutInflater.from(context);
            mDatas = datats;
        }
        
        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View arg0) {
                super(arg0);
            }
            
            ImageView mImg;
            TextView  mTxt;
        }
        
        @Override
        public int getItemCount() {
            return mDatas.size();
        }
        
        /**
         * 创建ViewHolder
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.ry_item_layout,
                                          viewGroup, false
                                         );
            ViewHolder viewHolder = new ViewHolder(view);
            
            viewHolder.mImg = (ImageView) view
                    .findViewById(R.id.id_index_gallery_item_image);
            return viewHolder;
        }
        
        /**
         * 设置值
         */
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            viewHolder.mImg.setImageResource(mDatas.get(i));
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.demo, menu);
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (mLayout != null) {
            if (mLayout.getPanelState() == PanelState.HIDDEN) {
                item.setTitle(R.string.action_show);
            } else {
                item.setTitle(R.string.action_hide);
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_toggle: {
                if (mLayout != null) {
                    if (mLayout.getPanelState() != PanelState.HIDDEN) {
                        mLayout.setPanelState(PanelState.HIDDEN);
                        item.setTitle(R.string.action_show);
                    } else {
                        mLayout.setPanelState(PanelState.COLLAPSED);
                        item.setTitle(R.string.action_hide);
                    }
                }
                return true;
            }
            case R.id.action_anchor: {
                if (mLayout != null) {
                    if (mLayout.getAnchorPoint() == 1.0f) {
                        mLayout.setAnchorPoint(0.7f);
                        mLayout.setPanelState(PanelState.ANCHORED);
                        item.setTitle(R.string.action_anchor_disable);
                    } else {
                        mLayout.setAnchorPoint(1.0f);
                        mLayout.setPanelState(PanelState.COLLAPSED);
                        item.setTitle(R.string.action_anchor_enable);
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == PanelState.EXPANDED || mLayout.getPanelState() == PanelState.ANCHORED)) {
            mLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
