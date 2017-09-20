package com.sothree.slidinguppanel.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class CustomActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        
//        initLv();
    }
    
    
    private void initLv(){
//        ListView lv = (ListView) findViewById(R.id.list);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Toast.makeText(DemoActivity.this, "onItemClick", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(CustomActivity.this, CustomActivity.class));
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
//                                                    );
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
    }
    
    public void frameClick(View view) {
        Toast.makeText(this, "fragme click", Toast.LENGTH_SHORT).show();
    }
}
