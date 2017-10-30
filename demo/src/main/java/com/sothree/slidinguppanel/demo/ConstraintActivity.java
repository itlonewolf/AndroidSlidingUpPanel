package com.sothree.slidinguppanel.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConstraintActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint);
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar));
        
        
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
    }
    
    public void underBtn(View view) {
        Toast.makeText(getBaseContext(), "underBtn", Toast.LENGTH_SHORT).show();
    }
}
